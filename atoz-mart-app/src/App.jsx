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
          <Route path="/cart" element={<CartPage accessToken={accessToken} isAuthenticated={isAuthenticated} />} />
          <Route path="/profile" element={<ProfilePage accessToken={accessToken} isAuthenticated={isAuthenticated} />} /> 
          <Route path="/profile/edit" element={<EditProfilePage accessToken={accessToken} isAuthenticated={isAuthenticated} />} /> 
        </Routes>
      </div>
    </Router>
  );
}

export default App;