import type { ReactNode } from 'react'
import { 
  Box, 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Container 
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/useAuthStore';

interface AuthenticatedLayoutProps {
  children: ReactNode;
  title?: string;
}

const AuthenticatedLayout = ({ 
  children, 
  title = 'Energy Meter Dashboard' 
}: AuthenticatedLayoutProps) => {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  
  const handleLogout = () => {
    logout();
    navigate('/login');
  };
  
  return (
    <Box sx={{ 
      display: 'flex', 
      flexDirection: 'column', 
      minHeight: '100vh' 
    }}>
      <AppBar position="static" sx={{ mb: 4 }}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            {title}
          </Typography>
          
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            {user && (
              <Typography 
                variant="body1" 
                sx={{ 
                  mr: 2,
                  display: { xs: 'none', sm: 'block' } 
                }}
              >
                Welcome, {user.firstName} {user.lastName}
              </Typography>
            )}
            <Button 
              color="inherit" 
              onClick={handleLogout}
              sx={{ fontWeight: 500 }}
            >
              Logout
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
      
      <Container component="main" sx={{ flex: 1, mb: 4 }}>
        {children}
      </Container>
      
      <Box 
        component="footer" 
        sx={{ 
          py: 3, 
          px: 2, 
          mt: 'auto',
          backgroundColor: (theme) => theme.palette.grey[200]
        }}
      >
        <Container maxWidth="md">
          <Typography variant="body2" color="text.secondary" align="center">
            Energy Meter Dashboard © {new Date().getFullYear()}
          </Typography>
        </Container>
      </Box>
    </Box>
  );
};

export default AuthenticatedLayout;