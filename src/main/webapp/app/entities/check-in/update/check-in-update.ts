import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { TicketService } from 'app/entities/ticket/service/ticket.service';
import { ITicket } from 'app/entities/ticket/ticket.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICheckIn } from '../check-in.model';
import { CheckInService } from '../service/check-in.service';

import { CheckInFormGroup, CheckInFormService } from './check-in-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-check-in-update',
  templateUrl: './check-in-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CheckInUpdate implements OnInit {
  readonly isSaving = signal(false);
  checkIn: ICheckIn | null = null;

  ticketsSharedCollection = signal<ITicket[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected checkInService = inject(CheckInService);
  protected checkInFormService = inject(CheckInFormService);
  protected ticketService = inject(TicketService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CheckInFormGroup = this.checkInFormService.createCheckInFormGroup();

  compareTicket = (o1: ITicket | null, o2: ITicket | null): boolean => this.ticketService.compareTicket(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ checkIn }) => {
      this.checkIn = checkIn;
      if (checkIn) {
        this.updateForm(checkIn);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const checkIn = this.checkInFormService.getCheckIn(this.editForm);
    if (checkIn.id === null) {
      this.subscribeToSaveResponse(this.checkInService.create(checkIn));
    } else {
      this.subscribeToSaveResponse(this.checkInService.update(checkIn));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICheckIn | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(checkIn: ICheckIn): void {
    this.checkIn = checkIn;
    this.checkInFormService.resetForm(this.editForm, checkIn);

    this.ticketsSharedCollection.update(tickets => this.ticketService.addTicketToCollectionIfMissing<ITicket>(tickets, checkIn.ticket));
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, checkIn.checkedBy));
  }

  protected loadRelationshipsOptions(): void {
    this.ticketService
      .query()
      .pipe(map((res: HttpResponse<ITicket[]>) => res.body ?? []))
      .pipe(map((tickets: ITicket[]) => this.ticketService.addTicketToCollectionIfMissing<ITicket>(tickets, this.checkIn?.ticket)))
      .subscribe((tickets: ITicket[]) => this.ticketsSharedCollection.set(tickets));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.checkIn?.checkedBy)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
