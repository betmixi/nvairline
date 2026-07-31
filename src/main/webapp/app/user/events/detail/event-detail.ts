import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';

/**
 * Trang xem chi tiet su kien truoc khi mua ve. Khong can dang nhap.
 * Chi hien thi thong tin + danh sach loai ve tham khao; chon ve va thanh toan
 * van nam o trang /event/:id/ticket.
 */
@Component({
  standalone: true,
  selector: 'jhi-user-event-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './event-detail.html',
  styleUrl: './event-detail.scss',
})
export default class UserEventDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventService = inject(EventService);
  private readonly cdr = inject(ChangeDetectorRef);

  event: IEvent | null = null;

  isLoading = true;

  notFound = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.isLoading = false;
      this.notFound = true;
      return;
    }

    this.eventService.findPublic(id).subscribe({
      next: event => {
        this.event = event;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.notFound = true;
        this.cdr.detectChanges();
      },
    });
  }

  get lowestPrice(): number | null {
    const prices = (this.event?.ticketTypes ?? [])
      .map(ticket => ticket.price)
      .filter((price): price is number => price !== null && price !== undefined);

    return prices.length ? Math.min(...prices) : null;
  }

  get hasAvailableTickets(): boolean {
    return (this.event?.ticketTypes ?? []).some(ticket => (ticket.remaining ?? 0) > 0);
  }

  buyTicket(): void {
    if (!this.event) {
      return;
    }

    this.router.navigate(['/event', this.event.id, 'ticket']);
  }
}
