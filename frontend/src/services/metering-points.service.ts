import api from '../utils/axios';
import { useMeteringPointsStore, type MeteringPoint } from '../stores/useMeteringPointsStore';
import { ERROR_MESSAGES, API_ENDPOINTS } from '../utils/constants';

class MeteringPointsService {
  async fetchMeteringPoints(): Promise<MeteringPoint[]> {
    const { setLoading, setError, setMeteringPoints, clearError } = useMeteringPointsStore.getState();
    
    setLoading(true);
    clearError();
    
    try {
      const response = await api.get<MeteringPoint[]>(API_ENDPOINTS.METERING_POINTS);

      setMeteringPoints(response.data);
      
      return response.data;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || ERROR_MESSAGES.FETCH_ADDRESSES_FAILED;
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  }
}

export const meteringPointsService = new MeteringPointsService();