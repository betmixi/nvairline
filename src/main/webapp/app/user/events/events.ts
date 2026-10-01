import { Component, ChangeDetectorRef, HostListener, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgbDate, NgbDatepicker } from '@ng-bootstrap/ng-bootstrap';
import dayjs from 'dayjs/esm';
import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';
import { IShowtime } from 'app/entities/showtime/showtime.model';
import { IAirport } from 'app/entities/airport/airport.model';
import { AirportService } from 'app/entities/airport/service/airport.service';
import { AccountService } from 'app/core/auth/account.service';
import { IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';
import { AdminCheckInService } from 'app/admin/check-in/check-in.service';
import { TripBuilderService } from 'app/booking/trip-builder.service';

type BookingTabId = 'mua-ve' | 'quan-ly' | 'lam-thu-tuc' | 'trang-thai' | 'lich-bay';
type TripType = 'round-trip' | 'one-way' | 'multi-city';

interface BookingTab {
  id: BookingTabId;
  label: string;
}

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, NgbDatepicker, RouterLink],
  templateUrl: './events.html',
  styleUrls: ['./events.scss'],
})
export class EventsComponent implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly airportService = inject(AirportService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly accountService = inject(AccountService);
  private readonly myTicketService = inject(MyTicketService);
  private readonly checkInService = inject(AdminCheckInService);
  private readonly tripBuilder = inject(TripBuilderService);

  /** Loi hien thi tren the tim kiem (vd chua chon du ngay di/ve cho ve khu hoi). */
  searchErrorMessage = '';

  /** So chang bay da chon trong hanh trinh khu hoi/nhieu chang dang xay dung do (0 = khong dang giua chung). */
  get tripBuilderLegCount(): number {
    return this.tripBuilder.legs().length;
  }

  /**
   * "Quản lý đặt chỗ": hiện sẵn toàn bộ vé của người dùng đang đăng nhập
   * (không cần nhập lại mã vé/họ để "xác minh" vì đã đăng nhập rồi).
   * Mã vé chỉ dùng để lọc bớt khi có nhiều vé.
   */
  bookingSearchCode = '';
  bookingTickets: IMyTicket[] = [];
  bookingLoading = false;
  bookingLoaded = false;

  /** Ve cua nguoi dung du dieu kien lam thu tuc (da thanh toan, chua check-in). */
  checkinTickets: IMyTicket[] = [];
  checkinLoading = false;
  checkinLoaded = false;
  confirmingTicketId: number | null = null;

  events: IEvent[] = [];
  filteredEvents: IEvent[] = [];
  isLoading = false;
  searchText = '';

  /** Phan trang danh sach ket qua (client-side, vi filteredEvents da tai het san). */
  readonly pageSize = 5;
  currentPage = 1;

  /** Danh sach san bay dung cho 2 truong "Tu" / "Den" tren the tim kiem (giong het trang chu). */
  airports: IAirport[] = [];

  /** Bo loc tuyen bay (san bay di / den) doc tu query param, dung de goi API. */
  departureAirportId: number | null = null;
  arrivalAirportId: number | null = null;

  /** Ngay bay (yyyy-MM-dd) doc tu query param, dung de goi API loc theo showtimeDate. */
  searchShowtimeDate: string | null = null;

  /** Cac tab kieu dat cho (chi "Mua ve" la thuc su hoat dong, con lai la placeholder de giong giao dien tham chieu). */
  readonly bookingTabs: BookingTab[] = [
    { id: 'mua-ve', label: 'Mua vé' },
    { id: 'trang-thai', label: 'Trạng thái chuyến bay' },
    { id: 'lich-bay', label: 'Tra cứu lịch bay' },
  ];
  readonly activeTab = signal<BookingTabId>('mua-ve');

  /** Loai hanh trinh: chi "Mot chieu" thuc su tim kiem duoc (backend chi ho tro tim mot chieu). */
  readonly tripType = signal<TripType>('one-way');

  /** Lua chon cua nguoi dung o the tim kiem, dong bo lai voi query param moi khi tim kiem. */
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

  departureAirportPreview(): IAirport | null {
    return this.airports.find(airport => airport.id === this.searchDepartureAirportId) ?? null;
  }

  arrivalAirportPreview(): IAirport | null {
    return this.airports.find(airport => airport.id === this.searchArrivalAirportId) ?? null;
  }

  ngOnInit(): void {
    this.loadAirports();
    this.activatedRoute.queryParamMap.subscribe(params => {
      const departure = params.get('departureAirportId');
      const arrival = params.get('arrivalAirportId');
      const showtimeDate = params.get('showtimeDate');
      const tab = params.get('tab');
      const tripType = params.get('tripType');
      this.departureAirportId = departure ? Number(departure) : null;
      this.arrivalAirportId = arrival ? Number(arrival) : null;
      this.searchDepartureAirportId = this.departureAirportId;
      this.searchArrivalAirportId = this.arrivalAirportId;
      this.searchShowtimeDate = showtimeDate;
      this.fromDate = showtimeDate ? this.fromIsoDate(showtimeDate) : null;
      // Chi cap nhat khi URL co chi dinh ro - neu khong giu nguyen gia tri hien tai (vd luc tu dong
      // chuyen sang tim chang ve cho khu hoi, URL khong kem tripType nen phai giu nguyen "round-trip").
      if (tripType === 'one-way' || tripType === 'round-trip' || tripType === 'multi-city') {
        this.tripType.set(tripType);
      }
      if (tab === 'trang-thai' || tab === 'lich-bay') {
        this.selectTab(tab);
      }
      this.loadEvents();
    });
  }

  /** yyyy-MM-dd -> NgbDate, dung de khoi phuc lich da chon khi quay lai trang tim kiem. */
  private fromIsoDate(iso: string): NgbDate | null {
    const [year, month, day] = iso.split('-').map(Number);
    return year && month && day ? NgbDate.from({ year, month, day }) : null;
  }

  /** NgbDate -> yyyy-MM-dd, dung lam tham so showtimeDate goi API tim kiem theo ngay. */
  private toIsoDate(d: NgbDate): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.year}-${pad(d.month)}-${pad(d.day)}`;
  }

  loadAirports(): void {
    this.airportService.query({ sort: ['code,asc'] }).subscribe({
      next: res => {
        this.airports = res.body ?? [];
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

  /** Chi danh dau dung 2 ngay da chon (di / ve), khong to mau khoang o giua. */
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

  /** Huy hanh trinh khu hoi/nhieu chang dang xay dung do (vd nguoi dung doi y, muon tim lai tu dau). */
  cancelTripInProgress(): void {
    this.tripBuilder.reset();
    this.searchErrorMessage = '';
  }

  loadEvents(): void {
    this.isLoading = true;
    this.eventService
      .queryPublic({
        page: 0,
        size: 100,
        sort: ['createdDate,desc'],
        departureAirportId: this.departureAirportId,
        arrivalAirportId: this.arrivalAirportId,
        showtimeDate: this.searchShowtimeDate,
        tripType: this.tripType(),
      })
      .subscribe({
        next: res => {
          // Bo cac su kien khong con suat chieu nao trong tuong lai (da het gio bay het),
          // tranh hien thi "chuyen bay" khong the dat duoc nua trong ket qua tim kiem.
          this.events = (res.body ?? []).filter(event => this.upcomingShowtimes(event).length > 0);
          this.filteredEvents = [...this.events];
          this.currentPage = 1;
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  searchEvents(): void {
    const keyword = this.searchText.trim().toLowerCase();
    if (!keyword) {
      this.filteredEvents = [...this.events];
    } else {
      this.filteredEvents = this.events.filter(
        event =>
          (event.title ?? '').toLowerCase().includes(keyword) ||
          (event.category?.name ?? '').toLowerCase().includes(keyword) ||
          (event.address?.location ?? '').toLowerCase().includes(keyword),
      );
    }
    this.currentPage = 1;
  }

  clearSearch(): void {
    this.searchText = '';
    this.filteredEvents = [...this.events];
    this.currentPage = 1;
  }

  trackById(index: number, item: IEvent): number {
    return item.id;
  }

  /** So trang hien co, luon it nhat 1 de khong bi chia cho 0. */
  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredEvents.length / this.pageSize));
  }

  /** Danh sach so trang hien thi (1-based), dung cho cac nut trang o cuoi ket qua. */
  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  /** Chi cac chuyen bay thuoc trang dang xem. */
  get pagedEvents(): IEvent[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredEvents.slice(start, start + this.pageSize);
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages || page === this.currentPage) {
      return;
    }
    this.currentPage = page;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  goToEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }

  /**
   * Bam vao 1 the ket qua tim kiem (hoac 1 muc gia tren the): sang trang chi tiet su kien
   * kem theo showtimeId cua suat chieu dang hien thi tren the, de trang chi tiet dung ngay
   * gio bay/gia do khi xac nhan mua - khong bat nguoi dung chon lai gio bay lan nua.
   */
  goToEventWithShowtime(event: IEvent): void {
    const showtime = this.nearestShowtime(event);
    this.router.navigate(['/events', event.id], showtime ? { queryParams: { showtimeId: showtime.id } } : undefined);
  }

  /** Cac suat chieu con ve trong tuong lai cua mot event (dung de loc bo su kien da het gio bay). */
  private upcomingShowtimes(event: IEvent): IShowtime[] {
    const now = dayjs();
    return (event.showtimes ?? []).filter(showtime => dayjs(showtime.startTime as never).isAfter(now));
  }

  /** Suat chieu gan nhat CON KHA DUNG (chua khoi hanh) cua event, dung de hien thi gio bay + gia ve tren flight-card. */
  nearestShowtime(event: IEvent): IShowtime | null {
    const upcoming = this.upcomingShowtimes(event);
    if (upcoming.length === 0) {
      return null;
    }
    return upcoming.reduce((earliest, current) =>
      dayjs(current.startTime).valueOf() < dayjs(earliest.startTime).valueOf() ? current : earliest,
    );
  }

  /**
   * showtime.startTime/endTime lay tu event.showtimes[] chua duoc EventService convert sang dayjs
   * (chi startTime/endTime cua chinh Event moi duoc convert), nen luon boc dayjs() truoc khi format
   * de tranh loi ".format is not a function".
   */
  formatShowtimeTime(value: unknown): string {
    if (!value) {
      return '--:--';
    }
    const d = dayjs(value as never);
    return d.isValid() ? d.format('HH:mm') : '--:--';
  }

  /** Ngay bay cua 1 showtime, dang dd/mm/yyyy, dung chung logic voi formatShowtimeTime. */
  formatShowtimeDate(value: unknown): string {
    if (!value) {
      return '';
    }
    const d = dayjs(value as never);
    return d.isValid() ? d.format('DD/MM/YYYY') : '';
  }

  /** Nhan loai hanh trinh hien tai, hien thi thay cho "Bay thang" tren the ket qua tim kiem. */
  tripTypeLabel(): string {
    switch (this.tripType()) {
      case 'round-trip':
        return 'Khứ hồi';
      case 'multi-city':
        return 'Nhiều chặng';
      default:
        return 'Một chiều';
    }
  }

  /** Thoi luong bay: hieu giua endTime va startTime, dang "Xh Yphut". */
  duration(showtime: IShowtime | null): string {
    if (!showtime?.startTime || !showtime.endTime) {
      return '';
    }
    const start = dayjs(showtime.startTime as never);
    const end = dayjs(showtime.endTime as never);
    const minutes = end.diff(start, 'minute');
    if (minutes <= 0) {
      return '';
    }
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h${m > 0 ? ` ${m}phút` : ''}` : `${m}phút`;
  }

  selectTab(tabId: BookingTabId): void {
    this.activeTab.set(tabId);

    if (tabId === 'quan-ly' && !this.bookingLoaded && this.accountService.isAuthenticated()) {
      this.loadMyBookings();
    }

    if (tabId === 'lam-thu-tuc' && !this.checkinLoaded && this.accountService.isAuthenticated()) {
      this.loadCheckinEligibleTickets();
    }
  }

  selectTripType(type: TripType): void {
    this.tripType.set(type);
    // Mot chieu chi can 1 ngay di, bo ngay ve neu truoc do dang o che do khu hoi.
    if (type === 'one-way') {
      this.toDate = null;
    }
    // Neu da co ket qua tim kiem tren trang (da chon san bay truoc do), tai lai ngay voi
    // loai hanh trinh moi - tranh truong hop bam doi tab nhung danh sach ben duoi khong
    // doi vi phai bam "Tim chuyen bay" lai moi goi API.
    if (this.departureAirportId !== null || this.arrivalAirportId !== null) {
      this.loadEvents();
    }
  }

  /** Hoan doi san bay di / den dang chon tren thanh tim kiem. */
  swapAirports(): void {
    const temp = this.searchDepartureAirportId;
    this.searchDepartureAirportId = this.searchArrivalAirportId;
    this.searchArrivalAirportId = temp;
  }

  /** Ap dung bo loc san bay di / den dang chon: cap nhat query param ngay tai trang, kich hoat tai lai danh sach. */
  applyAirportFilter(): void {
    this.searchErrorMessage = '';

    // Chi khoi tao lai hanh trinh khi day la mot lan tim kiem MOI (chua co chang nao duoc chon).
    // Neu dang giua chung mot chuyen khu hoi/nhieu chang (dang tim chang tiep theo), giu nguyen tripBuilder.
    if (this.tripBuilder.legs().length === 0) {
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
    }

    const queryParams: Record<string, number | string | null> = {
      departureAirportId: this.searchDepartureAirportId,
      arrivalAirportId: this.searchArrivalAirportId,
      showtimeDate: this.fromDate ? this.toIsoDate(this.fromDate) : null,
      tripType: this.tripType(),
    };
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams,
      queryParamsHandling: 'merge',
    });
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

  /** Tai ve du dieu kien lam thu tuc: da thanh toan va chua check-in. */
  loadCheckinEligibleTickets(): void {
    this.checkinLoading = true;

    this.myTicketService.query({ page: 0, size: 100, sort: ['id,desc'] }).subscribe({
      next: res => {
        const tickets = res.body ?? [];
        this.checkinTickets = tickets.filter(t => t.bookingStatus === 'PAID' && !t.checkedIn);
        this.checkinLoading = false;
        this.checkinLoaded = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.checkinLoading = false;
        this.checkinLoaded = true;
        this.cdr.detectChanges();
      },
    });
  }

  /** Khach tu lam thu tuc cho ve cua chinh minh (chi cho phep voi ve minh so huu - kiem tra o backend). */
  confirmSelfCheckIn(ticket: IMyTicket): void {
    if (this.confirmingTicketId !== null) {
      return;
    }

    this.confirmingTicketId = ticket.id;

    this.checkInService.confirmCheckIn(ticket.id).subscribe({
      next: () => {
        this.checkinTickets = this.checkinTickets.filter(t => t.id !== ticket.id);
        this.confirmingTicketId = null;
        this.cdr.detectChanges();
      },
      error: () => {
        this.confirmingTicketId = null;
        this.cdr.detectChanges();
      },
    });
  }

  /** Trang thai chuyen bay suy ra tu gio khoi hanh/ket thuc cua showtime - khong co nguon du lieu van hanh thuc te. */
  flightStatus(showtime: IShowtime | null): { label: string; cls: string } {
    if (!showtime?.startTime) {
      return { label: 'Không rõ', cls: 'status-unknown' };
    }
    const now = dayjs();
    const start = dayjs(showtime.startTime as never);
    const end = showtime.endTime ? dayjs(showtime.endTime as never) : null;

    if (now.isBefore(start)) {
      return { label: 'Chưa khởi hành', cls: 'status-scheduled' };
    }
    if (end && now.isAfter(end)) {
      return { label: 'Đã hoàn thành', cls: 'status-completed' };
    }
    return { label: 'Đang bay', cls: 'status-inflight' };
  }
}
