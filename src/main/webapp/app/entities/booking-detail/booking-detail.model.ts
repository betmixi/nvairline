import { IBooking } from 'app/entities/booking/booking.model';
import { IShowtimeSeat } from 'app/entities/showtime/showtime.model';

export interface IBookingDetail {
  id: number;
  price?: number | null;
  booking?: Pick<IBooking, 'id'> | null;
  showtimeSeat?: Pick<IShowtimeSeat, 'id' | 'status' | 'price' | 'seat'> | null;
}

export type NewBookingDetail = Omit<IBookingDetail, 'id'> & { id: null };
