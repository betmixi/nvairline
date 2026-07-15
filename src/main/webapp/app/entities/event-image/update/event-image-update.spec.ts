import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IEventImage } from '../event-image.model';
import { EventImageService } from '../service/event-image.service';

import { EventImageFormService } from './event-image-form.service';
import { EventImageUpdate } from './event-image-update';

describe('EventImage Management Update Component', () => {
  let comp: EventImageUpdate;
  let fixture: ComponentFixture<EventImageUpdate>;
  let activatedRoute: ActivatedRoute;
  let eventImageFormService: EventImageFormService;
  let eventImageService: EventImageService;
  let eventService: EventService;

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

    fixture = TestBed.createComponent(EventImageUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventImageFormService = TestBed.inject(EventImageFormService);
    eventImageService = TestBed.inject(EventImageService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Event query and add missing value', () => {
      const eventImage: IEventImage = { id: 14540 };
      const event: IEvent = { id: 22576 };
      eventImage.event = event;

      const eventCollection: IEvent[] = [{ id: 22576 }];
      vitest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      vitest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ eventImage });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.eventsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const eventImage: IEventImage = { id: 14540 };
      const event: IEvent = { id: 22576 };
      eventImage.event = event;

      activatedRoute.data = of({ eventImage });
      comp.ngOnInit();

      expect(comp.eventsSharedCollection()).toContainEqual(event);
      expect(comp.eventImage).toEqual(eventImage);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEventImage>();
      const eventImage = { id: 19308 };
      vitest.spyOn(eventImageFormService, 'getEventImage').mockReturnValue(eventImage);
      vitest.spyOn(eventImageService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(eventImage);
      saveSubject.complete();

      // THEN
      expect(eventImageFormService.getEventImage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventImageService.update).toHaveBeenCalledWith(expect.objectContaining(eventImage));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEventImage>();
      const eventImage = { id: 19308 };
      vitest.spyOn(eventImageFormService, 'getEventImage').mockReturnValue({ id: null });
      vitest.spyOn(eventImageService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventImage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(eventImage);
      saveSubject.complete();

      // THEN
      expect(eventImageFormService.getEventImage).toHaveBeenCalled();
      expect(eventImageService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEventImage>();
      const eventImage = { id: 19308 };
      vitest.spyOn(eventImageService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventImageService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEvent', () => {
      it('should forward to eventService', () => {
        const entity = { id: 22576 };
        const entity2 = { id: 3268 };
        vitest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
