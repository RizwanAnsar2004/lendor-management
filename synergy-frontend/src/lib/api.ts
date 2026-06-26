import { getToken, clearAuth } from './token';

export const BASE = 'http://localhost:8080';

type FetchOptions = RequestInit & { skipAuth?: boolean };

async function request<T>(path: string, opts: FetchOptions = {}): Promise<T> {
  const { skipAuth, ...init } = opts;
  const headers: Record<string, string> = {
    ...(init.body && !(init.body instanceof FormData) ? { 'Content-Type': 'application/json' } : {}),
    ...(init.headers as Record<string, string> ?? {}),
  };

  if (!skipAuth) {
    const token = getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${BASE}${path}`, { ...init, headers });

  if (res.status === 401) {
    clearAuth();
    window.location.href = '/login';
    throw new Error('Unauthorized');
  }

  if (!res.ok) {
    let body: { error?: string; message?: string; fields?: Record<string, string> } = {};
    try { body = await res.json(); } catch { /* ignore */ }
    const err: Error & { status?: number; fields?: Record<string, string> } = new Error(
      body.error ?? body.message ?? `HTTP ${res.status}`
    );
    err.status = res.status;
    err.fields = body.fields;
    throw err;
  }

  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const api = {
  get: <T>(path: string, opts?: FetchOptions) =>
    request<T>(path, { method: 'GET', ...opts }),
  post: <T>(path: string, body?: unknown, opts?: FetchOptions) =>
    request<T>(path, {
      method: 'POST',
      body: body instanceof FormData ? body : JSON.stringify(body),
      ...opts,
    }),
  put: <T>(path: string, body?: unknown, opts?: FetchOptions) =>
    request<T>(path, {
      method: 'PUT',
      body: body instanceof FormData ? body : JSON.stringify(body),
      ...opts,
    }),
};
