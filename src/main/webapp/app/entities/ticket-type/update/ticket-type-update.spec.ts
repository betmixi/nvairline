import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { TicketTypeService } from '../service/ticket-type.service';
import { ITicketType } from '../ticket-type.model';

import { TicketTypeFormService } from './ticket-type-form.service';
import { TicketTypeUpdate } from './ticket-type-update';

describe('TicketType Management Update Component', () => {
  let comp: TicketTypeUpdate;
  let fixture: ComponentFixture<TicketTypeUpdate>;
  let activatedRoute: ActivatedRoute;
  let ticketTypeFormService: TicketTypeFormService;
  let ticketTypeService: TicketTypeService;
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

    fixture = TestBed.createComponent(TicketTypeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketTypeFormService = TestBed.inject(TicketTypeFormService);
    ticketTypeService = TestBed.inject(TicketTypeService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Event query and add missing value', () => {
      const ticketType: ITicketType = { id: 26385 };
      const event: IEvent = { id: 22576 };
      ticketType.event = event;

      const eventCollection: IEvent[] = [{ id: 22576 }];
      vitest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      vitest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ticketType });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.eventsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ticketType: ITicketType = { id: 26385 };
      const event: IEvent = { id: 22576 };
      ticketType.event = event;

      activatedRoute.data = of({ ticketType });
      comp.ngOnInit();

      expect(comp.eventsSharedCollection()).toContainEqual(event);
      expect(comp.ticketType).toEqual(ticketType);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketType>();
      const ticketType = { id: 23701 };
      vitest.spyOn(ticketTypeFormService, 'getTicketType').mockReturnValue(ticketType);
      vitest.spyOn(ticketTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticketType);
      saveSubject.complete();

      // THEN
      expect(ticketTypeFormService.getTicketType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketTypeService.update).toHaveBeenCalledWith(expect.objectContaining(ticketType));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketType>();
      const ticketType = { id: 23701 };
      vitest.spyOn(ticketTypeFormService, 'getTicketType').mockReturnValue({ id: null });
      vitest.spyOn(ticketTypeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticketType);
      saveSubject.complete();

      // THEN
      expect(ticketTypeFormService.getTicketType).toHaveBeenCalled();
      expect(ticketTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketType>();
      const ticketType = { id: 23701 };
      vitest.spyOn(ticketTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketTypeService.update).toHaveBeenCalled();
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
