import { useEffect, useRef, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api, BASE } from '../../lib/api';
import { getToken } from '../../lib/token';

interface AppDetail {
  applicationId: string;
  borrowerEmail?: string;
  purpose: string;
  amount: number;
  currencyCode: string;
  termMonths: number;
  status: string;
  reviewStatus: string;
  profile?: Record<string, unknown>;
}

interface DocItem {
  documentId: string;
  docType: string;
  tag?: string;
  originalFilename: string;
  uploadedAt: string;
}

interface Note {
  noteId: string;
  body: string;
  createdAt: string;
  authorName?: string;
}

interface AuditEvent {
  id: string;
  action: string;
  detail?: string;
  recordedAt: string;
}

const TRANSITIONS = ['UNDER_REVIEW', 'INFO_REQUESTED', 'REVIEWED', 'APPROVED', 'REJECTED'];

export default function LenderApplicationDetail() {
  const { id } = useParams<{ id: string }>();
  const [tab, setTab] = useState<'overview' | 'documents' | 'notes' | 'audit'>('overview');
  const [app, setApp] = useState<AppDetail | null>(null);
  const [docs, setDocs] = useState<DocItem[]>([]);
  const [notes, setNotes] = useState<Note[]>([]);
  const [audit, setAudit] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Review
  const [reviewStatus, setReviewStatus] = useState('');
  const [recommendation, setRecommendation] = useState('');
  const [reviewError, setReviewError] = useState('');
  const [reviewLoading, setReviewLoading] = useState(false);
  const [reviewSaved, setReviewSaved] = useState(false);

  // Note
  const [noteBody, setNoteBody] = useState('');
  const [noteError, setNoteError] = useState('');
  const [noteLoading, setNoteLoading] = useState(false);

  useEffect(() => {
    Promise.all([
      api.get<AppDetail>(`/v1/lender/applications/${id}`),
      api.get<DocItem[]>(`/v1/lender/applications/${id}/documents`).catch(() => [] as DocItem[]),
      api.get<Note[]>(`/v1/lender/applications/${id}/notes`).catch(() => [] as Note[]),
      api.get<AuditEvent[]>(`/v1/lender/applications/${id}/audit`).catch(() => [] as AuditEvent[]),
    ]).then(([a, d, n, au]) => {
      setApp(a);
      setDocs(d);
      setNotes(n);
      setAudit(au);
      setReviewStatus(a.reviewStatus);
    }).catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  async function submitReview(e: React.FormEvent) {
    e.preventDefault();
    setReviewError('');
    if (!reviewStatus) return setReviewError('Select a review status.');
    setReviewLoading(true);
    try {
      const body: Record<string, string> = { reviewStatus };
      if (recommendation) body.recommendation = recommendation;
      const res = await api.put<AppDetail>(`/v1/lender/applications/${id}/review`, body);
      setApp(res);
      setReviewSaved(true);
      setTimeout(() => setReviewSaved(false), 2000);
    } catch (err: unknown) {
      setReviewError((err as Error).message);
    } finally {
      setReviewLoading(false);
    }
  }

  async function addNote(e: React.FormEvent) {
    e.preventDefault();
    setNoteError('');
    if (!noteBody.trim()) return setNoteError('Note body is required.');
    setNoteLoading(true);
    try {
      const n = await api.post<Note>(`/v1/lender/applications/${id}/notes`, { body: noteBody });
      setNotes(prev => [n, ...prev]);
      setNoteBody('');
    } catch (err: unknown) {
      const e = err as Error & { fields?: Record<string, string> };
      setNoteError(e.fields?.body ?? e.message);
    } finally {
      setNoteLoading(false);
    }
  }

  async function downloadDoc(docId: string, filename: string) {
    const token = getToken();
    const res = await fetch(`${BASE}/v1/lender/applications/${id}/documents/${docId}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!res.ok) return;
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename; a.click();
    URL.revokeObjectURL(url);
  }

  if (loading) return <div className="page-bg"><p style={{ color: '#6a7b8a' }}>Loading…</p></div>;
  if (error || !app) return <div className="page-bg"><p style={{ color: 'red' }}>{error || 'Not found'}</p></div>;

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg,#bfe0ff,#eaf4ff)', padding: '32px 16px' }}>
      <div style={{ maxWidth: 760, margin: '0 auto' }}>
        <Link to="/lender/dashboard" style={{ textDecoration: 'none', color: '#0d2b45', fontWeight: 700, display: 'inline-flex', alignItems: 'center', gap: 6, marginBottom: 16 }}>
          ← Applications
        </Link>

        {/* Header */}
        <div style={{ background: '#fff', borderRadius: 14, padding: '16px 22px', marginBottom: 16, boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontWeight: 700, color: '#0d2b45', fontSize: 16 }}>{app.purpose}</div>
              <div style={{ color: '#6a7b8a', fontSize: 13 }}>{app.currencyCode} {Number(app.amount).toLocaleString()} · {app.termMonths} months</div>
              {app.borrowerEmail && <div style={{ color: '#6a7b8a', fontSize: 13 }}>{app.borrowerEmail}</div>}
            </div>
            <span style={{ background: '#0a67c220', color: '#0a67c2', borderRadius: 8, padding: '4px 12px', fontSize: 12, fontWeight: 700 }}>
              {app.reviewStatus}
            </span>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', gap: 4, marginBottom: 16, background: '#fff', borderRadius: 12, padding: 4, boxShadow: '0 2px 8px rgba(10,40,80,.06)' }}>
          {(['overview', 'documents', 'notes', 'audit'] as const).map(t => (
            <button
              key={t}
              onClick={() => setTab(t)}
              style={{
                flex: 1, border: 'none', borderRadius: 10, padding: '10px', cursor: 'pointer', fontWeight: 700, fontSize: 12,
                background: tab === t ? 'linear-gradient(180deg,#0a67c2,#084f97)' : 'transparent',
                color: tab === t ? '#fff' : '#6a7b8a',
              }}
            >
              {t === 'overview' ? 'Review' : t === 'documents' ? `Docs (${docs.length})` : t === 'notes' ? `Notes (${notes.length})` : 'Audit'}
            </button>
          ))}
        </div>

        {/* Overview / Review Tab */}
        {tab === 'overview' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            {app.profile && (
              <div style={{ marginBottom: 20 }}>
                <h4 style={{ color: '#0d2b45', marginBottom: 10 }}>Borrower Profile</h4>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                  {Object.entries(app.profile).map(([k, v]) => (
                    v ? <div key={k} style={{ fontSize: 13 }}>
                      <span style={{ color: '#6a7b8a', marginRight: 6 }}>{k}:</span>
                      <strong style={{ color: '#0d2b45' }}>{String(v)}</strong>
                    </div> : null
                  ))}
                </div>
              </div>
            )}
            <h4 style={{ color: '#0d2b45', marginBottom: 10 }}>Update Review Status</h4>
            <form onSubmit={submitReview}>
              <div className="label">Review status</div>
              <select className="select" value={reviewStatus} onChange={e => setReviewStatus(e.target.value)}>
                {TRANSITIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
              <div className="label">Recommendation (optional)</div>
              <input className="input" value={recommendation} onChange={e => setRecommendation(e.target.value)} placeholder="Approve / Decline / …" />
              {reviewError && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{reviewError}</p>}
              <button className="btn" type="submit" disabled={reviewLoading} style={{ marginTop: 14 }}>
                {reviewLoading ? 'Saving…' : reviewSaved ? '✓ Saved!' : 'Update Review'}
              </button>
            </form>
          </div>
        )}

        {/* Documents Tab */}
        {tab === 'documents' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            {docs.length === 0 && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>No documents.</p>}
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

        {/* Notes Tab */}
        {tab === 'notes' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            <form onSubmit={addNote} style={{ marginBottom: 20 }}>
              <div className="label">Add note</div>
              <textarea
                value={noteBody}
                onChange={e => setNoteBody(e.target.value)}
                rows={3}
                style={{ width: '100%', border: '1px solid #dbe7f5', borderRadius: 10, padding: '10px 12px', fontSize: 14, outline: 'none', resize: 'vertical', fontFamily: 'inherit' }}
                placeholder="Internal review note…"
              />
              {noteError && <p style={{ color: 'red', fontSize: 13 }}>{noteError}</p>}
              <button className="btn" type="submit" disabled={noteLoading} style={{ marginTop: 4 }}>
                {noteLoading ? 'Adding…' : 'Add Note'}
              </button>
            </form>
            {notes.length === 0 && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>No notes yet.</p>}
            {notes.map(n => (
              <div key={n.noteId} style={{ padding: '12px 0', borderBottom: '1px solid #dbe7f5' }}>
                <div style={{ fontSize: 14, color: '#0d2b45', marginBottom: 4 }}>{n.body}</div>
                <div style={{ fontSize: 12, color: '#6a7b8a' }}>
                  {n.authorName ?? 'Reviewer'} · {new Date(n.createdAt).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Audit Tab */}
        {tab === 'audit' && (
          <div style={{ background: '#fff', borderRadius: 14, padding: '22px', boxShadow: '0 4px 16px rgba(10,40,80,.08)' }}>
            {audit.length === 0 && <p style={{ color: '#6a7b8a', textAlign: 'center' }}>No audit events.</p>}
            {audit.map(ev => (
              <div key={ev.id} style={{ padding: '10px 0', borderBottom: '1px solid #dbe7f5', display: 'flex', justifyContent: 'space-between' }}>
                <div>
                  <span style={{ fontWeight: 700, color: '#0d2b45', fontSize: 13 }}>{ev.action}</span>
                  {ev.detail && <span style={{ color: '#6a7b8a', fontSize: 12, marginLeft: 8 }}>{ev.detail}</span>}
                </div>
                <div style={{ color: '#6a7b8a', fontSize: 12 }}>{new Date(ev.recordedAt).toLocaleString()}</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
