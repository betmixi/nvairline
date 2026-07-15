import { IEvent } from 'app/entities/event/event.model';
import { IUser } from 'app/entities/user/user.model';

export interface IFavorite {
  id: number;
  user?: Pick<IUser, 'id' | 'login'> | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewFavorite = Omit<IFavorite, 'id'> & { id: null };
