import dayjs from 'dayjs/esm';

import { IEvent } from 'app/entities/event/event.model';

export interface ICoupon {
  id: number;
  code?: string | null;
  discount?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  quantity?: number | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewCoupon = Omit<ICoupon, 'id'> & { id: null };
