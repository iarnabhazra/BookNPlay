import { useEffect, useState } from 'react';

const KEY = 'booknplay.token';

export function useAuth() {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(KEY));

  useEffect(() => {
    if (token) localStorage.setItem(KEY, token); else localStorage.removeItem(KEY);
  }, [token]);

  function login(t: string) { setToken(t); }
  function logout() { setToken(null); }

  return { token, login, logout };
}
