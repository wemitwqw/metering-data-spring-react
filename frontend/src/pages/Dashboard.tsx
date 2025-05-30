import { useEffect, useState } from 'react';
import { 
  Typography, 
  Box, 
  Paper, 
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Card,
  CardContent
} from '@mui/material';
import { useMeteringPointsStore } from '../stores/useMeteringPointsStore';
import { useConsumptionStore } from '../stores/useConsumptionStore';
import { useAuthStore } from '../stores/useAuthStore';
import { meteringPointsService } from '../services/metering-points.service';
import { consumptionService } from '../services/consumptions.service';
import { authService } from '../services/auth.service';
import { useNavigate } from 'react-router-dom';
import ConsumptionChart from '../components/ConsumptionChart';

const Dashboard = () => {
  const navigate = useNavigate();
  const { meteringPoints, isLoading, error } = useMeteringPointsStore();
  const { setSelectedYear } = useConsumptionStore();
  const { user, isAuthenticated } = useAuthStore();
  const [selectedMeteringPoint, setSelectedMeteringPoint] = useState('');
  const [isInitialDataLoaded, setIsInitialDataLoaded] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      meteringPointsService.fetchMeteringPoints().catch((err) => {
        console.error('Error fetching metering points:', err);
      });
    }
  }, [isAuthenticated]);
  
  useEffect(() => {
    const initializeData = async () => {
      if (meteringPoints.length > 0 && !isInitialDataLoaded) {
        const firstMeteringPoint = meteringPoints[0].meterId;
        setSelectedMeteringPoint(firstMeteringPoint);
        
        try {
          const years = await consumptionService.fetchAvailableYears(firstMeteringPoint);

          const latestYear = years[years.length - 1];
          setSelectedYear(latestYear);
          
          await consumptionService.fetchConsumptions(firstMeteringPoint, latestYear);
          
          setIsInitialDataLoaded(true);
        } catch (err) {
          console.error('Error initializing data:', err);
        }
      }
    };
    
    initializeData();
  }, [meteringPoints, isInitialDataLoaded, setSelectedYear]);
  
  const handleMeteringPointChange = async (event: any) => {
    const newMeterId = event.target.value;
    setSelectedMeteringPoint(newMeterId);
    
    try {
      const years = await consumptionService.fetchAvailableYears(newMeterId);

      const latestYear = years[years.length - 1];
      setSelectedYear(latestYear);
      
      await consumptionService.fetchConsumptions(newMeterId, latestYear);
    } catch (err) {
      console.error('Error changing metering point:', err);
    }
  };
  
  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };
  
  const handleYearChange = (year: number) => {
    setSelectedYear(year);
    if (selectedMeteringPoint) {
      consumptionService.fetchConsumptions(selectedMeteringPoint, year).catch((err) => {
        console.error('Error fetching consumption data:', err);
      });
    }
  };
  
  const selectedMeterData = meteringPoints.find(point => point.meterId === selectedMeteringPoint);
  
  return (
    <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Box 
        component="header" 
        sx={{ 
          bgcolor: 'primary.main', 
          color: 'white', 
          py: 2, 
          mb: 4,
          boxShadow: 1
        }}
      >
        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          maxWidth: 'lg',
          mx: 'auto',
          px: 3
        }}>
          <Typography variant="h6">
            Dashboard
          </Typography>
          
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            {user && (
              <Typography variant="body1" sx={{ mr: 2 }}>
                Welcome, {user.firstName} {user.lastName}
              </Typography>
            )}
            <Box 
              component="button" 
              onClick={handleLogout}
              sx={{ 
                backgroundColor: 'transparent',
                color: 'white',
                border: 'none',
                cursor: 'pointer',
                fontFamily: 'inherit',
                fontSize: '1rem',
                padding: '8px 16px',
                borderRadius: '4px',
                transition: '0.2s',
                '&:hover': {
                  backgroundColor: 'rgba(255, 255, 255, 0.1)'
                }
              }}
            >
              Logout
            </Box>
          </Box>
        </Box>
      </Box>
      
      <Box 
        component="main" 
        sx={{ 
          maxWidth: 'lg', 
          width: '100%', 
          mx: 'auto', 
          px: 3, 
          mb: 4, 
          flex: '1 0 auto' 
        }}
      >
        <Paper sx={{ p: { xs: 2, md: 3 } }}>
          <Box sx={{ mb: 3 }}>
            <Typography variant="h5" gutterBottom>
              Your Metering Points
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Select a metering point from the dropdown below to view consumption data
            </Typography>
          </Box>
          
          {isLoading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          ) : meteringPoints.length === 0 ? (
            <Alert severity="info">
              You don't have any metering points associated with your account.
            </Alert>
          ) : (
            <Box sx={{ width: '100%' }}>
              <FormControl fullWidth sx={{ mb: 3 }}>
                <InputLabel id="metering-point-select-label">Metering Point</InputLabel>
                <Select
                  labelId="metering-point-select-label"
                  id="metering-point-select"
                  value={selectedMeteringPoint}
                  label="Metering Point"
                  onChange={handleMeteringPointChange}
                >
                  {meteringPoints.map((point) => (
                    <MenuItem key={point.meterId} value={point.meterId}>
                      {point.address}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              
              {selectedMeterData && (
                <Card variant="outlined" sx={{ mt: 2 }}>
                  <CardContent>
                    <Typography variant="body1">
                      Address: {selectedMeterData.address}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                      Meter ID: {selectedMeterData.meterId}
                    </Typography>
                  </CardContent>
                </Card>
              )}
              
              <ConsumptionChart 
                selectedMeterId={selectedMeteringPoint}
                onYearChange={handleYearChange}
              />
            </Box>
          )}
        </Paper>
      </Box>
      
      <Box 
        component="footer" 
        sx={{ 
          py: 3, 
          px: 2, 
          mt: 'auto',
          backgroundColor: (theme) => theme.palette.grey[200]
        }}
      >
        <Box sx={{ maxWidth: 'lg', mx: 'auto' }}>
          <Typography variant="body2" color="text.secondary" align="center">
            Metering Data Assessment - 2025
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default Dashboard;