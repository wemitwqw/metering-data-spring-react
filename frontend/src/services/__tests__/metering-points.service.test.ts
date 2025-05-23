import api from '../../utils/axios';
import { meteringPointsService } from '../metering-points.service';
import { useMeteringPointsStore } from '../../stores/useMeteringPointsStore';
import { ERROR_MESSAGES } from '../../utils/constants';
import { MeteringPoint } from '../../types/metering-point.type';

jest.mock('../../utils/axios', () => ({
  get: jest.fn(),
}));

jest.mock('../../stores/useMeteringPointsStore', () => ({
  useMeteringPointsStore: {
    getState: jest.fn(),
  },
}));

describe('MeteringPointsService', () => {
  const mockSetLoading = jest.fn();
  const mockSetError = jest.fn();
  const mockSetMeteringPoints = jest.fn();
  const mockClearError = jest.fn();
  
  const mockMeteringPoints: MeteringPoint[] = [
    {
      address: '123 Main St',
      meterId: 'meter-123'
    },
    {
      address: '456 Elm St',
      meterId: 'meter-456'
    }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    
    (useMeteringPointsStore.getState as jest.Mock).mockReturnValue({
      setLoading: mockSetLoading,
      setError: mockSetError,
      setMeteringPoints: mockSetMeteringPoints,
      clearError: mockClearError
    });
  });

  describe('fetchMeteringPoints', () => {
    it('should successfully fetch metering points', async () => {
      const mockResponse = {
        data: mockMeteringPoints
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      const result = await meteringPointsService.fetchMeteringPoints();
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith('/metering-points');
      expect(mockSetMeteringPoints).toHaveBeenCalledWith(mockMeteringPoints);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
      expect(result).toEqual(mockMeteringPoints);
    });
    
    it('should handle fetch failure and set error message', async () => {
      const mockError = {
        response: {
          data: {
            message: 'Failed to fetch metering points'
          }
        }
      };
      
      (api.get as jest.Mock).mockRejectedValueOnce(mockError);
      
      await expect(meteringPointsService.fetchMeteringPoints()).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith('/metering-points');
      expect(mockSetError).toHaveBeenCalledWith('Failed to fetch metering points');
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
    
    it('should use default error message when response has no message', async () => {
      const mockError = { response: {} };
      
      (api.get as jest.Mock).mockRejectedValueOnce(mockError);

      await expect(meteringPointsService.fetchMeteringPoints()).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.get).toHaveBeenCalledWith('/metering-points');
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.FETCH_METERING_POINTS_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });

    it('should handle network error without response object', async () => {
      const networkError = new Error('Network Error');
      
      (api.get as jest.Mock).mockRejectedValueOnce(networkError);

      await expect(meteringPointsService.fetchMeteringPoints()).rejects.toEqual(networkError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.FETCH_METERING_POINTS_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });

    it('should handle empty metering points data', async () => {
      const mockResponse = {
        data: []
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      const result = await meteringPointsService.fetchMeteringPoints();
      
      expect(mockSetMeteringPoints).toHaveBeenCalledWith([]);
      expect(result).toEqual([]);
    });

    it('should handle single metering point', async () => {
      const singleMeteringPoint = [mockMeteringPoints[0]];
      const mockResponse = {
        data: singleMeteringPoint
      };
      
      (api.get as jest.Mock).mockResolvedValueOnce(mockResponse);
      
      const result = await meteringPointsService.fetchMeteringPoints();
      
      expect(mockSetMeteringPoints).toHaveBeenCalledWith(singleMeteringPoint);
      expect(result).toEqual(singleMeteringPoint);
    });
  });
});