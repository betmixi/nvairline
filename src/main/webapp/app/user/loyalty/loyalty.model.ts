export interface ILoyaltyBalance {
  points: number;
}

export interface IPointsHistory {
  id: number;
  points: number;
  reason?: string | null;
  createdDate?: string | null;
  bookingId?: number | null;
}

export interface ILoyaltyOffer {
  id: string;
  label: string;
  description?: string | null;
  pointCost: number;
}

/** 1 coupon ca nhan da doi bang diem, con dung duoc - chon de ap vao gia khi dat ve. */
export interface ILoyaltyCoupon {
  code: string;
  label: string;
  discount: number;
}
