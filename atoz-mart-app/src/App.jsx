import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import LoginPage from './components/LoginPage';
import LoginCallback from './components/LoginCallback';
import HomePage from './components/HomePage';
import WishlistPage from './components/WishlistPage';
import CartPage from './components/CartPage';
import ProfilePage from './components/ProfilePage';
import EditProfilePage from './components/EditProfilePage';
import CreateProfilePage from './components/CreateProfilePage';

function App() {
  const [accessToken, setAccessToken] = useState(localStorage.getItem('accessToken') || null);
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('accessToken'));

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const expiration = localStorage.getItem('tokenExpiration');
    if (token && expiration && Date.now() < parseInt(expiration)) {
      setIsAuthenticated(true);
      setAccessToken(token);
    } else {
      setIsAuthenticated(false);
      setAccessToken(null);
      localStorage.removeItem('accessToken');
      localStorage.removeItem('authType');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('tokenExpiration');
      localStorage.removeItem('authState');
    }
  }, []);

  const handleLogout = () => {
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
        <Header isAuthenticated={isAuthenticated} logout={handleLogout} />
        <Routes>
          <Route path="/" element={<HomePage accessToken={accessToken} logout={handleLogout} isAuthenticated={isAuthenticated} />} />
          <Route path="/login" element={<LoginPage setAccessToken={setAccessToken} setIsAuthenticated={setIsAuthenticated} />} />
          <Route path="/login/callback" element={<LoginCallback setAccessToken={setAccessToken} setIsAuthenticated={setIsAuthenticated} />} />
          <Route path="/wishlist" element={<WishlistPage accessToken={accessToken} logout={handleLogout} isAuthenticated={isAuthenticated} />} />
          <Route path="/cart" element={<CartPage accessToken={accessToken} isAuthenticated={isAuthenticated} />} />
          <Route
            path="/profile"
            element={<ProfilePage accessToken={accessToken} isAuthenticated={isAuthenticated} onLogout={handleLogout} />}
          />
          <Route
            path="/profile/edit"
            element={<EditProfilePage accessToken={accessToken} isAuthenticated={isAuthenticated} />}
          />
          <Route
            path="/create-profile"
            element={<CreateProfilePage accessToken={accessToken} isAuthenticated={isAuthenticated} onLogout={handleLogout} />}
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;