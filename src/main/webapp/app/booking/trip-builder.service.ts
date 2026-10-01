import { Injectable, signal } from '@angular/core';

export type TripType = 'one-way' | 'round-trip' | 'multi-city';

export interface ILegSelection {
  showtimeId: number;
  seatIds: number[];
}

export interface INextLegSearch {
  departureAirportId?: number | null;
  arrivalAirportId?: number | null;
  showtimeDate?: string | null;
}

/**
 * Gom cac chang bay da chon (mot chieu chi co 1 chang, khu hoi/nhieu chang co
 * nhieu chang) qua nhieu lan dieu huong search -> event-detail -> seat-picker,
 * truoc khi gui het len trang thanh toan /payment/pay trong mot booking duy nhat.
 */
@Injectable({ providedIn: 'root' })
export class TripBuilderService {
  readonly tripType = signal<TripType>('one-way');
  readonly legs = signal<ILegSelection[]>([]);

  private nextLegSearchValue: INextLegSearch | null = null;

  reset(): void {
    this.tripType.set('one-way');
    this.legs.set([]);
    this.nextLegSearchValue = null;
  }

  setTripType(type: TripType): void {
    this.tripType.set(type);
  }

  setNextLegSearch(search: INextLegSearch): void {
    this.nextLegSearchValue = search;
  }

  /** Lay va xoa thong tin tim kiem cho chang tiep theo (dung 1 lan cho khu hoi). */
  consumeNextLegSearch(): INextLegSearch | null {
    const value = this.nextLegSearchValue;
    this.nextLegSearchValue = null;
    return value;
  }

  addLeg(leg: ILegSelection): void {
    this.legs.update(list => [...list, leg]);
  }

  /** Sau khi vua chon xong 1 chang, co can tu dong chuyen sang tim chang tiep theo khong (chi ap dung cho khu hoi, luon dung 2 chang). */
  needsAutoNextLeg(): boolean {
    return this.tripType() === 'round-trip' && this.legs().length === 1;
  }

  /** Nhieu chang cho phep nguoi dung chu dong bam "them chang" tren trang thanh toan sau khi da co it nhat 1 chang. */
  get canAddMoreLegs(): boolean {
    return this.tripType() === 'multi-city';
  }
}
