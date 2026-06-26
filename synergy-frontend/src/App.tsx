import { Navigate, Route, Routes } from 'react-router-dom';
import GetStarted from './pages/GetStarted';
import Verify from './pages/Verify';
import Register from './pages/Register';
import Login from './pages/Login';
import ResetPassword from './pages/ResetPassword';
import Purpose from './pages/Purpose';
import PassportInit from './pages/PassportInit';
import BorrowerDashboard from './pages/borrower/BorrowerDashboard';
import Apply from './pages/borrower/Apply';
import ApplicationDetail from './pages/borrower/ApplicationDetail';
import LenderDashboard from './pages/lender/LenderDashboard';
import LenderApplicationDetail from './pages/lender/LenderApplicationDetail';
import ProtectedRoute from './components/ProtectedRoute';

export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<Navigate to="/get-started" replace />} />
      <Route path="/get-started" element={<GetStarted />} />
      <Route path="/verify" element={<Verify />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route path="/reset-password" element={<ResetPassword />} />

      {/* Borrower onboarding */}
      <Route path="/purpose" element={<ProtectedRoute role="BORROWER"><Purpose /></ProtectedRoute>} />
      <Route path="/passport-init" element={<ProtectedRoute role="BORROWER"><PassportInit /></ProtectedRoute>} />

      {/* Borrower app */}
      <Route path="/borrower/dashboard" element={<ProtectedRoute role="BORROWER"><BorrowerDashboard /></ProtectedRoute>} />
      <Route path="/borrower/apply" element={<ProtectedRoute role="BORROWER"><Apply /></ProtectedRoute>} />
      <Route path="/borrower/applications/:id" element={<ProtectedRoute role="BORROWER"><ApplicationDetail /></ProtectedRoute>} />

      {/* Lender app */}
      <Route path="/lender/dashboard" element={<ProtectedRoute role="LENDER"><LenderDashboard /></ProtectedRoute>} />
      <Route path="/lender/applications/:id" element={<ProtectedRoute role="LENDER"><LenderApplicationDetail /></ProtectedRoute>} />

      {/* Legacy aliases */}
      <Route path="/dashboard" element={<Navigate to="/borrower/dashboard" replace />} />
      <Route path="/create-password" element={<Navigate to="/register" replace />} />

      <Route path="*" element={<Navigate to="/get-started" replace />} />
    </Routes>
  );
}
