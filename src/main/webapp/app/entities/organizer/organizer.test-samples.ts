import { IOrganizer, NewOrganizer } from './organizer.model';

export const sampleWithRequiredData: IOrganizer = {
  id: 21412,
  companyName: 'violently',
  taxCode: 'eulogise winding scoff',
};

export const sampleWithPartialData: IOrganizer = {
  id: 19490,
  companyName: 'waterspout behold',
  taxCode: 'curry',
  verified: false,
};

export const sampleWithFullData: IOrganizer = {
  id: 25340,
  companyName: 'ceramic',
  taxCode: 'bell about',
  description: '../fake-data/blob/hipster.txt',
  verified: true,
};

export const sampleWithNewData: NewOrganizer = {
  companyName: 'kielbasa entwine',
  taxCode: 'language pasta ugh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
