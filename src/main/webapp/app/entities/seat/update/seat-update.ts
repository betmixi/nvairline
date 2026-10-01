import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { AircraftService } from 'app/entities/aircraft/service/aircraft.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ISeat, SeatType } from '../seat.model';
import { SeatService } from '../service/seat.service';

import { SeatFormGroup, SeatFormService } from './seat-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-seat-update',
  templateUrl: './seat-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SeatUpdate implements OnInit {
  readonly isSaving = signal(false);
  seat: ISeat | null = null;

  readonly seatTypes: SeatType[] = ['STANDARD', 'VIP', 'COUPLE'];

  aircraftsSharedCollection = signal<IAircraft[]>([]);

  protected seatService = inject(SeatService);
  protected seatFormService = inject(SeatFormService);
  protected aircraftService = inject(AircraftService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SeatFormGroup = this.seatFormService.createSeatFormGroup();

  compareAircraft = (o1: IAircraft | null, o2: IAircraft | null): boolean => this.aircraftService.compareAircraft(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ seat }) => {
      this.seat = seat;
      if (seat) {
        this.updateForm(seat);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const seat = this.seatFormService.getSeat(this.editForm);
    if (seat.id === null) {
      this.subscribeToSaveResponse(this.seatService.create(seat));
    } else {
      this.subscribeToSaveResponse(this.seatService.update(seat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISeat | null>): void {
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

  protected updateForm(seat: ISeat): void {
    this.seat = seat;
    this.seatFormService.resetForm(this.editForm, seat);

    this.aircraftsSharedCollection.update(aircrafts =>
      this.aircraftService.addAircraftToCollectionIfMissing<IAircraft>(aircrafts, seat.aircraft),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.aircraftService
      .query()
      .pipe(map((res: HttpResponse<IAircraft[]>) => res.body ?? []))
      .pipe(
        map((aircrafts: IAircraft[]) => this.aircraftService.addAircraftToCollectionIfMissing<IAircraft>(aircrafts, this.seat?.aircraft)),
      )
      .subscribe((aircrafts: IAircraft[]) => this.aircraftsSharedCollection.set(aircrafts));
  }
}
