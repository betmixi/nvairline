import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import dayjs from 'dayjs/esm';

import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';
import { IShowtime } from 'app/entities/showtime/showtime.model';
import { ShowtimeService } from 'app/entities/showtime/service/showtime.service';

interface IShowtimeGroup {
  dateLabel: string;
  showtimes: IShowtime[];
}

/**
 * Danh sach suat chieu cua mot phim, nhom theo ngay, de nguoi dung chon suat
 * truoc khi vao trang chon ghe.
 */
@Component({
  standalone: true,
  selector: 'jhi-showtime-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './showtime-list.html',
  styleUrl: './showtime-list.scss',
})
export default class ShowtimeListComponent implements OnInit {
  event: IEvent | null = null;

  groups: IShowtimeGroup[] = [];

  isLoading = true;

  notFound = false;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventService = inject(EventService);
  private readonly showtimeService = inject(ShowtimeService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.isLoading = false;
      this.notFound = true;
      return;
    }

    this.loadEvent(id);
  }

  loadEvent(id: number): void {
    this.isLoading = true;

    this.eventService.findPublic(id).subscribe({
      next: event => {
        this.event = event;
        this.loadShowtimes(id);
      },
      error: () => {
        this.isLoading = false;
        this.notFound = true;
        this.cdr.detectChanges();
      },
    });
  }

  loadShowtimes(eventId: number): void {
    this.showtimeService.findByEvent(eventId).subscribe({
      next: res => {
        const now = dayjs();
        const showtimes = (res.body ?? [])
          .filter(showtime => !!showtime.startTime?.isAfter(now))
          .slice()
          .sort((a, b) => (a.startTime?.valueOf() ?? 0) - (b.startTime?.valueOf() ?? 0));
        this.groups = this.groupByDate(showtimes);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  selectShowtime(showtime: IShowtime): void {
    if (!this.event) {
      return;
    }

    this.router.navigate(['/event', this.event.id, 'showtimes', showtime.id, 'seats']);
  }

  /** Thoi luong bay: hieu giua endTime va startTime, dang "Xh Yphut". */
  duration(showtime: IShowtime): string {
    if (!showtime.startTime || !showtime.endTime) {
      return '';
    }
    const minutes = showtime.endTime.diff(showtime.startTime, 'minute');
    if (minutes <= 0) {
      return '';
    }
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h${m > 0 ? ` ${m}phút` : ''}` : `${m}phút`;
  }

  private groupByDate(showtimes: IShowtime[]): IShowtimeGroup[] {
    const byDate = new Map<string, IShowtimeGroup>();

    for (const showtime of showtimes) {
      const key = showtime.startTime?.format('YYYY-MM-DD') ?? 'unknown';
      let group = byDate.get(key);

      if (!group) {
        group = {
          dateLabel: showtime.startTime?.format('dddd, DD/MM/YYYY') ?? 'Chưa xác định',
          showtimes: [],
        };
        byDate.set(key, group);
      }

      group.showtimes.push(showtime);
    }

    return [...byDate.values()];
  }
}
