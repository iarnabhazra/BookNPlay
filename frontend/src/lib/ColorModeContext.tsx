import React, { createContext, useContext, useMemo, useState } from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';

interface ColorModeCtx { toggle: () => void; mode: 'light' | 'dark'; }
const Ctx = createContext<ColorModeCtx | undefined>(undefined);

export const ColorModeProvider: React.FC<{children: React.ReactNode}> = ({children}) => {
  const [mode,setMode] = useState<'light' | 'dark'>(() => (localStorage.getItem('bp_mode') as 'light' | 'dark') || 'light');
  const toggle = () => setMode(m => { const next = m === 'light' ? 'dark' : 'light'; localStorage.setItem('bp_mode', next); return next; });
  const theme = useMemo(()=> createTheme({
    palette: { mode, primary: { main: mode==='light' ? '#0055b3' : '#90caf9' }, secondary:{ main:'#ff7a18' } },
    shape:{ borderRadius:10 },
    typography:{ fontFamily:'Inter, system-ui, Roboto, Helvetica, Arial, sans-serif' }
  }),[mode]);
  return (
    <Ctx.Provider value={{toggle, mode}}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </Ctx.Provider>
  );
};

export function useColorMode(){
  const ctx = useContext(Ctx);
  if(!ctx) throw new Error('useColorMode must be used inside ColorModeProvider');
  return ctx;
}
