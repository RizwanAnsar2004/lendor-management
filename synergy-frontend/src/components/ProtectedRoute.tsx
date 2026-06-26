import { Navigate } from 'react-router-dom';
import { isAuthenticated, getRole } from '../lib/token';

interface Props {
  children: React.ReactNode;
  role?: 'BORROWER' | 'LENDER';
}

export default function ProtectedRoute({ children, role }: Props) {
  if (!isAuthenticated()) return <Navigate to="/login" replace />;
  if (role && getRole() !== role) return <Navigate to="/login" replace />;
  return <>{children}</>;
}
