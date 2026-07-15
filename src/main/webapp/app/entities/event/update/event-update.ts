import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
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

import { EventFormGroup, EventFormService } from './event-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-event-update',
  templateUrl: './event-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EventUpdate implements OnInit {
  readonly isSaving = signal(false);
  event: IEvent | null = null;

  categoriesSharedCollection = signal<ICategory[]>([]);
  addressesSharedCollection = signal<IAddress[]>([]);
  organizersSharedCollection = signal<IOrganizer[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected eventService = inject(EventService);
  protected eventFormService = inject(EventFormService);
  protected categoryService = inject(CategoryService);
  protected addressService = inject(AddressService);
  protected organizerService = inject(OrganizerService);
  protected activatedRoute = inject(ActivatedRoute);

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
      }

      this.loadRelationshipsOptions();
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

  previousState(): void {
    globalThis.history.back();
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

  protected subscribeToSaveResponse(result: Observable<IEvent | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
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
