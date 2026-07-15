import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICoupon, NewCoupon } from '../coupon.model';

export type PartialUpdateCoupon = Partial<ICoupon> & Pick<ICoupon, 'id'>;

type RestOf<T extends ICoupon | NewCoupon> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestCoupon = RestOf<ICoupon>;

export type NewRestCoupon = RestOf<NewCoupon>;

export type PartialUpdateRestCoupon = RestOf<PartialUpdateCoupon>;

@Injectable()
export class CouponsService {
  readonly couponsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly couponsResource = httpResource<RestCoupon[]>(() => {
    const params = this.couponsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of coupon that have been fetched. It is updated when the couponsResource emits a new value.
   * In case of error while fetching the coupons, the signal is set to an empty array.
   */
  readonly coupons = computed(() =>
    (this.couponsResource.hasValue() ? this.couponsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/coupons');

  protected convertValueFromServer(restCoupon: RestCoupon): ICoupon {
    return {
      ...restCoupon,
      startDate: restCoupon.startDate ? dayjs(restCoupon.startDate) : undefined,
      endDate: restCoupon.endDate ? dayjs(restCoupon.endDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CouponService extends CouponsService {
  protected readonly http = inject(HttpClient);

  create(coupon: NewCoupon): Observable<ICoupon> {
    const copy = this.convertValueFromClient(coupon);
    return this.http.post<RestCoupon>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(coupon: ICoupon): Observable<ICoupon> {
    const copy = this.convertValueFromClient(coupon);
    return this.http
      .put<RestCoupon>(`${this.resourceUrl}/${encodeURIComponent(this.getCouponIdentifier(coupon))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(coupon: PartialUpdateCoupon): Observable<ICoupon> {
    const copy = this.convertValueFromClient(coupon);
    return this.http
      .patch<RestCoupon>(`${this.resourceUrl}/${encodeURIComponent(this.getCouponIdentifier(coupon))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICoupon> {
    return this.http.get<RestCoupon>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICoupon[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCoupon[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCouponIdentifier(coupon: Pick<ICoupon, 'id'>): number {
    return coupon.id;
  }

  compareCoupon(o1: Pick<ICoupon, 'id'> | null, o2: Pick<ICoupon, 'id'> | null): boolean {
    return o1 && o2 ? this.getCouponIdentifier(o1) === this.getCouponIdentifier(o2) : o1 === o2;
  }

  addCouponToCollectionIfMissing<Type extends Pick<ICoupon, 'id'>>(
    couponCollection: Type[],
    ...couponsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const coupons: Type[] = couponsToCheck.filter(isPresent);
    if (coupons.length > 0) {
      const couponCollectionIdentifiers = couponCollection.map(couponItem => this.getCouponIdentifier(couponItem));
      const couponsToAdd = coupons.filter(couponItem => {
        const couponIdentifier = this.getCouponIdentifier(couponItem);
        if (couponCollectionIdentifiers.includes(couponIdentifier)) {
          return false;
        }
        couponCollectionIdentifiers.push(couponIdentifier);
        return true;
      });
      return [...couponsToAdd, ...couponCollection];
    }
    return couponCollection;
  }

  protected convertValueFromClient<T extends ICoupon | NewCoupon | PartialUpdateCoupon>(coupon: T): RestOf<T> {
    return {
      ...coupon,
      startDate: coupon.startDate?.toJSON() ?? null,
      endDate: coupon.endDate?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCoupon): ICoupon {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCoupon[]): ICoupon[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
