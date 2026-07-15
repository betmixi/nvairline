import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IOrganizer } from 'app/entities/organizer/organizer.model';
import { OrganizerService } from 'app/entities/organizer/service/organizer.service';
import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';

import { EventFormService } from './event-form.service';
import { EventUpdate } from './event-update';

describe('Event Management Update Component', () => {
  let comp: EventUpdate;
  let fixture: ComponentFixture<EventUpdate>;
  let activatedRoute: ActivatedRoute;
  let eventFormService: EventFormService;
  let eventService: EventService;
  let categoryService: CategoryService;
  let addressService: AddressService;
  let organizerService: OrganizerService;

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

    fixture = TestBed.createComponent(EventUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventFormService = TestBed.inject(EventFormService);
    eventService = TestBed.inject(EventService);
    categoryService = TestBed.inject(CategoryService);
    addressService = TestBed.inject(AddressService);
    organizerService = TestBed.inject(OrganizerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Category query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const category: ICategory = { id: 6752 };
      event.category = category;

      const categoryCollection: ICategory[] = [{ id: 6752 }];
      vitest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      vitest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.categoriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Address query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const address: IAddress = { id: 2318 };
      event.address = address;

      const addressCollection: IAddress[] = [{ id: 2318 }];
      vitest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
      const additionalAddresses = [address];
      const expectedCollection: IAddress[] = [...additionalAddresses, ...addressCollection];
      vitest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(addressService.query).toHaveBeenCalled();
      expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(
        addressCollection,
        ...additionalAddresses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.addressesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Organizer query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const organizer: IOrganizer = { id: 278 };
      event.organizer = organizer;

      const organizerCollection: IOrganizer[] = [{ id: 278 }];
      vitest.spyOn(organizerService, 'query').mockReturnValue(of(new HttpResponse({ body: organizerCollection })));
      const additionalOrganizers = [organizer];
      const expectedCollection: IOrganizer[] = [...additionalOrganizers, ...organizerCollection];
      vitest.spyOn(organizerService, 'addOrganizerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(organizerService.query).toHaveBeenCalled();
      expect(organizerService.addOrganizerToCollectionIfMissing).toHaveBeenCalledWith(
        organizerCollection,
        ...additionalOrganizers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.organizersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const event: IEvent = { id: 3268 };
      const category: ICategory = { id: 6752 };
      event.category = category;
      const address: IAddress = { id: 2318 };
      event.address = address;
      const organizer: IOrganizer = { id: 278 };
      event.organizer = organizer;

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection()).toContainEqual(category);
      expect(comp.addressesSharedCollection()).toContainEqual(address);
      expect(comp.organizersSharedCollection()).toContainEqual(organizer);
      expect(comp.event).toEqual(event);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEvent>();
      const event = { id: 22576 };
      vitest.spyOn(eventFormService, 'getEvent').mockReturnValue(event);
      vitest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(event);
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventService.update).toHaveBeenCalledWith(expect.objectContaining(event));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEvent>();
      const event = { id: 22576 };
      vitest.spyOn(eventFormService, 'getEvent').mockReturnValue({ id: null });
      vitest.spyOn(eventService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(event);
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(eventService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEvent>();
      const event = { id: 22576 };
      vitest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('should forward to categoryService', () => {
        const entity = { id: 6752 };
        const entity2 = { id: 4374 };
        vitest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAddress', () => {
      it('should forward to addressService', () => {
        const entity = { id: 2318 };
        const entity2 = { id: 19327 };
        vitest.spyOn(addressService, 'compareAddress');
        comp.compareAddress(entity, entity2);
        expect(addressService.compareAddress).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOrganizer', () => {
      it('should forward to organizerService', () => {
        const entity = { id: 278 };
        const entity2 = { id: 29151 };
        vitest.spyOn(organizerService, 'compareOrganizer');
        comp.compareOrganizer(entity, entity2);
        expect(organizerService.compareOrganizer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
