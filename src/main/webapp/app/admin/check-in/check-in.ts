import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AdminCheckInService } from './check-in.service';
import { ICheckInTicketInfo } from './check-in.model';

/** Man hinh lam thu tuc (check-in): tra ve theo so ve, xem thong tin, xac nhan. */
@Component({
  standalone: true,
  selector: 'jhi-admin-check-in',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './check-in.html',
  styleUrl: './check-in.scss',
})
export class AdminCheckInComponent {
  ticketNumber = '';
  isSearching = false;
  isConfirming = false;
  ticket: ICheckInTicketInfo | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  private readonly checkInService = inject(AdminCheckInService);
  private readonly cdr = inject(ChangeDetectorRef);

  passengerName(): string {
    const user = this.ticket?.bookingDetail?.booking?.user;
    const fullName = `${user?.firstName ?? ''} ${user?.lastName ?? ''}`.trim();
    return fullName || (user?.login ?? 'Khách hàng');
  }

  flightTitle(): string {
    return this.ticket?.bookingDetail?.showtimeSeat?.showtime?.event?.title ?? '—';
  }

  seatLabel(): string {
    const seat = this.ticket?.bookingDetail?.showtimeSeat?.seat;
    if (!seat) {
      return '—';
    }
    return `${seat.rowLabel ?? ''}${seat.seatNumber ?? ''}`.trim() || '—';
  }

  search(): void {
    const code = this.ticketNumber.trim();
    if (!code || this.isSearching) {
      return;
    }

    this.isSearching = true;
    this.errorMessage = null;
    this.successMessage = null;
    this.ticket = null;

    this.checkInService.lookupByTicketNumber(code).subscribe({
      next: ticket => {
        this.ticket = ticket;
        this.isSearching = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Không tìm thấy vé với số vé này.';
        this.isSearching = false;
        this.cdr.detectChanges();
      },
    });
  }

  confirmCheckIn(): void {
    if (!this.ticket || this.isConfirming) {
      return;
    }

    this.isConfirming = true;
    this.errorMessage = null;

    this.checkInService.confirmCheckIn(this.ticket.id).subscribe({
      next: () => {
        this.successMessage = `Đã làm thủ tục thành công cho ${this.passengerName()}.`;
        this.ticket = { ...this.ticket!, checkedIn: true, status: 'CHECKED_IN' };
        this.isConfirming = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.errorMessage = err?.error?.title ?? 'Không thể làm thủ tục cho vé này.';
        this.isConfirming = false;
        this.cdr.detectChanges();
      },
    });
  }

  reset(): void {
    this.ticketNumber = '';
    this.ticket = null;
    this.errorMessage = null;
    this.successMessage = null;
  }
}
