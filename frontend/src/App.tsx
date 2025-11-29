import { Routes, Route, Navigate, Link as RouterLink } from 'react-router-dom';
import React, { Suspense, lazy } from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { AuthPage } from './features/auth/AuthPage';
import { NotificationsWidget } from './features/notifications/NotificationsWidget';
import { useAuth } from './features/auth/useAuth';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { useColorMode } from './lib/ColorModeContext';

// Lazy loaded pages
const BookingPage = lazy(()=> import('./features/booking/BookingPage').then(m=>({default:m.BookingPage})));
const OnboardingPage = lazy(()=> import('./features/onboarding/OnboardingPage').then(m=>({default:m.OnboardingPage})));
const SearchPage = lazy(()=> import('./features/search/SearchPage').then(m=>({default:m.SearchPage})));
const PaymentsPage = lazy(()=> import('./features/payments/PaymentsPage').then(m=>({default:m.PaymentsPage})));
const UploadPage = lazy(()=> import('./features/upload/UploadPage').then(m=>({default:m.UploadPage})));

export default function App() {
  const { token, logout } = useAuth();
  const { mode, toggle } = useColorMode();
  return (
    <Box sx={{display:'flex', flexDirection:'column', minHeight:'100vh'}}>
      <AppBar position="static" color="primary" elevation={1}>
        <Toolbar sx={{gap:2}}>
          <Typography variant="h6" sx={{flexGrow:1, fontWeight:700}}>BooknPlay</Typography>
          <Stack direction="row" spacing={1} alignItems="center">
            <Button color="inherit" component={RouterLink} to="/search">Search</Button>
            <Button color="inherit" component={RouterLink} to="/booking">Booking</Button>
            <Button color="inherit" component={RouterLink} to="/onboarding">Onboard</Button>
            <Button color="inherit" component={RouterLink} to="/payments">Payments</Button>
            <Button color="inherit" component={RouterLink} to="/upload">Files</Button>
            <Tooltip title={mode === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}>
              <IconButton color="inherit" onClick={toggle} size="small">
                {mode === 'light' ? <DarkModeIcon fontSize="small" /> : <LightModeIcon fontSize="small" />}
              </IconButton>
            </Tooltip>
            {token ? <Button color="inherit" onClick={logout}>Logout</Button> : <Button color="inherit" component={RouterLink} to="/auth">Login</Button>}
            <NotificationsWidget />
          </Stack>
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{py:4, flexGrow:1}}>
        <Suspense fallback={<Typography variant="body1">Loading...</Typography>}>
          <Routes>
            <Route path="/" element={<Navigate to="/search" replace />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/booking" element={<RequireAuth><BookingPage /></RequireAuth>} />
            <Route path="/onboarding" element={<RequireAuth><OnboardingPage /></RequireAuth>} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/payments" element={<RequireAuth><PaymentsPage /></RequireAuth>} />
            <Route path="/upload" element={<RequireAuth><UploadPage /></RequireAuth>} />
          </Routes>
        </Suspense>
      </Container>
    </Box>
  );
}

function RequireAuth({children}:{children:JSX.Element}) {
  const { token } = useAuth();
  if(!token) return <Navigate to="/auth" replace />;
  return children;
}
