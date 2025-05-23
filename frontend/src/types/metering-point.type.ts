export interface MeteringPoint {
  address: string;
  meterId: string;
};

export interface MeteringPointState {
  meteringPoints: MeteringPoint[];
  isLoading: boolean;
  error: string | null;
  
  setMeteringPoints: (points: MeteringPoint[]) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  reset: () => void;
};