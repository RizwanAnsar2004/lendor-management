// Rewired to use gateway via lib/api
import { api } from '../lib/api';

export async function initPassport(payload: {
  purpose: string; originCountry: string; destCountry: string; fullName: string; dob: string;
}) {
  return api.post<{ passportId: string; status: string }>('/v1/passports/init', payload);
}

export async function connectSources(passportId: string, sources: string[]) {
  return api.post(`/v1/passports/${passportId}/sources`, { sources });
}

export async function generatePassport(passportId: string) {
  return api.post(`/v1/passports/${passportId}/generate`);
}
