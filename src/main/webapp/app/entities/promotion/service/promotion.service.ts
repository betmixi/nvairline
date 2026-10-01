import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPromotion, NewPromotion } from '../promotion.model';

export type PartialUpdatePromotion = Partial<IPromotion> & Pick<IPromotion, 'id'>;

@Injectable()
export class PromotionsService {
  readonly promotionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly promotionsResource = httpResource<IPromotion[]>(() => {
    const params = this.promotionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of promotion that have been fetched. It is updated when the promotionsResource emits a new value.
   * In case of error while fetching the promotions, the signal is set to an empty array.
   */
  readonly promotions = computed(() => (this.promotionsResource.hasValue() ? this.promotionsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/promotions');
}

@Injectable({ providedIn: 'root' })
export class PromotionService extends PromotionsService {
  protected readonly http = inject(HttpClient);

  create(promotion: NewPromotion): Observable<IPromotion> {
    return this.http.post<IPromotion>(this.resourceUrl, promotion);
  }

  update(promotion: IPromotion): Observable<IPromotion> {
    return this.http.put<IPromotion>(`${this.resourceUrl}/${encodeURIComponent(this.getPromotionIdentifier(promotion))}`, promotion);
  }

  partialUpdate(promotion: PartialUpdatePromotion): Observable<IPromotion> {
    return this.http.patch<IPromotion>(`${this.resourceUrl}/${encodeURIComponent(this.getPromotionIdentifier(promotion))}`, promotion);
  }

  find(id: number): Observable<IPromotion> {
    return this.http.get<IPromotion>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPromotion[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPromotion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  /** Cong khai: chi cac uu dai dang active, sap theo displayOrder, dung cho flyout "Kham Pha" o trang chu. */
  queryActive(): Observable<IPromotion[]> {
    return this.http.get<IPromotion[]>(this.resourceUrl, { params: { activeOnly: true } });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPromotionIdentifier(promotion: Pick<IPromotion, 'id'>): number {
    return promotion.id;
  }

  comparePromotion(o1: Pick<IPromotion, 'id'> | null, o2: Pick<IPromotion, 'id'> | null): boolean {
    return o1 && o2 ? this.getPromotionIdentifier(o1) === this.getPromotionIdentifier(o2) : o1 === o2;
  }

  addPromotionToCollectionIfMissing<Type extends Pick<IPromotion, 'id'>>(
    promotionCollection: Type[],
    ...promotionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const promotions: Type[] = promotionsToCheck.filter(isPresent);
    if (promotions.length > 0) {
      const promotionCollectionIdentifiers = promotionCollection.map(promotionItem => this.getPromotionIdentifier(promotionItem));
      const promotionsToAdd = promotions.filter(promotionItem => {
        const promotionIdentifier = this.getPromotionIdentifier(promotionItem);
        if (promotionCollectionIdentifiers.includes(promotionIdentifier)) {
          return false;
        }
        promotionCollectionIdentifiers.push(promotionIdentifier);
        return true;
      });
      return [...promotionsToAdd, ...promotionCollection];
    }
    return promotionCollection;
  }
}
