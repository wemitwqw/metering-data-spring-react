import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Box, 
  Container, 
  TextField, 
  Button, 
  Typography, 
  Paper,
  CircularProgress
} from '@mui/material';
import { useAuthStore } from '../stores/useAuthStore';
import { authService } from '../services/auth.service';
import ErrorMessage from '../components/ErrorMessage';
import { ROUTES, APP_NAME } from '../utils/constants';
import { validateForm } from '../utils/form.validator';

const Login = () => {
  const navigate = useNavigate();
  const { isLoading, error, clearError } = useAuthStore();
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [formError, setFormError] = useState('');
  
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setFormError('');
    clearError();
    
    validateForm(email, password, setFormError);
    
    try {
      await authService.login(email, password);
      navigate(ROUTES.DASHBOARD);
    } catch (error) {
      console.error('Login error:', error);
    }
  };
  
  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper 
          sx={{ 
            p: 4, 
            width: '100%',
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center'
          }}
        >
          <Typography component="h1" variant="h5" gutterBottom>
            {APP_NAME}
          </Typography>
          
          <Typography component="h2" variant="h6" gutterBottom>
            Sign In
          </Typography>
          
          <Box component="form" onSubmit={handleLogin} sx={{ mt: 2, width: '100%' }}>
            {(error || formError) && (
              <ErrorMessage 
                message={error || formError} 
                title="Authentication Error" 
              />
            )}
            
            <TextField
              margin="normal"
              required
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                setFormError('');
                if (error) clearError();
              }}
            />
            
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setFormError('');
                if (error) clearError();
              }}
            />
            
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, py: 1.5 }}
              disabled={isLoading}
            >
              {isLoading ? <CircularProgress size={24} /> : 'Sign In'}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Login;