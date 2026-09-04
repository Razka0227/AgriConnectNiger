import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import Spinner from '../components/Spinner';
import StatusBadge from '../components/StatusBadge';
import { formatFCFA, formatNumber, formatDateTime } from '../utils/format';

export default function OfferDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [offer, setOffer] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [qty, setQty] = useState('');
  const [address, setAddress] = useState('');
  const [locality, setLocality] = useState('');
  const [notes, setNotes] = useState('');
  const [busy, setBusy] = useState(false);

  const [review, setReview] = useState({ rating: 5, comment: '' });

  const load = () => {
    api
      .get(`/offers/${id}`)
      .then((o) => {
        setOffer(o);
        return api.get(`/reviews/${o.sellerId}`);
      })
      .then(setReviews)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (loading) {
    return <Spinner />;
  }
  if (!offer) {
    return <div className="empty card">{error || 'Offre introuvable'}</div>;
  }

  const isBuyer = user.role === 'BUYER';
  const canOrder = isBuyer && offer.status === 'ACTIVE';
  const total = qty ? Math.round(qty * offer.pricePerUnit * 100) / 100 : 0;

  const submitOrder = async (e) => {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      const order = await api.post('/orders', {
        items: [{ offerId: offer.id, quantity: parseFloat(qty) }],
        deliveryAddress: address,
        deliveryLocality: locality,
        notes
      });
      setSuccess(`Commande #${order.id} enregistrée (${formatFCFA(order.totalAmount)})`);
      navigate('/commandes');
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  const submitReview = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError('');
    try {
      await api.post(`/reviews/${offer.sellerId}`, review);
      api.get(`/reviews/${offer.sellerId}`).then(setReviews).catch(() => {});
      setReview({ rating: 5, comment: '' });
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <Link to="/marche" className="link">
        ← Retour au marché
      </Link>
      {error ? <div className="alert alert-error">{error}</div> : null}
      {success ? <div className="alert alert-success">{success}</div> : null}

      <div className="offer-detail card">
        <div className="offer-detail-head">
          <div>
            <span className="chip chip-cat">{offer.product.categoryLabel}</span>{' '}
            {offer.qualityGrade ? <span className="chip chip-grade">{offer.qualityGrade}</span> : null}{' '}
            <StatusBadge status={offer.status} />
            <h1>{offer.title}</h1>
            <p className="muted">
              {offer.product.name} ({offer.product.localName}) · {offer.unit}
            </p>
          </div>
        </div>

        <div className="detail-grid">
          <div>
            <h2 className="detail-price">{formatFCFA(offer.pricePerUnit)} <span className="muted">/ {offer.unit}</span></h2>
            <p>Quantité disponible : <strong>{formatNumber(offer.quantity)} {offer.unit}</strong></p>
            {offer.minOrderQuantity ? (
              <p>Commande minimale : <strong>{formatNumber(offer.minOrderQuantity)} {offer.unit}</strong></p>
            ) : null}
            <p>📍 {offer.locality || offer.region}, région {offer.region}</p>
            {offer.description ? <p className="muted">{offer.description}</p> : null}
          </div>
          <div className="seller-box">
            <h3>Vendeur</h3>
            <p>
              <strong>{offer.sellerName}</strong>
            </p>
            <p className="muted">{offer.sellerOrganization || 'Producteur indépendant'}</p>
            <p>
              Note : {'★'.repeat(Math.max(1, Math.round(offer.sellerRating)))} {offer.sellerRating > 0 ? `${offer.sellerRating}/5` : '—'}
            </p>
            <p className="muted">Publié le {formatDateTime(offer.createdAt)}</p>
          </div>
        </div>

        {canOrder ? (
          <form onSubmit={submitOrder} className="form order-form">
            <h3>Commander</h3>
            <div className="form-row">
              <label>
                Quantité ({offer.unit})
                <input
                  type="number"
                  min={offer.minOrderQuantity || 1}
                  step="0.5"
                  value={qty}
                  onChange={(e) => setQty(e.target.value)}
                  required
                />
              </label>
              <label>
                Localité de livraison
                <input value={locality} onChange={(e) => setLocality(e.target.value)} placeholder="Niamey" />
              </label>
            </div>
            <label>
              Adresse de livraison
              <input value={address} onChange={(e) => setAddress(e.target.value)} placeholder="Marché Katako, stand 12" />
            </label>
            <label>
              Remarques
              <input value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="Préciser la qualité attendue…" />
            </label>
            {qty ? (
              <p>
                Total estimé : <strong>{formatFCFA(total)}</strong>
              </p>
            ) : null}
            <button className="btn btn-primary" disabled={busy}>
              {busy ? 'Envoi…' : 'Passer la commande'}
            </button>
          </form>
        ) : offer.status === 'RESERVED' ? (
          <div className="alert alert-warn">Cette offre est réservée par une commande en cours.</div>
        ) : (
          <div className="alert alert-warn">Cette offre n'est plus disponible.</div>
        )}
      </div>

      <div className="card review-box">
        <h3>Évaluations du vendeur ({reviews.length})</h3>
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
        <form onSubmit={submitReview} className="form review-form">
          <h4>Donner mon avis</h4>
          <label>
            Note
            <select value={review.rating} onChange={(e) => setReview({ ...review, rating: Number(e.target.value) })}>
              {[1, 2, 3, 4, 5].map((n) => (
                <option key={n} value={n}>
                  {n} / 5
                </option>
              ))}
            </select>
          </label>
          <label>
            Commentaire
            <textarea value={review.comment} onChange={(e) => setReview({ ...review, comment: e.target.value })} />
          </label>
          <button className="btn btn-outline" disabled={busy}>
            Envoyer
          </button>
        </form>
      </div>
    </div>
  );
}
