import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IAuditLog } from '../audit-log.model';
import { AuditLogService } from '../service/audit-log.service';

import { AuditLogFormService } from './audit-log-form.service';
import { AuditLogUpdate } from './audit-log-update';

describe('AuditLog Management Update Component', () => {
  let comp: AuditLogUpdate;
  let fixture: ComponentFixture<AuditLogUpdate>;
  let activatedRoute: ActivatedRoute;
  let auditLogFormService: AuditLogFormService;
  let auditLogService: AuditLogService;
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

    fixture = TestBed.createComponent(AuditLogUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    auditLogFormService = TestBed.inject(AuditLogFormService);
    auditLogService = TestBed.inject(AuditLogService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const auditLog: IAuditLog = { id: 25691 };
      const user: IUser = { id: 3944 };
      auditLog.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const auditLog: IAuditLog = { id: 25691 };
      const user: IUser = { id: 3944 };
      auditLog.user = user;

      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.auditLog).toEqual(auditLog);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLog>();
      const auditLog = { id: 25090 };
      vitest.spyOn(auditLogFormService, 'getAuditLog').mockReturnValue(auditLog);
      vitest.spyOn(auditLogService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(auditLog);
      saveSubject.complete();

      // THEN
      expect(auditLogFormService.getAuditLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(auditLogService.update).toHaveBeenCalledWith(expect.objectContaining(auditLog));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLog>();
      const auditLog = { id: 25090 };
      vitest.spyOn(auditLogFormService, 'getAuditLog').mockReturnValue({ id: null });
      vitest.spyOn(auditLogService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(auditLog);
      saveSubject.complete();

      // THEN
      expect(auditLogFormService.getAuditLog).toHaveBeenCalled();
      expect(auditLogService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLog>();
      const auditLog = { id: 25090 };
      vitest.spyOn(auditLogService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(auditLogService.update).toHaveBeenCalled();
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
