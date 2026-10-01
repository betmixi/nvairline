import { AfterViewInit, Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild, inject } from '@angular/core';
import QRCode from 'qrcode';

/**
 * Ve ma QR ngay tren trinh duyet tu mot chuoi (vi du ma ve), khong goi API ben ngoai.
 */
@Component({
  standalone: true,
  selector: 'jhi-qr-code',
  template: '<canvas #canvas class="qr-canvas"></canvas>',
  styleUrl: './qr-code.scss',
})
export class QrCodeComponent implements AfterViewInit, OnChanges {
  @Input({ required: true }) value!: string;
  @Input() size = 140;

  @ViewChild('canvas') private readonly canvasRef!: ElementRef<HTMLCanvasElement>;

  private readonly elementRef = inject(ElementRef);

  ngAfterViewInit(): void {
    this.render();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['value'].firstChange) {
      this.render();
    }
  }

  private render(): void {
    if (!this.canvasRef || !this.value) {
      return;
    }

    QRCode.toCanvas(this.canvasRef.nativeElement, this.value, { width: this.size, margin: 1 }).catch(() => {
      // Ma trong, khong ve duoc thi bo qua, khung canvas se de trong.
    });
  }
}
