import { IEvent } from 'app/entities/event/event.model';

export interface TopEvent {
  eventId: number;
  title: string;
  ticketsSold: number;
  revenue: number;
}

export interface AdminDashboard {
  totalUsers: number;
  totalEvents: number;
  totalBookings: number;
  totalRevenue: number;
  recentEvents: IEvent[];
  topEventsByTickets: TopEvent[];
  topEventsByRevenue: TopEvent[];
}
