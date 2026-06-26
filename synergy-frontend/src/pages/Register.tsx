import { useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { api } from '../lib/api';

export default function Register() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const email = params.get('email') ?? '';

  const [dob, setDob] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    if (password !== confirm) return setError('Passwords do not match.');
    setLoading(true);
    try {
      await api.post('/auth/register', { email, password, dateOfBirth: dob }, { skipAuth: true });
      nav(`/login?hint=${encodeURIComponent(email)}`);
    } catch (err: unknown) {
      const e = err as Error & { fields?: Record<string, string> };
      if (e.fields) setFieldErrors(e.fields);
      else setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-bg">
      <div className="card">
        <div className="card-header">Complete registration</div>
        <div className="card-body">
          <p className="hint">Setting up account for <strong>{email}</strong></p>
          <form onSubmit={submit}>
            <div className="label">Date of birth</div>
            <input
              className="input"
              type="date"
              value={dob}
              onChange={e => setDob(e.target.value)}
              required
            />
            {fieldErrors.dateOfBirth && <p style={{ color: 'red', fontSize: 12 }}>{fieldErrors.dateOfBirth}</p>}

            <div className="label">Password</div>
            <input
              className="input"
              type="password"
              placeholder="Min 8 characters"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              minLength={8}
            />
            {fieldErrors.password && <p style={{ color: 'red', fontSize: 12 }}>{fieldErrors.password}</p>}

            <div className="label">Confirm password</div>
            <input
              className="input"
              type="password"
              placeholder="Repeat password"
              value={confirm}
              onChange={e => setConfirm(e.target.value)}
              required
            />

            {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Creating account…' : 'Create account'}
            </button>
          </form>
          <div className="footer">
            <Link className="link" to="/login">Already have an account?</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
