import { IBookingDetail, NewBookingDetail } from './booking-detail.model';

export const sampleWithRequiredData: IBookingDetail = {
  id: 21814,
};

export const sampleWithPartialData: IBookingDetail = {
  id: 18921,
  quantity: 23216,
};

export const sampleWithFullData: IBookingDetail = {
  id: 14278,
  quantity: 3426,
  price: 28998.43,
};

export const sampleWithNewData: NewBookingDetail = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
