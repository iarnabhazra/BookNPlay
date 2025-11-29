// Central API utilities: fetch wrapper, NDJSON streaming, error handling

export interface ApiError extends Error { status?: number; details?: any; }

const base = import.meta.env.VITE_API_BASE || '';

let errorListener: ((err: ApiError)=>void) | null = null;
export function registerApiErrorListener(fn: (err: ApiError)=>void){ errorListener = fn; }

function buildTraceHeaders(){
  const traceId = crypto.randomUUID().replace(/-/g,'');
  const spanId = traceId.substring(16); // simplistic
  return {
    'traceparent': `00-${traceId}-${spanId}-01`,
    'x-request-id': traceId,
    'x-client-start': Date.now().toString()
  };
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const traceHeaders = buildTraceHeaders();
  const res = await fetch(base + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...traceHeaders,
      ...(options.headers || {})
    }
  });
  if(!res.ok){
    const text = await res.text();
    const err: ApiError = Object.assign(new Error(`HTTP ${res.status}`), { status: res.status, details: safeJson(text) });
    errorListener?.(err);
    throw err;
  }
  if(res.status === 204) return undefined as any;
  const ct = res.headers.get('content-type') || '';
  if(ct.includes('application/json')) return res.json() as Promise<T>;
  return (await res.text()) as any;
}

export async function* ndjsonStream(path: string, options: RequestInit = {}): AsyncGenerator<any, void, unknown> {
  const res = await fetch(base + path, options);
  if(!res.ok){
    const err: ApiError = Object.assign(new Error(`HTTP ${res.status}`), { status: res.status });
    errorListener?.(err);
    throw err;
  }
  const reader = res.body?.getReader();
  if(!reader) return;
  const decoder = new TextDecoder();
  let buffer = '';
  while(true){
    const {done, value} = await reader.read();
    if(done) break;
    buffer += decoder.decode(value, {stream:true});
    let idx;
    while((idx = buffer.indexOf('\n')) >= 0){
      const line = buffer.slice(0, idx).trim();
      buffer = buffer.slice(idx+1);
      if(line){
        try { yield JSON.parse(line); } catch { yield line; }
      }
    }
  }
  if(buffer.trim()){
    try { yield JSON.parse(buffer.trim()); } catch { yield buffer.trim(); }
  }
}

function safeJson(txt: string){
  try { return JSON.parse(txt); } catch { return txt; }
}
