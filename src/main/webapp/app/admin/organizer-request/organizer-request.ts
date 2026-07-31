import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';
import { OrganizerRequestService } from './organizer-request.service';
import { AlertService } from 'app/core/util/alert.service';
@Component({
  selector: 'jhi-organizer-request',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './organizer-request.html',
  styleUrl: './organizer-request.scss',
})
export default class OrganizerRequestComponent implements OnInit {
  private service = inject(OrganizerRequestService);
  private cdr = inject(ChangeDetectorRef);
  private alertService = inject(AlertService);
  organizers: any[] = [];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.service.getPending().subscribe(res => {
      this.organizers = res;
      this.cdr.detectChanges();
    });
  }

  approve(id: number): void {
    this.service.approve(id).subscribe(() => {
      this.load(); // load lại từ server

      this.alertService.addAlert({
        type: 'success',
        message: 'Organizer approved successfully!',
      });
    });
  }

  reject(id: number): void {
    this.service.reject(id).subscribe(() => {
      this.load();

      this.alertService.addAlert({
        type: 'warning',
        message: 'Organizer rejected successfully!',
      });
    });
  }
}
