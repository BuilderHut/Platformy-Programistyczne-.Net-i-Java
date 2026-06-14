import dayjs from 'dayjs/esm';

import { IDrivingLicense, NewDrivingLicense } from './driving-license.model';

export const sampleWithRequiredData: IDrivingLicense = {
  id: 16285,
  licenseNumber: 'iterate near',
  issueDate: dayjs('2026-06-12'),
  expirationDate: dayjs('2026-06-11'),
};

export const sampleWithPartialData: IDrivingLicense = {
  id: 28065,
  licenseNumber: 'entrench formamide next',
  issueDate: dayjs('2026-06-11'),
  expirationDate: dayjs('2026-06-11'),
};

export const sampleWithFullData: IDrivingLicense = {
  id: 9070,
  licenseNumber: 'carelessly whether eek',
  issueDate: dayjs('2026-06-11'),
  expirationDate: dayjs('2026-06-12'),
};

export const sampleWithNewData: NewDrivingLicense = {
  licenseNumber: 'burly',
  issueDate: dayjs('2026-06-11'),
  expirationDate: dayjs('2026-06-11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
