import React, { useEffect, useRef, useState } from 'react';
import { ndjsonStream } from '../../lib/api';

export function useTurfSearch(lat: number | null, lng: number | null, trigger: number){
  const [items,setItems] = useState<string[]>([]);
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState<Error | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(()=>{
    if(lat == null || lng == null) return;
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;
    setItems([]); setError(null); setLoading(true);
    (async ()=>{
      try {
        for await (const chunk of ndjsonStream(`/api/search/turfs?lat=${lat}&lng=${lng}`, { signal: controller.signal })){
          setItems((prev: string[]) => [...prev, typeof chunk === 'string' ? chunk : String((chunk as any).name || chunk)]);
        }
      } catch (e:any){
        if(e.name !== 'AbortError') setError(e);
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  },[lat,lng,trigger]);

  return { items, loading, error };
}
