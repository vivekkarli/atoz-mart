import axios from 'axios';

export function generateRandomState() {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
}

export async function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  if (!refreshToken) {
    throw new Error('No refresh token available.');
  }

  try {
    const response = await axios.post(
      'http://localhost:8073/realms/master/protocol/openid-connect/token',
      new URLSearchParams({
        grant_type: 'refresh_token',
        client_id: 'atozmart-ui',
        client_secret: 'WC8Aq9qxVy8uFqcxN7NWYdWgasTkPmn6', // Hardcoded for now (not secure)
        refresh_token: refreshToken,
      }),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      }
    );

    const { access_token, refresh_token, expires_in } = response.data;

    localStorage.setItem('accessToken', access_token);
    localStorage.setItem('refreshToken', refresh_token);
    localStorage.setItem('tokenExpiration', Date.now() + expires_in * 1000);

    return access_token;
  } catch (error) {
    console.error('Token refresh failed:', error);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('tokenExpiration');
    localStorage.removeItem('authType');
    throw new Error('Failed to refresh token. Please log in again.');
  }
}