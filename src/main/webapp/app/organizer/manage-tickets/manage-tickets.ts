import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { TicketTypeService } from 'app/entities/ticket-type/service/ticket-type.service';
import { EventService } from 'app/entities/event/service/event.service';

import { ITicketType } from 'app/entities/ticket-type/ticket-type.model';
import { IEvent } from 'app/entities/event/event.model';

@Component({
  selector: 'app-manage-tickets',
  standalone: true,
  templateUrl: './manage-tickets.html',
  styleUrls: ['./manage-tickets.scss'],
  imports: [CommonModule, FormsModule],
})
export class ManageTicketsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private ticketService = inject(TicketTypeService);
  private eventService = inject(EventService);

  eventId = 0;

  event = signal<IEvent | null>(null);

  tickets = signal<ITicketType[]>([]);

  newTicket = {
    name: '',
    price: 0,
    quantity: 0,
  };

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('eventId'));

    this.loadEvent();

    this.loadTickets();
  }

  loadEvent(): void {
    this.eventService.find(this.eventId).subscribe(res => {
      this.event.set(res);
    });
  }

  loadTickets(): void {
    this.ticketService.findByEvent(this.eventId).subscribe(res => {
      this.tickets.set(res.body ?? []);
    });
  }

  saveTicket(): void {
    if (!this.newTicket.name.trim()) {
      alert('Ticket name required');

      return;
    }

    this.ticketService
      .create({
        id: null,

        name: this.newTicket.name,

        price: this.newTicket.price,

        quantity: this.newTicket.quantity,

        remaining: this.newTicket.quantity,

        event: {
          id: this.eventId,
        },
      })
      .subscribe(() => {
        this.newTicket = {
          name: '',
          price: 0,
          quantity: 0,
        };

        this.loadTickets();
      });
  }

  delete(ticket: ITicketType): void {
    if (!confirm('Delete ticket?')) {
      return;
    }

    this.ticketService.delete(ticket.id).subscribe(() => {
      this.loadTickets();
    });
  }

  finish(): void {
    this.router.navigate(['/organizer/events']);
  }
}
