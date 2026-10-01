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
