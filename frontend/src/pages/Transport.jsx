import { useEffect, useState } from 'react';
import api from '../api/client';
import Spinner from '../components/Spinner';
import { formatNumber, formatFCFA } from '../utils/format';

export default function Transport() {
  const [regions, setRegions] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');

  useEffect(() => {
    api.get('/regions').then(setRegions).catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams();
    if (from) params.set('fromRegion', from);
    if (to) params.set('toRegion', to);
    api
      .get('/transport?' + params.toString())
      .then(setRoutes)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [from, to]);

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Transport & logistique</h1>
          <p className="muted">Tarifs indicatifs par kg et délais entre les régions du Niger</p>
        </div>
      </div>

      <div className="filters card">
        <label>
          Départ
          <select value={from} onChange={(e) => setFrom(e.target.value)}>
            <option value="">Toutes</option>
            {regions.map((r) => (
              <option key={r.code} value={r.code}>
                {r.label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Arrivée
          <select value={to} onChange={(e) => setTo(e.target.value)}>
            <option value="">Toutes</option>
            {regions.map((r) => (
              <option key={r.code} value={r.code}>
                {r.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      {loading ? (
        <Spinner />
      ) : (
        <div className="table-wrap card">
          <table className="table">
            <thead>
              <tr>
                <th>Itinéraire</th>
                <th>Distance</th>
                <th>Durée estimée</th>
                <th>Coût / kg</th>
                <th>Prestataire</th>
              </tr>
            </thead>
            <tbody>
              {routes.map((r) => (
                <tr key={r.id}>
                  <td>
                    <strong>
                      {r.fromCity} → {r.toCity}
                    </strong>
                    <div className="muted">{r.name}</div>
                  </td>
                  <td>{formatNumber(r.distanceKm)} km</td>
                  <td>≈ {r.estimatedHours} h</td>
                  <td className="price">{formatFCFA(r.costPerKgCfa)}</td>
                  <td>{r.provider}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {routes.length === 0 ? <p className="empty-inline muted">Aucun itinéraire pour cette sélection.</p> : null}
        </div>
      )}

      <div className="card info-card">
        <h3>💡 Estimation de livraison</h3>
        <p className="muted">
          Prix indicatif = coût au kg × poids de votre commande. Le transporteur est attribué automatiquement à la
          confirmation de la commande. En zone rurale sans réseau, le suivi reste possible par <strong>SMS / USSD</strong>.
        </p>
      </div>
    </div>
  );
}
