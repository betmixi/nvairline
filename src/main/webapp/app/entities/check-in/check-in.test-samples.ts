import dayjs from 'dayjs/esm';

import { ICheckIn, NewCheckIn } from './check-in.model';

export const sampleWithRequiredData: ICheckIn = {
  id: 24782,
};

export const sampleWithPartialData: ICheckIn = {
  id: 28953,
  checkInTime: dayjs('2026-07-15T00:49'),
};

export const sampleWithFullData: ICheckIn = {
  id: 22689,
  checkInTime: dayjs('2026-07-15T03:50'),
};

export const sampleWithNewData: NewCheckIn = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
