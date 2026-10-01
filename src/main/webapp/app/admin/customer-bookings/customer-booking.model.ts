export type CustomerBookingType = 'TICKET' | 'BAGGAGE' | 'SEAT_UPGRADE' | 'ADDON';

export interface ICustomerBooking {
  bookingId: number;
  bookingDate: string;
  /** Thời điểm thanh toán thành công (chính xác hơn bookingDate khi xét "thời gian mua"). */
  paymentDate?: string | null;
  totalAmount: number;
  status: string;

  customerId: number;
  customerLogin?: string | null;
  customerFirstName?: string | null;
  customerLastName?: string | null;
  customerEmail?: string | null;

  flightTitle?: string | null;
  departureAirportCode?: string | null;
  arrivalAirportCode?: string | null;
  showtimeStart?: string | null;

  ticketCount: number;

  /** Mô tả từng chặng bay (khứ hồi/nhiều chặng) hoặc mô tả dịch vụ mua thêm, dạng chuỗi đã ghép sẵn từ backend. */
  legSummary?: string | null;
  legCount: number;

  /** Loại đơn: TICKET (đặt vé mới), BAGGAGE, SEAT_UPGRADE, ADDON (mua thêm cho vé đã có). */
  bookingType: CustomerBookingType;
}
