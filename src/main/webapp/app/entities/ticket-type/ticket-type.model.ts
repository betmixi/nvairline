import dayjs from 'dayjs/esm';

import { IEvent } from 'app/entities/event/event.model';

export interface ITicketType {
  id: number;
  name?: string | null;
  price?: number | null;
  quantity?: number | null;
  remaining?: number | null;
  saleStart?: dayjs.Dayjs | null;
  saleEnd?: dayjs.Dayjs | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewTicketType = Omit<ITicketType, 'id'> & { id: null };
