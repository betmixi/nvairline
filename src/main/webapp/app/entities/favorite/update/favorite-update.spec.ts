import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IFavorite } from '../favorite.model';
import { FavoriteService } from '../service/favorite.service';

import { FavoriteFormService } from './favorite-form.service';
import { FavoriteUpdate } from './favorite-update';

describe('Favorite Management Update Component', () => {
  let comp: FavoriteUpdate;
  let fixture: ComponentFixture<FavoriteUpdate>;
  let activatedRoute: ActivatedRoute;
  let favoriteFormService: FavoriteFormService;
  let favoriteService: FavoriteService;
  let userService: UserService;
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

    fixture = TestBed.createComponent(FavoriteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    favoriteFormService = TestBed.inject(FavoriteFormService);
    favoriteService = TestBed.inject(FavoriteService);
    userService = TestBed.inject(UserService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const favorite: IFavorite = { id: 14498 };
      const user: IUser = { id: 3944 };
      favorite.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Event query and add missing value', () => {
      const favorite: IFavorite = { id: 14498 };
      const event: IEvent = { id: 22576 };
      favorite.event = event;

      const eventCollection: IEvent[] = [{ id: 22576 }];
      vitest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      vitest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.eventsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const favorite: IFavorite = { id: 14498 };
      const user: IUser = { id: 3944 };
      favorite.user = user;
      const event: IEvent = { id: 22576 };
      favorite.event = event;

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.eventsSharedCollection()).toContainEqual(event);
      expect(comp.favorite).toEqual(favorite);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFavorite>();
      const favorite = { id: 4951 };
      vitest.spyOn(favoriteFormService, 'getFavorite').mockReturnValue(favorite);
      vitest.spyOn(favoriteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(favorite);
      saveSubject.complete();

      // THEN
      expect(favoriteFormService.getFavorite).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(favoriteService.update).toHaveBeenCalledWith(expect.objectContaining(favorite));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFavorite>();
      const favorite = { id: 4951 };
      vitest.spyOn(favoriteFormService, 'getFavorite').mockReturnValue({ id: null });
      vitest.spyOn(favoriteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(favorite);
      saveSubject.complete();

      // THEN
      expect(favoriteFormService.getFavorite).toHaveBeenCalled();
      expect(favoriteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFavorite>();
      const favorite = { id: 4951 };
      vitest.spyOn(favoriteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(favoriteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
