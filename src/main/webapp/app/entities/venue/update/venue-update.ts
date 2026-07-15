import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { VenueService } from '../service/venue.service';
import { IVenue } from '../venue.model';

import { VenueFormGroup, VenueFormService } from './venue-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-venue-update',
  templateUrl: './venue-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class VenueUpdate implements OnInit {
  readonly isSaving = signal(false);
  venue: IVenue | null = null;

  protected venueService = inject(VenueService);
  protected venueFormService = inject(VenueFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VenueFormGroup = this.venueFormService.createVenueFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ venue }) => {
      this.venue = venue;
      if (venue) {
        this.updateForm(venue);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const venue = this.venueFormService.getVenue(this.editForm);
    if (venue.id === null) {
      this.subscribeToSaveResponse(this.venueService.create(venue));
    } else {
      this.subscribeToSaveResponse(this.venueService.update(venue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IVenue | null>): void {
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

  protected updateForm(venue: IVenue): void {
    this.venue = venue;
    this.venueFormService.resetForm(this.editForm, venue);
  }
}
