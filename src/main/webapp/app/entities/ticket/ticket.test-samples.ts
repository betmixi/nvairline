import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 12568,
};

export const sampleWithPartialData: ITicket = {
  id: 9476,
  qrCode: '../fake-data/blob/hipster.txt',
  checkedIn: true,
};

export const sampleWithFullData: ITicket = {
  id: 14664,
  qrCode: '../fake-data/blob/hipster.txt',
  status: 'although aboard',
  checkedIn: true,
};

export const sampleWithNewData: NewTicket = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
