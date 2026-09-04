import { useEffect, useState } from 'react';
import api from '../api/client';
import OfferCard from '../components/OfferCard';
import Spinner from '../components/Spinner';

export default function Marketplace() {
  const [offers, setOffers] = useState([]);
  const [products, setProducts] = useState([]);
  const [regions, setRegions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ q: '', productId: '', region: '', maxPrice: '' });

  const load = () => {
    setLoading(true);
    const params = new URLSearchParams();
    if (filters.q) params.set('q', filters.q);
    if (filters.productId) params.set('productId', filters.productId);
    if (filters.region) params.set('region', filters.region);
    if (filters.maxPrice) params.set('maxPrice', filters.maxPrice);
    api
      .get('/offers?' + params.toString())
      .then(setOffers)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    api.get('/products').then(setProducts).catch(() => {});
    api.get('/regions').then(setRegions).catch(() => {});
  }, []);

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.productId, filters.region, filters.maxPrice]);

  const set = (k) => (e) => setFilters({ ...filters, [k]: e.target.value });

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Marché agricole</h1>
          <p className="muted">Produits disponibles directement chez les producteurs</p>
        </div>
      </div>

      <div className="filters card">
        <input
          className="search-input"
          placeholder="🔍 Rechercher (produit, village…)"
          value={filters.q}
          onChange={set('q')}
          onKeyDown={(e) => e.key === 'Enter' && load()}
        />
        <select value={filters.productId} onChange={set('productId')}>
          <option value="">Tous les produits</option>
          {products.map((p) => (
            <option key={p.id} value={p.id}>
              {p.name}
            </option>
          ))}
        </select>
        <select value={filters.region} onChange={set('region')}>
          <option value="">Toutes les régions</option>
          {regions.map((r) => (
            <option key={r.code} value={r.code}>
              {r.label}
            </option>
          ))}
        </select>
        <select value={filters.maxPrice} onChange={set('maxPrice')}>
          <option value="">Tous les prix</option>
          <option value="15000">≤ 15 000 FCFA</option>
          <option value="25000">≤ 25 000 FCFA</option>
          <option value="40000">≤ 40 000 FCFA</option>
        </select>
        <button className="btn btn-outline" onClick={load}>
          Rechercher
        </button>
      </div>

      {loading ? (
        <Spinner />
      ) : offers.length === 0 ? (
        <div className="empty card">Aucune offre ne correspond à votre recherche.</div>
      ) : (
        <div className="offer-grid">
          {offers.map((o) => (
            <OfferCard key={o.id} offer={o} />
          ))}
        </div>
      )}
    </div>
  );
}
