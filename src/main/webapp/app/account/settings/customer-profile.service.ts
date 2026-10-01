import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ICustomerProfile } from './customer-profile.model';

@Injectable({ providedIn: 'root' })
export class CustomerProfileService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-profile');

  find(): Observable<ICustomerProfile> {
    return this.http.get<ICustomerProfile>(`${this.resourceUrl}/me`);
  }

  save(profile: ICustomerProfile): Observable<ICustomerProfile> {
    return this.http.put<ICustomerProfile>(`${this.resourceUrl}/me`, profile);
  }
}
