# Frontend Integration Guide

## Dev Setup
1. Install deps:
```
npm install
```
2. Start backend services (gateway on :8080, payment-service on :8084, search-service on :8083 or routed through gateway).
3. Run dev server:
```
npm run dev
```
4. Proxy rules (vite.config.ts) forward `/api` to `http://localhost:8080`.

## Environment Variables
Copy `.env.example` to `.env` if you need to override base URL.

```
VITE_API_BASE=http://localhost:8080
```
Leave empty to rely on proxy.

## Available Pages
- /search: NDJSON streaming search (lat/lng based once migrated)
- /payments: Create payments and list existing

## Adding New API Calls
Use `src/lib/api.ts` for `apiFetch` or `ndjsonStream`.

## Production Build
```
npm run build
npm run preview
```
Serve build output from a static host (e.g., Nginx) configured to proxy `/api`.
