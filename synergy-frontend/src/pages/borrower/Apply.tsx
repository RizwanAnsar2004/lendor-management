import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../../lib/api';

export default function Apply() {
  const nav = useNavigate();
  const [form, setForm] = useState({
    lenderSlug: 'abc-fintech',
    purpose: 'PERSONAL_LOAN',
    amount: '',
    termMonths: '12',
    currencyCode: 'USD',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (k: string, v: string) => setForm(f => ({ ...f, [k]: v }));

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setLoading(true);
    try {
      const res = await api.post<{ applicationId: string }>('/v1/applications', {
        lenderSlug: form.lenderSlug,
        purpose: form.purpose,
        amount: Number(form.amount),
        termMonths: Number(form.termMonths),
        currencyCode: form.currencyCode,
      });
      nav(`/borrower/applications/${res.applicationId}`);
    } catch (err: unknown) {
      const e = err as Error & { fields?: Record<string, string> };
      if (e.fields) setFieldErrors(e.fields);
      else setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  const fe = (k: string) => fieldErrors[k] ? <p style={{ color: 'red', fontSize: 12, marginTop: 2 }}>{fieldErrors[k]}</p> : null;

  return (
    <div className="page-bg">
      <div className="card" style={{ width: 420 }}>
        <div className="card-header">New Application</div>
        <div className="card-body">
          <form onSubmit={submit}>
            <div className="label">Lender</div>
            <input className="input" value={form.lenderSlug} onChange={e => set('lenderSlug', e.target.value)} placeholder="e.g. abc-fintech" required />
            {fe('lenderSlug')}

            <div className="label">Purpose</div>
            <select className="select" value={form.purpose} onChange={e => set('purpose', e.target.value)}>
              <option value="PERSONAL_LOAN">Personal Loan</option>
              <option value="MORTGAGE">Mortgage</option>
              <option value="BUSINESS_LOAN">Business Loan</option>
              <option value="AUTO_FINANCE">Auto Finance</option>
            </select>
            {fe('purpose')}

            <div className="row" style={{ marginTop: 0 }}>
              <div>
                <div className="label">Amount</div>
                <input className="input" type="number" min={1} value={form.amount} onChange={e => set('amount', e.target.value)} placeholder="5000" required />
                {fe('amount')}
              </div>
              <div>
                <div className="label">Currency</div>
                <select className="select" value={form.currencyCode} onChange={e => set('currencyCode', e.target.value)}>
                  <option value="USD">USD</option>
                  <option value="SGD">SGD</option>
                  <option value="AED">AED</option>
                  <option value="GBP">GBP</option>
                  <option value="INR">INR</option>
                </select>
              </div>
            </div>

            <div className="label">Term (months)</div>
            <input className="input" type="number" min={1} value={form.termMonths} onChange={e => set('termMonths', e.target.value)} required />
            {fe('termMonths')}

            {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Creating…' : 'Create Application'}
            </button>
          </form>
          <div className="footer">
            <Link className="link" to="/borrower/dashboard">← Back to dashboard</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
