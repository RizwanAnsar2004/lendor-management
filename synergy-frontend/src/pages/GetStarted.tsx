import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../lib/api';

export default function GetStarted() {
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.post('/auth/otp/request', { email }, { skipAuth: true });
      nav(`/verify?email=${encodeURIComponent(email)}`);
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-bg">
      <div className="card">
        <div className="card-header">Create your account</div>
        <div className="card-body">
          <p className="hint">We'll send a one-time code to verify your email.</p>
          <form onSubmit={submit}>
            <div className="label">Email address</div>
            <input
              className="input"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
            {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
            <button className="btn" type="submit" disabled={loading || !email}>
              {loading ? 'Sending…' : 'Send OTP'}
            </button>
          </form>
          <div className="footer">
            Already have an account?{' '}
            <Link className="link" to="/login">Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
