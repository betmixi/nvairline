import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import dayjs from 'dayjs/esm';

import { IShowtime, IShowtimeSeat } from 'app/entities/showtime/showtime.model';
import { ShowtimeService } from 'app/entities/showtime/service/showtime.service';
import { TripBuilderService } from 'app/booking/trip-builder.service';

interface ISeatRow {
  rowLabel: string;
  seats: IShowtimeSeat[];
}

/** Mot hang ghe tren so do: 2 ghe ben trai loi di (khoang A) va 2 ghe ben phai loi di (khoang B). */
interface IVisualRow {
  left: IShowtimeSeat[];
  right: IShowtimeSeat[];
}

/** Mot khoang ghe theo hang (vd Khoang Thuong gia), xep tu dau may bay ve sau theo gia giam dan. */
interface ICabinSection {
  title: string;
  rows: IVisualRow[];
}

const MAX_SEATS = 8;

/**
 * So do ghe cua mot suat chieu: nguoi dung chon ghe roi bam tiep tuc thanh toan.
 */
@Component({
  standalone: true,
  selector: 'jhi-seat-picker',
  imports: [CommonModule, RouterLink],
  templateUrl: './seat-picker.html',
  styleUrl: './seat-picker.scss',
})
export default class SeatPickerComponent implements OnInit {
  showtime: IShowtime | null = null;

  /** So do thuc te chia theo khoang hang ghe (Thuong gia / Hang nhat / Pho thong), tinh 1 lan khi tai xong ghe. */
  sections: ICabinSection[] = [];

  selectedIds = new Set<number>();

  isLoading = true;

  notFound = false;

  /** Chuyen bay da khoi hanh (startTime da qua) - khong the dat ve nua. */
  departed = false;

  errorMessage = '';

  private allSeats: IShowtimeSeat[] = [];

  /** Nhan hien thi (vd "A1") theo so do moi, khoa la id cua ShowtimeSeat. */
  private seatLabels = new Map<number, string>();

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly showtimeService = inject(ShowtimeService);
  private readonly tripBuilder = inject(TripBuilderService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const showtimeId = Number(this.route.snapshot.paramMap.get('showtimeId'));

    if (!showtimeId) {
      this.isLoading = false;
      this.notFound = true;
      return;
    }

    this.load(showtimeId);
  }

  load(showtimeId: number): void {
    this.isLoading = true;

    this.showtimeService.find(showtimeId).subscribe({
      next: showtime => {
        if (!showtime.startTime?.isAfter(dayjs())) {
          this.showtime = null;
          this.departed = true;
          this.isLoading = false;
          this.cdr.detectChanges();
          return;
        }

        this.showtime = showtime;
        this.loadSeats(showtimeId);
      },
      error: () => {
        this.isLoading = false;
        this.notFound = true;
        this.cdr.detectChanges();
      },
    });
  }

  loadSeats(showtimeId: number): void {
    this.showtimeService.getSeats(showtimeId).subscribe({
      next: seats => {
        this.allSeats = seats;
        this.sections = this.buildSections(this.groupByRow(seats));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  isSelected(seat: IShowtimeSeat): boolean {
    return this.selectedIds.has(seat.id);
  }

  isAvailable(seat: IShowtimeSeat): boolean {
    return seat.status === 'AVAILABLE';
  }

  toggleSeat(seat: IShowtimeSeat): void {
    if (!this.isAvailable(seat)) {
      return;
    }

    if (this.selectedIds.has(seat.id)) {
      this.selectedIds.delete(seat.id);
      this.errorMessage = '';
    } else {
      if (this.selectedIds.size >= MAX_SEATS) {
        this.errorMessage = `Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế.`;
        return;
      }
      this.selectedIds.add(seat.id);
      this.errorMessage = '';
    }
  }

  get selectedSeats(): IShowtimeSeat[] {
    return this.allSeats.filter(seat => this.selectedIds.has(seat.id));
  }

  get selectedLabels(): string {
    return this.selectedSeats
      .slice()
      .sort((a, b) => this.seatLabel(a).localeCompare(this.seatLabel(b)))
      .map(seat => this.seatLabel(seat))
      .join(', ');
  }

  get total(): number {
    return this.selectedSeats.reduce((sum, seat) => sum + Number(seat.price ?? 0), 0);
  }

  /** Nhan hien thi cua ghe kieu hang khong thuc te (vd "12A"), khong con theo rowLabel/seatNumber goc. */
  seatLabel(seat: IShowtimeSeat): string {
    return this.seatLabels.get(seat.id) ?? `${seat.seat?.rowLabel ?? ''}${seat.seat?.seatNumber ?? ''}`;
  }

  /**
   * Chia so do thanh cac khoang hang ghe (Thuong gia / Hang nhat / Pho thong),
   * xep tu dau may bay ve sau theo gia giam dan - giong bo cuc cabin thuc te.
   * Moi hang co 2 ghe ben trai loi di + 2 ghe ben phai loi di (4 ghe/hang).
   * Danh so rieng cho tung ben: ben trai la "khoang A" (1A, 2A, 3A...), ben
   * phai la "khoang B" (1B, 2B, 3B...), moi ben danh so lien tuc doc theo
   * than may bay.
   */
  private buildSections(rows: ISeatRow[]): ICabinSection[] {
    this.seatLabels = new Map<number, string>();

    const sortedRows = rows.slice().sort((a, b) => this.rowPrice(b) - this.rowPrice(a));

    const sections: ICabinSection[] = [];
    let leftCounter = 0;
    let rightCounter = 0;

    for (const row of sortedRows) {
      const title = this.sectionTitle(row);
      let section = sections[sections.length - 1];

      if (!section || section.title !== title) {
        section = { title, rows: [] };
        sections.push(section);
      }

      for (let i = 0; i < row.seats.length; i += 4) {
        const left = row.seats.slice(i, i + 2);
        const right = row.seats.slice(i + 2, i + 4);

        if (left.length === 2 && right.length === 2) {
          for (const seat of left) {
            leftCounter += 1;
            this.seatLabels.set(seat.id, `${leftCounter}A`);
          }
          for (const seat of right) {
            rightCounter += 1;
            this.seatLabels.set(seat.id, `${rightCounter}B`);
          }
          section.rows.push({ left, right });
        }
      }
    }

    return sections;
  }

  private rowPrice(row: ISeatRow): number {
    return Number(row.seats[0]?.price ?? 0);
  }

  private sectionTitle(row: ISeatRow): string {
    switch (row.seats[0]?.seat?.seatType) {
      case 'VIP':
        return 'Khoang Thương gia';
      case 'COUPLE':
        return 'Khoang Hạng nhất';
      default:
        return 'Khoang Phổ thông';
    }
  }

  continueToPayment(): void {
    if (!this.showtime || this.selectedIds.size === 0) {
      return;
    }

    this.tripBuilder.addLeg({ showtimeId: this.showtime.id, seatIds: [...this.selectedIds] });

    if (this.tripBuilder.needsAutoNextLeg()) {
      const nextSearch = this.tripBuilder.consumeNextLegSearch();
      // /events se tao lai component moi (route khac han), phai truyen lai tripType tuong minh
      // vi tin hieu nay khong con luu o component cu - lay tu TripBuilderService (con song ca chuyen).
      this.router.navigate(['/events'], { queryParams: { ...nextSearch, tripType: this.tripBuilder.tripType() } });
      return;
    }

    this.router.navigate(['/payment/pay']);
  }

  private groupByRow(seats: IShowtimeSeat[]): ISeatRow[] {
    const byRow = new Map<string, IShowtimeSeat[]>();

    for (const seat of seats) {
      const rowLabel = seat.seat?.rowLabel ?? '?';
      const list = byRow.get(rowLabel) ?? [];
      list.push(seat);
      byRow.set(rowLabel, list);
    }

    return [...byRow.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([rowLabel, rowSeats]) => ({
        rowLabel,
        seats: rowSeats.slice().sort((a, b) => (a.seat?.seatNumber ?? 0) - (b.seat?.seatNumber ?? 0)),
      }));
  }
}
