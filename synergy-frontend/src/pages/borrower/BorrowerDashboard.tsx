import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../../lib/api';
import { clearAuth } from '../../lib/token';

interface Application {
  applicationId: string;
  purpose: string;
  amount: number;
  currencyCode: string;
  status: string;
  reviewStatus: string;
  createdAt: string;
}

const STATUS_COLOR: Record<string, string> = {
  DRAFT: '#6a7b8a',
  SUBMITTED: '#0a67c2',
  UNDER_REVIEW: '#d97706',
  APPROVED: '#16a34a',
  REJECTED: '#dc2626',
};

export default function BorrowerDashboard() {
  const nav = useNavigate();
  const [apps, setApps] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get<Application[]>('/v1/applications')
      .then(setApps)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  function signOut() {
    clearAuth();
    nav('/login');
  }

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg,#bfe0ff,#eaf4ff)', padding: '32px 16px' }}>
      <div style={{ maxWidth: 700, margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <h2 style={{ margin: 0, color: '#0d2b45', fontSize: 22 }}>My Applications</h2>
          <div style={{ display: 'flex', gap: 10 }}>
            <button
              onClick={() => nav('/borrower/apply')}
              style={{ background: 'linear-gradient(180deg,#0a67c2,#084f97)', color: '#fff', border: 'none', borderRadius: 10, padding: '10px 18px', cursor: 'pointer', fontWeight: 700 }}
            >
              + New Application
            </button>
            <button
              onClick={signOut}
              style={{ background: '#fff', border: '1px solid #dbe7f5', borderRadius: 10, padding: '10px 14px', cursor: 'pointer', color: '#6a7b8a', fontWeight: 600 }}
            >
              Sign out
            </button>
          </div>
        </div>

        {loading && <p style={{ textAlign: 'center', color: '#6a7b8a' }}>Loading…</p>}
        {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}

        {!loading && apps.length === 0 && (
          <div style={{ background: '#fff', borderRadius: 14, padding: 40, textAlign: 'center', boxShadow: '0 20px 50px rgba(10,40,80,.12)' }}>
            <p style={{ color: '#6a7b8a', marginBottom: 16 }}>No applications yet.</p>
            <button
              onClick={() => nav('/borrower/apply')}
              style={{ background: 'linear-gradient(180deg,#0a67c2,#084f97)', color: '#fff', border: 'none', borderRadius: 10, padding: '12px 24px', cursor: 'pointer', fontWeight: 700 }}
            >
              Start your first application
            </button>
          </div>
        )}

        {apps.map(app => (
          <Link
            key={app.applicationId}
            to={`/borrower/applications/${app.applicationId}`}
            style={{ textDecoration: 'none' }}
          >
            <div style={{
              background: '#fff', borderRadius: 14, padding: '18px 22px', marginBottom: 14,
              boxShadow: '0 4px 16px rgba(10,40,80,.08)', border: '1px solid #dbe7f5',
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            }}>
              <div>
                <div style={{ fontWeight: 700, color: '#0d2b45', marginBottom: 4 }}>
                  {app.purpose} — {app.currencyCode} {Number(app.amount).toLocaleString()}
                </div>
                <div style={{ fontSize: 12, color: '#6a7b8a' }}>
                  {new Date(app.createdAt).toLocaleDateString()}
                </div>
              </div>
              <span style={{
                background: (STATUS_COLOR[app.status] ?? '#6a7b8a') + '20',
                color: STATUS_COLOR[app.status] ?? '#6a7b8a',
                borderRadius: 8, padding: '4px 12px', fontSize: 12, fontWeight: 700,
              }}>
                {app.status}
              </span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
