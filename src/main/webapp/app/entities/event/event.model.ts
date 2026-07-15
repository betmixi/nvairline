import dayjs from 'dayjs/esm';

import { IAddress } from 'app/entities/address/address.model';
import { ICategory } from 'app/entities/category/category.model';
import { IOrganizer } from 'app/entities/organizer/organizer.model';

export interface IEvent {
  id: number;
  title?: string | null;
  description?: string | null;
  banner?: string | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  status?: boolean | null;
  createdDate?: dayjs.Dayjs | null;
  category?: Pick<ICategory, 'id' | 'name'> | null;
  address?: Pick<IAddress, 'id' | 'location'> | null;
  organizer?: Pick<IOrganizer, 'id' | 'companyName'> | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
