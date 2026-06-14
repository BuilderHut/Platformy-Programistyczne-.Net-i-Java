import dayjs from 'dayjs/esm';

export interface IDrivingLicense {
  id: number;
  licenseNumber?: string | null;
  issueDate?: dayjs.Dayjs | null;
  expirationDate?: dayjs.Dayjs | null;
}

export type NewDrivingLicense = Omit<IDrivingLicense, 'id'> & { id: null };
