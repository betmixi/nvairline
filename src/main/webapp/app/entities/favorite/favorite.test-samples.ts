import { IFavorite, NewFavorite } from './favorite.model';

export const sampleWithRequiredData: IFavorite = {
  id: 18480,
};

export const sampleWithPartialData: IFavorite = {
  id: 21728,
};

export const sampleWithFullData: IFavorite = {
  id: 16043,
};

export const sampleWithNewData: NewFavorite = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
