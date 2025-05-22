import { create } from 'zustand';

export interface MeteringPoint {
  address: string;
  meterId: string;
}

interface MeteringPointState {
  meteringPoints: MeteringPoint[];
  isLoading: boolean;
  error: string | null;
  
  setMeteringPoints: (points: MeteringPoint[]) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  reset: () => void;
}

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