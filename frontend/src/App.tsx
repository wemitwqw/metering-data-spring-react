import { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';
import { useAuthStore } from './stores/useAuthStore';
import { initializeApi, setAuthToken } from './services/meteringPointService';
import { createTheme } from '@mui/material/styles';
import { ROUTES } from './utils/constants';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    }
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          padding: '16px',
          borderRadius: '8px',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: '4px',
        },
      },
    },
  },
});

function App() {
  const { token } = useAuthStore();
  
  useEffect(() => {
    initializeApi();

    if (token) {
      setAuthToken(token);
    }
  }, [token]);
  
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path={ROUTES.LOGIN} element={<Login />} />
          
          <Route element={<ProtectedRoute />}>
            <Route path={ROUTES.DASHBOARD} element={<Dashboard />} />
          </Route>

          <Route 
            path="*" 
            element={
              token ? <Navigate to={ROUTES.DASHBOARD} replace /> : <Navigate to={ROUTES.LOGIN} replace />
            } 
          />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;