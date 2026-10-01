import { IAircraft } from 'app/entities/aircraft/aircraft.model';

export type SeatType = 'STANDARD' | 'VIP' | 'COUPLE';

export interface ISeat {
  id: number;
  rowLabel?: string | null;
  seatNumber?: number | null;
  seatType?: SeatType | null;
  aircraft?: Pick<IAircraft, 'id' | 'name'> | null;
}

export type NewSeat = Omit<ISeat, 'id'> & { id: null };
