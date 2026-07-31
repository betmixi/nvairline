import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RevenueService, IOrganizerRevenue } from './revenue.service';
import { DecimalPipe, DatePipe } from '@angular/common';
@Component({
  standalone: true,
  selector: 'app-revenue',
  imports: [DecimalPipe, DatePipe],
  templateUrl: './revenue.html',
  styleUrl: './revenue.scss',
})
export class RevenueComponent implements OnInit {
  private revenueService = inject(RevenueService);
  private cdr = inject(ChangeDetectorRef);

  revenue?: IOrganizerRevenue;

  ngOnInit(): void {
    this.revenueService.getRevenue().subscribe({
      next: res => {
        console.log('Revenue:', res);

        this.revenue = { ...res }; // tạo object mới

        this.cdr.detectChanges();
      },
      error: err => console.error(err),
    });
  }
}
