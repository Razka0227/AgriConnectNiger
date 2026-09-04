import { useEffect, useState } from 'react';
import api from '../api/client';
import Spinner from '../components/Spinner';
import { formatFCFA, formatDate } from '../utils/format';

export default function Prices() {
  const [products, setProducts] = useState([]);
  const [regions, setRegions] = useState([]);
  const [prices, setPrices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [productId, setProductId] = useState('');
  const [region, setRegion] = useState('');

  useEffect(() => {
    api.get('/products').then(setProducts).catch(() => {});
    api.get('/regions').then(setRegions).catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams();
    if (productId) params.set('productId', productId);
    if (region) params.set('region', region);
    api
      .get('/prices?' + params.toString())
      .then(setPrices)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [productId, region]);

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Prix du marché</h1>
          <p className="muted">Cotations en FCFA relevées par le SIMA (Système d'Information des Marchés Agricoles)</p>
        </div>
      </div>

      <div className="filters card">
        <select value={productId} onChange={(e) => setProductId(e.target.value)}>
          <option value="">Tous les produits</option>
          {products.map((p) => (
            <option key={p.id} value={p.id}>
              {p.name}
            </option>
          ))}
        </select>
        <select value={region} onChange={(e) => setRegion(e.target.value)}>
          <option value="">Toutes les régions</option>
          {regions.map((r) => (
            <option key={r.code} value={r.code}>
              {r.label}
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <Spinner />
      ) : (
        <div className="table-wrap card">
          <table className="table">
            <thead>
              <tr>
                <th>Produit</th>
                <th>Marché</th>
                <th>Région</th>
                <th>Prix</th>
                <th>Date</th>
                <th>Source</th>
              </tr>
            </thead>
            <tbody>
              {prices.map((p) => (
                <tr key={p.id}>
                  <td>
                    <strong>{p.productName}</strong>
                    <div className="muted">{p.unit}</div>
                  </td>
                  <td>{p.marketName}</td>
                  <td>{p.region}</td>
                  <td className="price">{formatFCFA(p.pricePerUnit)}</td>
                  <td>{formatDate(p.date)}</td>
                  <td>{p.source}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {prices.length === 0 ? <p className="empty-inline muted">Aucune donnée pour cette sélection.</p> : null}
        </div>
      )}
    </div>
  );
}
