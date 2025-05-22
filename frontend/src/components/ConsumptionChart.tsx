import { 
  Card, 
  CardContent, 
  Typography, 
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { useConsumptionStore } from '../stores/useConsumptionStore';
import Loading from './Loading';
import ErrorMessage from './ErrorMessage';

interface ConsumptionChartProps {
  selectedMeterId: string;
  onYearChange: (year: number) => void;
}

const ConsumptionChart = ({ selectedMeterId, onYearChange }: ConsumptionChartProps) => {
  const { consumptions, isLoading, error, selectedYear } = useConsumptionStore();
  
  const currentYear = new Date().getFullYear();
  const availableYears = Array.from({ length: 5 }, (_, i) => currentYear - i);
  
  const handleYearChange = (event: any) => {
    const year = event.target.value;
    onYearChange(year);
  };
  
  // Prepare data for the chart
  const chartData = consumptions.map(consumption => ({
    month: consumption.month.charAt(0) + consumption.month.slice(1).toLowerCase(),
    amount: consumption.amount,
    costWithVat: consumption.totalCostEurWithVat,
    costWithoutVat: consumption.totalCostEur
  }));
  
  const totalConsumption = consumptions.reduce((sum, item) => sum + item.amount, 0);
  const totalCostWithVat = consumptions.reduce((sum, item) => sum + item.totalCostEurWithVat, 0);
  const averageCentsPerKwh = consumptions.length > 0 
    ? consumptions.reduce((sum, item) => sum + item.centsPerKwhWithVat, 0) / consumptions.length 
    : 0;
  
  if (isLoading) {
    return <Loading message="Loading consumption data..." />;
  }
  
  if (error) {
    return <ErrorMessage message={error} title="Failed to load consumption data" />;
  }
  
  if (!selectedMeterId) {
    return (
      <Card variant="outlined">
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Consumption Data
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Please select a metering point to view consumption data.
          </Typography>
        </CardContent>
      </Card>
    );
  }
  
  return (
    <Card variant="outlined" sx={{ mt: 3 }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h6">
            Consumption Data
          </Typography>
          
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel id="year-select-label">Year</InputLabel>
            <Select
              labelId="year-select-label"
              value={selectedYear}
              label="Year"
              onChange={handleYearChange}
            >
              {availableYears.map((year) => (
                <MenuItem key={year} value={year}>
                  {year}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
        
        {consumptions.length === 0 ? (
          <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
            No consumption data available for the selected year.
          </Typography>
        ) : (
          <>
            {/* Summary Cards */}
            <Box sx={{ 
              display: 'grid', 
              gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr 1fr' }, 
              gap: 2, 
              mb: 3 
            }}>
              <Card variant="outlined" sx={{ backgroundColor: 'primary.50' }}>
                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                  <Typography variant="h6" color="primary">
                    {totalConsumption.toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total kWh
                  </Typography>
                </CardContent>
              </Card>
              <Card variant="outlined" sx={{ backgroundColor: 'success.50' }}>
                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                  <Typography variant="h6" color="success.main">
                    €{totalCostWithVat.toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Cost (with VAT)
                  </Typography>
                </CardContent>
              </Card>
              <Card variant="outlined" sx={{ backgroundColor: 'warning.50' }}>
                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                  <Typography variant="h6" color="warning.main">
                    {averageCentsPerKwh.toFixed(2)}¢
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Avg. cents/kWh
                  </Typography>
                </CardContent>
              </Card>
            </Box>
            
            {/* Consumption Chart */}
            <Box sx={{ height: 400, width: '100%' }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={chartData}
                  margin={{
                    top: 20,
                    right: 30,
                    left: 20,
                    bottom: 5,
                  }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="month" 
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis 
                    yAxisId="left"
                    orientation="left"
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis 
                    yAxisId="right"
                    orientation="right"
                    tick={{ fontSize: 12 }}
                  />
                  <Tooltip 
                    formatter={(value, name) => [
                      name === 'amount' ? `${value} kWh` : `€${Number(value).toFixed(2)}`,
                      name === 'amount' ? 'Consumption' : 
                      name === 'costWithVat' ? 'Cost (with VAT)' : 'Cost (without VAT)'
                    ]}
                  />
                  <Legend />
                  <Bar 
                    yAxisId="left"
                    dataKey="amount" 
                    fill="#1976d2" 
                    name="Consumption (kWh)"
                  />
                  <Bar 
                    yAxisId="right"
                    dataKey="costWithVat" 
                    fill="#dc004e" 
                    name="Cost with VAT (€)"
                  />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default ConsumptionChart;