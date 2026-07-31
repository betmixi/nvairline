import { Component, ChangeDetectorRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventCardComponent } from 'app/shared/event-card/event-card';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, EventCardComponent],
  templateUrl: './events.html',
  styleUrls: ['./events.scss'],
})
export class EventsComponent implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly cdr = inject(ChangeDetectorRef);

  events: IEvent[] = [];
  filteredEvents: IEvent[] = [];
  isLoading = false;
  searchText = '';
  ngOnInit(): void {
    this.loadEvents();
  }
  loadEvents(): void {
    this.isLoading = true;
    this.eventService
      .queryPublic({
        page: 0,
        size: 100,
        sort: ['createdDate,desc'],
      })
      .subscribe({
        next: res => {
          this.events = res.body ?? [];
          this.filteredEvents = [...this.events];
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }
  searchEvents(): void {
    const keyword = this.searchText.trim().toLowerCase();
    if (!keyword) {
      this.filteredEvents = [...this.events];
      return;
    }
    this.filteredEvents = this.events.filter(
      event =>
        (event.title ?? '').toLowerCase().includes(keyword) ||
        (event.category?.name ?? '').toLowerCase().includes(keyword) ||
        (event.address?.location ?? '').toLowerCase().includes(keyword) ||
        (event.organizer?.companyName ?? '').toLowerCase().includes(keyword),
    );
  }
  clearSearch(): void {
    this.searchText = '';
    this.filteredEvents = [...this.events];
  }
  trackById(index: number, item: IEvent): number {
    return item.id;
  }
}
