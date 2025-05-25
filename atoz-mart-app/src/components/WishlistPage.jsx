import { useState, useEffect } from 'react';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';
import { useNavigate } from 'react-router-dom';

function WishlistPage({ accessToken, logout, isAuthenticated }) {
  const [wishlistItems, setWishlistItems] = useState([]);
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
    if (isAuthenticated && currentToken) {
      fetchWishlist();
    }
  }, [isAuthenticated, currentToken]);

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const fetchWishlist = async () => {
    try {
      let token = currentToken;
      if (!token) {
        token = localStorage.getItem('accessToken');
        if (!token) {
          setError('No access token available. Please log in.');
          return;
        }
        setCurrentToken(token);
      }

      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        token = await refreshAccessToken();
        setCurrentToken(token);
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        return;
      }

      const response = await axios.get('http://localhost:8072/atozmart/wishlist/items', {
        headers: { Authorization: `Bearer ${token}` },
      });

      console.log('Wishlist API Response:', response.data);

      const mappedItems = response.data.map((item) => ({
        name: item.itemName,
        price: item.price,
      }));

      const validItems = mappedItems.filter(
        (item) => item.name && typeof item.price === 'string' && item.price.trim() !== ''
      );

      if (validItems.length !== response.data.length) {
        setError('Some wishlist items are invalid and have been skipped.');
      }

      setWishlistItems(validItems);
      setError(null);
    } catch (error) {
      console.error('Error fetching wishlist:', error);
      if (error.response && error.response.status === 404 && error.response.data?.errorMsg === 'no items in wishlist') {
        // Handle the expected case of an empty wishlist
        setWishlistItems([]);
        setError(null);
      } else if (error.response) {
        setError(`Failed to fetch wishlist: ${error.response.data?.message || error.message}`);
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

  const addToCart = async (item) => {
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

      const quantity = quantities[item.name] || 1;
      await axios.post(
        'http://localhost:8072/atozmart/cart/items',
        {
          itemName: item.name,
          unitPrice: item.price,
          quantity: parseInt(quantity),
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setError(null);
      alert(`Added ${quantity} of ${item.name} to cart`);
    } catch (error) {
      console.error('Error adding to cart:', error);
      if (error.response) {
        setError(`Failed to add to cart: ${error.response.data?.message || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const deleteFromWishlist = async (item) => {
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

      console.log(`Deleting item from wishlist: ${item.name}`);

      await axios.delete('http://localhost:8072/atozmart/wishlist/items', {
        params: { itemName: item.name },
        headers: { Authorization: `Bearer ${token}` },
      });

      setError(null);
      alert(`Removed ${item.name} from wishlist`);
      fetchWishlist(); // Refresh the wishlist after deletion
    } catch (error) {
      console.error('Error deleting from wishlist:', error);
      if (error.response) {
        setError(`Failed to delete from wishlist: ${error.response.data?.message || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const clearWishlist = async () => {
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

      await axios.delete('http://localhost:8072/atozmart/wishlist/items', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setError(null);
      alert('Wishlist cleared successfully');
      fetchWishlist(); // Refresh the wishlist after clearing
    } catch (error) {
      console.error('Error clearing wishlist:', error);
      if (error.response) {
        setError(`Failed to clear wishlist: ${error.response.data?.message || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  if (!isAuthenticated) {
    return (
      <div>
        <h1 className="text-3xl font-bold mb-4">Your Wishlist</h1>
        <p className="text-gray-600 text-center">
          Please log in to view your wishlist. Click the "Login" button in the header.
        </p>
      </div>
    );
  }

  return (
    <div>
      {error && (
        <div className="bg-red-500 text-white p-4 text-center">
          {error}
        </div>
      )}
      <h1 className="text-3xl font-bold mb-4">Your Wishlist</h1>
      {wishlistItems.length === 0 ? (
        <p className="text-gray-600">Your wishlist is empty.</p>
      ) : (
        <div>
          <div className="mb-4">
            <button
              onClick={clearWishlist}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              Clear Wishlist
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {wishlistItems.map((item) => (
              <div key={item.name} className="border p-4 rounded shadow">
                <img
                  src="https://via.placeholder.com/150"
                  alt={item.name || 'Item'}
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
                    onClick={() => addToCart(item)}
                    className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                  >
                    Add to Cart
                  </button>
                  <button
                    onClick={() => deleteFromWishlist(item)}
                    className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default WishlistPage;