import dayjs from 'dayjs/esm';

import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { IEvent } from 'app/entities/event/event.model';
import { SeatType } from 'app/entities/seat/seat.model';

export interface IShowtime {
  id: number;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  basePrice?: number | null;
  vipPrice?: number | null;
  couplePrice?: number | null;
  event?: Pick<IEvent, 'id' | 'title' | 'banner'> | null;
  aircraft?: Pick<IAircraft, 'id' | 'name' | 'totalRows' | 'totalColumns' | 'roomType'> | null;
}

export type NewShowtime = Omit<IShowtime, 'id'> & { id: null };

export type ShowtimeSeatStatus = 'AVAILABLE' | 'HELD' | 'BOOKED';

export interface IShowtimeSeat {
  id: number;
  status?: ShowtimeSeatStatus | null;
  price?: number | null;
  showtime?: Pick<IShowtime, 'id'> | null;
  seat?: {
    id: number;
    rowLabel?: string | null;
    seatNumber?: number | null;
    seatType?: SeatType | null;
  } | null;
}
