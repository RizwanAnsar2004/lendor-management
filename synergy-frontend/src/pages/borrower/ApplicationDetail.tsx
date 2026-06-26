import { useEffect, useRef, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api, BASE } from '../../lib/api';
import { getToken } from '../../lib/token';

interface AppDetail {
  applicationId: string;
  status: string;
  reviewStatus: string;
  purpose: string;
  amount: number;
  currencyCode: string;
  termMonths: number;
  lenderName?: string;
  profile?: Record<string, unknown>;
}

interface DocItem {
  documentId: string;
  docType: string;
  tag?: string;
  originalFilename: string;
  uploadedAt: string;
}

const ALLOWED_TYPES = ['application/pdf', 'image/png', 'image/jpeg'];
const MAX_SIZE = 10 * 1024 * 1024;

export default function ApplicationDetail() {
  const { id } = useParams<{ id: string }>();
  const nav = useNavigate();
  const [tab, setTab] = useState<'profile' | 'documents' | 'submit'>('profile');
  const [app, setApp] = useState<AppDetail | null>(null);
  const [docs, setDocs] = useState<DocItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Profile form
  const [profile, setProfile] = useState({
    fullName: '', dob: '', nationality: '', country: '',
    city: '', address: '', phone: '', email: '',
    employmentStatus: 'EMPLOYED', employerName: '', monthlyIncome: '', incomeCurrency: 'USD',
  });
  const [profileErrors, setProfileErrors] = useState<Record<string, string>>({});
  const [profileSaved, setProfileSaved] = useState(false);
  const [profileLoading, setProfileLoading] = useState(false);

  // Document upload
  const fileRef = useRef<HTMLInputElement>(null);
  const [docType, setDocType] = useState('IDENTITY');
  const [docTag, setDocTag] = useState('');
  const [uploadError, setUploadError] = useState('');
  const [uploading, setUploading] = useState(false);

  // Submit
  const [submitError, setSubmitError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    Promise.all([
      api.get<AppDetail>(`/v1/applications/${id}`),
      api.get<DocItem[]>(`/v1/applications/${id}/documents`),
    ]).then(([a, d]) => {
      setApp(a);
      setDocs(d);
      if (a.profile) {
        const p = a.profile as Record<string, string>;
        setProfile(prev => ({ ...prev, ...Object.fromEntries(Object.entries(p).map(([k, v]) => [k, v ?? ''])) }));
      }
    }).catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  const sp = (k: string, v: string) => setProfile(prev => ({ ...prev, [k]: v }));

  async function saveProfile(e: React.FormEvent) {
    e.preventDefault();
    setProfileErrors({});
    setProfileLoading(true);
    try {
      await api.put(`/v1/applications/${id}/profile`, {
        ...profile,
        monthlyIncome: Number(profile.monthlyIncome),
      });
      setProfileSaved(true);
      setTimeout(() => setProfileSaved(false), 2000);
    } catch (err: unknown) {
      const e = err as Error & { fields?: Record<string, string> };
      if (e.fields) setProfileErrors(e.fields);
    } finally {
      setProfileLoading(false);
    }
  }

  async function upload(e: React.FormEvent) {
    e.preventDefault();
    setUploadError('');
    const file = fileRef.current?.files?.[0];
    if (!file) return setUploadError('Select a file.');
    if (!ALLOWED_TYPES.includes(file.type)) return setUploadError('Only PDF, PNG, JPG allowed.');
    if (file.size > MAX_SIZE) return setUploadError('File must be under 10 MB.');

    const fd = new FormData();
    fd.append('file', file);
    fd.append('docType', docType);
    if (docTag) fd.append('tag', docTag);

    setUploading(true);
    try {
      const doc = await api.post<DocItem>(`/v1/applications/${id}/documents`, fd);
      setDocs(prev => [...prev, doc]);
      if (fileRef.current) fileRef.current.value = '';
      setDocTag('');
    } catch (err: unknown) {
      setUploadError((err as Error).message);
    } finally {
      setUploading(false);
    }
  }

  async function downloadDoc(docId: string, filename: string) {
    const token = getToken();
    const res = await fetch(`${BASE}/v1/applications/${id}/documents/${docId}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!res.ok) return;
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename; a.click();
    URL.revokeObjectURL(url);
  }

  async function submitApp() {
    setSubmitError('');
    if (docs.length === 0) return setSubmitError('Upload at least one document before submitting.');
    setSubmitting(true);
    try {
      const res = await api.post<AppDetail>(`/v1/applications/${id}/submit`);
      setApp(res);
    } catch (err: unknown) {
      const e = err as Error & { status?: number };
      if (e.status === 409) setSubmitError('Application already submitted.');
      else setSubmitError(e.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) return <div className="page-bg"><p style={{ color: '#6a7b8a' }}>Loading…</p></div>;
  if (error || !app) return <div className="page-bg"><p style={{ color: 'red' }}>{error || 'Not found'}</p></div>;

  const isSubmitted = app.status === 'SUBMITTED';

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg,#bfe0ff,#eaf4ff)', padding: '32px 16px' }}>
      <div style={{ maxWidth: 700, margin: '0 auto' }}>
        <Link to="/borrower/dashboard" style={{ textDecoration: 'none', color: '#0d2b45', fontWeight: 700, display: 'inline-flex', alignItems: 'center', gap: 6, marginBottom: 16 }}>
          ← Dashboard
        </Link>
        <div style={{ background: '#fff', borderRadius: 14, padding: '16px 22px', marginBottom: 16, boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontWeight: 700, color: '#0d2b45', fontSize: 16 }}>{app.purpose}</div>
              <div style={{ color: '#6a7b8a', fontSize: 13 }}>{app.currencyCode} {Number(app.amount).toLocaleString()} · {app.termMonths} months</div>
            </div>
            <span style={{ background: '#0a67c220', color: '#0a67c2', borderRadius: 8, padding: '4px 12px', fontSize: 12, fontWeight: 700 }}>
              {app.status}
            </span>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', gap: 4, marginBottom: 16, background: '#fff', borderRadius: 12, padding: 4, boxShadow: '0 2px 8px rgba(10,40,80,.06)' }}>
          {(['profile', 'documents', 'submit'] as const).map(t => (
            <button
              key={t}
              onClick={() => setTab(t)}
              style={{
                flex: 1, border: 'none', borderRadius: 10, padding: '10px', cursor: 'pointer', fontWeight: 700, fontSize: 13,
                background: tab === t ? 'linear-gradient(180deg,#0a67c2,#084f97)' : 'transparent',
                color: tab === t ? '#fff' : '#6a7b8a',
              }}
            >
              {t === 'profile' ? 'Profile' : t === 'documents' ? `Documents (${docs.length})` : 'Submit'}
            </button>
          ))}
        </div>

        {/* Profile Tab */}
        {tab === 'profile' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            <form onSubmit={saveProfile}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                {[
                  ['fullName', 'Full name', 'text'],
                  ['dob', 'Date of birth', 'date'],
                  ['nationality', 'Nationality', 'text'],
                  ['country', 'Country', 'text'],
                  ['city', 'City', 'text'],
                  ['phone', 'Phone', 'tel'],
                  ['email', 'Email', 'email'],
                  ['employerName', 'Employer name', 'text'],
                  ['monthlyIncome', 'Monthly income', 'number'],
                  ['incomeCurrency', 'Income currency', 'text'],
                ].map(([k, label, type]) => (
                  <div key={k}>
                    <div className="label">{label}</div>
                    <input
                      className="input"
                      type={type}
                      value={profile[k as keyof typeof profile]}
                      onChange={e => sp(k, e.target.value)}
                      disabled={isSubmitted}
                    />
                    {profileErrors[k] && <p style={{ color: 'red', fontSize: 12, margin: '2px 0 0' }}>{profileErrors[k]}</p>}
                  </div>
                ))}
                <div>
                  <div className="label">Address</div>
                  <input className="input" value={profile.address} onChange={e => sp('address', e.target.value)} disabled={isSubmitted} />
                </div>
                <div>
                  <div className="label">Employment status</div>
                  <select className="select" value={profile.employmentStatus} onChange={e => sp('employmentStatus', e.target.value)} disabled={isSubmitted}>
                    <option value="EMPLOYED">Employed</option>
                    <option value="SELF_EMPLOYED">Self-employed</option>
                    <option value="UNEMPLOYED">Unemployed</option>
                    <option value="RETIRED">Retired</option>
                  </select>
                </div>
              </div>
              {!isSubmitted && (
                <button className="btn" type="submit" disabled={profileLoading} style={{ marginTop: 18 }}>
                  {profileLoading ? 'Saving…' : profileSaved ? '✓ Saved!' : 'Save Profile'}
                </button>
              )}
            </form>
          </div>
        )}

        {/* Documents Tab */}
        {tab === 'documents' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            {!isSubmitted && (
              <form onSubmit={upload} style={{ marginBottom: 24 }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  <div>
                    <div className="label">Document type</div>
                    <select className="select" value={docType} onChange={e => setDocType(e.target.value)}>
                      <option value="IDENTITY">Identity</option>
                      <option value="INCOME_PROOF">Income proof</option>
                      <option value="ADDRESS_PROOF">Address proof</option>
                      <option value="BANK_STATEMENT">Bank statement</option>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>
                  <div>
                    <div className="label">Tag (optional)</div>
                    <input className="input" value={docTag} onChange={e => setDocTag(e.target.value)} placeholder="e.g. passport" />
                  </div>
                </div>
                <div className="label">File (PDF, PNG, JPG — max 10 MB)</div>
                <input ref={fileRef} type="file" accept=".pdf,.png,.jpg,.jpeg" style={{ marginBottom: 8 }} />
                {uploadError && <p style={{ color: 'red', fontSize: 13 }}>{uploadError}</p>}
                <button className="btn" type="submit" disabled={uploading}>
                  {uploading ? 'Uploading…' : 'Upload Document'}
                </button>
              </form>
            )}

            {docs.length === 0 && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>No documents uploaded yet.</p>}
            {docs.map(doc => (
              <div key={doc.documentId} style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '12px 0', borderBottom: '1px solid #dbe7f5',
              }}>
                <div>
                  <div style={{ fontWeight: 600, color: '#0d2b45', fontSize: 14 }}>{doc.docType}{doc.tag ? ` · ${doc.tag}` : ''}</div>
                  <div style={{ color: '#6a7b8a', fontSize: 12 }}>{doc.originalFilename} · {new Date(doc.uploadedAt).toLocaleDateString()}</div>
                </div>
                <button
                  onClick={() => downloadDoc(doc.documentId, doc.originalFilename)}
                  style={{ background: 'none', border: '1px solid #dbe7f5', borderRadius: 8, padding: '6px 12px', cursor: 'pointer', color: '#0a67c2', fontSize: 12, fontWeight: 600 }}
                >
                  ↓ Download
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Submit Tab */}
        {tab === 'submit' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '32px 22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)', textAlign: 'center' }}>
            {isSubmitted ? (
              <>
                <div style={{ fontSize: 48, marginBottom: 12 }}>✅</div>
                <h3 style={{ color: '#0d2b45', margin: 0 }}>Application submitted!</h3>
                <p style={{ color: '#6a7b8a' }}>A lender will review your application. Current status: <strong>{app.reviewStatus}</strong></p>
              </>
            ) : (
              <>
                <div style={{ fontSize: 48, marginBottom: 12 }}>📋</div>
                <h3 style={{ color: '#0d2b45' }}>Ready to submit?</h3>
                <p style={{ color: '#6a7b8a' }}>Make sure your profile is complete and all required documents are uploaded ({docs.length} uploaded).</p>
                {submitError && <p style={{ color: 'red', fontSize: 13 }}>{submitError}</p>}
                <button
                  onClick={submitApp}
                  disabled={submitting}
                  style={{ background: 'linear-gradient(180deg,#0a67c2,#084f97)', color: '#fff', border: 'none', borderRadius: 10, padding: '14px 32px', cursor: 'pointer', fontWeight: 700, fontSize: 15, marginTop: 8 }}
                >
                  {submitting ? 'Submitting…' : 'Submit Application'}
                </button>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
