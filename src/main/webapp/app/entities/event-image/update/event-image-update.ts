import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IEventImage } from '../event-image.model';
import { EventImageService } from '../service/event-image.service';

import { EventImageFormGroup, EventImageFormService } from './event-image-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-event-image-update',
  templateUrl: './event-image-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EventImageUpdate implements OnInit {
  readonly isSaving = signal(false);
  eventImage: IEventImage | null = null;

  eventsSharedCollection = signal<IEvent[]>([]);

  protected eventImageService = inject(EventImageService);
  protected eventImageFormService = inject(EventImageFormService);
  protected eventService = inject(EventService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventImageFormGroup = this.eventImageFormService.createEventImageFormGroup();

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventImage }) => {
      this.eventImage = eventImage;
      if (eventImage) {
        this.updateForm(eventImage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const eventImage = this.eventImageFormService.getEventImage(this.editForm);
    if (eventImage.id === null) {
      this.subscribeToSaveResponse(this.eventImageService.create(eventImage));
    } else {
      this.subscribeToSaveResponse(this.eventImageService.update(eventImage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEventImage | null>): void {
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

  protected updateForm(eventImage: IEventImage): void {
    this.eventImage = eventImage;
    this.eventImageFormService.resetForm(this.editForm, eventImage);

    this.eventsSharedCollection.update(events => this.eventService.addEventToCollectionIfMissing<IEvent>(events, eventImage.event));
  }

  protected loadRelationshipsOptions(): void {
    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.eventImage?.event)))
      .subscribe((events: IEvent[]) => this.eventsSharedCollection.set(events));
  }
}
