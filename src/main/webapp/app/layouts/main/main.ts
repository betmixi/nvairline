import { ChangeDetectionStrategy, Component, DOCUMENT, OnInit, Renderer2, RendererFactory2, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';

import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { filter, map } from 'rxjs';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { AccountService } from 'app/core/auth/account.service';
import AdminShellComponent from 'app/layouts/admin-shell/admin-shell';
import PageRibbon from '../profiles/page-ribbon';

@Component({
  selector: 'jhi-main',
  changeDetection: ChangeDetectionStrategy.Default,
  templateUrl: './main.html',
  styleUrl: './main.scss',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, PageRibbon, AdminShellComponent],
})
export default class Main implements OnInit {
  private readonly router = inject(Router);

  /** Cac tien to duong dan cua toan bo trang quan tri (admin/staff): dashboard tuy bien
   * lan cac trang CRUD sinh tu JHipster. Duoc rendered full-bleed de AdminShellComponent
   * toan quyen kiem soat bo cuc/mau nen thay vi bi nhet trong khung .jh-card mac dinh. */
  private static readonly ADMIN_URL_PREFIXES = [
    'admin',
    'authority',
    'event',
    'category',
    'venue',
    'aircraft',
    'airport',
    'promotion',
    'seat',
    'booking',
    'booking-detail',
    'payment',
    'review',
    'favorite',
    'notification',
    'event-image',
    'coupon',
    'report',
    'check-in',
    'audit-log',
    'address',
    'ticket',
    'user-management',
  ];

  private static isAdminUrl(url: string): boolean {
    return Main.ADMIN_URL_PREFIXES.some(prefix => new RegExp(`^/${prefix}(/|\\?|$)`).test(url));
  }

  private static isFullBleedUrl(url: string): boolean {
    return url === '/' || url === '/events' || url.startsWith('/events?') || Main.isAdminUrl(url);
  }

  readonly isAdminRoute = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => Main.isAdminUrl(this.router.url)),
    ),
    { initialValue: Main.isAdminUrl(this.router.url) },
  );

  /** Cac trang xac thuc tu thiet ke full-screen rieng: an sidebar va khung .jh-card cua layout chung. */
  private static isAuthUrl(url: string): boolean {
    return (
      url === '/login' || url.startsWith('/account/register') || url.startsWith('/account/reset') || url.startsWith('/account/activate')
    );
  }

  readonly isFullBleedRoute = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => Main.isFullBleedUrl(this.router.url)),
    ),
    { initialValue: Main.isFullBleedUrl(this.router.url) },
  );

  readonly isAuthRoute = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => Main.isAuthUrl(this.router.url)),
    ),
    { initialValue: Main.isAuthUrl(this.router.url) },
  );

  private readonly renderer: Renderer2;
  private readonly htmlElement: HTMLElement;

  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly document = inject(DOCUMENT);
  private readonly translateService = inject(TranslateService);
  private readonly rootRenderer = inject(RendererFactory2);

  constructor() {
    this.htmlElement = this.document.documentElement;
    this.renderer = this.rootRenderer.createRenderer(this.htmlElement, null);

    // Bat/tat class tren <body> de content/scss/admin-theme.scss tao kieu lai cac
    // thanh phan Bootstrap mac dinh (bang, nut, modal...) trong khu vuc quan tri -
    // dung <body> (khong phai mot div bao ngoai) vi modal cua ng-bootstrap duoc gan
    // thang vao cuoi <body>, nam ngoai cay component cua trang.
    effect(() => {
      this.document.body.classList.toggle('admin-theme-active', this.isAdminRoute());
    });
  }

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(this.htmlElement, 'lang', langChangeEvent.lang);
    });
  }
}
