import { useEffect, useRef, useState } from 'react';
import { Client, IMessage, StompHeaders } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../auth/useAuth';
import { Paper, Typography, Chip, List, ListItem, ListItemText, Stack, Divider } from '@mui/material';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';

interface Notice { type: string; message: string; ts: number; }

export function NotificationsWidget(){
  const { token } = useAuth();
  const [connected,setConnected] = useState(false);
  const [messages,setMessages] = useState<Notice[]>([]);
  const clientRef = useRef<Client | null>(null);

  useEffect(()=>{
    if(!token) return;
    const client = new Client({
      webSocketFactory: () => new SockJS('/api/notifications/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` } as StompHeaders,
      debug: () => {},
      onConnect: () => {
        setConnected(true);
        client.subscribe('/topic/notifications', (frame: IMessage) => {
          try {
            const msg = JSON.parse(frame.body);
            setMessages(m => [...m, { type: msg.type || 'INFO', message: msg.message || frame.body, ts: Date.now() }]);
          } catch {
            setMessages(m => [...m, { type: 'INFO', message: frame.body, ts: Date.now() }]);
          }
        });
      },
      onStompError: (err: any) => { console.error('STOMP error', err); },
      onWebSocketClose: () => setConnected(false)
    });
    clientRef.current = client;
    client.activate();
    return () => { try { client.deactivate(); } catch {} };
  },[token]);

  const recent = messages.slice(-20).reverse();
  return (
    <Paper elevation={connected ? 4 : 1} sx={{p:1.5, width:320}}>
      <Stack direction="row" spacing={1} alignItems="center" sx={{mb:1}}>
        <NotificationsActiveIcon color={connected ? 'primary' : 'disabled'} fontSize="small" />
        <Typography variant="subtitle2" fontWeight={600}>Notifications</Typography>
        <Chip label={connected ? 'Live' : 'Offline'} size="small" color={connected ? 'success' : 'default'} variant={connected ? 'filled' : 'outlined'} />
      </Stack>
      <Divider sx={{mb:1}} />
      <List dense disablePadding sx={{maxHeight:180, overflow:'auto'}}>
        {recent.map(m => (
          <ListItem key={m.ts} disableGutters sx={{py:0.3}}>
            <ListItemText primaryTypographyProps={{fontSize:12}} primary={`${m.type}: ${m.message}`} />
          </ListItem>
        ))}
        {recent.length === 0 && <Typography variant="caption" color="text.secondary">No messages yet.</Typography>}
      </List>
    </Paper>
  );
}
