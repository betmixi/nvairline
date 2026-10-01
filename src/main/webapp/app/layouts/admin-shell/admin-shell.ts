import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

interface AdminNavLink {
  path: string;
  icon: string;
  label: string;
  /** Mac dinh chi ADMIN thay duoc; dat 'staff' de ROLE_STAFF cung thay. */
  visibility?: 'staff';
}

/**
 * Thanh dieu huong ngang dung chung cho toan bo khu vuc quan tri (moi trang duoi
 * /admin/* va toan bo trang CRUD sinh tu JHipster nhu /event, /coupon, /booking...).
 *
 * Duoc render boi layouts/main/main.html (khong phai boi router) khi URL hien tai
 * thuoc khu vuc admin - xem Main.isAdminRoute(). Chon cach nay thay vi bao mot
 * component quanh <router-outlet> vi cach do gay loi NG0203 (injection context)
 * voi NgbModal o cac trang CRUD dung modal xoa.
 */
@Component({
  selector: 'jhi-admin-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.scss',
  imports: [RouterLink, RouterLinkActive, HasAnyAuthorityDirective],
})
export default class AdminShellComponent {
  readonly navLinks: AdminNavLink[] = [
    { path: '/admin/dashboard', icon: '🏠', label: 'Tổng quan' },
    { path: '/event', icon: '✈️', label: 'Chuyến bay' },
    { path: '/admin/customer-bookings', icon: '🧾', label: 'Quản lý đặt vé và thống kê' },
    { path: '/coupon', icon: '🏷️', label: 'Ưu đãi' },
    { path: '/admin/reviews', icon: '⭐', label: 'Đánh giá' },
    { path: '/admin/support-requests', icon: '💬', label: 'Hỗ trợ' },
    { path: '/user-management', icon: '👥', label: 'Người dùng và phân quyền' },
    { path: '/airport', icon: '🛬', label: 'Sân bay' },
    { path: '/aircraft', icon: '🛩️', label: 'Máy bay và ghế' },
  ];
}
