import dayjs from 'dayjs/esm';

import { IAuditLog, NewAuditLog } from './audit-log.model';

export const sampleWithRequiredData: IAuditLog = {
  id: 24557,
};

export const sampleWithPartialData: IAuditLog = {
  id: 19682,
  action: 'yum huff',
  tableName: 'insidious',
  recordId: 7974,
  createdDate: dayjs('2026-07-14T18:33'),
};

export const sampleWithFullData: IAuditLog = {
  id: 16792,
  action: 'hasty carelessly neighboring',
  tableName: 'worldly',
  recordId: 2865,
  createdDate: dayjs('2026-07-15T01:18'),
};

export const sampleWithNewData: NewAuditLog = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
