import dayjs from 'dayjs/esm';

import { IRental, NewRental } from './rental.model';

export const sampleWithRequiredData: IRental = {
  id: 5216,
  startDate: dayjs('2026-06-11'),
  endDate: dayjs('2026-06-11'),
  status: 'PLANNED',
};

export const sampleWithPartialData: IRental = {
  id: 9989,
  startDate: dayjs('2026-06-11'),
  endDate: dayjs('2026-06-11'),
  status: 'PLANNED',
};

export const sampleWithFullData: IRental = {
  id: 4722,
  startDate: dayjs('2026-06-11'),
  endDate: dayjs('2026-06-11'),
  totalPrice: 2444.61,
  status: 'PLANNED',
};

export const sampleWithNewData: NewRental = {
  startDate: dayjs('2026-06-11'),
  endDate: dayjs('2026-06-11'),
  status: 'PLANNED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
