import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import Spinner from '../components/Spinner';
import { formatDate } from '../utils/format';

export default function Profile() {
  const { user } = useAuth();
  const [reviews, setReviews] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([api.get(`/reviews/${user.id}`), api.get('/stats/dashboard')])
      .then(([r, s]) => {
        setReviews(r);
        setStats(s);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [user.id]);

  if (loading) {
    return <Spinner />;
  }

  const initials = user.name.split(' ').map((w) => w[0]).slice(0, 2).join('');

  return (
    <div>
      <h1>Mon profil</h1>
      <div className="card profile-card">
        <div className="profile-head">
          <span className="avatar avatar-lg">{initials}</span>
          <div>
            <h2>{user.name}</h2>
            <p className="muted">
              {user.roleLabel} · {user.locality || '—'} · région {user.region}
            </p>
            {user.organization ? <p className="muted">{user.organization}</p> : null}
          </div>
        </div>
        <div className="profile-details">
          <p>
            📞 <strong>{user.phone}</strong>
          </p>
          {user.email ? (
            <p>
              ✉️ <strong>{user.email}</strong>
            </p>
          ) : null}
          {stats?.myRating > 0 ? (
            <p>
              ⭐ <strong>{stats.myRating}/5</strong> <span className="muted">({reviews.length} évaluation(s))</span>
            </p>
          ) : null}
          <p className="muted">Membre depuis le {formatDate(user.createdAt)}</p>
        </div>
      </div>

      <div className="card">
        <h3>Mes évaluations ({reviews.length})</h3>
        {reviews.length === 0 ? <p className="muted">Aucune évaluation pour le moment.</p> : null}
        {reviews.map((r) => (
          <div key={r.id} className="review-item">
            <div className="review-head">
              <strong>{r.reviewerName}</strong>
              <span>{'★'.repeat(r.rating)}</span>
            </div>
            <p className="muted">{r.comment}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
