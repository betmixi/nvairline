import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPayment, NewPayment } from '../payment.model';

export type PartialUpdatePayment = Partial<IPayment> & Pick<IPayment, 'id'>;

type RestOf<T extends IPayment | NewPayment> = Omit<T, 'paymentDate'> & {
  paymentDate?: string | null;
};

export type RestPayment = RestOf<IPayment>;

export type NewRestPayment = RestOf<NewPayment>;

export type PartialUpdateRestPayment = RestOf<PartialUpdatePayment>;

@Injectable()
export class PaymentsService {
  readonly paymentsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly paymentsResource = httpResource<RestPayment[]>(() => {
    const params = this.paymentsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of payment that have been fetched. It is updated when the paymentsResource emits a new value.
   * In case of error while fetching the payments, the signal is set to an empty array.
   */
  readonly payments = computed(() =>
    (this.paymentsResource.hasValue() ? this.paymentsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/payments');

  protected convertValueFromServer(restPayment: RestPayment): IPayment {
    return {
      ...restPayment,
      paymentDate: restPayment.paymentDate ? dayjs(restPayment.paymentDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PaymentService extends PaymentsService {
  protected readonly http = inject(HttpClient);

  create(payment: NewPayment): Observable<IPayment> {
    const copy = this.convertValueFromClient(payment);
    return this.http.post<RestPayment>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(payment: IPayment): Observable<IPayment> {
    const copy = this.convertValueFromClient(payment);
    return this.http
      .put<RestPayment>(`${this.resourceUrl}/${encodeURIComponent(this.getPaymentIdentifier(payment))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(payment: PartialUpdatePayment): Observable<IPayment> {
    const copy = this.convertValueFromClient(payment);
    return this.http
      .patch<RestPayment>(`${this.resourceUrl}/${encodeURIComponent(this.getPaymentIdentifier(payment))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPayment> {
    return this.http
      .get<RestPayment>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPayment[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPaymentIdentifier(payment: Pick<IPayment, 'id'>): number {
    return payment.id;
  }

  comparePayment(o1: Pick<IPayment, 'id'> | null, o2: Pick<IPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentIdentifier(o1) === this.getPaymentIdentifier(o2) : o1 === o2;
  }

  addPaymentToCollectionIfMissing<Type extends Pick<IPayment, 'id'>>(
    paymentCollection: Type[],
    ...paymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const payments: Type[] = paymentsToCheck.filter(isPresent);
    if (payments.length > 0) {
      const paymentCollectionIdentifiers = paymentCollection.map(paymentItem => this.getPaymentIdentifier(paymentItem));
      const paymentsToAdd = payments.filter(paymentItem => {
        const paymentIdentifier = this.getPaymentIdentifier(paymentItem);
        if (paymentCollectionIdentifiers.includes(paymentIdentifier)) {
          return false;
        }
        paymentCollectionIdentifiers.push(paymentIdentifier);
        return true;
      });
      return [...paymentsToAdd, ...paymentCollection];
    }
    return paymentCollection;
  }

  protected convertValueFromClient<T extends IPayment | NewPayment | PartialUpdatePayment>(payment: T): RestOf<T> {
    return {
      ...payment,
      paymentDate: payment.paymentDate?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPayment): IPayment {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPayment[]): IPayment[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
