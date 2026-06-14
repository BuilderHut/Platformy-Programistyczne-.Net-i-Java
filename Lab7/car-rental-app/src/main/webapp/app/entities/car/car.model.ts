import { ICategory } from 'app/entities/category/category.model';
import { CarStatus } from 'app/entities/enumerations/car-status.model';

export interface ICar {
  id: number;
  brand?: string | null;
  model?: string | null;
  productionYear?: number | null;
  dailyPrice?: number | null;
  status?: keyof typeof CarStatus | null;
  categorieses?: Pick<ICategory, 'id' | 'name'>[] | null;
}

export type NewCar = Omit<ICar, 'id'> & { id: null };
