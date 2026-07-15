import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { TicketService } from 'app/entities/ticket/service/ticket.service';
import { ITicket } from 'app/entities/ticket/ticket.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { ICheckIn } from '../check-in.model';
import { CheckInService } from '../service/check-in.service';

import { CheckInFormService } from './check-in-form.service';
import { CheckInUpdate } from './check-in-update';

describe('CheckIn Management Update Component', () => {
  let comp: CheckInUpdate;
  let fixture: ComponentFixture<CheckInUpdate>;
  let activatedRoute: ActivatedRoute;
  let checkInFormService: CheckInFormService;
  let checkInService: CheckInService;
  let ticketService: TicketService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(CheckInUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    checkInFormService = TestBed.inject(CheckInFormService);
    checkInService = TestBed.inject(CheckInService);
    ticketService = TestBed.inject(TicketService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Ticket query and add missing value', () => {
      const checkIn: ICheckIn = { id: 20452 };
      const ticket: ITicket = { id: 29380 };
      checkIn.ticket = ticket;

      const ticketCollection: ITicket[] = [{ id: 29380 }];
      vitest.spyOn(ticketService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketCollection })));
      const additionalTickets = [ticket];
      const expectedCollection: ITicket[] = [...additionalTickets, ...ticketCollection];
      vitest.spyOn(ticketService, 'addTicketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      expect(ticketService.query).toHaveBeenCalled();
      expect(ticketService.addTicketToCollectionIfMissing).toHaveBeenCalledWith(
        ticketCollection,
        ...additionalTickets.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ticketsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const checkIn: ICheckIn = { id: 20452 };
      const checkedBy: IUser = { id: 3944 };
      checkIn.checkedBy = checkedBy;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [checkedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const checkIn: ICheckIn = { id: 20452 };
      const ticket: ITicket = { id: 29380 };
      checkIn.ticket = ticket;
      const checkedBy: IUser = { id: 3944 };
      checkIn.checkedBy = checkedBy;

      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      expect(comp.ticketsSharedCollection()).toContainEqual(ticket);
      expect(comp.usersSharedCollection()).toContainEqual(checkedBy);
      expect(comp.checkIn).toEqual(checkIn);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICheckIn>();
      const checkIn = { id: 14585 };
      vitest.spyOn(checkInFormService, 'getCheckIn').mockReturnValue(checkIn);
      vitest.spyOn(checkInService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(checkIn);
      saveSubject.complete();

      // THEN
      expect(checkInFormService.getCheckIn).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(checkInService.update).toHaveBeenCalledWith(expect.objectContaining(checkIn));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICheckIn>();
      const checkIn = { id: 14585 };
      vitest.spyOn(checkInFormService, 'getCheckIn').mockReturnValue({ id: null });
      vitest.spyOn(checkInService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(checkIn);
      saveSubject.complete();

      // THEN
      expect(checkInFormService.getCheckIn).toHaveBeenCalled();
      expect(checkInService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICheckIn>();
      const checkIn = { id: 14585 };
      vitest.spyOn(checkInService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(checkInService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTicket', () => {
      it('should forward to ticketService', () => {
        const entity = { id: 29380 };
        const entity2 = { id: 23717 };
        vitest.spyOn(ticketService, 'compareTicket');
        comp.compareTicket(entity, entity2);
        expect(ticketService.compareTicket).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
