import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { tap } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { LoginService } from 'app/login/login.service';

export const authExpiredInterceptor: HttpInterceptorFn = (req, next) => {
  const accountService = inject(AccountService);
  const loginService = inject(LoginService);
  const stateStorageService = inject(StateStorageService);
  const router = inject(Router);

  return next(req).pipe(
    tap({
      error(err: HttpErrorResponse) {
        // Chi ep dang nhap lai neu nguoi dung DA dang nhap va phien het han.
        // Khach vang lai (chua dang nhap) gap 401 o cac API cong khai thi khong dieu huong sang /login,
        // de ho van xem duoc trang chu / danh sach su kien binh thuong.
        if (err.status === 401 && err.url && !err.url.includes('api/account') && accountService.isAuthenticated()) {
          stateStorageService.storeUrl(router.routerState.snapshot.url);
          loginService.logout();
          router.navigate(['/login']);
        }
      },
    }),
  );
};
