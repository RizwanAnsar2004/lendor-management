import { Link } from 'react-router-dom';

export default function ResetPassword() {
  return (
    <div className="page-bg">
      <div className="card">
        <div className="card-header">Reset password</div>
        <div className="card-body">
          <p className="hint">Password reset is not available in this demo. Contact your administrator.</p>
          <div className="footer">
            <Link className="link" to="/login">Back to sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
