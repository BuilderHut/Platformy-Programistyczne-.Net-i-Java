import { ICar } from 'app/entities/car/car.model';

export interface ICategory {
  id: number;
  name?: string | null;
  description?: string | null;
  carses?: Pick<ICar, 'id'>[] | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
