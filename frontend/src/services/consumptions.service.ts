import api from '../utils/axios';
import { useConsumptionStore } from '../stores/useConsumptionStore';
import { ERROR_MESSAGES, API_ENDPOINTS } from '../utils/constants';
import { Consumption } from 'src/types/consumption.type';

class ConsumptionService {
  async fetchConsumptions(meterId: string, year: number): Promise<Consumption[]> {
    const { setLoading, setError, setConsumptions, clearError } = useConsumptionStore.getState();
    
    setLoading(true);
    clearError();
    
    try {
      const response = await api.get<Consumption[]>(API_ENDPOINTS.CONSUMPTIONS, {
        params: {
          meterId,
          year
        }
      });

      setConsumptions(response.data);
      
      return response.data;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || ERROR_MESSAGES.FETCH_CONSUMPTIONS_FAILED;
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  async fetchAvailableYears(meterId: string): Promise<number[]> {
    const { setLoadingYears, setAvailableYears } = useConsumptionStore.getState();
    
    setLoadingYears(true);
    
    try {
      const response = await api.get<number[]>(API_ENDPOINTS.YEARS, {
        params: {
          meterId
        }
      });

      setAvailableYears(response.data);
      return response.data;
    } catch (error: any) {
      const currentYear = new Date().getFullYear();
      const defaultYears = Array.from({ length: 5 }, (_, i) => currentYear - i);
      setAvailableYears(defaultYears);
      return defaultYears;
    } finally {
      setLoadingYears(false);
    }
  }
}

export const consumptionService = new ConsumptionService();