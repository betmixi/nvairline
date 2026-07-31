import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { faCalendarDays, faClipboardList, faMoneyBillWave, faPlus, faStar, faTicket } from '@fortawesome/free-solid-svg-icons';

import { OrganizerDashboardService } from './dashboard.service';
import { OrganizerDashboard } from './dashboard.model';

@Component({
  standalone: true,
  selector: 'jhi-organizer-dashboard',
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(OrganizerDashboardService);
  private cdr = inject(ChangeDetectorRef);

  faCalendar = faCalendarDays;
  faMoney = faMoneyBillWave;
  faTicket = faTicket;
  faBooking = faClipboardList;
  faStar = faStar;
  faPlus = faPlus;

  myDashboard: OrganizerDashboard = {
    companyName: '',
    totalEvents: 0,
    publishedEvents: 0,
    totalTickets: 0,
    totalBookings: 0,
    totalRevenue: 0,
    latestEvents: [],
  };

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.dashboardService.getDashboard().subscribe({
      next: res => {
        this.myDashboard = {
          companyName: res.companyName,
          totalEvents: res.totalEvents,
          publishedEvents: res.publishedEvents,
          totalTickets: res.totalTickets,
          totalBookings: res.totalBookings,
          totalRevenue: res.totalRevenue,
          latestEvents: res.latestEvents ?? [],
        };

        console.log(this.myDashboard);

        this.cdr.detectChanges();
      },
    });
  }
}
