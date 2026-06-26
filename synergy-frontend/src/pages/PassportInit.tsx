import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../lib/api';

const SOURCES = ['BANK', 'TAX', 'PAYROLL', 'CREDIT_BUREAU'];
const CORRIDORS = [
  { origin: 'US', dest: 'SG', label: 'US → Singapore' },
  { origin: 'IN', dest: 'AE', label: 'India → UAE' },
  { origin: 'SG', dest: 'AU', label: 'Singapore → Australia' },
  { origin: 'AE', dest: 'GB', label: 'UAE → UK' },
];

export default function PassportInit() {
  const nav = useNavigate();
  const [step, setStep] = useState(0);
  const [corridor, setCorridor] = useState(CORRIDORS[0]);
  const [fullName, setFullName] = useState('');
  const [dob, setDob] = useState('');
  const [sources, setSources] = useState<string[]>([]);
  const [passportId, setPassportId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const purpose = sessionStorage.getItem('gcp.purpose') ?? 'LOAN';

  function toggleSource(s: string) {
    setSources(prev => prev.includes(s) ? prev.filter(x => x !== s) : [...prev, s]);
  }

  async function initPassport() {
    setError('');
    setLoading(true);
    try {
      const res = await api.post<{ passportId: string; status: string }>('/v1/passports/init', {
        purpose,
        originCountry: corridor.origin,
        destCountry: corridor.dest,
        fullName,
        dob,
      });
      setPassportId(res.passportId);
      setStep(2);
    } catch (err: unknown) {
      const e = err as Error & { fields?: Record<string, string> };
      setError(e.fields ? Object.values(e.fields).join(', ') : e.message);
    } finally {
      setLoading(false);
    }
  }

  async function connectSources() {
    setError('');
    setLoading(true);
    try {
      await api.post(`/v1/passports/${passportId}/sources`, { sources });
      setStep(3);
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function generate() {
    setError('');
    setLoading(true);
    try {
      await api.post(`/v1/passports/${passportId}/generate`);
      sessionStorage.setItem('gcp.passportId', passportId);
      nav('/borrower/dashboard');
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-bg">
      <div className="card" style={{ width: 420 }}>
        {step === 0 && (
          <>
            <div className="card-header">Step 1 — Choose corridor</div>
            <div className="card-body">
              <p className="hint">Select your origin and destination countries.</p>
              {CORRIDORS.map(c => (
                <div
                  key={c.label}
                  className={`tile${corridor.label === c.label ? ' selected' : ''}`}
                  style={{ flexDirection: 'row', justifyContent: 'flex-start', marginBottom: 8 }}
                  onClick={() => setCorridor(c)}
                >
                  <span style={{ fontSize: 14, fontWeight: 600 }}>{c.label}</span>
                </div>
              ))}
              <button className="btn" onClick={() => setStep(1)}>Next</button>
            </div>
          </>
        )}
        {step === 1 && (
          <>
            <div className="card-header">Step 2 — Your profile</div>
            <div className="card-body">
              <div className="label">Full name</div>
              <input className="input" value={fullName} onChange={e => setFullName(e.target.value)} placeholder="As on ID" />
              <div className="label">Date of birth</div>
              <input className="input" type="date" value={dob} onChange={e => setDob(e.target.value)} />
              {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
              <button className="btn" disabled={loading || !fullName || !dob} onClick={initPassport}>
                {loading ? 'Saving…' : 'Next'}
              </button>
            </div>
          </>
        )}
        {step === 2 && (
          <>
            <div className="card-header">Step 3 — Connect data sources</div>
            <div className="card-body">
              <p className="hint">Select the data sources to include in your passport.</p>
              <div className="grid2">
                {SOURCES.map(s => (
                  <div
                    key={s}
                    className={`tile${sources.includes(s) ? ' selected' : ''}`}
                    onClick={() => toggleSource(s)}
                  >
                    <span style={{ fontSize: 13, fontWeight: 600 }}>{s}</span>
                  </div>
                ))}
              </div>
              {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
              <button className="btn" disabled={loading} onClick={connectSources} style={{ marginTop: 14 }}>
                {loading ? 'Connecting…' : 'Connect'}
              </button>
            </div>
          </>
        )}
        {step === 3 && (
          <>
            <div className="card-header">Step 4 — Generate passport</div>
            <div className="card-body">
              <p className="hint">Your passport is ready to be generated. This finalises your credit identity.</p>
              {error && <p style={{ color: 'red', fontSize: 13, marginTop: 8 }}>{error}</p>}
              <button className="btn" disabled={loading} onClick={generate}>
                {loading ? 'Generating…' : '🚀 Generate Passport'}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
