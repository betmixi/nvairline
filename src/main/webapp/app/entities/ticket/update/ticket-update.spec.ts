import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBookingDetail } from 'app/entities/booking-detail/booking-detail.model';
import { BookingDetailService } from 'app/entities/booking-detail/service/booking-detail.service';
import { TicketService } from '../service/ticket.service';
import { ITicket } from '../ticket.model';

import { TicketFormService } from './ticket-form.service';
import { TicketUpdate } from './ticket-update';

describe('Ticket Management Update Component', () => {
  let comp: TicketUpdate;
  let fixture: ComponentFixture<TicketUpdate>;
  let activatedRoute: ActivatedRoute;
  let ticketFormService: TicketFormService;
  let ticketService: TicketService;
  let bookingDetailService: BookingDetailService;

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

    fixture = TestBed.createComponent(TicketUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketFormService = TestBed.inject(TicketFormService);
    ticketService = TestBed.inject(TicketService);
    bookingDetailService = TestBed.inject(BookingDetailService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call BookingDetail query and add missing value', () => {
      const ticket: ITicket = { id: 23717 };
      const bookingDetail: IBookingDetail = { id: 17036 };
      ticket.bookingDetail = bookingDetail;

      const bookingDetailCollection: IBookingDetail[] = [{ id: 17036 }];
      vitest.spyOn(bookingDetailService, 'query').mockReturnValue(of(new HttpResponse({ body: bookingDetailCollection })));
      const additionalBookingDetails = [bookingDetail];
      const expectedCollection: IBookingDetail[] = [...additionalBookingDetails, ...bookingDetailCollection];
      vitest.spyOn(bookingDetailService, 'addBookingDetailToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      expect(bookingDetailService.query).toHaveBeenCalled();
      expect(bookingDetailService.addBookingDetailToCollectionIfMissing).toHaveBeenCalledWith(
        bookingDetailCollection,
        ...additionalBookingDetails.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.bookingDetailsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ticket: ITicket = { id: 23717 };
      const bookingDetail: IBookingDetail = { id: 17036 };
      ticket.bookingDetail = bookingDetail;

      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      expect(comp.bookingDetailsSharedCollection()).toContainEqual(bookingDetail);
      expect(comp.ticket).toEqual(ticket);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicket>();
      const ticket = { id: 29380 };
      vitest.spyOn(ticketFormService, 'getTicket').mockReturnValue(ticket);
      vitest.spyOn(ticketService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticket);
      saveSubject.complete();

      // THEN
      expect(ticketFormService.getTicket).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketService.update).toHaveBeenCalledWith(expect.objectContaining(ticket));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicket>();
      const ticket = { id: 29380 };
      vitest.spyOn(ticketFormService, 'getTicket').mockReturnValue({ id: null });
      vitest.spyOn(ticketService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticket);
      saveSubject.complete();

      // THEN
      expect(ticketFormService.getTicket).toHaveBeenCalled();
      expect(ticketService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITicket>();
      const ticket = { id: 29380 };
      vitest.spyOn(ticketService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBookingDetail', () => {
      it('should forward to bookingDetailService', () => {
        const entity = { id: 17036 };
        const entity2 = { id: 6599 };
        vitest.spyOn(bookingDetailService, 'compareBookingDetail');
        comp.compareBookingDetail(entity, entity2);
        expect(bookingDetailService.compareBookingDetail).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
