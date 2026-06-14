import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 4218,
  brand: 'dim yuck splendid',
  model: 'sans integer contrast',
  productionYear: 6750,
  dailyPrice: 31467.5,
  status: 'SERVICE',
};

export const sampleWithPartialData: ICar = {
  id: 19509,
  brand: 'supportive',
  model: 'astride',
  productionYear: 12057,
  dailyPrice: 30134.5,
  status: 'AVAILABLE',
};

export const sampleWithFullData: ICar = {
  id: 22796,
  brand: 'testimonial yahoo a',
  model: 'even parody',
  productionYear: 13959,
  dailyPrice: 18138.82,
  status: 'SERVICE',
};

export const sampleWithNewData: NewCar = {
  brand: 'ick',
  model: 'consequently till ruin',
  productionYear: 7109,
  dailyPrice: 26886.9,
  status: 'SERVICE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
