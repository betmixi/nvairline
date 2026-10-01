import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ISupportRequest, SupportService } from 'app/core/util/support.service';

/** Quan ly toan bo yeu cau ho tro trong he thong - danh cho admin (UC Lien he ho tro). */
@Component({
  standalone: true,
  selector: 'jhi-admin-support',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './support-admin.html',
  styleUrl: './support-admin.scss',
})
export class AdminSupportComponent implements OnInit {
  requests: ISupportRequest[] = [];
  isLoading = true;

  /** Bo loc: 'ALL' | 'PENDING' | 'REPLIED'. */
  statusFilter: 'ALL' | 'PENDING' | 'REPLIED' = 'ALL';
  keyword = '';

  replyingId: number | null = null;
  replyDraft = '';
  isSavingReply = false;

  private readonly supportService = inject(SupportService);
  private readonly cdr = inject(ChangeDetectorRef);

  get filteredRequests(): ISupportRequest[] {
    const kw = this.keyword.trim().toLowerCase();

    return this.requests.filter(r => {
      if (this.statusFilter !== 'ALL' && r.status !== this.statusFilter) {
        return false;
      }

      if (!kw) {
        return true;
      }

      return r.topic.toLowerCase().includes(kw) || r.content.toLowerCase().includes(kw) || this.requesterName(r).toLowerCase().includes(kw);
    });
  }

  get pendingCount(): number {
    return this.requests.filter(r => r.status === 'PENDING').length;
  }

  ngOnInit(): void {
    this.load();
  }

  requesterName(request: ISupportRequest): string {
    const fullName = `${request.user?.firstName ?? ''} ${request.user?.lastName ?? ''}`.trim();
    return fullName || (request.user?.login ?? 'Người dùng');
  }

  startReply(request: ISupportRequest): void {
    this.replyingId = request.id;
    this.replyDraft = request.reply ?? '';
  }

  cancelReply(): void {
    this.replyingId = null;
    this.replyDraft = '';
  }

  submitReply(request: ISupportRequest): void {
    if (this.isSavingReply || !this.replyDraft.trim()) {
      return;
    }

    this.isSavingReply = true;

    this.supportService.reply(request.id, this.replyDraft.trim()).subscribe({
      next: updated => {
        request.reply = updated.reply;
        request.status = updated.status;
        request.repliedDate = updated.repliedDate;
        this.isSavingReply = false;
        this.replyingId = null;
        this.replyDraft = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.isSavingReply = false;
        this.cdr.detectChanges();
      },
    });
  }

  private load(): void {
    this.supportService.getAllForAdmin().subscribe({
      next: requests => {
        this.requests = requests;
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
