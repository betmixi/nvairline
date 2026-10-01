import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { environment } from 'environments/environment';

import { AccountService } from 'app/core/auth/account.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { TranslateDirective } from 'app/shared/language';

/**
 * Sidebar dieu huong chinh cua ung dung (thay the top navbar CGV truoc day), phong theo
 * bo cuc sidebar trai cua vietnamairlines.com: logo + menu doc + khoi tai khoan/Lotusmiles
 * o cuoi sidebar. Component van ten la "Navbar" / selector "jhi-navbar" de giam rui ro thay
 * doi outlet dat ten 'navbar' trong app.routes.ts.
 */
@Component({
  selector: 'jhi-navbar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  host: {
    '[class.is-collapsed]': 'isSidebarCollapsed()',
  },
  imports: [RouterLink, RouterLinkActive, FontAwesomeModule, NgbCollapse, HasAnyAuthorityDirective, TranslateDirective],
})
export default class Navbar implements OnInit {
  readonly inProduction = signal(true);
  /** true khi sidebar mobile dang thu gon (an menu), dieu khien boi nut hamburger tren man hinh nho. */
  readonly isNavbarCollapsed = signal(true);
  /** true khi sidebar desktop o trang thai thu nho chi con icon (nguoi dung bam mui ten thu gon). */
  readonly isSidebarCollapsed = signal(false);
  /** true khi khoi "tai khoan" (Ve cua toi/Chuyen bay da luu/Settings/Password/Sign out) dang mo rong. */
  readonly isAccountMenuOpen = signal(false);
  readonly openAPIEnabled = signal(false);
  readonly version: string;
  private readonly accountService = inject(AccountService);
  readonly account = this.accountService.account;

  private readonly loginService = inject(LoginService);
  private readonly profileService = inject(ProfileService);
  protected readonly router = inject(Router);

  constructor() {
    const { VERSION } = environment;
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    } else {
      this.version = '';
    }
  }
  goHome(): void {
    this.collapseNavbar();
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      void this.router.navigate(['/admin/dashboard']);
      return;
    }

    void this.router.navigate(['/']);
  }
  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction.set(profileInfo.inProduction ?? true);
      this.openAPIEnabled.set(profileInfo.openAPIEnabled ?? false);
    });
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed.update(collapsed => !collapsed);
  }

  toggleAccountMenu(): void {
    this.isAccountMenuOpen.update(open => !open);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    if (!globalThis.confirm('Bạn có chắc chắn muốn đăng xuất không?')) {
      return;
    }
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }
}
