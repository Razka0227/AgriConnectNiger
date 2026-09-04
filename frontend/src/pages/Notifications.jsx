import { useEffect, useState } from 'react';
import api from '../api/client';
import Spinner from '../components/Spinner';
import { timeAgo } from '../utils/format';

const TYPE_ICON = { ORDER: '📦', PRICE_ALERT: '📈', WEATHER: '🌤️', SYSTEM: '⚙️', INFO: 'ℹ️' };

export default function Notifications() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api
      .get('/notifications')
      .then(setItems)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const markAll = async () => {
    try {
      await api.patch('/notifications/read-all', {});
      load();
    } catch {
      /* ignore */
    }
  };

  const markOne = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`, {});
      setItems((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    } catch {
      /* ignore */
    }
  };

  const unread = items.filter((n) => !n.read).length;

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Alertes</h1>
          <p className="muted">
            {unread > 0 ? `${unread} non lue(s)` : 'Tout est à jour'}
          </p>
        </div>
        {unread > 0 ? (
          <button className="btn btn-outline" onClick={markAll}>
            Tout marquer lu
          </button>
        ) : null}
      </div>

      {loading ? (
        <Spinner />
      ) : items.length === 0 ? (
        <div className="empty card">Aucune alerte pour le moment.</div>
      ) : (
        <div className="list">
          {items.map((n) => (
            <button key={n.id} className={`card notif ${n.read ? 'notif-read' : ''}`} onClick={() => !n.read && markOne(n.id)}>
              <span className="notif-icon">{TYPE_ICON[n.type] || '🔔'}</span>
              <div>
                <div className="notif-title">{n.title}</div>
                <div className="muted">{n.message}</div>
                <div className="notif-meta">
                  <span>{n.type}</span>·<span>{n.channel}</span>·<span>{timeAgo(n.createdAt)}</span>
                </div>
              </div>
              {!n.read ? <span className="dot" /> : null}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
