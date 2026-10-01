import dayjs from 'dayjs/esm';

import { IAddress } from 'app/entities/address/address.model';
import { IAirport } from 'app/entities/airport/airport.model';
import { ICategory } from 'app/entities/category/category.model';
import { IShowtime } from 'app/entities/showtime/showtime.model';
export interface IEvent {
  id: number;
  title?: string | null;
  description?: string | null;
  banner?: string | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  status?: boolean | null;
  createdDate?: dayjs.Dayjs | null;
  supportsOneWay?: boolean | null;
  supportsRoundTrip?: boolean | null;
  supportsMultiCity?: boolean | null;
  category?: Pick<ICategory, 'id' | 'name'> | null;
  address?: Pick<IAddress, 'id' | 'location'> | null;
  price?: number | null;
  showtimes?: IShowtime[] | null;
  departureAirport?: Pick<IAirport, 'id' | 'code' | 'name' | 'city'> | null;
  arrivalAirport?: Pick<IAirport, 'id' | 'code' | 'name' | 'city'> | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
