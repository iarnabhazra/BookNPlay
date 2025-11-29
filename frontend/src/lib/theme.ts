import { createTheme } from '@mui/material/styles';

export const appTheme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#0055b3' },
    secondary: { main: '#ff7a18' },
    background: { default: '#f5f7fa', paper: '#ffffff' }
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: 'Inter, system-ui, Roboto, Helvetica, Arial, sans-serif',
    h1: { fontSize: '2.25rem', fontWeight: 600 },
    h2: { fontSize: '1.75rem', fontWeight: 600 },
    button: { textTransform: 'none', fontWeight: 600 }
  },
  components: {
    MuiButton: { styleOverrides: { root: { borderRadius: 8 } } },
    MuiPaper: { styleOverrides: { root: { borderRadius: 12 } } }
  }
});
