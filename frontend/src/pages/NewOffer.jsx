import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';

export default function NewOffer() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState({
    title: '',
    productId: '',
    quantity: '',
    unit: '',
    pricePerUnit: '',
    minOrderQuantity: '',
    qualityGrade: '',
    locality: user.locality || '',
    description: ''
  });

  useEffect(() => {
    api.get('/products').then(setProducts).catch(() => {});
  }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      const offer = await api.post('/offers', {
        title: form.title,
        productId: Number(form.productId),
        quantity: form.quantity ? parseFloat(form.quantity) : null,
        unit: form.unit,
        pricePerUnit: parseFloat(form.pricePerUnit),
        minOrderQuantity: form.minOrderQuantity ? parseFloat(form.minOrderQuantity) : null,
        qualityGrade: form.qualityGrade || null,
        locality: form.locality,
        description: form.description || null
      });
      navigate(`/offres/${offer.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <Link to="/mes-offres" className="link">
        ← Mes offres
      </Link>
      <h1>Publier une offre</h1>
      <p className="muted">Proposez vos produits aux acheteurs de tout le Niger</p>

      <form onSubmit={submit} className="form card form-wide">
        {error ? <div className="alert alert-error">{error}</div> : null}
        <label>
          Titre de l'offre
          <input value={form.title} onChange={set('title')} placeholder="Mil de Matamèye, très bonne qualité" required />
        </label>
        <div className="form-row">
          <label>
            Produit
            <select value={form.productId} onChange={set('productId')} required>
              <option value="">Choisir…</option>
              {products.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.localName})
                </option>
              ))}
            </select>
          </label>
          <label>
            Unité
            <input value={form.unit} onChange={set('unit')} placeholder="sac de 100 kg" required />
          </label>
        </div>
        <div className="form-row">
          <label>
            Quantité disponible
            <input type="number" min="0" step="0.5" value={form.quantity} onChange={set('quantity')} />
          </label>
          <label>
            Prix unitaire (FCFA)
            <input type="number" min="1" value={form.pricePerUnit} onChange={set('pricePerUnit')} placeholder="28000" required />
          </label>
        </div>
        <div className="form-row">
          <label>
            Commande minimale (facultatif)
            <input type="number" min="0" step="0.5" value={form.minOrderQuantity} onChange={set('minOrderQuantity')} />
          </label>
          <label>
            Qualité
            <select value={form.qualityGrade} onChange={set('qualityGrade')}>
              <option value="">Standard</option>
              <option value="Premium">Premium</option>
              <option value="Bio">Bio</option>
            </select>
          </label>
        </div>
        <label>
          Localité
          <input value={form.locality} onChange={set('locality')} />
        </label>
        <label>
          Description
          <textarea value={form.description} onChange={set('description')} placeholder="Décrivez votre produit, la récolte…" />
        </label>
        <button className="btn btn-primary" disabled={busy}>
          {busy ? 'Publication…' : 'Publier l\'offre'}
        </button>
      </form>
    </div>
  );
}
