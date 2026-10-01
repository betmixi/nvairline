import dayjs from 'dayjs/esm';

import { IEventReview } from 'app/core/util/engagement.service';

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
  /** Thu tu chang bay trong booking (0 = chang dau...), null neu booking chi co 1 chang. */
  legIndex?: number | null;
  bookingStatus?: string | null;
  bookingDate?: dayjs.Dayjs | null;
  showtimeId?: number | null;
  showtimeStartTime?: dayjs.Dayjs | null;
  aircraftName?: string | null;
  passengerName?: string | null;
  seatLabel?: string | null;
  seatType?: string | null;
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
export type RestMyTicket = Omit<IMyTicket, 'bookingDate' | 'eventStartTime' | 'eventEndTime' | 'showtimeStartTime'> & {
  bookingDate?: string | null;
  eventStartTime?: string | null;
  eventEndTime?: string | null;
  showtimeStartTime?: string | null;
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

  // ----- Trạng thái UI cho khối "Đánh giá chuyến bay", chỉ dùng phía client -----
  /** Đánh giá của chính người dùng hiện tại cho sự kiện này, nếu đã có. */
  myReview?: IEventReview | null;
  reviewFormOpen?: boolean;
  reviewRating?: number;
  reviewComment?: string;
  isSubmittingReview?: boolean;
  isDeletingReview?: boolean;
  reviewError?: string | null;
}
