import dayjs from 'dayjs/esm';

import { IEvent } from 'app/entities/event/event.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReview {
  id: number;
  rating?: number | null;
  comment?: string | null;
  createdDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
