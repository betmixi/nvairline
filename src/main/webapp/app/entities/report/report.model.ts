import dayjs from 'dayjs/esm';

import { IEvent } from 'app/entities/event/event.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReport {
  id: number;
  reason?: string | null;
  status?: string | null;
  createdDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewReport = Omit<IReport, 'id'> & { id: null };
