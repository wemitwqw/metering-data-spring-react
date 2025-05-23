import { create } from 'zustand';
import { Consumption, ConsumptionState } from 'src/types/consumption.type';

export const useConsumptionStore = create<ConsumptionState>((set) => ({
  consumptions: [],
  isLoading: false,
  error: null,
  selectedYear: new Date().getFullYear(),
  
  setConsumptions: (consumptions: Consumption[]) => {
    set({ consumptions, error: null });
  },
  
  setLoading: (loading: boolean) => {
    set({ isLoading: loading });
  },
  
  setError: (error: string | null) => {
    set({ error });
  },
  
  setSelectedYear: (year: number) => {
    set({ selectedYear: year });
  },
  
  clearError: () => {
    set({ error: null });
  },
  
  reset: () => {
    set({ 
      consumptions: [], 
      isLoading: false, 
      error: null,
      selectedYear: new Date().getFullYear()
    });
  }
}));