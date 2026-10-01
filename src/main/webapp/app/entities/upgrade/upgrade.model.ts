export interface IUpgradeOption {
  seatType: string;
  seatTypeLabel: string;
  price: number;
  priceDifference: number;
  available: boolean;
}

export interface ITicketUpgradeInfo {
  ticketId: number;
  currentSeatType: string;
  currentSeatTypeLabel: string;
  currentPrice: number;
  maxTier: boolean;
  upgradable: boolean;
  reason?: string | null;
  options: IUpgradeOption[];
}

export interface IUpgradeResponse {
  upgradeId: number;
  bookingId: number;
  priceDifference: number;
}
