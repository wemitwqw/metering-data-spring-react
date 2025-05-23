import { ERROR_MESSAGES } from "./constants";

// simple dummy validator function
export const validateForm = (
    email: string, 
    password: string, 
    setFormError: React.Dispatch<React.SetStateAction<string>>
) => {
    if (!email) {
        setFormError(ERROR_MESSAGES.REQUIRED_FIELD);
        return;
    }
    
    if (!password) {
        setFormError(ERROR_MESSAGES.REQUIRED_FIELD);
        return;
    }
};