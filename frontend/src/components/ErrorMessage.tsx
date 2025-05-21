import { Alert, AlertTitle, Box, Button } from '@mui/material';

interface ErrorMessageProps {
  message: string;
  title?: string;
  onRetry?: () => void;
}

const ErrorMessage = ({ 
  message, 
  title = 'Error', 
  onRetry 
}: ErrorMessageProps) => {
  return (
    <Box sx={{ width: '100%', my: 2 }}>
      <Alert 
        severity="error"
        action={
          onRetry ? (
            <Button 
              color="inherit" 
              size="small" 
              onClick={onRetry}
              sx={{ 
                fontWeight: 500,
                textTransform: 'none'
              }}
            >
              Retry
            </Button>
          ) : undefined
        }
      >
        <AlertTitle>{title}</AlertTitle>
        {message}
      </Alert>
    </Box>
  );
};

export default ErrorMessage;