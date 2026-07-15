import dayjs from 'dayjs/esm';

import { IBooking } from 'app/entities/booking/booking.model';

export interface IPayment {
  id: number;
  method?: string | null;
  transactionCode?: string | null;
  amount?: number | null;
  status?: string | null;
  paymentDate?: dayjs.Dayjs | null;
  booking?: Pick<IBooking, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
