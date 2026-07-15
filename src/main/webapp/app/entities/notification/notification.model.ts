import dayjs from 'dayjs/esm';

import { IUser } from 'app/entities/user/user.model';

export interface INotification {
  id: number;
  title?: string | null;
  content?: string | null;
  read?: boolean | null;
  sentDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
