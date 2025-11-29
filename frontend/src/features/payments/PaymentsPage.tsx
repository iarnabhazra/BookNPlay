import React, { useEffect, useState } from 'react';
import { useAuth } from '../auth/useAuth';
import { createPayment, listPayments, PaymentDto } from './paymentApi';
import { Box, Paper, Typography, TextField, Button, Stack, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Chip, CircularProgress, IconButton, Tooltip } from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import RefreshIcon from '@mui/icons-material/Refresh';
import useInterval from '../../lib/useInterval';

export function PaymentsPage(){
  const { token } = useAuth();
  const [payments,setPayments] = useState<PaymentDto[]>([]);
  const [bookingRef,setBookingRef] = useState('');
  const [baseAmount,setBaseAmount] = useState('');
  const [demandFactor,setDemandFactor] = useState('0');
  const [slot,setSlot] = useState('');
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState<string | null>(null);

  async function load(){
    if(!token) return;
    setLoading(true); setError(null);
    try {
      const data = await listPayments();
      setPayments(data);
    } catch(e:any){
      setError(e.message || 'Failed to load payments');
    } finally { setLoading(false); }
  }

  async function create(e: React.FormEvent<HTMLFormElement>){
    e.preventDefault();
    await createPayment({
      bookingRef,
      baseAmount: Number(baseAmount),
      demandFactor: Number(demandFactor),
      slot: slot || new Date().toISOString()
    });
    setBookingRef(''); setBaseAmount(''); setDemandFactor('0'); setSlot('');
    load();
  }

  useEffect(()=>{ if(token) load(); },[token]);

  // Poll every 30s when connected & page visible
  useInterval(()=>{ if(document.visibilityState==='visible' && token) load(); }, 30000);

  return (
    <Box>
      <Paper elevation={2} sx={{p:3, maxWidth:560, position:'relative'}} component="form" onSubmit={create}>
        <Typography variant="h5" gutterBottom fontWeight={600}>Create Payment</Typography>
        <Stack spacing={2}>
          <TextField label="Booking Ref" value={bookingRef} onChange={(e)=>setBookingRef(e.target.value)} required size="small" />
          <Stack direction={{xs:'column', sm:'row'}} spacing={2}>
            <TextField label="Base Amount" type="number" value={baseAmount} onChange={(e)=>setBaseAmount(e.target.value)} required size="small" fullWidth />
            <TextField label="Demand Factor" type="number" inputProps={{min:0,max:100}} value={demandFactor} onChange={(e)=>setDemandFactor(e.target.value)} size="small" fullWidth />
          </Stack>
          <TextField label="Slot (optional)" type="datetime-local" value={slot} onChange={(e)=>setSlot(e.target.value)} size="small" InputLabelProps={{shrink:true}} />
          <Stack direction="row" spacing={2} alignItems="center">
            <Button type="submit" variant="contained" startIcon={<AddCircleIcon />}>Create Payment</Button>
            <Tooltip title="Refresh payments"><span><IconButton onClick={load} disabled={loading}><RefreshIcon /></IconButton></span></Tooltip>
            {loading && <CircularProgress size={20} />}
          </Stack>
          {error && <Typography color="error" variant="body2">{error}</Typography>}
        </Stack>
      </Paper>
      <TableContainer component={Paper} sx={{mt:4}}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Booking Ref</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payments.map(p => (
              <TableRow key={p.id} hover>
                <TableCell>{p.id}</TableCell>
                <TableCell>{p.bookingRef}</TableCell>
                <TableCell>{p.amount}</TableCell>
                <TableCell><Chip size="small" label={p.status} color={p.status === 'SUCCESS' ? 'success' : p.status === 'FAILED' ? 'error' : 'default'} /></TableCell>
              </TableRow>
            ))}
            {(!loading && payments.length === 0) && (
              <TableRow>
                <TableCell colSpan={4} align="center" sx={{py:3, color:'text.secondary'}}>No payments yet.</TableCell>
              </TableRow>
            )}
            {loading && payments.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} align="center" sx={{py:3}}><CircularProgress size={28} /></TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
