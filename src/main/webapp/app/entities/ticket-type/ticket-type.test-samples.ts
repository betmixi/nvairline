import dayjs from 'dayjs/esm';

import { ITicketType, NewTicketType } from './ticket-type.model';

export const sampleWithRequiredData: ITicketType = {
  id: 19567,
  name: 'gadzooks',
  price: 2176.44,
  quantity: 26588,
  remaining: 7887,
};

export const sampleWithPartialData: ITicketType = {
  id: 4154,
  name: 'for',
  price: 18298.76,
  quantity: 1410,
  remaining: 17691,
  saleEnd: dayjs('2026-07-15T02:58'),
};

export const sampleWithFullData: ITicketType = {
  id: 32693,
  name: 'depart',
  price: 6809.49,
  quantity: 32478,
  remaining: 4595,
  saleStart: dayjs('2026-07-14T10:57'),
  saleEnd: dayjs('2026-07-14T17:50'),
};

export const sampleWithNewData: NewTicketType = {
  name: 'woot flickering fleck',
  price: 6099.68,
  quantity: 27218,
  remaining: 22330,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
