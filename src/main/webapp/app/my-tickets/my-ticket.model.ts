import dayjs from 'dayjs/esm';

/**
 * Một vé trong ví vé của người dùng.
 *
 * Tương ứng với `MyTicketDTO` ở backend, nhưng các trường thời gian đã được
 * chuyển từ chuỗi ISO sang `dayjs`.
 */
export interface IMyTicket {
  id: number;
  qrCode?: string | null;
  status?: string | null;
  checkedIn?: boolean | null;
  bookingId?: number | null;
  bookingStatus?: string | null;
  bookingDate?: dayjs.Dayjs | null;
  ticketTypeId?: number | null;
  ticketTypeName?: string | null;
  price?: number | null;
  eventId?: number | null;
  eventTitle?: string | null;
  eventBanner?: string | null;
  eventStartTime?: dayjs.Dayjs | null;
  eventEndTime?: dayjs.Dayjs | null;
  location?: string | null;
  address?: string | null;
  city?: string | null;
}

/** Dạng thô nhận từ API, thời gian còn là chuỗi ISO. */
export type RestMyTicket = Omit<IMyTicket, 'bookingDate' | 'eventStartTime' | 'eventEndTime'> & {
  bookingDate?: string | null;
  eventStartTime?: string | null;
  eventEndTime?: string | null;
};

/** Nhóm vé theo sự kiện để hiển thị. */
export interface IEventTicketGroup {
  eventId: number | null;
  eventTitle: string;
  eventBanner?: string | null;
  eventStartTime?: dayjs.Dayjs | null;
  location?: string | null;
  city?: string | null;
  tickets: IMyTicket[];
}
