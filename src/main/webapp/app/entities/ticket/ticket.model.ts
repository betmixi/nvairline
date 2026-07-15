import { IBookingDetail } from 'app/entities/booking-detail/booking-detail.model';

export interface ITicket {
  id: number;
  qrCode?: string | null;
  status?: string | null;
  checkedIn?: boolean | null;
  bookingDetail?: Pick<IBookingDetail, 'id'> | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
