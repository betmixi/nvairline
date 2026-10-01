export interface IAircraft {
  id: number;
  name?: string | null;
  totalRows?: number | null;
  totalColumns?: number | null;
  roomType?: string | null;
}

export type NewAircraft = Omit<IAircraft, 'id'> & { id: null };
