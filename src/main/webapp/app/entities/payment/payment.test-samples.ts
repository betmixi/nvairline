import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 4942,
};

export const sampleWithPartialData: IPayment = {
  id: 27804,
  transactionCode: 'underplay kiddingly metal',
  paymentDate: dayjs('2026-07-14T14:41'),
};

export const sampleWithFullData: IPayment = {
  id: 28239,
  method: 'hourly unwieldy',
  transactionCode: 'because',
  amount: 5500.46,
  status: 'baa plastic likewise',
  paymentDate: dayjs('2026-07-14T08:57'),
};

export const sampleWithNewData: NewPayment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
