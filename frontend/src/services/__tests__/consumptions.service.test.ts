import api from '../../utils/axios';
import { consumptionService } from '../consumptions.service';
import { useConsumptionStore } from '../../stores/useConsumptionStore';
import { API_ENDPOINTS, ERROR_MESSAGES } from '../../utils/constants';
import { Consumption } from '../../types/consumption.type';

jest.mock('../../utils/axios', () => ({
  get: jest.fn(),
}));

jest.mock('../../stores/useConsumptionStore', () => ({
  useConsumptionStore: {
    getState: jest.fn(),
  },
}));

describe('ConsumptionService', () => {
  const mockSetLoading = jest.fn();
  const mockSetError = jest.fn();
  const mockSetConsumptions = jest.fn();
  const mockClearError = jest.fn();
  
  const mockConsumptions: Consumption[] = [
    {
      monthNumber: 1,
      month: 'January',
      amount: 400,
      amountUnit: 'kWh',
      totalCostEur: 80,
      totalCostEurWithVat: 96,
      centsPerKwh: 20,
      centsPerKwhWithVat: 24
    },
    {
      monthNumber: 2,
      month: 'February',
      amount: 350,
      amountUnit: 'kWh',
      totalCostEur: 70,
      totalCostEurWithVat: 84,
      centsPerKwh: 20,
      centsPerKwhWithVat: 24
    }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    
    (useConsumptionStore.getState as jest.Mock).mockReturnValue({
      setLoading: mockSetLoading,
      setError: mockSetError,
      setConsumptions: mockSetConsumptions,
      clearError: mockClearError
    });
  });

  describe('fetchConsumptions', () => {
    it('should successfully fetch consumptions for a meter and year', async () => {
      const meterId = 'meter-123';
      const year = 2024;
      
      const mockResponse = {
        data: mockConsumptions
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      const result = await consumptionService.fetchConsumptions(meterId, year);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith(API_ENDPOINTS.CONSUMPTIONS, {
        params: {
          meterId,
          year
        }
      });
      expect(mockSetConsumptions).toHaveBeenCalledWith(mockConsumptions);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
      expect(result).toEqual(mockConsumptions);
    });
    
    it('should handle fetch failure and set error message', async () => {
      const meterId = 'meter-123';
      const year = 2024;
      
      const mockError = {
        response: {
          data: {
            message: 'Failed to fetch consumption data'
          }
        }
      };
      
      (api.get as jest.Mock).mockRejectedValueOnce(mockError);
      
      await expect(consumptionService.fetchConsumptions(meterId, year)).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith('/consumptions', {
        params: {
          meterId,
          year
        }
      });
      expect(mockSetError).toHaveBeenCalledWith('Failed to fetch consumption data');
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
    
    it('should use default error message when response has no message', async () => {
      const meterId = 'meter-123';
      const year = 2024;
      
      const mockError = { response: {} };
      
      (api.get as jest.Mock).mockRejectedValueOnce(mockError);
      
      await expect(consumptionService.fetchConsumptions(meterId, year)).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith('/consumptions', {
        params: {
          meterId,
          year
        }
      });
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.FETCH_CONSUMPTIONS_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });

    it('should handle network error without response object', async () => {
      const meterId = 'meter-123';
      const year = 2024;
      
      const networkError = new Error('Network Error');
      
      (api.get as jest.Mock).mockRejectedValueOnce(networkError);
      
      await expect(consumptionService.fetchConsumptions(meterId, year)).rejects.toEqual(networkError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.FETCH_CONSUMPTIONS_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });

    it('should handle empty consumption data', async () => {
      const meterId = 'meter-123';
      const year = 2024;
      
      const mockResponse = {
        data: []
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      const result = await consumptionService.fetchConsumptions(meterId, year);
      
      expect(mockSetConsumptions).toHaveBeenCalledWith([]);
      expect(result).toEqual([]);
    });

    it('should handle different year parameters', async () => {
      const meterId = 'meter-456';
      const year = 2023;
      
      const mockResponse = {
        data: mockConsumptions
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      await consumptionService.fetchConsumptions(meterId, year);
      
      expect(api.get).toHaveBeenCalledWith('/consumptions', {
        params: {
          meterId: 'meter-456',
          year: 2023
        }
      });
    });
  });
});