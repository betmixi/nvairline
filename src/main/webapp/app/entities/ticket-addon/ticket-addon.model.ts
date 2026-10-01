export interface IAddonCatalogItem {
  itemCode: string;
  itemLabel: string;
  unitPrice: number;
  maxQuantity: number;
}

export interface IPurchasedAddon {
  id: number;
  itemCode: string;
  itemLabel: string;
  quantity: number;
  totalPrice: number;
  status: string;
}

export interface ITicketAddonInfo {
  ticketId: number;
  addonType: string;
  purchasable: boolean;
  reason?: string | null;
  purchased: IPurchasedAddon[];
  catalog: IAddonCatalogItem[];
}

export interface IAddonCartItem {
  itemCode: string;
  quantity: number;
}

export interface IAddonPurchaseResponse {
  bookingId: number;
  totalPrice: number;
}
