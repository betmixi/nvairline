import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faAddressBook,
  faCalendarDays,
  faCircleQuestion,
  faSackDollar,
  faStar,
  faTicket,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';
import { AdminDashboardService } from './admin-dashboard.service';
import { AdminDashboard, TopEvent } from './admin-dashboard.model';

/** Thang mau tim dam dan theo hang (da kiem tra tuong phan + do doc thang do). */
const RANK_COLORS = ['#3f32b8', '#5a4ae0', '#7462f7', '#8f7ffd', '#a99dff'];

export type RankMeasure = 'tickets' | 'revenue';

export interface RankedEvent {
  title: string;
  value: number;
  display: string;
  percent: number;
  color: string;
}

export interface RevenueSlice {
  label: string;
  value: number;
  percent: number;
  color: string;
  dashArray: string;
  dashOffset: number;
}

/** Ban kinh + chu vi vong tron cua bieu do donut (xem donutSlices()). */
const DONUT_RADIUS = 60;
const DONUT_CIRCUMFERENCE = 2 * Math.PI * DONUT_RADIUS;
const OTHERS_COLOR = '#d8dbe6';

@Component({
  selector: 'jhi-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class DashboardComponent implements OnInit {
  faUsers = faUsers;
  faCalendar = faCalendarDays;
  faTicket = faTicket;
  faMoney = faSackDollar;
  faStar = faStar;
  faSupport = faCircleQuestion;
  faCustomers = faAddressBook;

  dashboard: AdminDashboard | null = null;

  measure: RankMeasure = 'tickets';

  private readonly dashboardService = inject(AdminDashboardService);
  private readonly cdr = inject(ChangeDetectorRef);

  setMeasure(measure: RankMeasure): void {
    this.measure = measure;
  }

  /** Top 5 su kien theo tieu chi dang chon, kem % so voi su kien dan dau. */
  get rankedEvents(): RankedEvent[] {
    const source = this.measure === 'tickets' ? this.dashboard?.topEventsByTickets : this.dashboard?.topEventsByRevenue;

    const rows = source ?? [];

    const valueOf = (event: TopEvent): number => (this.measure === 'tickets' ? event.ticketsSold : event.revenue);

    const max = Math.max(...rows.map(valueOf), 1);

    return rows.map((event, index) => {
      const value = valueOf(event);

      return {
        title: event.title,
        value,
        display: this.measure === 'tickets' ? `${value.toLocaleString('vi-VN')} vé` : `${value.toLocaleString('vi-VN')} ₫`,
        percent: (value / max) * 100,
        color: RANK_COLORS[index] ?? RANK_COLORS[RANK_COLORS.length - 1],
      };
    });
  }

  /** Ty trong doanh thu: top 5 su kien + phan con lai ("Cac su kien khac"), ve bang donut chart. */
  get revenueSlices(): RevenueSlice[] {
    const top5 = this.dashboard?.topEventsByRevenue ?? [];
    const totalRevenue = this.dashboard?.totalRevenue ?? 0;
    const top5Sum = top5.reduce((sum, event) => sum + event.revenue, 0);
    const others = Math.max(totalRevenue - top5Sum, 0);

    const raw = [
      ...top5.map((event, index) => ({
        label: event.title,
        value: event.revenue,
        color: RANK_COLORS[index] ?? RANK_COLORS[RANK_COLORS.length - 1],
      })),
      ...(others > 0 ? [{ label: 'Các sự kiện khác', value: others, color: OTHERS_COLOR }] : []),
    ];

    const total = raw.reduce((sum, row) => sum + row.value, 0) || 1;

    let cumulativePercent = 0;

    return raw.map(row => {
      const percent = (row.value / total) * 100;
      const dashLength = (percent / 100) * DONUT_CIRCUMFERENCE;

      const slice: RevenueSlice = {
        label: row.label,
        value: row.value,
        percent,
        color: row.color,
        dashArray: `${dashLength} ${DONUT_CIRCUMFERENCE - dashLength}`,
        dashOffset: -((cumulativePercent / 100) * DONUT_CIRCUMFERENCE),
      };

      cumulativePercent += percent;

      return slice;
    });
  }

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.dashboardService.getDashboard().subscribe({
      next: (res: AdminDashboard) => {
        this.dashboard = res;

        this.cdr.detectChanges();
      },
      error(err) {
        console.error('Dashboard Error:', err);
      },
    });
  }
}
