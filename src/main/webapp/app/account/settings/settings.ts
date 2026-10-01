import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { forkJoin } from 'rxjs';

import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { CustomerProfileService } from './customer-profile.service';
import { ICustomerProfile } from './customer-profile.model';

const initialAccount: Account = {} as Account;

@Component({
  selector: 'jhi-settings',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AlertError, ReactiveFormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.scss',
})
export default class Settings implements OnInit {
  readonly success = signal(false);
  readonly isLoading = signal(true);

  settingsForm = new FormGroup({
    firstName: new FormControl(initialAccount.firstName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    lastName: new FormControl(initialAccount.lastName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    email: new FormControl(initialAccount.email, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    langKey: new FormControl(initialAccount.langKey, { nonNullable: true }),

    activated: new FormControl(initialAccount.activated, { nonNullable: true }),
    authorities: new FormControl(initialAccount.authorities, { nonNullable: true }),
    imageUrl: new FormControl(initialAccount.imageUrl, { nonNullable: true }),
    login: new FormControl(initialAccount.login, { nonNullable: true }),
  });

  /** Thong tin khach hang bo sung, luu rieng qua CustomerProfileService. */
  profileForm = new FormGroup({
    phone: new FormControl<string | null>(null, { validators: [Validators.maxLength(20)] }),
    dateOfBirth: new FormControl<string | null>(null),
    gender: new FormControl<string | null>(null),
    idNumber: new FormControl<string | null>(null, { validators: [Validators.maxLength(20)] }),
    address: new FormControl<string | null>(null, { validators: [Validators.maxLength(255)] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly customerProfileService = inject(CustomerProfileService);

  ngOnInit(): void {
    forkJoin({ account: this.accountService.identity(), profile: this.customerProfileService.find() }).subscribe({
      next: ({ account, profile }) => {
        if (account) {
          this.settingsForm.patchValue(account);
        }
        this.profileForm.patchValue(profile);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  save(): void {
    this.success.set(false);

    const account = this.settingsForm.getRawValue();
    const profile: ICustomerProfile = this.profileForm.getRawValue();

    forkJoin({
      account: this.accountService.save(account),
      profile: this.customerProfileService.save(profile),
    }).subscribe({
      next: () => {
        this.success.set(true);
        this.accountService.authenticate(account);
      },
      error() {
        // Handled by interceptor.
      },
    });
  }
}
