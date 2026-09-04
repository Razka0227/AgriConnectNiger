import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import Spinner from '../components/Spinner';
import StatusBadge from '../components/StatusBadge';
import { formatFCFA, formatDateTime } from '../utils/format';

const ACTIONS = {
  PENDING: [
    { s: 'CONFIRMED', l: 'Confirmer la commande', roles: ['BUYER', 'ADMIN'] },
    { s: 'CANCELLED', l: 'Annuler', roles: ['BUYER', 'FARMER', 'ADMIN'] }
  ],
  CONFIRMED: [
    { s: 'PACKED', l: 'Marquer préparée', roles: ['FARMER', 'ADMIN'] },
    { s: 'CANCELLED', l: 'Annuler', roles: ['BUYER', 'FARMER', 'ADMIN'] }
  ],
  PACKED: [
    { s: 'IN_TRANSIT', l: 'Lancer la livraison', roles: ['TRANSPORTER', 'FARMER', 'ADMIN'] },
    { s: 'CANCELLED', l: 'Annuler', roles: ['BUYER', 'FARMER', 'ADMIN'] }
  ],
  IN_TRANSIT: [{ s: 'DELIVERED', l: 'Confirmer la livraison', roles: ['TRANSPORTER', 'BUYER', 'ADMIN'] }]
};

export default function Orders() {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tab, setTab] = useState(user.role === 'BUYER' ? 'buyer' : user.role === 'TRANSPORTER' ? 'transporter' : 'seller');

  const endpoint = tab === 'buyer' ? '/orders/buyer' : tab === 'transporter' ? '/orders/transporter' : '/orders/seller';

  const load = () => {
    setLoading(true);
    api
      .get(endpoint)
      .then(setOrders)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, [endpoint, tab]);

  const transition = async (id, status) => {
    try {
      await api.patch(`/orders/${id}/status`, { status });
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const tabs = [];
  if (user.role === 'BUYER' || user.role === 'ADMIN') tabs.push({ k: 'buyer', l: 'Mes achats' });
  if (user.role === 'FARMER' || user.role === 'ADMIN') tabs.push({ k: 'seller', l: 'Mes ventes' });
  if (user.role === 'TRANSPORTER' || user.role === 'ADMIN') tabs.push({ k: 'transporter', l: 'Mes livraisons' });

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Commandes</h1>
          <p className="muted">Suivez le cycle complet : commande → préparation → livraison</p>
        </div>
      </div>

      <div className="tabs">
        {tabs.map((t) => (
          <button key={t.k} className={tab === t.k ? 'tab active' : 'tab'} onClick={() => setTab(t.k)}>
            {t.l}
          </button>
        ))}
      </div>

      {error ? <div className="alert alert-error">{error}</div> : null}
      {loading ? (
        <Spinner />
      ) : orders.length === 0 ? (
        <div className="empty card">Aucune commande pour le moment.</div>
      ) : (
        <div className="list">
          {orders.map((o) => {
            const actions = (ACTIONS[o.status] || []).filter((a) => a.roles.includes(user.role));
            return (
              <div key={o.id} className="card order-card">
                <div className="order-head">
                  <span className="order-id">Commande #{o.id}</span>
                  <StatusBadge status={o.status} />
                </div>
                <div className="order-items">
                  {o.items.map((it) => (
                    <div key={it.id} className="order-item">
                      <Link to={`/offres/${it.offerId}`} className="row-title">
                        {it.offerTitle}
                      </Link>
                      <span>
                        {it.quantity} {it.unit} × {formatFCFA(it.unitPrice)}
                      </span>
                      <span>
                        vendeur : <strong>{it.sellerName}</strong>
                      </span>
                    </div>
                  ))}
                </div>
                <div className="order-foot">
                  <div>
                    <span className="muted">
                      {o.deliveryLocality || o.deliveryAddress ? `Livraison : ${o.deliveryLocality || ''} ${o.deliveryAddress || ''}` : ''}{' '}
                      {o.transporterName ? `· Transporteur : ${o.transporterName}` : ''}
                    </span>
                    <div className="muted">{formatDateTime(o.createdAt)}</div>
                  </div>
                  <div className="order-total">Total : {formatFCFA(o.totalAmount)}</div>
                </div>
                {actions.length > 0 ? (
                  <div className="order-actions">
                    {actions.map((a) => (
                      <button key={a.s} className="btn btn-sm btn-outline" onClick={() => transition(o.id, a.s)}>
                        {a.l}
                      </button>
                    ))}
                  </div>
                ) : null}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
