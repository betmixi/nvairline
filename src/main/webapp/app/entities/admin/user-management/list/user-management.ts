import { DatePipe } from '@angular/common';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { combineLatest } from 'rxjs';

import { SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, SortState, sortStateSignal } from 'app/shared/sort';
import { AuthorityService } from '../../authority/service/authority.service';
import { UserManagementDeleteDialog } from '../delete/user-management-delete-dialog';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';

@Component({
  selector: 'jhi-user-mgmt',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './user-management.html',
  styleUrl: './user-management.component.scss',

  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    NgbPagination,
    TranslateDirective,
    SortDirective,
    SortByDirective,
    ItemCount,
    DatePipe,
  ],
})
export class UserManagement implements OnInit {
  readonly currentAccount = inject(AccountService).account;
  readonly users = signal<IUserManagement[] | null>(null);
  readonly isLoading = signal(false);
  readonly totalItems = signal(0);
  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly page = signal(0);
  sortState = sortStateSignal({});

  /** Tim nhanh theo ten dang nhap, email, ho ten (loc tren trang dang tai, giong pattern event/coupon). */
  keyword = '';

  /** Luu login cua user dang ghi phan quyen, de khoa nut va tranh bam trung trong luc cho response. */
  readonly savingAuthorityFor = signal<string | null>(null);

  private readonly authorityService = inject(AuthorityService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly allAuthorities = computed(() => this.authorityService.authorities().map(authority => authority.name));
  private readonly userService = inject(UserManagementService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sortService = inject(SortService);
  private readonly modalService = inject(NgbModal);

  constructor() {
    this.authorityService.authoritiesParams.set({});
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  get filteredUsers(): IUserManagement[] {
    const kw = this.keyword.trim().toLowerCase();
    const list = this.users() ?? [];
    if (!kw) {
      return list;
    }
    return list.filter(user =>
      [user.login, user.email, user.firstName, user.lastName].filter(Boolean).join(' ').toLowerCase().includes(kw),
    );
  }

  /** Danh sach role hien thi tren tung dong: tai khoan ADMIN chi hien badge ADMIN co dinh, cac user khac chi duoc chon USER/STAFF. */
  rowAuthorities(user: IUserManagement): string[] {
    if (user.authorities?.includes('ROLE_ADMIN')) {
      return ['ROLE_ADMIN'];
    }
    return this.allAuthorities().filter(authority => authority !== 'ROLE_ADMIN');
  }

  /** Bat/tat 1 role ngay tren danh sach, khong can vao trang Edit rieng. */
  toggleAuthority(user: IUserManagement, authorityName: string): void {
    // Role ADMIN la co dinh, khong duoc gan/go qua UI nay.
    if (authorityName === 'ROLE_ADMIN') {
      return;
    }

    const current = user.authorities ?? [];
    const hasIt = current.includes(authorityName);

    // Moi user phai con it nhat 1 role.
    if (hasIt && current.length <= 1) {
      return;
    }
    if (this.savingAuthorityFor()) {
      return;
    }

    const authorities = hasIt ? current.filter(a => a !== authorityName) : [...current, authorityName];

    this.savingAuthorityFor.set(user.login);
    this.userService.update({ ...user, authorities }).subscribe({
      next: () => {
        this.users.update(list => (list ?? []).map(u => (u.login === user.login ? { ...u, authorities } : u)));
        this.savingAuthorityFor.set(null);
      },
      error: () => this.savingAuthorityFor.set(null),
    });
  }

  setActive(userManagement: IUserManagement, isActivated: boolean): void {
    this.userService.update({ ...userManagement, activated: isActivated }).subscribe(() => this.loadAll());
  }

  trackIdentity(item: IUserManagement): number {
    return item.id!;
  }

  deleteUser(userManagement: IUserManagement): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userManagement = userManagement;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  loadAll(): void {
    this.isLoading.set(true);
    this.userService
      .query({
        page: this.page() - 1,
        size: this.itemsPerPage(),
        sort: this.sortService.buildSortParam(this.sortState(), 'id'),
      })
      .subscribe({
        next: (res: HttpResponse<IUserManagement[]>) => {
          this.isLoading.set(false);
          this.onSuccess(res.body, res.headers);
        },
        error: () => this.isLoading.set(false),
      });
  }

  transition(sortState?: SortState): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page(),
        sort: this.sortService.buildSortParam(sortState ?? this.sortState()),
      },
    });
  }

  private handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get(PAGE_HEADER);
      this.page.set(+(page ?? 1));
      this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data.defaultSort));
      this.loadAll();
    });
  }

  private onSuccess(users: IUserManagement[] | null, headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
    this.users.set(users);
  }
}
