import React, { createContext, useCallback, useContext, useState } from 'react';
import { Snackbar, Alert } from '@mui/material';

interface ErrorContextValue {
  pushError: (msg: string) => void;
}

const ErrorContext = createContext<ErrorContextValue | undefined>(undefined);

export const ErrorProvider: React.FC<{children: React.ReactNode}> = ({children}) => {
  const [open,setOpen] = useState(false);
  const [message,setMessage] = useState('');

  const pushError = useCallback((msg: string) => { setMessage(msg); setOpen(true); }, []);

  return (
    <ErrorContext.Provider value={{pushError}}>
      {children}
      <Snackbar open={open} autoHideDuration={4000} onClose={()=>setOpen(false)} anchorOrigin={{vertical:'bottom', horizontal:'center'}}>
        <Alert severity="error" onClose={()=>setOpen(false)} variant="filled">{message}</Alert>
      </Snackbar>
    </ErrorContext.Provider>
  );
};

export function useError(){
  const ctx = useContext(ErrorContext);
  if(!ctx) throw new Error('useError must be inside ErrorProvider');
  return ctx;
}
