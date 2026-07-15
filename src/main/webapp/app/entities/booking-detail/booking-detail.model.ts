import { IBooking } from 'app/entities/booking/booking.model';
import { ITicketType } from 'app/entities/ticket-type/ticket-type.model';

export interface IBookingDetail {
  id: number;
  quantity?: number | null;
  price?: number | null;
  booking?: Pick<IBooking, 'id'> | null;
  ticketType?: Pick<ITicketType, 'id' | 'name'> | null;
}

export type NewBookingDetail = Omit<IBookingDetail, 'id'> & { id: null };
