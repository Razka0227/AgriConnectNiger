import { useEffect, useState } from 'react';
import api from '../api/client';
import Spinner from '../components/Spinner';
import { formatDate } from '../utils/format';

const ICONS = {
  CLEAR: '☀️',
  PARTLY_CLOUDY: '🌤️',
  CLOUDY: '☁️',
  RAINY: '🌧️',
  WINDY: '💨',
  DUSTY: '🌫️',
  STORMY: '⛈️'
};

export default function Weather() {
  const [regions, setRegions] = useState([]);
  const [forecast, setForecast] = useState([]);
  const [loading, setLoading] = useState(true);
  const [region, setRegion] = useState('');

  useEffect(() => {
    api
      .get('/regions')
      .then((r) => {
        setRegions(r);
        if (r.length) {
          setRegion(r[0].code);
        }
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    api
      .get('/weather' + (region ? `?region=${region}` : ''))
      .then(setForecast)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [region]);

  return (
    <div>
      <div className="page-head">
        <div>
          <h1>Météo agricole</h1>
          <p className="muted">Prévisions et conseils aux cultures par région</p>
        </div>
      </div>

      <div className="filters card">
        <select value={region} onChange={(e) => setRegion(e.target.value)}>
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
        <div className="weather-grid">
          {forecast.map((w) => (
            <div key={w.id} className="card weather-card">
              <div className="weather-day">{formatDate(w.date)}</div>
              <div className="weather-icon">{ICONS[w.condition] || '🌡️'}</div>
              <div className="weather-cond">{w.conditionLabel}</div>
              <div className="weather-temp">
                {Math.round(w.tempMinC)}°C – {Math.round(w.tempMaxC)}°C
              </div>
              <div className="weather-meta">
                <span>💧 {w.humidityPct}%</span>
                <span>🌧️ {w.rainfallMm} mm</span>
              </div>
              {w.advice ? <div className="weather-advice">💡 {w.advice}</div> : null}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
