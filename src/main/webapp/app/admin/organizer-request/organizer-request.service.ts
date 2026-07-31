import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrganizerRequestService {
  private http = inject(HttpClient);

  getPending(): Observable<any[]> {
    return this.http.get<any[]>('/api/admin/organizers/pending');
  }

  approve(id: number): Observable<any> {
    return this.http.patch(`/api/admin/organizers/${id}/approve`, {});
  }

  reject(id: number): Observable<any> {
    return this.http.patch(`/api/admin/organizers/${id}/reject`, {});
  }
}
