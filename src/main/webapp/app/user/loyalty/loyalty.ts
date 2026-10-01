import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { LoyaltyService } from './loyalty.service';
import { ILoyaltyOffer, IPointsHistory } from './loyalty.model';

/** Trang Tich diem Lotusmiles: xem so du, lich su va doi diem lay uu dai. */
@Component({
  standalone: true,
  selector: 'jhi-loyalty',
  imports: [CommonModule, RouterLink],
  templateUrl: './loyalty.html',
  styleUrl: './loyalty.scss',
})
export class LoyaltyComponent implements OnInit {
  points = 0;
  history: IPointsHistory[] = [];
  isLoading = true;

  offers: ILoyaltyOffer[] = [];
  /** Id uu dai dang duoc doi (khoa nut de tranh bam kep), null neu khong co. */
  redeemingId: string | null = null;
  redeemError: string | null = null;
  redeemSuccessMessage: string | null = null;

  private readonly loyaltyService = inject(LoyaltyService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadBalance();

    this.loyaltyService.getMyHistory().subscribe({
      next: res => {
        this.history = res.body ?? [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.loyaltyService.getOffers().subscribe({
      next: offers => {
        this.offers = offers;
        this.cdr.detectChanges();
      },
    });
  }

  private loadBalance(): void {
    this.loyaltyService.getMyBalance().subscribe({
      next: balance => {
        this.points = balance.points;
        this.cdr.detectChanges();
      },
    });
  }

  private reloadHistory(): void {
    this.loyaltyService.getMyHistory().subscribe({
      next: res => {
        this.history = res.body ?? [];
        this.cdr.detectChanges();
      },
    });
  }

  redeem(offer: ILoyaltyOffer): void {
    if (this.redeemingId || this.points < offer.pointCost) {
      return;
    }

    this.redeemingId = offer.id;
    this.redeemError = null;
    this.redeemSuccessMessage = null;

    this.loyaltyService.redeem(offer.id).subscribe({
      next: balance => {
        this.points = balance.points;
        this.redeemingId = null;
        this.redeemSuccessMessage = `Đã đổi "${offer.label}" thành công!`;
        this.reloadHistory();
        this.cdr.detectChanges();
      },
      error: (err: { error?: { message?: string } }) => {
        this.redeemError =
          err.error?.message === 'error.insufficientpoints'
            ? 'Bạn không đủ điểm để đổi ưu đãi này.'
            : 'Không đổi được ưu đãi này, vui lòng thử lại.';
        this.redeemingId = null;
        this.cdr.detectChanges();
      },
    });
  }
}
