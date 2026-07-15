import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEventImage, NewEventImage } from '../event-image.model';

export type PartialUpdateEventImage = Partial<IEventImage> & Pick<IEventImage, 'id'>;

@Injectable()
export class EventImagesService {
  readonly eventImagesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly eventImagesResource = httpResource<IEventImage[]>(() => {
    const params = this.eventImagesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of eventImage that have been fetched. It is updated when the eventImagesResource emits a new value.
   * In case of error while fetching the eventImages, the signal is set to an empty array.
   */
  readonly eventImages = computed(() => (this.eventImagesResource.hasValue() ? this.eventImagesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/event-images');
}

@Injectable({ providedIn: 'root' })
export class EventImageService extends EventImagesService {
  protected readonly http = inject(HttpClient);

  create(eventImage: NewEventImage): Observable<IEventImage> {
    return this.http.post<IEventImage>(this.resourceUrl, eventImage);
  }

  update(eventImage: IEventImage): Observable<IEventImage> {
    return this.http.put<IEventImage>(`${this.resourceUrl}/${encodeURIComponent(this.getEventImageIdentifier(eventImage))}`, eventImage);
  }

  partialUpdate(eventImage: PartialUpdateEventImage): Observable<IEventImage> {
    return this.http.patch<IEventImage>(`${this.resourceUrl}/${encodeURIComponent(this.getEventImageIdentifier(eventImage))}`, eventImage);
  }

  find(id: number): Observable<IEventImage> {
    return this.http.get<IEventImage>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IEventImage[]>> {
    const options = createRequestOption(req);
    return this.http.get<IEventImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEventImageIdentifier(eventImage: Pick<IEventImage, 'id'>): number {
    return eventImage.id;
  }

  compareEventImage(o1: Pick<IEventImage, 'id'> | null, o2: Pick<IEventImage, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventImageIdentifier(o1) === this.getEventImageIdentifier(o2) : o1 === o2;
  }

  addEventImageToCollectionIfMissing<Type extends Pick<IEventImage, 'id'>>(
    eventImageCollection: Type[],
    ...eventImagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const eventImages: Type[] = eventImagesToCheck.filter(isPresent);
    if (eventImages.length > 0) {
      const eventImageCollectionIdentifiers = eventImageCollection.map(eventImageItem => this.getEventImageIdentifier(eventImageItem));
      const eventImagesToAdd = eventImages.filter(eventImageItem => {
        const eventImageIdentifier = this.getEventImageIdentifier(eventImageItem);
        if (eventImageCollectionIdentifiers.includes(eventImageIdentifier)) {
          return false;
        }
        eventImageCollectionIdentifiers.push(eventImageIdentifier);
        return true;
      });
      return [...eventImagesToAdd, ...eventImageCollection];
    }
    return eventImageCollection;
  }
}
