import dayjs from 'dayjs/esm';

import { IUser } from 'app/entities/user/user.model';

export interface IAuditLog {
  id: number;
  action?: string | null;
  tableName?: string | null;
  recordId?: number | null;
  createdDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewAuditLog = Omit<IAuditLog, 'id'> & { id: null };
