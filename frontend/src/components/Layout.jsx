import { useEffect, useState } from 'react';
import { NavLink, Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';

const links = [
  { to: '/marche', label: 'Marché', icon: '🛒' },
  { to: '/prix', label: 'Prix', icon: '📊' },
  { to: '/meteo', label: 'Météo', icon: '🌤️' },
  { to: '/transport', label: 'Transport', icon: '🚚' },
  { to: '/commandes', label: 'Commandes', icon: '📦' },
  { to: '/mes-offres', label: 'Mes offres', icon: '🌾' },
  { to: '/profil', label: 'Profil', icon: '👤' }
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [unread, setUnread] = useState(0);

  useEffect(() => {
    if (user) {
      api
        .get('/notifications/unread-count')
        .then((r) => setUnread(r.count))
        .catch(() => {});
    }
  }, [user]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const initials = (user?.name || '?').split(' ').map((w) => w[0]).slice(0, 2).join('');

  return (
    <div className="layout">
      <header className="topbar">
        <Link to="/" className="brand">
          <span className="brand-logo">🌾</span>
          <span>
            AgriConnect <strong>Niger</strong>
          </span>
        </Link>
        <nav className="nav-desktop">
          {links.map((l) => (
            <NavLink key={l.to} to={l.to} className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              <span className="nav-icon">{l.icon}</span>
              {l.label}
            </NavLink>
          ))}
          <NavLink to="/notifications" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
            <span className="nav-icon">🔔</span>
            Alertes
            {unread > 0 ? <span className="badge-count">{unread}</span> : null}
          </NavLink>
          <button className="btn btn-ghost" onClick={handleLogout}>
            Quitter
          </button>
        </nav>
        <button className="burger" onClick={() => setOpen(!open)} aria-label="Menu">
          ☰
        </button>
      </header>

      {open && (
        <nav className="nav-mobile">
          {links.map((l) => (
            <NavLink key={l.to} to={l.to} onClick={() => setOpen(false)} className="nav-link">
              <span className="nav-icon">{l.icon}</span>
              {l.label}
            </NavLink>
          ))}
          <NavLink to="/notifications" onClick={() => setOpen(false)} className="nav-link">
            <span className="nav-icon">🔔</span> Alertes{' '}
            {unread > 0 ? <span className="badge-count">{unread}</span> : null}
          </NavLink>
          <div className="nav-user">
            <span className="avatar">{initials}</span>
            <span>{user?.name}</span>
            <button className="btn btn-ghost" onClick={handleLogout}>
              Quitter
            </button>
          </div>
        </nav>
      )}

      <main className="main">
        <div className="container">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
