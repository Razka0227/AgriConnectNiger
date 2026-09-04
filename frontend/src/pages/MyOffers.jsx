import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import Spinner from '../components/Spinner';
import StatusBadge from '../components/StatusBadge';
import { formatFCFA } from '../utils/format';

export default function MyOffers() {
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    api
      .get('/offers/mine/list')
      .then(setOffers)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const changeStatus = async (id, status) => {
    try {
      await api.patch(`/offers/${id}/status`, status);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Mes offres</h1>
          <p className="muted">Gérez vos produits en vente</p>
        </div>
        <Link to="/offres/nouvelle" className="btn btn-primary">
          + Nouvelle offre
        </Link>
      </div>

      {error ? <div className="alert alert-error">{error}</div> : null}
      {loading ? (
        <Spinner />
      ) : offers.length === 0 ? (
        <div className="empty card">
          Vous n'avez pas encore d'offre. <Link to="/offres/nouvelle">Publiez votre première offre</Link>.
        </div>
      ) : (
        <div className="list">
          {offers.map((o) => (
            <div key={o.id} className="card row-card">
              <div className="row-main">
                <Link to={`/offres/${o.id}`} className="row-title">
                  {o.title}
                </Link>
                <div className="row-sub">
                  <span>{o.product.name} · {o.unit}</span>
                  <span>📍 {o.locality || o.region}</span>
                </div>
              </div>
              <div className="row-right">
                <span className="price">{formatFCFA(o.pricePerUnit)}</span>
                <StatusBadge status={o.status} />
                {o.status === 'ACTIVE' ? (
                  <button className="btn btn-sm btn-danger" onClick={() => changeStatus(o.id, 'CLOSED')}>
                    Clôturer
                  </button>
                ) : null}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
