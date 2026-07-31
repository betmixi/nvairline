import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';
import { forkJoin } from 'rxjs';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { UploadService } from 'app/core/util/upload.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IOrganizer } from 'app/entities/organizer/organizer.model';
import { OrganizerService } from 'app/entities/organizer/service/organizer.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';
import { FormsModule } from '@angular/forms';
import { EventFormGroup, EventFormService } from './event-form.service';
import { TicketTypeService } from 'app/entities/ticket-type/service/ticket-type.service';
import { NewTicketType } from 'app/entities/ticket-type/ticket-type.model';
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-event-update',
  templateUrl: './event-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, FormsModule],
})
export class EventUpdate implements OnInit {
  readonly isSaving = signal(false);
  event: IEvent | null = null;
  tickets: any[] = [];
  private originalTicketIds: number[] = [];

  categoriesSharedCollection = signal<ICategory[]>([]);
  addressesSharedCollection = signal<IAddress[]>([]);
  organizersSharedCollection = signal<IOrganizer[]>([]);

  readonly isUploadingBanner = signal(false);
  readonly bannerUploadError = signal<string | null>(null);

  protected dataUtils = inject(DataUtils);
  protected uploadService = inject(UploadService);
  protected eventManager = inject(EventManager);
  protected eventService = inject(EventService);
  protected eventFormService = inject(EventFormService);
  protected categoryService = inject(CategoryService);
  protected addressService = inject(AddressService);
  protected organizerService = inject(OrganizerService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected accountService = inject(AccountService);
  protected ticketTypeService = inject(TicketTypeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  compareAddress = (o1: IAddress | null, o2: IAddress | null): boolean => this.addressService.compareAddress(o1, o2);

  compareOrganizer = (o1: IOrganizer | null, o2: IOrganizer | null): boolean => this.organizerService.compareOrganizer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;

      if (event) {
        this.updateForm(event);

        if (event.id) {
          this.loadTickets(event.id);
        }
      }

      this.loadRelationshipsOptions();
    });
  }
  protected loadTickets(eventId: number): void {
    this.ticketTypeService.findByEvent(eventId).subscribe({
      next: res => {
        const data = res.body ?? [];

        this.originalTicketIds = data.filter(t => t.id != null).map(t => t.id!);

        this.tickets = data.map(ticket => ({
          id: ticket.id,

          name: ticket.name ?? '',

          price: ticket.price ?? 0,

          quantity: ticket.quantity ?? 0,

          remaining: ticket.remaining ?? ticket.quantity ?? 0,
        }));

        if (this.tickets.length === 0) {
          this.addTicket();
        }
      },

      error: err => console.error(err),
    });
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

  addTicket(): void {
    this.tickets.push({
      id: null,
      name: '',
      price: 0,
      quantity: 0,
      remaining: 0,
    });
  }

  removeTicket(index: number): void {
    this.tickets.splice(index, 1);
    if (this.tickets.length === 0) {
      this.addTicket();
    }
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
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.router.navigate(['/event']);
    } else {
      this.router.navigate(['/organizer/events']);
    }
  }
  protected subscribeToSaveResponse(result: Observable<IEvent>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: event => {
        this.saveTickets(event.id);
      },
      error: () => this.onSaveError(),
    });
  }
  protected saveTickets(eventId: number): void {
    const requests: Observable<any>[] = [];
    const currentIds = this.tickets.filter(ticket => ticket.id != null).map(ticket => ticket.id);

    const deletedIds = this.originalTicketIds.filter(id => !currentIds.includes(id));

    deletedIds.forEach(id => {
      requests.push(this.ticketTypeService.delete(id));
    });
    this.tickets.forEach(ticket => {
      const dto = {
        id: ticket.id,
        name: ticket.name,
        price: ticket.price,
        quantity: ticket.quantity,
        remaining: ticket.id ? ticket.remaining : ticket.quantity,
        saleStart: null,
        saleEnd: null,
        event: {
          id: eventId,
          title: '',
        },
      };
      if (ticket.id) {
        requests.push(this.ticketTypeService.update(dto as any));
      } else {
        requests.push(
          this.ticketTypeService.create({
            ...dto,
            id: null,
          }),
        );
      }
    });
    if (requests.length === 0) {
      this.navigateAfterSave();
      return;
    }
    forkJoin(requests).subscribe({
      next: () => {
        this.navigateAfterSave();
      },
      error: err => {
        console.error(err);
      },
    });
  }

  protected onSaveError(): void {
    // Api for inheritance.
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
    this.organizersSharedCollection.update(organizers =>
      this.organizerService.addOrganizerToCollectionIfMissing<IOrganizer>(organizers, event.organizer),
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

    this.organizerService
      .query()
      .pipe(map((res: HttpResponse<IOrganizer[]>) => res.body ?? []))
      .pipe(
        map((organizers: IOrganizer[]) =>
          this.organizerService.addOrganizerToCollectionIfMissing<IOrganizer>(organizers, this.event?.organizer),
        ),
      )
      .subscribe((organizers: IOrganizer[]) => this.organizersSharedCollection.set(organizers));
  }
}
