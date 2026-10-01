import { Component, ChangeDetectorRef, HostListener, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbDate, NgbDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { IAirport } from 'app/entities/airport/airport.model';
import { AirportService } from 'app/entities/airport/service/airport.service';
import { AccountService } from 'app/core/auth/account.service';
import { TranslateDirective } from 'app/shared/language';
import { IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';
import { TripBuilderService } from 'app/booking/trip-builder.service';

type BookingTabId = 'mua-ve' | 'quan-ly' | 'lam-thu-tuc' | 'trang-thai' | 'lich-bay';
type TripType = 'round-trip' | 'one-way' | 'multi-city';

interface BookingTab {
  id: BookingTabId;
  label: string;
}

interface FlashNews {
  headline: string;
  date: string;
}

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [CommonModule, FormsModule, TranslateDirective, RouterLink, NgbDatepicker],
})
export default class Home implements OnInit {
  protected readonly accountService = inject(AccountService);
  public readonly account = this.accountService.account;
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly router = inject(Router);
  private readonly myTicketService = inject(MyTicketService);
  private readonly tripBuilder = inject(TripBuilderService);

  /** Loi hien thi tren the tim kiem (vd chua chon du ngay di/ve cho ve khu hoi). */
  searchErrorMessage = '';

  /**
   * "Quản lý đặt chỗ": hiện sẵn toàn bộ vé của người dùng đang đăng nhập
   * (không cần nhập lại mã vé/họ để "xác minh" vì đã đăng nhập rồi).
   * Mã vé chỉ dùng để lọc bớt khi có nhiều vé.
   */
  bookingSearchCode = '';
  bookingTickets: IMyTicket[] = [];
  bookingLoading = false;
  bookingLoaded = false;

  /** Tin nhanh hien thi duoi khung tim kiem, duyet qua lai bang nut < / >. */
  readonly flashNews: FlashNews[] = [
    { headline: 'Vietnam Airlines chuyển nhà ga khai thác tại một số sân bay quốc tế', date: '2026-03-17' },
    { headline: 'Ưu đãi vé máy bay dịp cuối năm dành cho hội viên Lotusmiles', date: '2026-03-01' },
    { headline: 'Thông báo tạm dừng hoạt động sân bay Liên Khương', date: '2026-02-26' },
  ];
  readonly currentNewsIndex = signal(0);

  currentNews(): FlashNews {
    return this.flashNews[this.currentNewsIndex()];
  }

  prevNews(): void {
    this.currentNewsIndex.set((this.currentNewsIndex() - 1 + this.flashNews.length) % this.flashNews.length);
  }

  nextNews(): void {
    this.currentNewsIndex.set((this.currentNewsIndex() + 1) % this.flashNews.length);
  }

  /** Danh sach san bay dung cho 2 truong "Tu" / "Den" tren the tim kiem. */
  airports: IAirport[] = [];

  /** Cac tab kieu dat cho (chi "Mua ve" la thuc su hoat dong, con lai la placeholder de giong giao dien tham chieu). */
  readonly bookingTabs: BookingTab[] = [
    { id: 'mua-ve', label: 'Mua vé' },
    { id: 'quan-ly', label: 'Quản lý đặt chỗ' },
    { id: 'lam-thu-tuc', label: 'Làm thủ tục' },
    { id: 'trang-thai', label: 'Trạng thái chuyến bay' },
    { id: 'lich-bay', label: 'Tra cứu lịch bay' },
  ];
  readonly activeTab = signal<BookingTabId>('mua-ve');

  /** Loai hanh trinh: chi "Mot chieu" thuc su tim kiem duoc (backend chi ho tro tim mot chieu). */
  readonly tripType = signal<TripType>('one-way');

  /** Lua chon cua nguoi dung o the tim kiem chuyen bay. */
  searchDepartureAirportId: number | null = null;
  searchArrivalAirportId: number | null = null;

  /** Khoang ngay di / ngay ve dang chon tren lich. */
  fromDate: NgbDate | null = null;
  toDate: NgbDate | null = null;
  showDatePicker = false;

  /** Dropdown tim kiem san bay dang mo ('departure' | 'arrival' | null). */
  openAirportDropdown: 'departure' | 'arrival' | null = null;
  departureSearchText = '';
  arrivalSearchText = '';

  private readonly airportService = inject(AirportService);

  departureAirportPreview(): IAirport | null {
    return this.airports.find(airport => airport.id === this.searchDepartureAirportId) ?? null;
  }

  arrivalAirportPreview(): IAirport | null {
    return this.airports.find(airport => airport.id === this.searchArrivalAirportId) ?? null;
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnInit(): void {
    this.loadAirports();
  }

  loadAirports(): void {
    this.airportService.query({ sort: ['code,asc'] }).subscribe({
      next: res => {
        this.airports = res.body ?? [];
        // Mac dinh chon san Hà Nội lam diem di neu nguoi dung chua chon gi.
        if (this.searchDepartureAirportId === null) {
          this.searchDepartureAirportId = this.airports.find(airport => airport.code === 'HAN')?.id ?? null;
        }
        this.cdr.detectChanges();
      },
    });
  }

  /** Danh sach san bay da loc theo tu khoa tim kiem trong dropdown "Tu" (loai san bay dang chon o "Den"). */
  filteredDepartureAirports(): IAirport[] {
    return this.filterAirports(this.departureSearchText, this.searchArrivalAirportId);
  }

  /** Danh sach san bay da loc theo tu khoa tim kiem trong dropdown "Den" (loai san bay dang chon o "Tu"). */
  filteredArrivalAirports(): IAirport[] {
    return this.filterAirports(this.arrivalSearchText, this.searchDepartureAirportId);
  }

  private filterAirports(keyword: string, excludeAirportId: number | null): IAirport[] {
    const q = keyword.trim().toLowerCase();
    return this.airports.filter(airport => {
      if (excludeAirportId !== null && airport.id === excludeAirportId) {
        return false;
      }
      if (!q) {
        return true;
      }
      return (
        (airport.code ?? '').toLowerCase().includes(q) ||
        (airport.name ?? '').toLowerCase().includes(q) ||
        (airport.city ?? '').toLowerCase().includes(q)
      );
    });
  }

  toggleAirportDropdown(which: 'departure' | 'arrival', event: MouseEvent): void {
    const opening = this.openAirportDropdown !== which;
    this.openAirportDropdown = opening ? which : null;
    if (opening) {
      const fieldEl = event.currentTarget as HTMLElement;
      // Doi Angular render xong dropdown ('@if') roi cuon 1 lan duy nhat cho muot.
      window.setTimeout(() => {
        const targetEl = fieldEl.querySelector<HTMLElement>('.airport-dropdown') ?? fieldEl;
        const targetBottom = targetEl.getBoundingClientRect().bottom;
        const extraMargin = 90;
        const delta = targetBottom - window.innerHeight + extraMargin;
        if (delta > 0) {
          window.scrollBy({ top: delta, behavior: 'smooth' });
        }
      }, 60);
    }
  }

  closeAirportDropdown(): void {
    this.openAirportDropdown = null;
  }

  /** Dong dropdown / lich khi nguoi dung click ra ngoai (click ben trong da duoc stopPropagation). */
  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeAirportDropdown();
    this.showDatePicker = false;
  }

  toggleDatePicker(event: MouseEvent): void {
    const opening = !this.showDatePicker;
    this.showDatePicker = opening;
    if (opening) {
      const fieldEl = event.currentTarget as HTMLElement;
      window.setTimeout(() => {
        const targetEl = fieldEl.querySelector<HTMLElement>('.date-range-dropdown') ?? fieldEl;
        const targetBottom = targetEl.getBoundingClientRect().bottom;
        const extraMargin = 90;
        const delta = targetBottom - window.innerHeight + extraMargin;
        if (delta > 0) {
          window.scrollBy({ top: delta, behavior: 'smooth' });
        }
      }, 60);
    }
  }

  /**
   * Chon ngay tren lich. "Mot chieu" va "Nhieu chang" chi can 1 ngay cho moi lan tim (nhieu chang
   * khong co khai niem "ngay ve" - moi chang tim rieng), chon xong dong lich luon. Rieng "Khu hoi"
   * lan 1 la ngay di, lan 2 la ngay ve (kieu date-range cua Vietnam Airlines).
   */
  onDateSelect(date: NgbDate): void {
    if (this.tripType() !== 'round-trip') {
      this.fromDate = date;
      this.toDate = null;
      this.showDatePicker = false;
      return;
    }
    if (!this.fromDate || (this.fromDate && this.toDate)) {
      this.fromDate = date;
      this.toDate = null;
    } else if (date.after(this.fromDate)) {
      this.toDate = date;
      this.showDatePicker = false;
    } else {
      this.toDate = this.fromDate;
      this.fromDate = date;
    }
  }

  /** Ngay bat dau / ket thuc (to dam). */
  isRange(date: NgbDate): boolean {
    return !!(date.equals(this.fromDate) || (this.toDate && date.equals(this.toDate)));
  }

  /** Cac ngay nam giua khoang da chon (to nhat, dang khoi vuong lien mach). */
  isInside(date: NgbDate): boolean {
    return !!(this.fromDate && this.toDate && date.after(this.fromDate) && date.before(this.toDate));
  }

  private formatDate(d: NgbDate | null): string {
    if (!d) {
      return '';
    }
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${pad(d.day)}/${pad(d.month)}/${d.year}`;
  }

  dateRangeLabel(): string {
    if (!this.fromDate) {
      return '';
    }
    return this.toDate ? `${this.formatDate(this.fromDate)} - ${this.formatDate(this.toDate)}` : this.formatDate(this.fromDate);
  }

  selectDepartureAirport(airportId: number): void {
    this.searchDepartureAirportId = airportId;
    this.departureSearchText = '';
    this.openAirportDropdown = null;
  }

  selectArrivalAirport(airportId: number): void {
    this.searchArrivalAirportId = airportId;
    this.arrivalSearchText = '';
    this.openAirportDropdown = null;
  }

  /** Bo chon diem di da chon truoc do (khong mo dropdown khi bam nut xoá). */
  clearDepartureAirport(event: MouseEvent): void {
    event.stopPropagation();
    this.searchDepartureAirportId = null;
  }

  /** Bo chon diem den da chon truoc do. */
  clearArrivalAirport(event: MouseEvent): void {
    event.stopPropagation();
    this.searchArrivalAirportId = null;
  }

  /** Bo chon ngay di / ngay ve da chon truoc do. */
  clearDates(event: MouseEvent): void {
    event.stopPropagation();
    this.fromDate = null;
    this.toDate = null;
  }

  selectTab(tabId: BookingTabId): void {
    if (tabId === 'lam-thu-tuc' || tabId === 'trang-thai' || tabId === 'lich-bay') {
      // Cac tab nay da duoc trien khai day du ben trang tim kiem chuyen bay (/events), khong lap lai UI o Home.
      void this.router.navigate(['/events'], { queryParams: { tab: tabId } });
      return;
    }

    this.activeTab.set(tabId);

    if (tabId === 'quan-ly' && !this.bookingLoaded && this.accountService.isAuthenticated()) {
      this.loadMyBookings();
    }
  }

  selectTripType(type: TripType): void {
    this.tripType.set(type);
    // Mot chieu chi can 1 ngay di, bo ngay ve neu truoc do dang o che do khu hoi.
    if (type === 'one-way') {
      this.toDate = null;
    }
  }

  /** Dieu huong sang trang danh sach chuyen bay, kem theo bo loc san bay di / den / ngay (neu co chon). Khoi tao lai hanh trinh moi (khu hoi/nhieu chang) tren TripBuilderService. */
  searchFlights(): void {
    this.searchErrorMessage = '';
    this.tripBuilder.reset();
    this.tripBuilder.setTripType(this.tripType());

    if (this.tripType() === 'round-trip') {
      if (!this.toDate) {
        this.searchErrorMessage = 'Vui lòng chọn cả ngày đi và ngày về cho vé khứ hồi.';
        return;
      }
      this.tripBuilder.setNextLegSearch({
        departureAirportId: this.searchArrivalAirportId,
        arrivalAirportId: this.searchDepartureAirportId,
        showtimeDate: this.toIsoDate(this.toDate),
      });
    }

    const queryParams: Record<string, number | string> = { tripType: this.tripType() };
    if (this.searchDepartureAirportId) {
      queryParams['departureAirportId'] = this.searchDepartureAirportId;
    }
    if (this.searchArrivalAirportId) {
      queryParams['arrivalAirportId'] = this.searchArrivalAirportId;
    }
    if (this.fromDate) {
      queryParams['showtimeDate'] = this.toIsoDate(this.fromDate);
    }
    this.router.navigate(['/events'], { queryParams });
  }

  /** NgbDate -> yyyy-MM-dd, dung lam tham so showtimeDate goi API tim kiem theo ngay. */
  private toIsoDate(d: NgbDate): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.year}-${pad(d.month)}-${pad(d.day)}`;
  }

  /** Hoan doi san bay di / den khi nguoi dung bam nut hoan doi tren the tim kiem. */
  swapAirports(): void {
    const temp = this.searchDepartureAirportId;
    this.searchDepartureAirportId = this.searchArrivalAirportId;
    this.searchArrivalAirportId = temp;
  }

  /** Tai toan bo ve cua nguoi dung dang dang nhap cho tab "Quan ly dat cho". */
  loadMyBookings(): void {
    this.bookingLoading = true;

    this.myTicketService.query({ page: 0, size: 100, sort: ['id,desc'] }).subscribe({
      next: res => {
        this.bookingTickets = res.body ?? [];
        this.bookingLoading = false;
        this.bookingLoaded = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.bookingLoading = false;
        this.bookingLoaded = true;
        this.cdr.detectChanges();
      },
    });
  }

  /** Danh sach ve da loc theo mã vé (neu co nhap), khong bat buoc. */
  get filteredBookingTickets(): IMyTicket[] {
    const code = this.bookingSearchCode.trim().toLowerCase();
    if (!code) {
      return this.bookingTickets;
    }
    return this.bookingTickets.filter(
      ticket => (ticket.qrCode ?? '').toLowerCase().includes(code) || String(ticket.bookingId ?? '') === code,
    );
  }
}
