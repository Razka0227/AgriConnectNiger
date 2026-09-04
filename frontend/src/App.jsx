import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Marketplace from './pages/Marketplace';
import OfferDetail from './pages/OfferDetail';
import NewOffer from './pages/NewOffer';
import MyOffers from './pages/MyOffers';
import Orders from './pages/Orders';
import Prices from './pages/Prices';
import Weather from './pages/Weather';
import Transport from './pages/Transport';
import Notifications from './pages/Notifications';
import Profile from './pages/Profile';

function Protected({ children }) {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) {
    return <div className="center-screen">Chargement…</div>;
  }
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/"
        element={
          <Protected>
            <Layout />
          </Protected>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="marche" element={<Marketplace />} />
        <Route path="offres/:id" element={<OfferDetail />} />
        <Route path="offres/nouvelle" element={<NewOffer />} />
        <Route path="mes-offres" element={<MyOffers />} />
        <Route path="commandes" element={<Orders />} />
        <Route path="prix" element={<Prices />} />
        <Route path="meteo" element={<Weather />} />
        <Route path="transport" element={<Transport />} />
        <Route path="notifications" element={<Notifications />} />
        <Route path="profil" element={<Profile />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
