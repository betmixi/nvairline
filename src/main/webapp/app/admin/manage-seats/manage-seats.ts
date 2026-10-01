import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AircraftService } from 'app/entities/aircraft/service/aircraft.service';
import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { SeatService } from 'app/entities/seat/service/seat.service';
import { ISeat, SeatType } from 'app/entities/seat/seat.model';

@Component({
  selector: 'app-manage-seats',
  standalone: true,
  templateUrl: './manage-seats.html',
  styleUrls: ['./manage-seats.scss'],
  imports: [CommonModule, FormsModule],
})
export class ManageSeatsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private seatService = inject(SeatService);
  private aircraftService = inject(AircraftService);

  aircraftId = 0;

  aircraft = signal<IAircraft | null>(null);

  seats = signal<ISeat[]>([]);

  readonly seatTypes: SeatType[] = ['STANDARD', 'VIP', 'COUPLE'];

  isSaving = false;

  errorMessage = '';

  /** Tim theo hang ghe / so ghe / ma ghe (VD: "A", "12", "A12"). */
  keyword = '';

  /** Loc theo loai ghe: 'ALL' hoac mot trong cac SeatType. */
  seatTypeFilter: SeatType | 'ALL' = 'ALL';

  newSeat = {
    rowLabel: '',
    seatNumber: 1,
    seatType: 'STANDARD' as SeatType,
  };

  get filteredSeats(): ISeat[] {
    const kw = this.keyword.trim().toLowerCase();

    return this.seats().filter(seat => {
      if (this.seatTypeFilter !== 'ALL' && seat.seatType !== this.seatTypeFilter) {
        return false;
      }

      if (!kw) {
        return true;
      }

      const code = `${seat.rowLabel ?? ''}${seat.seatNumber ?? ''}`.toLowerCase();
      return code.includes(kw) || (seat.rowLabel ?? '').toLowerCase().includes(kw) || String(seat.seatNumber ?? '').includes(kw);
    });
  }

  ngOnInit(): void {
    this.aircraftId = Number(this.route.snapshot.paramMap.get('aircraftId'));

    this.loadAircraft();
    this.loadSeats();
  }

  loadAircraft(): void {
    this.aircraftService.find(this.aircraftId).subscribe(res => {
      this.aircraft.set(res);
    });
  }

  loadSeats(): void {
    this.seatService.findByAircraft(this.aircraftId).subscribe(res => {
      const seats = (res.body ?? [])
        .slice()
        .sort((a, b) => (a.rowLabel ?? '').localeCompare(b.rowLabel ?? '') || (a.seatNumber ?? 0) - (b.seatNumber ?? 0));
      this.seats.set(seats);
    });
  }

  seatTypeLabel(seatType?: SeatType | null): string {
    switch (seatType) {
      case 'VIP':
        return 'Thương gia';
      case 'COUPLE':
        return 'Hạng nhất';
      default:
        return 'Phổ thông';
    }
  }

  saveSeat(): void {
    this.errorMessage = '';

    const rowLabel = this.newSeat.rowLabel.trim();
    if (!rowLabel) {
      this.errorMessage = 'Vui lòng nhập hàng ghế.';
      return;
    }

    if (!this.newSeat.seatNumber || this.newSeat.seatNumber < 1) {
      this.errorMessage = 'Vui lòng nhập số ghế hợp lệ.';
      return;
    }

    this.isSaving = true;

    this.seatService
      .create({
        id: null,
        rowLabel,
        seatNumber: Number(this.newSeat.seatNumber),
        seatType: this.newSeat.seatType,
        aircraft: { id: this.aircraftId },
      })
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.newSeat = { rowLabel: '', seatNumber: 1, seatType: 'STANDARD' };
          this.loadSeats();
        },
        error: err => {
          this.isSaving = false;
          this.errorMessage = err?.error?.title ?? 'Không tạo được ghế.';
        },
      });
  }

  updateSeatType(seat: ISeat, seatType: SeatType): void {
    this.seatService
      .update({
        ...seat,
        seatType,
      })
      .subscribe(() => {
        this.loadSeats();
      });
  }

  delete(seat: ISeat): void {
    if (!confirm('Xoá ghế này?')) {
      return;
    }

    this.seatService.delete(seat.id).subscribe({
      next: () => this.loadSeats(),
      error: () => {
        this.errorMessage = 'Không xoá được ghế này (có thể đã được dùng trong vé đặt chỗ).';
      },
    });
  }

  finish(): void {
    this.router.navigate(['/aircraft']);
  }
}
