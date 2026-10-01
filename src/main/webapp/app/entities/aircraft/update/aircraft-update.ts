import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAircraft } from '../aircraft.model';
import { AircraftService } from '../service/aircraft.service';

import { AircraftFormGroup, AircraftFormService } from './aircraft-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-aircraft-update',
  templateUrl: './aircraft-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AircraftUpdate implements OnInit {
  readonly isSaving = signal(false);
  aircraft: IAircraft | null = null;

  protected aircraftService = inject(AircraftService);
  protected aircraftFormService = inject(AircraftFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AircraftFormGroup = this.aircraftFormService.createAircraftFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ aircraft }) => {
      this.aircraft = aircraft;
      if (aircraft) {
        this.updateForm(aircraft);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const aircraft = this.aircraftFormService.getAircraft(this.editForm);
    if (aircraft.id === null) {
      this.subscribeToSaveResponse(this.aircraftService.create(aircraft));
    } else {
      this.subscribeToSaveResponse(this.aircraftService.update(aircraft));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAircraft | null>): void {
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

  protected updateForm(aircraft: IAircraft): void {
    this.aircraft = aircraft;
    this.aircraftFormService.resetForm(this.editForm, aircraft);
  }
}
