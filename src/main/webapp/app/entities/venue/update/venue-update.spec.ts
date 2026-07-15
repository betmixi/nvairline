import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { VenueService } from '../service/venue.service';
import { IVenue } from '../venue.model';

import { VenueFormService } from './venue-form.service';
import { VenueUpdate } from './venue-update';

describe('Venue Management Update Component', () => {
  let comp: VenueUpdate;
  let fixture: ComponentFixture<VenueUpdate>;
  let activatedRoute: ActivatedRoute;
  let venueFormService: VenueFormService;
  let venueService: VenueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(VenueUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    venueFormService = TestBed.inject(VenueFormService);
    venueService = TestBed.inject(VenueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const venue: IVenue = { id: 22163 };

      activatedRoute.data = of({ venue });
      comp.ngOnInit();

      expect(comp.venue).toEqual(venue);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVenue>();
      const venue = { id: 5387 };
      vitest.spyOn(venueFormService, 'getVenue').mockReturnValue(venue);
      vitest.spyOn(venueService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ venue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(venue);
      saveSubject.complete();

      // THEN
      expect(venueFormService.getVenue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(venueService.update).toHaveBeenCalledWith(expect.objectContaining(venue));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVenue>();
      const venue = { id: 5387 };
      vitest.spyOn(venueFormService, 'getVenue').mockReturnValue({ id: null });
      vitest.spyOn(venueService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ venue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(venue);
      saveSubject.complete();

      // THEN
      expect(venueFormService.getVenue).toHaveBeenCalled();
      expect(venueService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVenue>();
      const venue = { id: 5387 };
      vitest.spyOn(venueService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ venue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(venueService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
