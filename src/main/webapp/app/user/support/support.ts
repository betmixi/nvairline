import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { INewSupportRequest, ISupportRequest, SupportService } from 'app/core/util/support.service';

/** UC Lien he ho tro: xem thong tin lien he, chon chu de, gui yeu cau va xem phan hoi. */
@Component({
  standalone: true,
  selector: 'jhi-support',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './support.html',
  styleUrl: './support.scss',
})
export default class SupportComponent implements OnInit {
  topics: string[] = [];
  selectedTopic: string | null = null;
  content = '';

  myRequests: ISupportRequest[] = [];
  isLoadingRequests = true;

  isSubmitting = false;
  submitError: string | null = null;
  submitSuccess = false;

  private readonly supportService = inject(SupportService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.supportService.getTopics().subscribe({
      next: topics => {
        this.topics = topics;
        this.cdr.detectChanges();
      },
      error: () => undefined,
    });

    this.loadMyRequests();
  }

  selectTopic(topic: string): void {
    this.selectedTopic = topic;
    this.submitError = null;
  }

  submitRequest(): void {
    if (this.isSubmitting) {
      return;
    }

    if (!this.selectedTopic) {
      this.submitError = 'Vui lòng chọn nội dung cần hỗ trợ.';
      return;
    }

    if (!this.content.trim()) {
      this.submitError = 'Vui lòng nhập nội dung yêu cầu.';
      return;
    }

    this.isSubmitting = true;
    this.submitError = null;
    this.submitSuccess = false;

    const request: INewSupportRequest = { topic: this.selectedTopic, content: this.content.trim() };

    this.supportService.createRequest(request).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.submitSuccess = true;
        this.content = '';
        this.selectedTopic = null;
        this.loadMyRequests();
      },
      error: () => {
        this.isSubmitting = false;
        this.submitError = 'Gửi yêu cầu hỗ trợ thất bại, vui lòng thử lại.';
        this.cdr.detectChanges();
      },
    });
  }

  private loadMyRequests(): void {
    this.isLoadingRequests = true;

    this.supportService.getMyRequests().subscribe({
      next: requests => {
        this.myRequests = requests;
        this.isLoadingRequests = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoadingRequests = false;
        this.cdr.detectChanges();
      },
    });
  }
}
