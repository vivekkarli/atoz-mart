import { useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function LoginCallback({ setAccessToken, setIsAuthenticated }) {
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if (code) {
      const tokenEndpoint = 'http://localhost:8073/realms/master/protocol/openid-connect/token';
      const data = new URLSearchParams();
      data.append('grant_type', 'authorization_code');
      data.append('client_id', 'atozmart-ui');
      data.append('client_secret', 'WC8Aq9qxVy8uFqcxN7NWYdWgasTkPmn6');
      data.append('redirect_uri', 'http://localhost:5173/login/callback');
      data.append('code', code);

      axios
        .post(tokenEndpoint, data, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        })
        .then((response) => {
          const { access_token, refresh_token, expires_in } = response.data;
          localStorage.setItem('accessToken', access_token);
          localStorage.setItem('refreshToken', refresh_token);
          localStorage.setItem('authType', 'keycloak');
          localStorage.setItem('tokenExpiration', Date.now() + expires_in * 1000);
          setAccessToken(access_token);
          setIsAuthenticated(true);
          navigate('/'); // Redirect to Home page after login
        })
        .catch((error) => {
          console.error('Error exchanging code for token:', error);
          navigate('/login');
        });
    } else {
      navigate('/login');
    }
  }, [navigate, setAccessToken, setIsAuthenticated]);

  return <div className="text-center mt-10">Processing login...</div>;
}

export default LoginCallback;