import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCalendarDays, faSackDollar, faTicket, faUserClock, faUsers } from '@fortawesome/free-solid-svg-icons';
import { AdminDashboardService } from './admin-dashboard.service';
import { AdminDashboard, TopEvent } from './admin-dashboard.model';
import { IOrganizer } from 'app/entities/organizer/organizer.model';

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

@Component({
  selector: 'jhi-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(AdminDashboardService);
  private readonly cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  faUsers = faUsers;
  faCalendar = faCalendarDays;
  faTicket = faTicket;
  faUserClock = faUserClock;
  faMoney = faSackDollar;

  dashboard: AdminDashboard | null = null;
  organizers: IOrganizer[] = [];

  measure: RankMeasure = 'tickets';

  setMeasure(measure: RankMeasure): void {
    this.measure = measure;
  }

  /** Top 5 su kien theo tieu chi dang chon, kem % so voi su kien dan dau. */
  get rankedEvents(): RankedEvent[] {
    const source = this.measure === 'tickets' ? this.dashboard?.topEventsByTickets : this.dashboard?.topEventsByRevenue;

    const rows = source ?? [];

    const valueOf = (event: TopEvent): number => (this.measure === 'tickets' ? event.ticketsSold : event.revenue) ?? 0;

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

  ngOnInit(): void {
    this.loadDashboard();
    this.loadPendingOrganizers();
  }

  private loadDashboard(): void {
    this.dashboardService.getDashboard().subscribe({
      next: (res: AdminDashboard) => {
        console.log('Dashboard:', res);

        this.dashboard = res;

        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Dashboard Error:', err);
      },
    });
  }

  private loadPendingOrganizers(): void {
    this.dashboardService.getPendingOrganizers().subscribe({
      next: res => {
        this.organizers = res;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Pending Organizer Error:', err);
      },
    });
  }

  approve(id: number): void {
    this.dashboardService.approveOrganizer(id).subscribe({
      next: () => {
        this.loadDashboard();
        this.loadPendingOrganizers();
      },
      error: err => {
        console.error(err);
      },
    });
  }

  reject(id: number): void {
    this.dashboardService.rejectOrganizer(id).subscribe({
      next: () => {
        this.loadDashboard();
        this.loadPendingOrganizers();
      },
      error: err => {
        console.error(err);
      },
    });
  }
  goOrganizerRequests(): void {
    this.router.navigate(['/admin/organizer-requests']).then(success => {
      console.log('Navigate:', success);
    });
  }
}
