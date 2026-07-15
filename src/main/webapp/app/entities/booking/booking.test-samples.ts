import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 4955,
};

export const sampleWithPartialData: IBooking = {
  id: 15511,
  bookingDate: dayjs('2026-07-14T15:53'),
  status: 'instead why godfather',
};

export const sampleWithFullData: IBooking = {
  id: 28482,
  bookingDate: dayjs('2026-07-14T20:34'),
  totalAmount: 23079.34,
  status: 'aw past',
};

export const sampleWithNewData: NewBooking = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
