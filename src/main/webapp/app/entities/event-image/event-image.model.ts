import { IEvent } from 'app/entities/event/event.model';

export interface IEventImage {
  id: number;
  imageUrl?: string | null;
  event?: Pick<IEvent, 'id' | 'title'> | null;
}

export type NewEventImage = Omit<IEventImage, 'id'> & { id: null };
