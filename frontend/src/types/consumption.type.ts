export interface Consumption {
  monthNumber: number;
  month: string;
  amount: number;
  amountUnit: string;
  totalCostEur: number;
  totalCostEurWithVat: number;
  centsPerKwh: number;
  centsPerKwhWithVat: number;
};

export interface ConsumptionState {
  consumptions: Consumption[];
  isLoading: boolean;
  error: string | null;
  selectedYear: number;
  availableYears: number[];
  isLoadingYears: boolean;
  
  setConsumptions: (consumptions: Consumption[]) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedYear: (year: number) => void;
  clearError: () => void;
  reset: () => void;
  setAvailableYears: (years: number[]) => void;
  setLoadingYears: (loading: boolean) => void;
};