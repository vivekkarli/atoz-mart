import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function LoginPage({ setAccessToken, setIsAuthenticated }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [mail, setMail] = useState('');
  const [mobileNo, setMobileNo] = useState('');
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [showSignup, setShowSignup] = useState(false); // Toggle between login and signup UI
  const navigate = useNavigate();

  const handleAtoZMartLogin = async () => {
    try {
      const response = await axios.post('http://localhost:8072/atozmart/authserver/login', {
        username,
        password,
      });
      console.log(response.headers);
      localStorage.setItem('accessToken', response.headers['x-access-token']);
      localStorage.setItem('authType', 'atozmart');
      localStorage.setItem('tokenExpiration', Date.now() + 60 * 1000);
      setAccessToken(response.headers['x-access-token']);
      setIsAuthenticated(true);
      setError(null);
      setSuccessMessage(null);
      navigate('/');
    } catch (error) {
      console.log(error);
      setError('Login failed. Please check your credentials.');
      setSuccessMessage(null);
    }
  };

  const handleKeycloakLogin = () => {
    const authUrl = `http://localhost:8073/realms/master/protocol/openid-connect/auth?client_id=atozmart-ui&redirect_uri=${encodeURIComponent('http://localhost:5173/login/callback')}&response_type=code&scope=openid`;
    window.location.href = authUrl;
  };

  const handleSignup = async () => {
    try {
      await axios.post('http://localhost:8072/atozmart/authserver/signup', {
        username,
        password,
        mail,
        mobileNo,
      });
      // On successful signup, show the login UI with a success message
      setShowSignup(false);
      setSuccessMessage('Signed up successfully, now login');
      setError(null);
      setUsername('');
      setPassword('');
      setMail('');
      setMobileNo('');
    } catch (error) {
      console.log(error);
      setError('Signup failed. Please try again.');
      setSuccessMessage(null);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10">
      <h1 className="text-3xl font-bold mb-4">
        {showSignup ? 'Sign Up for AtoZMart' : 'Login to AtoZMart'}
      </h1>
      {error && (
        <div className="bg-red-500 text-white p-4 text-center mb-4">
          {error}
        </div>
      )}
      {successMessage && !showSignup && (
        <div className="bg-green-500 text-white p-4 text-center mb-4">
          {successMessage}
        </div>
      )}
      <div className="mb-4">
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="w-full p-2 border rounded"
        />
      </div>
      <div className="mb-4">
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-2 border rounded"
        />
      </div>
      {showSignup && (
        <>
          <div className="mb-4">
            <input
              type="email"
              placeholder="Email"
              value={mail}
              onChange={(e) => setMail(e.target.value)}
              className="w-full p-2 border rounded"
            />
          </div>
          <div className="mb-4">
            <input
              type="text"
              placeholder="Mobile Number"
              value={mobileNo}
              onChange={(e) => setMobileNo(e.target.value)}
              className="w-full p-2 border rounded"
            />
          </div>
        </>
      )}
      <div className="flex space-x-2">
        {showSignup ? (
          <>
            <button
              onClick={handleSignup}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Sign Up
            </button>
            <button
              onClick={() => {
                setShowSignup(false);
                setError(null);
                setSuccessMessage(null);
                setUsername('');
                setPassword('');
                setMail('');
                setMobileNo('');
              }}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Back to Login
            </button>
          </>
        ) : (
          <>
            <button
              onClick={handleAtoZMartLogin}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Login with AtoZMart
            </button>
            <button
              onClick={handleKeycloakLogin}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Login with Keycloak
            </button>
            <button
              onClick={() => {
                setShowSignup(true);
                setError(null);
                setSuccessMessage(null);
                setUsername('');
                setPassword('');
                setMail('');
                setMobileNo('');
              }}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Sign Up
            </button>
          </>
        )}
      </div>
    </div>
  );
}

export default LoginPage;