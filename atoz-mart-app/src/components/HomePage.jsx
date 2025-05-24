import { useState, useEffect } from 'react';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';
import { useNavigate } from 'react-router-dom';

function HomePage({ accessToken, logout, isAuthenticated }) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sortBy, setSortBy] = useState('name');
  const [direction, setDirection] = useState('asc');
  const [totalPages, setTotalPages] = useState(0);
  const [quantities, setQuantities] = useState({});
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (accessToken && accessToken !== currentToken) {
      setCurrentToken(accessToken);
    }
  }, [accessToken]);

  useEffect(() => {
    fetchItems();
  }, [page, sortBy, direction, currentToken]);

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const fetchItems = async () => {
    try {
      let token = currentToken;
      const headers = token ? { Authorization: `Bearer ${token}` } : {};

      if (token) {
        const authType = localStorage.getItem('authType');
        if (authType === 'keycloak' && isTokenExpired()) {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } else if (isTokenExpired()) {
          setError('Session expired. Please log in again.');
          return;
        }
      }

      const response = await axios.get(`http://localhost:8072/atozmart/catalog/items`, {
        params: { page, size, 'sort-by': sortBy, direction },
        headers,
      });
      setItems(response.data);
      setTotalPages(response.data.totalPages || 10);
      setError(null);
    } catch (error) {
      console.error('Error fetching items:', error);
      if (error.response) {
        setError(`Failed to fetch items: ${error.response.data || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const handleQuantityChange = (itemName, value) => {
    setQuantities({ ...quantities, [itemName]: value });
  };

  const addToWishlist = async (item) => {
    if (!isAuthenticated) {
      setError('Please log in to add items to your wishlist. Click the "Login" button in the header.');
      return;
    }

    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        token = await refreshAccessToken();
        setCurrentToken(token);
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        return;
      }

      await axios.post(
        'http://localhost:8072/atozmart/wishlist/items',
        {
          itemName: item.name,
          price: item.price,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setError(null);
      alert(`Added ${item.name} to wishlist`);
    } catch (error) {
      console.error('Error adding to wishlist:', error);
      if (error.response) {
        setError(`Failed to add to wishlist: ${error.response.data || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const addToCart = (item) => {
    const quantity = quantities[item.name] || 1;
    alert(`Added ${quantity} of ${item.name} to cart`);
  };

  return (
    <div>
      {error && (
        <div className="bg-red-500 text-white p-4 text-center">
          {error}
        </div>
      )}
      <h1 className="text-3xl font-bold mb-4">Welcome to AtoZMart</h1>
      <div className="mb-4 flex space-x-4">
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          className="p-2 border rounded"
        >
          <option value="name">Name</option>
          <option value="price">Price</option>
        </select>
        <select
          value={direction}
          onChange={(e) => setDirection(e.target.value)}
          className="p-2 border rounded"
        >
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        {items.map((item) => (
          <div key={item.name} className="border p-4 rounded shadow">
            <img
              src="https://via.placeholder.com/150"
              alt={item.name}
              className="w-full h-40 object-cover mb-2"
            />
            <h2 className="text-xl font-semibold">{item.name}</h2>
            <p className="text-gray-600">₹{item.price}</p>
            <div className="mt-2">
              <input
                type="number"
                min="1"
                value={quantities[item.name] || 1}
                onChange={(e) => handleQuantityChange(item.name, e.target.value)}
                className="w-16 p-1 border rounded"
              />
            </div>
            <div className="mt-4 flex space-x-2">
              <button
                onClick={() => addToWishlist(item)}
                className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600"
              >
                Add to Wishlist
              </button>
              <button
                onClick={() => addToCart(item)}
                className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
              >
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>
      <div className="mt-4 flex space-x-2">
        <button
          onClick={() => setPage(0)}
          disabled={page === 0}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
        >
          First
        </button>
        <button
          onClick={() => setPage(page - 1)}
          disabled={page === 0}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
        >
          Previous
        </button>
        <span className="px-4 py-2">Page {page + 1} of {totalPages}</span>
        <button
          onClick={() => setPage(page + 1)}
          disabled={page >= totalPages - 1}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
        >
          Next
        </button>
        <button
          onClick={() => setPage(totalPages - 1)}
          disabled={page >= totalPages - 1}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
        >
          Last
        </button>
      </div>
    </div>
  );
}

export default HomePage;