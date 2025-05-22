import { 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem 
} from '@mui/material';
import type { MeteringPoint } from '../stores/useMeteringPointsStore';

interface MeteringPointSelectorProps {
  meteringPoints: MeteringPoint[];
  selectedMeteringPoint: string;
  onMeteringPointChange: (meterId: string) => void;
}

const MeteringPointSelector = ({ 
  meteringPoints, 
  selectedMeteringPoint, 
  onMeteringPointChange 
}: MeteringPointSelectorProps) => {
  const handleChange = (event: any) => {
    onMeteringPointChange(event.target.value);
  };
  
  return (
    <FormControl fullWidth sx={{ mb: 3 }}>
      <InputLabel id="metering-point-select-label">Metering Point</InputLabel>
      <Select
        labelId="metering-point-select-label"
        id="metering-point-select"
        value={selectedMeteringPoint}
        label="Metering Point"
        onChange={handleChange}
      >
        {meteringPoints.map((point) => (
          <MenuItem key={point.meterId} value={point.meterId}>
            {point.address}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};

export default MeteringPointSelector;