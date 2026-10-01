import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAirport } from '../airport.model';
import { AirportService } from '../service/airport.service';

import { AirportFormGroup, AirportFormService } from './airport-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-airport-update',
  templateUrl: './airport-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AirportUpdate implements OnInit {
  readonly isSaving = signal(false);
  airport: IAirport | null = null;

  protected airportService = inject(AirportService);
  protected airportFormService = inject(AirportFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AirportFormGroup = this.airportFormService.createAirportFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ airport }) => {
      this.airport = airport;
      if (airport) {
        this.updateForm(airport);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const airport = this.airportFormService.getAirport(this.editForm);
    if (airport.id === null) {
      this.subscribeToSaveResponse(this.airportService.create(airport));
    } else {
      this.subscribeToSaveResponse(this.airportService.update(airport));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAirport | null>): void {
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

  protected updateForm(airport: IAirport): void {
    this.airport = airport;
    this.airportFormService.resetForm(this.editForm, airport);
  }
}
