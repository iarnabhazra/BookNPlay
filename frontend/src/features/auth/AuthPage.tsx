import { useState } from 'react';
import axios from 'axios';
import { useAuth } from './useAuth';

export function AuthPage() {
  const { login } = useAuth();
  const [email,setEmail] = useState('');
  const [password,setPassword] = useState('');
  const [mode,setMode] = useState<'login'|'register'>('login');
  const [error,setError] = useState<string | null>(null);

  async function submit(e: React.FormEvent){
    e.preventDefault();
    setError(null);
    try {
      if(mode==='register') {
        await axios.post('/api/auth/register',{email,password});
      }
      const res = await axios.post('/api/auth/login',{email,password});
      login(res.data.token);
    } catch(err:any){
      setError(err.message);
    }
  }

  return <div style={{padding:24}}>
    <h2>{mode==='login'?'Login':'Register'}</h2>
    <form onSubmit={submit} style={{display:'flex', flexDirection:'column', gap:12, maxWidth:320}}>
      <input placeholder='Email' value={email} onChange={e=>setEmail(e.target.value)} />
      <input placeholder='Password' type='password' value={password} onChange={e=>setPassword(e.target.value)} />
      <button type='submit'>Submit</button>
      <button type='button' onClick={()=>setMode(m=>m==='login'?'register':'login')}>Switch to {mode==='login'?'Register':'Login'}</button>
      {error && <div style={{color:'red'}}>{error}</div>}
    </form>
  </div>;
}
