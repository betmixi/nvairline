import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IFavorite } from '../favorite.model';
import { FavoriteService } from '../service/favorite.service';

import { FavoriteFormGroup, FavoriteFormService } from './favorite-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-favorite-update',
  templateUrl: './favorite-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FavoriteUpdate implements OnInit {
  readonly isSaving = signal(false);
  favorite: IFavorite | null = null;

  usersSharedCollection = signal<IUser[]>([]);
  eventsSharedCollection = signal<IEvent[]>([]);

  protected favoriteService = inject(FavoriteService);
  protected favoriteFormService = inject(FavoriteFormService);
  protected userService = inject(UserService);
  protected eventService = inject(EventService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FavoriteFormGroup = this.favoriteFormService.createFavoriteFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ favorite }) => {
      this.favorite = favorite;
      if (favorite) {
        this.updateForm(favorite);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const favorite = this.favoriteFormService.getFavorite(this.editForm);
    if (favorite.id === null) {
      this.subscribeToSaveResponse(this.favoriteService.create(favorite));
    } else {
      this.subscribeToSaveResponse(this.favoriteService.update(favorite));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFavorite | null>): void {
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

  protected updateForm(favorite: IFavorite): void {
    this.favorite = favorite;
    this.favoriteFormService.resetForm(this.editForm, favorite);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, favorite.user));
    this.eventsSharedCollection.update(events => this.eventService.addEventToCollectionIfMissing<IEvent>(events, favorite.event));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.favorite?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.favorite?.event)))
      .subscribe((events: IEvent[]) => this.eventsSharedCollection.set(events));
  }
}
