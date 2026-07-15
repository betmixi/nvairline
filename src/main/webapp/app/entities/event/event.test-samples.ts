import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 18174,
  title: 'scent yum',
};

export const sampleWithPartialData: IEvent = {
  id: 31982,
  title: 'near past',
  banner: 'around',
  createdDate: dayjs('2026-07-14T13:06'),
};

export const sampleWithFullData: IEvent = {
  id: 14744,
  title: 'know yahoo',
  description: '../fake-data/blob/hipster.txt',
  banner: 'opera overstay',
  startTime: dayjs('2026-07-14T17:29'),
  endTime: dayjs('2026-07-15T02:43'),
  status: true,
  createdDate: dayjs('2026-07-14T05:59'),
};

export const sampleWithNewData: NewEvent = {
  title: 'throughout solder aw',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
