export interface EventDashboard {
  id: number;

  title: string;

  banner: string | null;

  status: boolean;

  createdDate: string;
}

export interface OrganizerDashboard {
  companyName: string;

  totalEvents: number;

  publishedEvents: number;

  totalTickets: number;

  totalBookings: number;

  totalRevenue: number;

  latestEvents: EventDashboard[];
}
