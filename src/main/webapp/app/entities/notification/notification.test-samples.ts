import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 10110,
};

export const sampleWithPartialData: INotification = {
  id: 29,
  read: true,
};

export const sampleWithFullData: INotification = {
  id: 5787,
  title: 'some extra-large',
  content: '../fake-data/blob/hipster.txt',
  read: true,
  sentDate: dayjs('2026-07-14T15:37'),
};

export const sampleWithNewData: NewNotification = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
