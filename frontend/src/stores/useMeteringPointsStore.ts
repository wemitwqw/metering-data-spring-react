import { create } from 'zustand';
import { MeteringPoint, MeteringPointState } from 'src/types/metering-point.type';

export const useMeteringPointsStore = create<MeteringPointState>((set) => ({
  meteringPoints: [],
  isLoading: false,
  error: null,
  
  setMeteringPoints: (points: MeteringPoint[]) => {
    set({ meteringPoints: points, error: null });
  },
  
  setLoading: (loading: boolean) => {
    set({ isLoading: loading });
  },
  
  setError: (error: string | null) => {
    set({ error });
  },
  
  clearError: () => {
    set({ error: null });
  },
  
  reset: () => {
    set({ meteringPoints: [], isLoading: false, error: null });
  }
}));