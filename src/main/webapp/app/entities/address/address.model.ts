export interface IAddress {
  id: number;
  location?: string | null;
  address?: string | null;
  city?: string | null;
  capacity?: number | null;
}

export type NewAddress = Omit<IAddress, 'id'> & { id: null };
