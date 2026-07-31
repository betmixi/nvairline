import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EventService } from '../service/event.service';
import { IEvent } from '../event.model';
import { ChangeDetectorRef } from '@angular/core';
import { TicketTypeService } from '../../ticket-type/service/ticket-type.service';
import { ITicketType } from '../../ticket-type/ticket-type.model';

@Component({
  standalone: true,
  selector: 'jhi-event-ticket',
  imports: [CommonModule, RouterLink],
  templateUrl: './event-ticket.html',
  styleUrl: './event-ticket.scss',
})
export default class EventTicketComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly eventService = inject(EventService);
  private readonly ticketTypeService = inject(TicketTypeService);
  constructor() {
    console.log('EventTicketComponent CREATED');
  }
  event: IEvent | null = null;

  ticketTypes: ITicketType[] = [];

  selectedTicket: ITicketType | null = null;

  quantity = 1;

  isLoading = true;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.isLoading = false;
      return;
    }

    this.loadEvent(id);
  }

  loadEvent(id: number): void {
    console.log('loadEvent');

    this.isLoading = true;

    this.eventService.find(id).subscribe({
      next: event => {
        console.log('EVENT =', event);

        this.event = event;

        console.log('this.event =', this.event);

        this.loadTicketTypes(id);
      },

      error: err => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }

  loadTicketTypes(eventId: number): void {
    this.ticketTypeService
      .query({
        'eventId.equals': eventId,

        sort: ['price,asc'],
      })
      .subscribe({
        next: res => {
          this.ticketTypes = res.body ?? [];

          if (this.ticketTypes.length > 0) {
            this.selectedTicket = this.ticketTypes[0];
          }

          this.isLoading = false;

          this.cdr.detectChanges();

          console.log('After detectChanges:', this.isLoading, this.ticketTypes.length);
        },
      });
  }

  selectTicket(ticket: ITicketType): void {
    if ((ticket.remaining ?? 0) === 0) {
      return;
    }

    this.selectedTicket = ticket;

    this.quantity = 1;
  }

  increase(): void {
    if (!this.selectedTicket) {
      return;
    }

    if (this.quantity < (this.selectedTicket.remaining ?? 0)) {
      this.quantity++;
    }
  }

  decrease(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  get total(): number {
    if (!this.selectedTicket) {
      return 0;
    }

    return (this.selectedTicket.price ?? 0) * this.quantity;
  }

  continue(): void {
    if (!this.selectedTicket) {
      return;
    }

    this.router.navigate(['/payment/pay'], {
      queryParams: {
        ticketTypeId: this.selectedTicket.id,
        quantity: this.quantity,
      },
    });
  }
}
