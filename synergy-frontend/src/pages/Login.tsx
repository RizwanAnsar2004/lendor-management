import { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { api } from '../lib/api';
import { saveAuth } from '../lib/token';

interface LoginResponse {
  accessToken: string;
}

function decodeJwt(token: string): Record<string, unknown> {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return {};
  }
}

export default function Login() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const hint = params.get('hint') ?? '';

  const [email, setEmail] = useState(hint);
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await api.post<LoginResponse>('/auth/login', { email, password }, { skipAuth: true });
      const claims = decodeJwt(res.accessToken);
      const role = (claims.role ?? claims.roles ?? claims.authorities ?? 'BORROWER') as string;
      const userId = (claims.sub ?? claims.userId ?? '') as string;
      saveAuth(res.accessToken, Array.isArray(role) ? role[0] : role, userId);
      const effectiveRole = Array.isArray(role) ? role[0] : role;
      if (effectiveRole === 'LENDER') nav('/lender/dashboard');
      else nav('/borrower/dashboard');
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-bg">
      <div className="card">
        <div className="card-header">Sign in</div>
        <div className="card-body">
          <form onSubmit={submit}>
            <div className="label">Email</div>
            <input
              className="input"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
            <div className="label">Password</div>
            <input
              className="input"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
            {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Signing in…' : 'Sign in'}
            </button>
          </form>
          <div className="footer" style={{ marginTop: 10 }}>
            New here?{' '}
            <Link className="link" to="/get-started">Create account</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
