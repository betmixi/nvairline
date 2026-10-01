import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IBookingDetail } from 'app/entities/booking-detail/booking-detail.model';
import { BookingDetailService } from 'app/entities/booking-detail/service/booking-detail.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { TicketService } from '../service/ticket.service';
import { ITicket } from '../ticket.model';

import { TicketFormGroup, TicketFormService } from './ticket-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-ticket-update',
  templateUrl: './ticket-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TicketUpdate implements OnInit {
  readonly isSaving = signal(false);
  ticket: ITicket | null = null;

  bookingDetailsSharedCollection = signal<IBookingDetail[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected ticketService = inject(TicketService);
  protected ticketFormService = inject(TicketFormService);
  protected bookingDetailService = inject(BookingDetailService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TicketFormGroup = this.ticketFormService.createTicketFormGroup();

  compareBookingDetail = (o1: IBookingDetail | null, o2: IBookingDetail | null): boolean =>
    this.bookingDetailService.compareBookingDetail(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticket }) => {
      this.ticket = ticket;
      if (ticket) {
        this.updateForm(ticket);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('dugxApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ticket = this.ticketFormService.getTicket(this.editForm);
    if (ticket.id === null) {
      this.subscribeToSaveResponse(this.ticketService.create(ticket));
    } else {
      this.subscribeToSaveResponse(this.ticketService.update(ticket));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITicket | null>): void {
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

  protected updateForm(ticket: ITicket): void {
    this.ticket = ticket;
    this.ticketFormService.resetForm(this.editForm, ticket);

    this.bookingDetailsSharedCollection.update(bookingDetails =>
      this.bookingDetailService.addBookingDetailToCollectionIfMissing<IBookingDetail>(bookingDetails, ticket.bookingDetail),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookingDetailService
      .query()
      .pipe(map((res: HttpResponse<IBookingDetail[]>) => res.body ?? []))
      .pipe(
        map((bookingDetails: IBookingDetail[]) =>
          this.bookingDetailService.addBookingDetailToCollectionIfMissing<IBookingDetail>(bookingDetails, this.ticket?.bookingDetail),
        ),
      )
      .subscribe((bookingDetails: IBookingDetail[]) => this.bookingDetailsSharedCollection.set(bookingDetails));
  }
}
