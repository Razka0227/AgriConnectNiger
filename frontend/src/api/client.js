const API_BASE = import.meta.env.VITE_API_URL || '/api';

export function getToken() {
  return localStorage.getItem('ac_token') || '';
}

function clearSession() {
  localStorage.removeItem('ac_token');
  localStorage.removeItem('ac_user');
  if (window.location.hash !== '#/login') {
    window.location.hash = '#/login';
  }
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const res = await fetch(API_BASE + path, { ...options, headers });
  if (res.status === 401) {
    clearSession();
    const err = new Error('Session expirée. Veuillez vous reconnecter.');
    err.status = 401;
    throw err;
  }
  if (!res.ok) {
    let msg = 'Erreur du serveur';
    try {
      const data = await res.json();
      msg = data.message || (data.errors ? Object.values(data.errors).join(', ') : msg);
    } catch {
      /* ignore */
    }
    const err = new Error(msg);
    err.status = res.status;
    throw err;
  }
  if (res.status === 204) {
    return null;
  }
  return res.json();
}

export default {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
  patch: (path, body) => request(path, { method: 'PATCH', body: JSON.stringify(body) }),
  del: (path) => request(path, { method: 'DELETE' })
};
