import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const PURPOSES = [
  { key: 'LOAN', icon: '💳', label: 'Personal Loan' },
  { key: 'MORTGAGE', icon: '🏠', label: 'Mortgage' },
  { key: 'BUSINESS', icon: '🏢', label: 'Business Loan' },
  { key: 'AUTO', icon: '🚗', label: 'Auto Finance' },
];

export default function Purpose() {
  const nav = useNavigate();
  const [selected, setSelected] = useState('');

  function proceed() {
    sessionStorage.setItem('gcp.purpose', selected);
    nav('/passport-init');
  }

  return (
    <div className="page-bg">
      <div className="card" style={{ width: 420 }}>
        <div className="card-header">What are you applying for?</div>
        <div className="card-body">
          <p className="hint">Select the purpose of your application.</p>
          <div className="grid2">
            {PURPOSES.map(p => (
              <div
                key={p.key}
                className={`tile${selected === p.key ? ' selected' : ''}`}
                onClick={() => setSelected(p.key)}
              >
                <div className="icon">{p.icon}</div>
                <span style={{ fontSize: 13, fontWeight: 600 }}>{p.label}</span>
              </div>
            ))}
          </div>
          <button className="btn" disabled={!selected} onClick={proceed} style={{ marginTop: 18 }}>
            Continue
          </button>
        </div>
      </div>
    </div>
  );
}
