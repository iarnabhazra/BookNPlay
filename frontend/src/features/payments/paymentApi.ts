import { apiFetch } from '../../lib/api';

export interface PaymentCreateRequest {
  bookingRef: string;
  baseAmount: number;
  demandFactor: number;
  slot: string; // ISO string
}

export interface PaymentDto { id: number; amount: number; status: string; bookingRef: string; }

export async function createPayment(req: PaymentCreateRequest){
  return apiFetch('/api/payments', {
    method: 'POST',
    body: JSON.stringify(req)
  });
}

export async function listPayments(): Promise<PaymentDto[]> {
  return apiFetch('/api/payments');
}
