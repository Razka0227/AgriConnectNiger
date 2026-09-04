import { Link } from 'react-router-dom';
import { formatFCFA, formatNumber, timeAgo } from '../utils/format';

export default function OfferCard({ offer }) {
  const stars = offer.sellerRating ? '★ '.repeat(Math.round(offer.sellerRating)) : '';
  return (
    <Link to={`/offres/${offer.id}`} className="card offer-card">
      <div className="offer-card-head">
        <span className="chip chip-cat">{offer.product.categoryLabel}</span>
        {offer.qualityGrade ? <span className="chip chip-grade">{offer.qualityGrade}</span> : null}
      </div>
      <h3 className="offer-card-title">{offer.title}</h3>
      <p className="offer-card-product">{offer.product.name} · {offer.unit}</p>
      <div className="offer-card-price">
        <span className="price">{formatFCFA(offer.pricePerUnit)}</span>
        <span className="per"> / {offer.unit}</span>
      </div>
      <div className="offer-card-meta">
        <span>📍 {offer.locality || offer.region}, {offer.region}</span>
        <span>📦 {formatNumber(offer.quantity)} {offer.unit}</span>
      </div>
      <div className="offer-card-seller">
        <span className="avatar avatar-sm">{(offer.sellerName || '?').split(' ').map((w) => w[0]).slice(0, 2).join('')}</span>
        <span>{offer.sellerName}</span>
        <span className="stars">{stars || '—'}</span>
        <span className="muted">{timeAgo(offer.createdAt)}</span>
      </div>
    </Link>
  );
}
