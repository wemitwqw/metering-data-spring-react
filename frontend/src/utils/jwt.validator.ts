import { useAuthStore } from "../stores/useAuthStore";

export const shouldRefreshToken = (): boolean => {
    const { refreshTokenExpiresAt } = useAuthStore.getState();
    if (!refreshTokenExpiresAt) return true;
    
    return Date.now() > refreshTokenExpiresAt;
};