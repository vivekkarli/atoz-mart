import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import LoginPage from './components/LoginPage';
import LoginCallback from './components/LoginCallback';
import HomePage from './components/HomePage';
import WishlistPage from './components/WishlistPage';

function App() {
  const [accessToken, setAccessToken] = useState(localStorage.getItem('accessToken') || null);
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('accessToken'));

  useEffect(() => {
    if (accessToken) {
      setIsAuthenticated(true);
    } else {
      setIsAuthenticated(false);
    }
  }, [accessToken]);

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('authType');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('tokenExpiration');
    localStorage.removeItem('authState');
    setAccessToken(null);
    setIsAuthenticated(false);
    window.location.href = '/';
  };

  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <Header isAuthenticated={isAuthenticated} logout={logout} />
        <Routes>
          <Route path="/" element={<HomePage accessToken={accessToken} logout={logout} isAuthenticated={isAuthenticated} />} />
          <Route path="/login" element={<LoginPage setAccessToken={setAccessToken} setIsAuthenticated={setIsAuthenticated} />} />
          <Route path="/login/callback" element={<LoginCallback setAccessToken={setAccessToken} setIsAuthenticated={setIsAuthenticated} />} />
          <Route path="/wishlist" element={<WishlistPage accessToken={accessToken} logout={logout} isAuthenticated={isAuthenticated} />} />
          <Route path="/cart" element={<div className="text-center mt-10">Cart Page (Coming Soon)</div>} />
          <Route path="/profile" element={<div className="text-center mt-10">Profile Page (Coming Soon)</div>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;