export interface IVenue {
  id: number;
  name?: string | null;
  address?: string | null;
  city?: string | null;
  country?: string | null;
  capacity?: number | null;
}

export type NewVenue = Omit<IVenue, 'id'> & { id: null };
