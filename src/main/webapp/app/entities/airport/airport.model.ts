export interface IAirport {
  id: number;
  code?: string | null;
  name?: string | null;
  city?: string | null;
}

export type NewAirport = Omit<IAirport, 'id'> & { id: null };
