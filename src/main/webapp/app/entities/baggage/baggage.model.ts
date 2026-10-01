export interface IBaggageOption {
  weightKg: number;
  price: number;
}

export interface IPurchasedBaggage {
  id: number;
  weightKg: number;
  price: number;
  status: string;
}

export interface ITicketBaggageInfo {
  ticketId: number;
  purchasable: boolean;
  reason?: string | null;
  totalPurchasedKg: number;
  purchased: IPurchasedBaggage[];
  options: IBaggageOption[];
}

export interface IBaggageResponse {
  purchaseId: number;
  bookingId: number;
  price: number;
}

/**
 * Bảng giá hành lý ký gửi trả trước (khớp với BaggageService.CATALOG ở
 * backend) — dùng để hiển thị lựa chọn ngay lúc đặt vé, trước khi có ticketId.
 */
export const BAGGAGE_CATALOG: IBaggageOption[] = [
  { weightKg: 5, price: 50000 },
  { weightKg: 10, price: 90000 },
  { weightKg: 20, price: 160000 },
  { weightKg: 30, price: 220000 },
];
