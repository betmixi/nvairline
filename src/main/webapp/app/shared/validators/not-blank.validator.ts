import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Khong cho phep chuoi chi gom khoang trang (khi co nhap nhung toan dau cach). */
export const notBlank: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value = control.value;
  return typeof value === 'string' && value.length > 0 && value.trim().length === 0 ? { blank: true } : null;
};
