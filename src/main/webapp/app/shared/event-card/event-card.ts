import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { IEvent } from 'app/entities/event/event.model';

/**
 * The mot su kien dung chung cho trang chu va trang danh sach su kien,
 * de 2 noi luon giong nhau ve giao dien.
 */
@Component({
  standalone: true,
  selector: 'jhi-event-card',
  imports: [CommonModule, RouterLink],
  templateUrl: './event-card.html',
  styleUrl: './event-card.scss',
})
export class EventCardComponent {
  @Input({ required: true }) event!: IEvent;

  getCategory(): string {
    return this.event.category?.name ?? 'Event';
  }

  getLocation(): string {
    return this.event.address?.location ?? 'Đang cập nhật';
  }

  getPrice(): string {
    if (this.event.price === null || this.event.price === undefined) {
      return 'Liên hệ';
    }
    return Number(this.event.price).toLocaleString('vi-VN') + ' đ';
  }
}
