/**
 * Hinh dang thuc te tra ve tu GET /api/tickets/qr/{code} (join fetch day du booking.user,
 * showtimeSeat.seat, showtimeSeat.showtime.event) - rieng cho man hinh check-in cua nhan vien,
 * khong dung lai model ITicket sinh tu JHipster vi model do chi Pick 'id' nong can.
 */
export interface ICheckInTicketInfo {
  id: number;
  qrCode?: string | null;
  status?: string | null;
  checkedIn?: boolean | null;
  bookingDetail?: {
    id: number;
    booking?: {
      id: number;
      status?: string | null;
      user?: {
        login?: string | null;
        firstName?: string | null;
        lastName?: string | null;
      } | null;
    } | null;
    showtimeSeat?: {
      id: number;
      seat?: { id: number; rowLabel?: string | null; seatNumber?: number | null; seatType?: string | null } | null;
      showtime?: {
        id: number;
        startTime?: string | null;
        event?: { id: number; title?: string | null } | null;
      } | null;
    } | null;
  } | null;
}
