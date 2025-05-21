import { create } from 'zustand';
import { meteringPointsApi } from '../services/meteringPointsService';
import { ERROR_MESSAGES } from '../utils/constants';
import { useAuthStore } from '../stores/useAuthStore';
import { setAuthToken } from '../services/meteringPointsService';

export interface MeteringPoint {
  address: string;
  meterId: string;
}

interface MeteringPointState {
  meteringPoints: MeteringPoint[];
  isLoading: boolean;
  error: string | null;
  fetchMeteringPoints: () => Promise<void>;
  clearError: () => void;
}

export const useMeteringPointsStore = create<MeteringPointState>((set) => ({
  meteringPoints: [],
  isLoading: false,
  error: null,
  
  fetchMeteringPoints: async () => {
    set({ isLoading: true, error: null });
    
    try {
      const token = useAuthStore.getState().token;
      if (token) {
        setAuthToken(token);
      }
      
      const response = await meteringPointsApi.fetchMeteringPoints();
      set({ meteringPoints: response.data, isLoading: false });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.response?.data?.message || ERROR_MESSAGES.FETCH_ADDRESSES_FAILED
      });
      throw error;
    }
  },
  
  clearError: () => set({ error: null })
}));