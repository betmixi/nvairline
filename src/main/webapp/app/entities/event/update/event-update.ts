import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject, signal } from '@angular/core';
import { AsyncValidatorFn, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, catchError, finalize, map, of, switchMap, tap, timer } from 'rxjs';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { UploadService } from 'app/core/util/upload.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { IAirport } from 'app/entities/airport/airport.model';
import { AirportService } from 'app/entities/airport/service/airport.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';
import { FormsModule } from '@angular/forms';
import { EventFormGroup, EventFormService } from './event-form.service';
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-event-update',
  templateUrl: './event-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, FormsModule, RouterLink],
})
export class EventUpdate implements OnInit {
  readonly isSaving = signal(false);
  event: IEvent | null = null;

  categoriesSharedCollection = signal<ICategory[]>([]);
  addressesSharedCollection = signal<IAddress[]>([]);
  airportsSharedCollection = signal<IAirport[]>([]);

  readonly isUploadingBanner = signal(false);
  readonly bannerUploadError = signal<string | null>(null);

  protected dataUtils = inject(DataUtils);
  protected uploadService = inject(UploadService);
  protected eventManager = inject(EventManager);
  protected eventService = inject(EventService);
  protected eventFormService = inject(EventFormService);
  protected categoryService = inject(CategoryService);
  protected addressService = inject(AddressService);
  protected airportService = inject(AirportService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  compareAddress = (o1: IAddress | null, o2: IAddress | null): boolean => this.addressService.compareAddress(o1, o2);

  compareAirport = (o1: IAirport | null, o2: IAirport | null): boolean => this.airportService.compareAirport(o1, o2);

  ngOnInit(): void {
    this.editForm.controls.title.addAsyncValidators(this.titleUniqueValidator());

    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;

      if (event) {
        this.updateForm(event);
      }

      this.loadRelationshipsOptions();
    });
  }
  /** Bao loi neu so hieu chuyen bay da ton tai (khong phan biet hoa thuong), bo qua chinh chuyen bay dang sua. */
  private titleUniqueValidator(): AsyncValidatorFn {
    return control => {
      const value = ((control.value as string | null) ?? '').trim();
      if (!value) {
        return of(null);
      }
      return timer(400).pipe(
        switchMap(() => this.eventService.query({ 'title.contains': value, size: 50 })),
        map(res =>
          (res.body ?? []).some(e => e.id !== this.event?.id && (e.title ?? '').trim().toLowerCase() === value.toLowerCase())
            ? { titleExists: true }
            : null,
        ),
        catchError(() => of(null)),
        tap(() => this.cdr.markForCheck()),
      );
    };
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('dugxApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  /** Nguoi dung chon anh banner tu may: tai len server roi gan URL tra ve vao form. */
  onBannerFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.bannerUploadError.set(null);

    if (!file.type.startsWith('image/')) {
      this.bannerUploadError.set('Vui lòng chọn một tệp ảnh (JPG, PNG, GIF, WEBP).');
      input.value = '';
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      this.bannerUploadError.set('Ảnh không được lớn hơn 5MB.');
      input.value = '';
      return;
    }

    this.isUploadingBanner.set(true);

    this.uploadService.uploadImage(file).subscribe({
      next: url => {
        this.editForm.patchValue({ banner: url });
        this.isUploadingBanner.set(false);
      },
      error: () => {
        this.bannerUploadError.set('Tải ảnh lên thất bại, vui lòng thử lại.');
        this.isUploadingBanner.set(false);
        input.value = '';
      },
    });
  }

  save(): void {
    this.isSaving.set(true);
    const event = this.eventFormService.getEvent(this.editForm);
    if (event.id === null) {
      this.subscribeToSaveResponse(this.eventService.create(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.update(event));
    }
  }
  previousState(): void {
    this.navigateAfterSave();
  }

  private navigateAfterSave(): void {
    this.router.navigate(['/event']);
  }
  protected subscribeToSaveResponse(result: Observable<IEvent>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => {
        this.navigateAfterSave();
      },
      error: () => this.onSaveError(),
    });
  }

  protected onSaveError(): void {
    // Cuon len thong bao loi (jhi-alert-error) de nguoi dung nhin thay ly do luu that bai (trung, de trong, so am...).
    setTimeout(() => document.querySelector('jhi-alert-error')?.scrollIntoView({ behavior: 'smooth', block: 'center' }), 100);
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(event: IEvent): void {
    this.event = event;
    this.eventFormService.resetForm(this.editForm, event);

    this.categoriesSharedCollection.update(categories =>
      this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, event.category),
    );
    this.addressesSharedCollection.update(addresses =>
      this.addressService.addAddressToCollectionIfMissing<IAddress>(addresses, event.address),
    );
    this.airportsSharedCollection.update(airports =>
      this.airportService.addAirportToCollectionIfMissing<IAirport>(airports, event.departureAirport, event.arrivalAirport),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.event?.category),
        ),
      )
      .subscribe((categories: ICategory[]) => this.categoriesSharedCollection.set(categories));

    this.addressService
      .query()
      .pipe(map((res: HttpResponse<IAddress[]>) => res.body ?? []))
      .pipe(map((addresses: IAddress[]) => this.addressService.addAddressToCollectionIfMissing<IAddress>(addresses, this.event?.address)))
      .subscribe((addresses: IAddress[]) => this.addressesSharedCollection.set(addresses));

    this.airportService
      .query()
      .pipe(map((res: HttpResponse<IAirport[]>) => res.body ?? []))
      .pipe(
        map((airports: IAirport[]) =>
          this.airportService.addAirportToCollectionIfMissing<IAirport>(airports, this.event?.departureAirport, this.event?.arrivalAirport),
        ),
      )
      .subscribe((airports: IAirport[]) => this.airportsSharedCollection.set(airports));
  }
}
