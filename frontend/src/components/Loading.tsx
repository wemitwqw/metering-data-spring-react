import { Box, CircularProgress, Typography } from '@mui/material';
import type { SxProps, Theme } from '@mui/material/styles';

interface LoadingProps {
  message?: string;
  fullPage?: boolean;
}

const Loading = ({ message = 'Loading...', fullPage = false }: LoadingProps) => {
  const containerStyles: SxProps<Theme> = fullPage 
    ? { 
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        width: '100%'
      } 
    : { 
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 4
      };
  
  return (
    <Box sx={containerStyles}>
      <CircularProgress size={40} thickness={4} />
      {message && (
        <Typography 
          variant="body1" 
          color="text.secondary" 
          sx={{ mt: 2 }}
        >
          {message}
        </Typography>
      )}
    </Box>
  );
};

export default Loading;