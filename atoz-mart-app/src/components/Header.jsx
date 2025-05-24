import { useNavigate, useLocation } from 'react-router-dom';

function Header({ isAuthenticated, logout }) {
  const navigate = useNavigate();
  const location = useLocation();

  const getButtonClass = (path) => {
    const baseClass = "text-white px-4 py-2 rounded hover:bg-purple-600";
    const activeClass = location.pathname === path ? "bg-purple-700" : "bg-purple-500";
    return `${baseClass} ${activeClass}`;
  };

  const getLoginLogoutClass = () => {
    const baseClass = "text-white px-4 py-2 rounded hover:bg-blue-600";
    const activeClass = location.pathname === '/login' && !isAuthenticated ? "bg-blue-700" : "bg-blue-500";
    return `${baseClass} ${activeClass}`;
  };

  return (
    <div className="flex justify-between items-center mb-4">
      <h1 className="text-3xl font-bold">AtoZMart</h1>
      <div className="flex space-x-4">
        <button
          onClick={() => navigate('/')}
          className={getButtonClass('/')}
        >
          Home
        </button>
        <button
          onClick={() => navigate('/wishlist')}
          className={getButtonClass('/wishlist')}
        >
          Wishlist
        </button>
        <button
          onClick={() => navigate('/cart')}
          className={getButtonClass('/cart')}
        >
          Cart
        </button>
        <button
          onClick={() => navigate('/profile')}
          className={getButtonClass('/profile')}
        >
          Profile
        </button>
        {isAuthenticated ? (
          <button
            onClick={logout}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Logout
          </button>
        ) : (
          <button
            onClick={() => navigate('/login')}
            className={getLoginLogoutClass()}
          >
            Login
          </button>
        )}
      </div>
    </div>
  );
}

export default Header;