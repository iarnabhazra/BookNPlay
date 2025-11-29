import { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../auth/useAuth';

export function OnboardingPage() {
  const { token } = useAuth();
  const [turfName,setTurfName] = useState('');
  const [location,setLocation] = useState('');
  const [ownerEmail,setOwnerEmail] = useState('');
  const [msg,setMsg] = useState<string | null>(null);

  async function submit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setMsg(null);
    await axios.post('/api/turfs/onboarding',{turfName, location, ownerEmail},{ headers: { Authorization: `Bearer ${token}` }});
    setMsg('Submitted');
  }

  return <div style={{padding:24}}>
    <h2>Onboard Turf</h2>
    <form onSubmit={submit} style={{display:'flex', flexDirection:'column', gap:8, maxWidth:340}}>
  <input value={ownerEmail} onChange={(e: React.ChangeEvent<HTMLInputElement>)=>setOwnerEmail(e.target.value)} placeholder='Owner Email' />
  <input value={turfName} onChange={(e: React.ChangeEvent<HTMLInputElement>)=>setTurfName(e.target.value)} placeholder='Turf Name' />
  <input value={location} onChange={(e: React.ChangeEvent<HTMLInputElement>)=>setLocation(e.target.value)} placeholder='Location' />
      <button>Submit</button>
    </form>
    {msg && <div>{msg}</div>}
  </div>;
}
