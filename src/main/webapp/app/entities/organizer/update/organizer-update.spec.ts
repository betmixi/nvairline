import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IOrganizer } from '../organizer.model';
import { OrganizerService } from '../service/organizer.service';

import { OrganizerFormService } from './organizer-form.service';
import { OrganizerUpdate } from './organizer-update';

describe('Organizer Management Update Component', () => {
  let comp: OrganizerUpdate;
  let fixture: ComponentFixture<OrganizerUpdate>;
  let activatedRoute: ActivatedRoute;
  let organizerFormService: OrganizerFormService;
  let organizerService: OrganizerService;
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

    fixture = TestBed.createComponent(OrganizerUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organizerFormService = TestBed.inject(OrganizerFormService);
    organizerService = TestBed.inject(OrganizerService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const organizer: IOrganizer = { id: 29151 };
      const user: IUser = { id: 3944 };
      organizer.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organizer });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const organizer: IOrganizer = { id: 29151 };
      const user: IUser = { id: 3944 };
      organizer.user = user;

      activatedRoute.data = of({ organizer });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.organizer).toEqual(organizer);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOrganizer>();
      const organizer = { id: 278 };
      vitest.spyOn(organizerFormService, 'getOrganizer').mockReturnValue(organizer);
      vitest.spyOn(organizerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organizer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(organizer);
      saveSubject.complete();

      // THEN
      expect(organizerFormService.getOrganizer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organizerService.update).toHaveBeenCalledWith(expect.objectContaining(organizer));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOrganizer>();
      const organizer = { id: 278 };
      vitest.spyOn(organizerFormService, 'getOrganizer').mockReturnValue({ id: null });
      vitest.spyOn(organizerService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organizer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(organizer);
      saveSubject.complete();

      // THEN
      expect(organizerFormService.getOrganizer).toHaveBeenCalled();
      expect(organizerService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IOrganizer>();
      const organizer = { id: 278 };
      vitest.spyOn(organizerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organizer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organizerService.update).toHaveBeenCalled();
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
  });
});
