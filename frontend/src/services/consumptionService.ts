import api from '../utils/axios';
import { useConsumptionStore, type Consumption } from '../stores/useConsumptionStore';
import { ERROR_MESSAGES } from '../utils/constants';

class ConsumptionService {
  async fetchConsumptions(meterId: string, year: number): Promise<Consumption[]> {
    const { setLoading, setError, setConsumptions, clearError } = useConsumptionStore.getState();
    
    setLoading(true);
    clearError();
    
    try {
      const response = await api.get<Consumption[]>('/consumptions', {
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
}

export const consumptionService = new ConsumptionService();