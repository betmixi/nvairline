import dayjs from 'dayjs/esm';

import { IReport, NewReport } from './report.model';

export const sampleWithRequiredData: IReport = {
  id: 9015,
};

export const sampleWithPartialData: IReport = {
  id: 11333,
  reason: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IReport = {
  id: 31098,
  reason: '../fake-data/blob/hipster.txt',
  status: 'yet',
  createdDate: dayjs('2026-07-15T06:18'),
};

export const sampleWithNewData: NewReport = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
