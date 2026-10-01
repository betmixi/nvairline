import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface ISupportRequest {
  id: number;
  topic: string;
  content: string;
  status: 'PENDING' | 'REPLIED';
  reply?: string | null;
  createdDate: string;
  repliedDate?: string | null;
  user?: { id: number; login?: string | null; firstName?: string | null; lastName?: string | null } | null;
}

export interface INewSupportRequest {
  topic: string;
  content: string;
}

/** UC Lien he ho tro: khach hang gui yeu cau, quan tri vien phan hoi. */
@Injectable({ providedIn: 'root' })
export class SupportService {
  private readonly http = inject(HttpClient);

  getTopics(): Observable<string[]> {
    return this.http.get<string[]>('/api/support-requests/topics');
  }

  createRequest(request: INewSupportRequest): Observable<ISupportRequest> {
    return this.http.post<ISupportRequest>('/api/support-requests', request);
  }

  getMyRequests(): Observable<ISupportRequest[]> {
    return this.http.get<ISupportRequest[]>('/api/support-requests/mine');
  }

  // ---------- Quan ly (admin) ----------

  getAllForAdmin(): Observable<ISupportRequest[]> {
    return this.http.get<ISupportRequest[]>('/api/support-requests');
  }

  reply(id: number, reply: string): Observable<ISupportRequest> {
    return this.http.patch<ISupportRequest>(`/api/support-requests/${id}/reply`, { reply });
  }
}
