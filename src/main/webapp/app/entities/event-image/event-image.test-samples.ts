import { IEventImage, NewEventImage } from './event-image.model';

export const sampleWithRequiredData: IEventImage = {
  id: 5477,
  imageUrl: 'uh-huh',
};

export const sampleWithPartialData: IEventImage = {
  id: 6895,
  imageUrl: 'overload',
};

export const sampleWithFullData: IEventImage = {
  id: 23096,
  imageUrl: 'etch since whether',
};

export const sampleWithNewData: NewEventImage = {
  imageUrl: 'victoriously',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
