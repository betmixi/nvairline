import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAuditLog, NewAuditLog } from '../audit-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAuditLog for edit and NewAuditLogFormGroupInput for create.
 */
type AuditLogFormGroupInput = IAuditLog | PartialWithRequiredKeyOf<NewAuditLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAuditLog | NewAuditLog> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type AuditLogFormRawValue = FormValueOf<IAuditLog>;

type NewAuditLogFormRawValue = FormValueOf<NewAuditLog>;

type AuditLogFormDefaults = Pick<NewAuditLog, 'id' | 'createdDate'>;

type AuditLogFormGroupContent = {
  id: FormControl<AuditLogFormRawValue['id'] | NewAuditLog['id']>;
  action: FormControl<AuditLogFormRawValue['action']>;
  tableName: FormControl<AuditLogFormRawValue['tableName']>;
  recordId: FormControl<AuditLogFormRawValue['recordId']>;
  createdDate: FormControl<AuditLogFormRawValue['createdDate']>;
  user: FormControl<AuditLogFormRawValue['user']>;
};

export type AuditLogFormGroup = FormGroup<AuditLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AuditLogFormService {
  createAuditLogFormGroup(auditLog?: AuditLogFormGroupInput): AuditLogFormGroup {
    const auditLogRawValue = this.convertAuditLogToAuditLogRawValue({
      ...this.getFormDefaults(),
      ...(auditLog ?? { id: null }),
    });

    return new FormGroup<AuditLogFormGroupContent>({
      id: new FormControl(
        { value: auditLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      action: new FormControl(auditLogRawValue.action),
      tableName: new FormControl(auditLogRawValue.tableName),
      recordId: new FormControl(auditLogRawValue.recordId),
      createdDate: new FormControl(auditLogRawValue.createdDate),
      user: new FormControl(auditLogRawValue.user),
    });
  }

  getAuditLog(form: AuditLogFormGroup): IAuditLog | NewAuditLog {
    return this.convertAuditLogRawValueToAuditLog(form.getRawValue());
  }

  resetForm(form: AuditLogFormGroup, auditLog: AuditLogFormGroupInput): void {
    const auditLogRawValue = this.convertAuditLogToAuditLogRawValue({ ...this.getFormDefaults(), ...auditLog });
    form.reset({
      ...auditLogRawValue,
      id: { value: auditLogRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AuditLogFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
    };
  }

  private convertAuditLogRawValueToAuditLog(rawAuditLog: AuditLogFormRawValue | NewAuditLogFormRawValue): IAuditLog | NewAuditLog {
    return {
      ...rawAuditLog,
      createdDate: dayjs(rawAuditLog.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertAuditLogToAuditLogRawValue(
    auditLog: IAuditLog | (Partial<NewAuditLog> & AuditLogFormDefaults),
  ): AuditLogFormRawValue | PartialWithRequiredKeyOf<NewAuditLogFormRawValue> {
    return {
      ...auditLog,
      createdDate: auditLog.createdDate ? auditLog.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
