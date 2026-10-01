import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAuditLog } from '../audit-log.model';
import { AuditLogService } from '../service/audit-log.service';

import { AuditLogFormGroup, AuditLogFormService } from './audit-log-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-audit-log-update',
  templateUrl: './audit-log-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AuditLogUpdate implements OnInit {
  readonly isSaving = signal(false);
  auditLog: IAuditLog | null = null;

  usersSharedCollection = signal<IUser[]>([]);

  protected auditLogService = inject(AuditLogService);
  protected auditLogFormService = inject(AuditLogFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AuditLogFormGroup = this.auditLogFormService.createAuditLogFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ auditLog }) => {
      this.auditLog = auditLog;
      if (auditLog) {
        this.updateForm(auditLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const auditLog = this.auditLogFormService.getAuditLog(this.editForm);
    if (auditLog.id === null) {
      this.subscribeToSaveResponse(this.auditLogService.create(auditLog));
    } else {
      this.subscribeToSaveResponse(this.auditLogService.update(auditLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAuditLog | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Cuon len thong bao loi (jhi-alert-error) de nguoi dung nhin thay ly do luu that bai (trung, de trong, so am...).
    setTimeout(() => document.querySelector('jhi-alert-error')?.scrollIntoView({ behavior: 'smooth', block: 'center' }), 100);
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(auditLog: IAuditLog): void {
    this.auditLog = auditLog;
    this.auditLogFormService.resetForm(this.editForm, auditLog);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, auditLog.user));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.auditLog?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
