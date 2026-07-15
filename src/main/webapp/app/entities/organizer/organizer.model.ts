import { IUser } from 'app/entities/user/user.model';

export interface IOrganizer {
  id: number;
  companyName?: string | null;
  taxCode?: string | null;
  description?: string | null;
  verified?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewOrganizer = Omit<IOrganizer, 'id'> & { id: null };
