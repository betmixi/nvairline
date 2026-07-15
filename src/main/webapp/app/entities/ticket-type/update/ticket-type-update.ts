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
import { TicketTypeService } from '../service/ticket-type.service';
import { ITicketType } from '../ticket-type.model';

import { TicketTypeFormGroup, TicketTypeFormService } from './ticket-type-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-ticket-type-update',
  templateUrl: './ticket-type-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TicketTypeUpdate implements OnInit {
  readonly isSaving = signal(false);
  ticketType: ITicketType | null = null;

  eventsSharedCollection = signal<IEvent[]>([]);

  protected ticketTypeService = inject(TicketTypeService);
  protected ticketTypeFormService = inject(TicketTypeFormService);
  protected eventService = inject(EventService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TicketTypeFormGroup = this.ticketTypeFormService.createTicketTypeFormGroup();

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticketType }) => {
      this.ticketType = ticketType;
      if (ticketType) {
        this.updateForm(ticketType);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ticketType = this.ticketTypeFormService.getTicketType(this.editForm);
    if (ticketType.id === null) {
      this.subscribeToSaveResponse(this.ticketTypeService.create(ticketType));
    } else {
      this.subscribeToSaveResponse(this.ticketTypeService.update(ticketType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITicketType | null>): void {
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

  protected updateForm(ticketType: ITicketType): void {
    this.ticketType = ticketType;
    this.ticketTypeFormService.resetForm(this.editForm, ticketType);

    this.eventsSharedCollection.update(events => this.eventService.addEventToCollectionIfMissing<IEvent>(events, ticketType.event));
  }

  protected loadRelationshipsOptions(): void {
    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.ticketType?.event)))
      .subscribe((events: IEvent[]) => this.eventsSharedCollection.set(events));
  }
}
