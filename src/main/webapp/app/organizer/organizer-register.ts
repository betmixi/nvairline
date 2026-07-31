import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OnInit } from '@angular/core';
import { OrganizerRegisterService } from './organizer-register.service';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'jhi-organizer-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './organizer-register.html',
  styleUrl: './organizer-register.scss',
})
export default class OrganizerRegisterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly organizerService = inject(OrganizerRegisterService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  request: any = null;

  loading = true;
  ngOnInit(): void {
    console.log('A: ngOnInit');

    this.organizerService.getMyRequest().subscribe({
      next: res => {
        console.log('B: success', res);

        this.request = res;
        this.loading = false;
        this.cdr.detectChanges();
      },

      error: err => {
        console.log('C: error', err);

        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }
  form = this.fb.group({
    companyName: ['', [Validators.required]],
    taxCode: ['', [Validators.required]],
    description: [''],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const data = {
      companyName: this.form.value.companyName ?? '',
      taxCode: this.form.value.taxCode ?? '',
      description: this.form.value.description ?? '',
    };

    this.organizerService.register(data).subscribe({
      next: () => {
        this.request = {
          status: 'PENDING',
        };
      },
      error: err => {
        alert(err.error?.detail ?? 'Có lỗi xảy ra.');
      },
    });
  }
}
