import { IAddress, NewAddress } from './address.model';

export const sampleWithRequiredData: IAddress = {
  id: 2568,
  location: 'admired yum',
  address: 'joyfully huzzah violently',
  city: 'Kiehnport',
  capacity: 2550,
};

export const sampleWithPartialData: IAddress = {
  id: 1335,
  location: 'seldom boo',
  address: 'psst frank down',
  city: 'Port Emilyport',
  capacity: 12801,
};

export const sampleWithFullData: IAddress = {
  id: 16440,
  location: 'ouch molasses',
  address: 'overdue obsess willfully',
  city: 'Borermouth',
  capacity: 1297,
};

export const sampleWithNewData: NewAddress = {
  location: 'zowie uh-huh pitiful',
  address: 'meanwhile polite kettledrum',
  city: 'Azusa',
  capacity: 11427,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
