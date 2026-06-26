const KEY = 'gcp_token';
const ROLE_KEY = 'gcp_role';
const UID_KEY = 'gcp_uid';

export function saveAuth(token: string, role: string, userId: string) {
  localStorage.setItem(KEY, token);
  localStorage.setItem(ROLE_KEY, role);
  localStorage.setItem(UID_KEY, userId);
}

export function getToken(): string | null {
  return localStorage.getItem(KEY);
}

export function getRole(): string | null {
  return localStorage.getItem(ROLE_KEY);
}

export function getUserId(): string | null {
  return localStorage.getItem(UID_KEY);
}

export function clearAuth() {
  localStorage.removeItem(KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(UID_KEY);
}

export function isAuthenticated(): boolean {
  return !!getToken();
}
