import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import StatCard from '../components/StatCard';
import OfferCard from '../components/OfferCard';
import Spinner from '../components/Spinner';

export default function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([api.get('/stats/dashboard'), api.get('/offers')])
      .then(([s, o]) => {
        setStats(s);
        setOffers(o.slice(0, 4));
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Spinner />;
  }

  const greeting = `Bonjour, ${user.name.split(' ')[0]} 👋`;
  const isFarmer = user.role === 'FARMER';

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>{greeting}</h1>
          <p className="muted">
            {user.roleLabel} · {user.locality || user.region} · {user.organization || '—'}
          </p>
        </div>
        <div className="page-actions">
          {isFarmer ? (
            <Link to="/offres/nouvelle" className="btn btn-primary">
              + Publier une offre
            </Link>
          ) : (
            <Link to="/marche" className="btn btn-primary">
              Voir le marché
            </Link>
          )}
        </div>
      </div>

      <div className="stats-grid">
        <StatCard icon="🌾" label="Offres actives" value={stats.activeOffers} />
        <StatCard
          icon={isFarmer ? '📦' : '🛒'}
          label={isFarmer ? 'Mes commandes' : 'Mes commandes'}
          value={stats.myOrders}
        />
        {isFarmer ? <StatCard icon="🚚" label="À livrer" value={stats.ordersToDeliver} /> : null}
        <StatCard icon="🔔" label="Alertes non lues" value={stats.unreadNotifications} />
        {stats.myRating > 0 ? <StatCard icon="⭐" label="Ma note" value={`${stats.myRating}/5`} /> : null}
      </div>

      <div className="section">
        <div className="section-head">
          <h2>Dernières offres</h2>
          <Link to="/marche" className="link">
            Tout voir →
          </Link>
        </div>
        <div className="offer-grid">
          {offers.map((o) => (
            <OfferCard key={o.id} offer={o} />
          ))}
        </div>
      </div>
    </div>
  );
}
