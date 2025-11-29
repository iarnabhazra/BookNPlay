import { useState } from 'react';
import { useAuth } from '../auth/useAuth';

export function UploadPage(){
  const { token } = useAuth();
  const [file,setFile] = useState<File | null>(null);
  const [status,setStatus] = useState('');

  async function doUpload(e: React.FormEvent<HTMLFormElement>){
    e.preventDefault();
    if(!file) return;
    const form = new FormData();
    form.append('file', file);
    const resp = await fetch('/api/turfs/files/upload',{ method:'POST', headers: { Authorization: `Bearer ${token}` }, body: form });
    setStatus(resp.ok ? 'Uploaded' : 'Failed');
  }

  return <div style={{padding:24}}>
    <h2>File Upload</h2>
    <form onSubmit={doUpload} style={{display:'flex', gap:8}}>
  <input type='file' onChange={(e: React.ChangeEvent<HTMLInputElement>)=> setFile(e.target.files ? e.target.files[0] : null)} />
      <button disabled={!file}>Upload</button>
    </form>
    {status && <div>{status}</div>}
  </div>;
}
