export interface IPromotion {
  id: number;
  title?: string | null;
  description?: string | null;
  icon?: string | null;
  targetUrl?: string | null;
  displayOrder?: number | null;
  active?: boolean | null;
}

export type NewPromotion = Omit<IPromotion, 'id'> & { id: null };
