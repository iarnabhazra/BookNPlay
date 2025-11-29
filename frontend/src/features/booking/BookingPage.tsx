import { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../auth/useAuth';

export function BookingPage() {
  const { token } = useAuth();
  const [turfId,setTurfId] = useState('1');
  const [start,setStart] = useState('');
  const [duration,setDuration] = useState(60);
  const [result,setResult] = useState<any>(null);
  const [error,setError] = useState<string | null>(null);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      const slotStart = new Date(start).toISOString();
      const slotEnd = new Date(new Date(start).getTime() + duration*60000).toISOString();
      const res = await axios.post('/api/bookings',{turfId: Number(turfId), slotStart, slotEnd, userId: 'me'},{ headers: { Authorization: `Bearer ${token}` }});
      setResult(res.data);
    } catch(err:any){ setError(err.message); }
  }

  return <div style={{padding:24}}>
    <h2>Book Turf</h2>
    <form onSubmit={submit} style={{display:'flex', flexDirection:'column', gap:8, maxWidth:340}}>
      <input value={turfId} onChange={e=>setTurfId(e.target.value)} placeholder='Turf ID' />
      <input value={start} onChange={e=>setStart(e.target.value)} type='datetime-local' />
      <input value={duration} onChange={e=>setDuration(Number(e.target.value))} type='number' />
      <button>Book</button>
      {error && <div style={{color:'red'}}>{error}</div>}
    </form>
    {result && <pre>{JSON.stringify(result,null,2)}</pre>}
  </div>;
}
