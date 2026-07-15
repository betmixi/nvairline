import { IVenue, NewVenue } from './venue.model';

export const sampleWithRequiredData: IVenue = {
  id: 26103,
  name: 'past like corner',
  address: 'wherever old-fashioned pave',
  city: 'Everettton',
  country: 'Norfolk Island',
  capacity: 25524,
};

export const sampleWithPartialData: IVenue = {
  id: 1664,
  name: 'gosh',
  address: 'ah',
  city: 'North Cooper',
  country: 'New Caledonia',
  capacity: 2168,
};

export const sampleWithFullData: IVenue = {
  id: 26635,
  name: 'whoa creator chap',
  address: 'while',
  city: 'Mistyside',
  country: 'Czechia',
  capacity: 21644,
};

export const sampleWithNewData: NewVenue = {
  name: 'now',
  address: 'brightly mispronounce sonnet',
  city: 'Lake Vinnie',
  country: 'Greece',
  capacity: 5223,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
