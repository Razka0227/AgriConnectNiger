import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('ac_user') || 'null');
    } catch {
      return null;
    }
  });
  const [token, setToken] = useState(() => localStorage.getItem('ac_token') || '');
  const [loading, setLoading] = useState(() => !!localStorage.getItem('ac_token'));

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    api
      .get('/auth/me')
      .then((u) => {
        setUser(u);
        localStorage.setItem('ac_user', JSON.stringify(u));
      })
      .catch(() => {
        setUser(null);
        setToken('');
        localStorage.removeItem('ac_token');
        localStorage.removeItem('ac_user');
      })
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const store = (res) => {
    localStorage.setItem('ac_token', res.token);
    localStorage.setItem('ac_user', JSON.stringify(res.user));
    setToken(res.token);
    setUser(res.user);
  };

  const login = async (phone, password) => {
    const res = await api.post('/auth/login', { phone, password });
    store(res);
    return res.user;
  };

  const register = async (payload) => {
    const res = await api.post('/auth/register', payload);
    store(res);
    return res.user;
  };

  const logout = () => {
    localStorage.removeItem('ac_token');
    localStorage.removeItem('ac_user');
    setToken('');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
