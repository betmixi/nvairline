import dayjs from 'dayjs/esm';

import { ICoupon, NewCoupon } from './coupon.model';

export const sampleWithRequiredData: ICoupon = {
  id: 28157,
  code: 'alive fat',
};

export const sampleWithPartialData: ICoupon = {
  id: 23797,
  code: 'wetly',
  startDate: dayjs('2026-07-14T23:24'),
  endDate: dayjs('2026-07-14T10:48'),
  quantity: 23979,
};

export const sampleWithFullData: ICoupon = {
  id: 23446,
  code: 'inscribe',
  discount: 27169.94,
  startDate: dayjs('2026-07-14T11:55'),
  endDate: dayjs('2026-07-15T04:15'),
  quantity: 14151,
};

export const sampleWithNewData: NewCoupon = {
  code: 'per fax',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
