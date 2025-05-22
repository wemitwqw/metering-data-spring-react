import { create } from 'zustand';

export interface Consumption {
  monthNumber: number;
  month: string;
  amount: number;
  amountUnit: string;
  totalCostEur: number;
  totalCostEurWithVat: number;
  centsPerKwh: number;
  centsPerKwhWithVat: number;
}

interface ConsumptionState {
  consumptions: Consumption[];
  isLoading: boolean;
  error: string | null;
  selectedYear: number;
  
  setConsumptions: (consumptions: Consumption[]) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedYear: (year: number) => void;
  clearError: () => void;
  reset: () => void;
}

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