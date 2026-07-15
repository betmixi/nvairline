import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFavorite, NewFavorite } from '../favorite.model';

export type PartialUpdateFavorite = Partial<IFavorite> & Pick<IFavorite, 'id'>;

@Injectable()
export class FavoritesService {
  readonly favoritesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly favoritesResource = httpResource<IFavorite[]>(() => {
    const params = this.favoritesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of favorite that have been fetched. It is updated when the favoritesResource emits a new value.
   * In case of error while fetching the favorites, the signal is set to an empty array.
   */
  readonly favorites = computed(() => (this.favoritesResource.hasValue() ? this.favoritesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/favorites');
}

@Injectable({ providedIn: 'root' })
export class FavoriteService extends FavoritesService {
  protected readonly http = inject(HttpClient);

  create(favorite: NewFavorite): Observable<IFavorite> {
    return this.http.post<IFavorite>(this.resourceUrl, favorite);
  }

  update(favorite: IFavorite): Observable<IFavorite> {
    return this.http.put<IFavorite>(`${this.resourceUrl}/${encodeURIComponent(this.getFavoriteIdentifier(favorite))}`, favorite);
  }

  partialUpdate(favorite: PartialUpdateFavorite): Observable<IFavorite> {
    return this.http.patch<IFavorite>(`${this.resourceUrl}/${encodeURIComponent(this.getFavoriteIdentifier(favorite))}`, favorite);
  }

  find(id: number): Observable<IFavorite> {
    return this.http.get<IFavorite>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IFavorite[]>> {
    const options = createRequestOption(req);
    return this.http.get<IFavorite[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFavoriteIdentifier(favorite: Pick<IFavorite, 'id'>): number {
    return favorite.id;
  }

  compareFavorite(o1: Pick<IFavorite, 'id'> | null, o2: Pick<IFavorite, 'id'> | null): boolean {
    return o1 && o2 ? this.getFavoriteIdentifier(o1) === this.getFavoriteIdentifier(o2) : o1 === o2;
  }

  addFavoriteToCollectionIfMissing<Type extends Pick<IFavorite, 'id'>>(
    favoriteCollection: Type[],
    ...favoritesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const favorites: Type[] = favoritesToCheck.filter(isPresent);
    if (favorites.length > 0) {
      const favoriteCollectionIdentifiers = favoriteCollection.map(favoriteItem => this.getFavoriteIdentifier(favoriteItem));
      const favoritesToAdd = favorites.filter(favoriteItem => {
        const favoriteIdentifier = this.getFavoriteIdentifier(favoriteItem);
        if (favoriteCollectionIdentifiers.includes(favoriteIdentifier)) {
          return false;
        }
        favoriteCollectionIdentifiers.push(favoriteIdentifier);
        return true;
      });
      return [...favoritesToAdd, ...favoriteCollection];
    }
    return favoriteCollection;
  }
}
