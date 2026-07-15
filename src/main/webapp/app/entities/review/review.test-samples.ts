import dayjs from 'dayjs/esm';

import { IReview, NewReview } from './review.model';

export const sampleWithRequiredData: IReview = {
  id: 6640,
  rating: 2,
};

export const sampleWithPartialData: IReview = {
  id: 12934,
  rating: 2,
  createdDate: dayjs('2026-07-14T07:55'),
};

export const sampleWithFullData: IReview = {
  id: 30916,
  rating: 4,
  comment: '../fake-data/blob/hipster.txt',
  createdDate: dayjs('2026-07-14T13:48'),
};

export const sampleWithNewData: NewReview = {
  rating: 2,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
