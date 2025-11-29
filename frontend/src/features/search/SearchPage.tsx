import { useState } from 'react';
import { useAuth } from '../auth/useAuth';
import { Box, Paper, Typography, TextField, Button, List, ListItem, ListItemIcon, ListItemText, LinearProgress, Stack } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

export function SearchPage() {
  const { token } = useAuth();
  const [query,setQuery] = useState('');
  const [results,setResults] = useState<string[]>([]);
  const [loading,setLoading] = useState(false);

  async function runSearch(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setResults([]);
    // Stream NDJSON from search-service via gateway
    const resp = await fetch(`/api/search/turfs?q=${encodeURIComponent(query)}`, { headers: { Authorization: `Bearer ${token}` }});
    const reader = resp.body?.getReader();
    if(reader){
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
            try { const obj = JSON.parse(line); setResults((r: string[]) => [...r, obj.name || line]); } catch { /* ignore */ }
          }
        }
      }
    }
    setLoading(false);
  }

  return (
    <Box>
      <Paper elevation={2} sx={{p:3, mb:4}}>
        <Typography variant="h5" gutterBottom fontWeight={600}>Search Turfs</Typography>
        <Stack component="form" direction={{xs:'column', sm:'row'}} spacing={2} onSubmit={runSearch}>
          <TextField size="small" label="Query" value={query} onChange={(e)=>setQuery(e.target.value)} fullWidth />
          <Button type="submit" variant="contained" startIcon={<SearchIcon />} disabled={loading}>Search</Button>
        </Stack>
        {loading && <LinearProgress sx={{mt:2}} />}
      </Paper>
      <Paper variant="outlined" sx={{p:2}}>
        <Typography variant="subtitle1" sx={{mb:1, fontWeight:600}}>Results ({results.length})</Typography>
        <List dense>
          {results.map((r,i)=> (
            <ListItem key={i} divider disableGutters>
              <ListItemIcon sx={{minWidth:32}}><SearchIcon fontSize="small" /></ListItemIcon>
              <ListItemText primaryTypographyProps={{fontSize:14}} primary={r} />
            </ListItem>
          ))}
          {!loading && results.length===0 && <Typography variant="body2" color="text.secondary" sx={{p:1}}>No results yet. Run a search.</Typography>}
        </List>
      </Paper>
    </Box>
  );
}
