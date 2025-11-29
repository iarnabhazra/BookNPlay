import { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../auth/useAuth';

interface PaymentDto { id: number; amount: number; status: string; bookingId: number; }

export function PaymentsPage(){
  const { token } = useAuth();
  const [payments,setPayments] = useState<PaymentDto[]>([]);
  const [bookingId,setBookingId] = useState('');
  const [amount,setAmount] = useState('');

  async function load(){
    const r = await axios.get('/api/payments',{ headers: { Authorization: `Bearer ${token}` }});
    setPayments(r.data || []);
  }

  async function create(e: React.FormEvent){
    e.preventDefault();
    await axios.post('/api/payments',{ bookingId: Number(bookingId), amount: Number(amount) },{ headers: { Authorization: `Bearer ${token}` }});
    setBookingId(''); setAmount('');
    load();
  }

  useEffect(()=>{ if(token) load(); },[token]);

  return <div style={{padding:24}}>
    <h2>Payments</h2>
    <form onSubmit={create} style={{display:'flex', gap:8}}>
      <input value={bookingId} onChange={e=>setBookingId(e.target.value)} placeholder='Booking Id' />
      <input value={amount} onChange={e=>setAmount(e.target.value)} placeholder='Amount' />
      <button>Create</button>
    </form>
    <table style={{marginTop:16}}>
      <thead><tr><th>ID</th><th>Booking</th><th>Amount</th><th>Status</th></tr></thead>
      <tbody>
        {payments.map(p=> <tr key={p.id}><td>{p.id}</td><td>{p.bookingId}</td><td>{p.amount}</td><td>{p.status}</td></tr>)}
      </tbody>
    </table>
  </div>;
}
