import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { AircraftService } from 'app/entities/aircraft/service/aircraft.service';
import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { ShowtimeService } from 'app/entities/showtime/service/showtime.service';
import { IShowtime } from 'app/entities/showtime/showtime.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';

@Component({
  selector: 'app-manage-showtimes',
  standalone: true,
  templateUrl: './manage-showtimes.html',
  styleUrls: ['./manage-showtimes.scss'],
  imports: [CommonModule, FormsModule],
})
export class ManageShowtimesComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private showtimeService = inject(ShowtimeService);
  private eventService = inject(EventService);
  private aircraftService = inject(AircraftService);

  eventId = 0;

  event = signal<IEvent | null>(null);

  showtimes = signal<IShowtime[]>([]);

  aircrafts = signal<IAircraft[]>([]);

  isSaving = false;

  errorMessage = '';

  /** Tim theo ten may bay hoac ngay gio bay (VD: "A321", "14/09"). */
  keyword = '';

  get filteredShowtimes(): IShowtime[] {
    const kw = this.keyword.trim().toLowerCase();
    if (!kw) {
      return this.showtimes();
    }

    return this.showtimes().filter(showtime => {
      const aircraftName = (showtime.aircraft?.name ?? '').toLowerCase();
      const startText = showtime.startTime?.format('DD/MM/YYYY HH:mm').toLowerCase() ?? '';
      const endText = showtime.endTime?.format('DD/MM/YYYY HH:mm').toLowerCase() ?? '';
      return aircraftName.includes(kw) || startText.includes(kw) || endText.includes(kw);
    });
  }

  newShowtime = {
    aircraftId: null as number | null,
    startTime: '',
    endTime: '',
    basePrice: 0,
    vipPrice: 0,
    couplePrice: 0,
  };

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('eventId'));

    this.loadEvent();
    this.loadShowtimes();
    this.loadAircrafts();
  }

  loadEvent(): void {
    this.eventService.find(this.eventId).subscribe(res => {
      this.event.set(res);
    });
  }

  loadShowtimes(): void {
    this.showtimeService.findByEvent(this.eventId).subscribe(res => {
      const showtimes = (res.body ?? []).slice().sort((a, b) => (a.startTime?.valueOf() ?? 0) - (b.startTime?.valueOf() ?? 0));
      this.showtimes.set(showtimes);
    });
  }

  loadAircrafts(): void {
    this.aircraftService.query({ size: 200 }).subscribe(res => {
      this.aircrafts.set(res.body ?? []);
    });
  }

  saveShowtime(): void {
    this.errorMessage = '';

    const validationError = this.validateNewShowtime();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.isSaving = true;

    this.showtimeService
      .create({
        id: null,
        startTime: dayjs(this.newShowtime.startTime),
        endTime: dayjs(this.newShowtime.endTime),
        basePrice: Number(this.newShowtime.basePrice) || 0,
        vipPrice: Number(this.newShowtime.vipPrice) || 0,
        couplePrice: Number(this.newShowtime.couplePrice) || 0,
        event: { id: this.eventId },
        aircraft: { id: this.newShowtime.aircraftId },
      })
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.newShowtime = {
            aircraftId: null,
            startTime: '',
            endTime: '',
            basePrice: 0,
            vipPrice: 0,
            couplePrice: 0,
          };
          this.loadShowtimes();
        },
        error: err => {
          this.isSaving = false;
          this.errorMessage = this.serverErrorMessage(err);
        },
      });
  }

  /** Kiem tra du lieu o phia client truoc khi gui: bo trong, so am, thoi gian sai, gio bay bi trung. */
  private validateNewShowtime(): string | null {
    const { aircraftId, startTime, endTime } = this.newShowtime;

    if (!aircraftId) {
      return 'Vui lòng chọn máy bay.';
    }

    if (!startTime || !endTime) {
      return 'Vui lòng nhập giờ bắt đầu và kết thúc.';
    }

    const start = dayjs(startTime);
    const end = dayjs(endTime);
    if (!start.isValid() || !end.isValid()) {
      return 'Giờ bắt đầu hoặc giờ kết thúc không hợp lệ.';
    }
    if (!end.isAfter(start)) {
      return 'Giờ kết thúc phải sau giờ bắt đầu.';
    }

    const prices: [string, unknown][] = [
      ['Giá vé phổ thông', this.newShowtime.basePrice],
      ['Giá vé thương gia', this.newShowtime.vipPrice],
      ['Giá vé phổ thông đặc biệt', this.newShowtime.couplePrice],
    ];
    for (const [label, value] of prices) {
      if (value === null || value === undefined || value === '' || Number.isNaN(Number(value))) {
        return `Vui lòng nhập ${label.toLowerCase()}.`;
      }
      if (Number(value) < 0) {
        return `${label} không được là số âm.`;
      }
    }
    if (Number(this.newShowtime.basePrice) === 0) {
      return 'Giá vé phổ thông phải lớn hơn 0.';
    }

    for (const existing of this.showtimes()) {
      if (existing.startTime?.isSame(start)) {
        return 'Chuyến bay này đã có giờ bay bắt đầu vào thời điểm đó.';
      }
      if (existing.aircraft?.id === aircraftId && existing.startTime && existing.endTime && existing.startTime.isBefore(end) && existing.endTime.isAfter(start)) {
        return 'Máy bay này đã có giờ bay khác trùng khoảng thời gian đã chọn.';
      }
    }

    return null;
  }

  private serverErrorMessage(err: any): string {
    const messages: Record<string, string> = {
      'error.timerequired': 'Vui lòng nhập giờ bắt đầu và kết thúc.',
      'error.invalidtime': 'Giờ kết thúc phải sau giờ bắt đầu.',
      'error.negativeprice': 'Giá vé không được là số âm.',
      'error.showtimeexists': 'Chuyến bay này đã có giờ bay bắt đầu vào thời điểm đó.',
      'error.aircraftbusy': 'Máy bay này đã có giờ bay khác trùng khoảng thời gian đã chọn.',
      'error.roomrequired': 'Vui lòng chọn máy bay.',
    };
    const key = (err?.error?.message ?? err?.headers?.get?.('X-dugxApp-error')) as string | undefined;
    return (key && messages[key]) ?? err?.error?.title ?? 'Không tạo được giờ bay.';
  }

  delete(showtime: IShowtime): void {
    if (!confirm('Xoá giờ bay này?')) {
      return;
    }

    this.showtimeService.delete(showtime.id).subscribe(() => {
      this.loadShowtimes();
    });
  }

  finish(): void {
    this.router.navigate(['/event']);
  }
}
