import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';

const ROLES = [
  { code: 'FARMER', label: 'Agriculteur / Producteur' },
  { code: 'BUYER', label: 'Acheteur / Grossiste' },
  { code: 'TRANSPORTER', label: 'Transporteur / Logisticien' }
];

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [regions, setRegions] = useState([]);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState({
    name: '',
    phone: '',
    email: '',
    password: '',
    role: 'FARMER',
    region: 'NIAMEY',
    locality: '',
    organization: ''
  });

  useEffect(() => {
    api
      .get('/regions')
      .then((r) => setRegions(r))
      .catch(() => {});
  }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      await register(form);
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="auth-screen">
      <div className="auth-card card">
        <div className="auth-brand">
          <span className="brand-logo">🌾</span>
          <h1>Créer un compte</h1>
          <p className="muted">Rejoignez le réseau agricole du Niger</p>
        </div>
        <form onSubmit={submit} className="form">
          {error ? <div className="alert alert-error">{error}</div> : null}
          <label>
            Nom complet
            <input value={form.name} onChange={set('name')} placeholder="Moussa Ibrahim" required />
          </label>
          <label>
            Téléphone (ex: 97000000)
            <input type="tel" value={form.phone} onChange={set('phone')} required />
          </label>
          <label>
            Email (facultatif)
            <input type="email" value={form.email} onChange={set('email')} />
          </label>
          <label>
            Mot de passe
            <input type="password" value={form.password} onChange={set('password')} minLength={6} required />
          </label>
          <label>
            Je suis…
            <select value={form.role} onChange={set('role')}>
              {ROLES.map((r) => (
                <option key={r.code} value={r.code}>
                  {r.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            Région
            <select value={form.region} onChange={set('region')}>
              {regions.map((r) => (
                <option key={r.code} value={r.code}>
                  {r.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            Localité / Village
            <input value={form.locality} onChange={set('locality')} placeholder="Matamèye" />
          </label>
          <label>
            Organisation / Coopérative (facultatif)
            <input value={form.organization} onChange={set('organization')} placeholder="Coopérative Matamèye" />
          </label>
          <button className="btn btn-primary" disabled={busy}>
            {busy ? 'Création…' : "S'inscrire"}
          </button>
        </form>
        <p className="center muted">
          Déjà inscrit ? <Link to="/login">Se connecter</Link>
        </p>
      </div>
    </div>
  );
}
