import dayjs from 'dayjs/esm';

import { ITicket } from 'app/entities/ticket/ticket.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICheckIn {
  id: number;
  checkInTime?: dayjs.Dayjs | null;
  ticket?: Pick<ITicket, 'id'> | null;
  checkedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCheckIn = Omit<ICheckIn, 'id'> & { id: null };
