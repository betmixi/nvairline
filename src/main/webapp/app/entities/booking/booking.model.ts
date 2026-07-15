import dayjs from 'dayjs/esm';

import { IUser } from 'app/entities/user/user.model';

export interface IBooking {
  id: number;
  bookingDate?: dayjs.Dayjs | null;
  totalAmount?: number | null;
  status?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
