import { useRef, useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { api } from '../lib/api';

export default function Verify() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const email = params.get('email') ?? '';

  const [digits, setDigits] = useState(['', '', '', '', '', '']);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [resent, setResent] = useState(false);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  function handleChange(i: number, val: string) {
    const d = val.replace(/\D/g, '').slice(-1);
    const next = [...digits];
    next[i] = d;
    setDigits(next);
    if (d && i < 5) inputRefs.current[i + 1]?.focus();
  }

  function handleKeyDown(i: number, e: React.KeyboardEvent) {
    if (e.key === 'Backspace' && !digits[i] && i > 0) inputRefs.current[i - 1]?.focus();
  }

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    const code = digits.join('');
    if (code.length < 6) return setError('Enter all 6 digits.');
    setError('');
    setLoading(true);
    try {
      await api.post('/auth/otp/verify', { email, code }, { skipAuth: true });
      nav(`/register?email=${encodeURIComponent(email)}`);
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function resend() {
    try {
      await api.post('/auth/otp/request', { email }, { skipAuth: true });
      setResent(true);
      setTimeout(() => setResent(false), 3000);
    } catch {
      // ignore
    }
  }

  return (
    <div className="page-bg">
      <div className="card">
        <div className="card-header">Check your email</div>
        <div className="card-body">
          <p className="hint">We sent a 6-digit code to <strong>{email}</strong></p>
          <form onSubmit={submit}>
            <div className="otp">
              {digits.map((d, i) => (
                <input
                  key={i}
                  ref={el => { inputRefs.current[i] = el; }}
                  type="text"
                  inputMode="numeric"
                  maxLength={1}
                  value={d}
                  onChange={e => handleChange(i, e.target.value)}
                  onKeyDown={e => handleKeyDown(i, e)}
                  autoFocus={i === 0}
                />
              ))}
            </div>
            {error && <p style={{ color: 'red', fontSize: 13, marginTop: 4 }}>{error}</p>}
            {resent && <p style={{ color: 'green', fontSize: 13, marginTop: 4 }}>Code resent!</p>}
            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Verifying…' : 'Verify'}
            </button>
          </form>
          <div className="footer">
            Didn't receive it?{' '}
            <span className="link" style={{ cursor: 'pointer' }} onClick={resend}>Resend</span>
            {' · '}
            <Link className="link" to="/get-started">Change email</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
