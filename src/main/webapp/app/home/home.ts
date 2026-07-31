import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';
import { AccountService } from 'app/core/auth/account.service';
import { TranslateDirective } from 'app/shared/language';
import { EventCardComponent } from 'app/shared/event-card/event-card';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [CommonModule, TranslateDirective, RouterLink, EventCardComponent],
})
export default class Home implements OnInit {
  public readonly account = inject(AccountService).account;
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly router = inject(Router);
  events: IEvent[] = [];

  private readonly eventService = inject(EventService);
  login(): void {
    this.router.navigate(['/login']);
  }
  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventService
      .queryPublic({
        page: 0,
        size: 3,
        sort: ['createdDate,desc'],
      })
      .subscribe({
        next: res => {
          this.events = res.body ?? [];

          this.cdr.detectChanges();
        },
      });
  }
}
