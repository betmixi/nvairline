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

    if (!this.newShowtime.aircraftId) {
      this.errorMessage = 'Vui lòng chọn máy bay.';
      return;
    }

    if (!this.newShowtime.startTime || !this.newShowtime.endTime) {
      this.errorMessage = 'Vui lòng nhập giờ bắt đầu và kết thúc.';
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
          this.errorMessage = err?.error?.title ?? 'Không tạo được giờ bay.';
        },
      });
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
