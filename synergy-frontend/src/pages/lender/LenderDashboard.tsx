import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../../lib/api';
import { clearAuth } from '../../lib/token';

interface AppSummary {
  applicationId: string;
  borrowerEmail?: string;
  purpose: string;
  amount: number;
  currencyCode: string;
  status: string;
  reviewStatus: string;
  submittedAt?: string;
  lenderSlug?: string;
}

const REVIEW_COLOR: Record<string, string> = {
  NEW: '#6a7b8a',
  UNDER_REVIEW: '#d97706',
  INFO_REQUESTED: '#7c3aed',
  REVIEWED: '#0a67c2',
  APPROVED: '#16a34a',
  REJECTED: '#dc2626',
};

export default function LenderDashboard() {
  const nav = useNavigate();
  const [apps, setApps] = useState<AppSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    api.get<AppSummary[]>('/v1/lender/applications')
      .then(setApps)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const statuses = ['ALL', 'NEW', 'UNDER_REVIEW', 'INFO_REQUESTED', 'REVIEWED'];
  const visible = filter === 'ALL' ? apps : apps.filter(a => a.reviewStatus === filter);

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg,#bfe0ff,#eaf4ff)', padding: '32px 16px' }}>
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h2 style={{ margin: 0, color: '#0d2b45' }}>Lender Dashboard</h2>
          <button
            onClick={() => { clearAuth(); nav('/login'); }}
            style={{ background: '#fff', border: '1px solid #dbe7f5', borderRadius: 10, padding: '10px 14px', cursor: 'pointer', color: '#6a7b8a', fontWeight: 600 }}
          >
            Sign out
          </button>
        </div>

        {/* Filter tabs */}
        <div style={{ display: 'flex', gap: 6, marginBottom: 16, flexWrap: 'wrap' }}>
          {statuses.map(s => (
            <button
              key={s}
              onClick={() => setFilter(s)}
              style={{
                border: 'none', borderRadius: 8, padding: '6px 14px', cursor: 'pointer', fontSize: 12, fontWeight: 700,
                background: filter === s ? '#0a67c2' : '#fff',
                color: filter === s ? '#fff' : '#6a7b8a',
              }}
            >
              {s === 'ALL' ? `All (${apps.length})` : `${s} (${apps.filter(a => a.reviewStatus === s).length})`}
            </button>
          ))}
        </div>

        {loading && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>Loading…</p>}
        {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
        {!loading && visible.length === 0 && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>No applications found.</p>}

        {visible.map(app => (
          <Link key={app.applicationId} to={`/lender/applications/${app.applicationId}`} style={{ textDecoration: 'none' }}>
            <div style={{
              background: '#fff', borderRadius: 14, padding: '16px 22px', marginBottom: 12,
              boxShadow: '0 4px 16px rgba(10,40,80,.08)', border: '1px solid #dbe7f5',
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            }}>
              <div>
                <div style={{ fontWeight: 700, color: '#0d2b45', marginBottom: 3 }}>
                  {app.purpose} — {app.currencyCode} {Number(app.amount).toLocaleString()}
                </div>
                <div style={{ fontSize: 12, color: '#6a7b8a' }}>
                  {app.borrowerEmail ?? 'Borrower'} · {app.submittedAt ? new Date(app.submittedAt).toLocaleDateString() : '—'}
                </div>
              </div>
              <span style={{
                background: (REVIEW_COLOR[app.reviewStatus] ?? '#6a7b8a') + '20',
                color: REVIEW_COLOR[app.reviewStatus] ?? '#6a7b8a',
                borderRadius: 8, padding: '4px 12px', fontSize: 12, fontWeight: 700,
              }}>
                {app.reviewStatus}
              </span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
