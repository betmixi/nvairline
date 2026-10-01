import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { EngagementService } from 'app/core/util/engagement.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventCardComponent } from 'app/shared/event-card/event-card';

/** Danh sach su kien nguoi dung da luu. */
@Component({
  standalone: true,
  selector: 'jhi-user-favorites',
  imports: [CommonModule, RouterLink, EventCardComponent],
  templateUrl: './favorites.html',
  styleUrl: './favorites.scss',
})
export class FavoritesComponent implements OnInit {
  events: IEvent[] = [];
  isLoading = true;

  private readonly engagementService = inject(EngagementService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.engagementService.myFavorites().subscribe({
      next: events => {
        this.events = events;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }
}
