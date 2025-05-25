import { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';
import { useNavigate } from 'react-router-dom';

function HomePage({ accessToken, logout, isAuthenticated }) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('name');
  const [direction, setDirection] = useState('asc');
  const [lastPage, setLastPage] = useState(false);
  const [noOfItems, setNoOfItems] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [pageHistory, setPageHistory] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const isInitialMount = useRef(true); // Track initial render

  useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      fetchItems();
    } else if (!loading) {
      fetchItems();
    }
  }, [page, size, sortBy, direction, lastPage, currentToken]);

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const fetchItems = async () => {
    setLoading(true);
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
          setLoading(false);
          return;
        }
      }

      const params = {
        size,
        'sort-by': sortBy,
        direction,
        lastPage,
      };
      if (!lastPage) {
        params.page = page;
      }

      const response = await axios.get(`http://localhost:8072/atozmart/catalog/items`, {
        params,
        headers,
      });

      setItems(response.data.items || []);
      setNoOfItems(response.data.noOfItems || 0);
      setTotalPages(response.data.totalPages || 1);

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
    } finally {
      setLoading(false);
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

  const addToCart = async (item) => {
    if (!isAuthenticated) {
      setError('Please log in to add items to your cart. Click the "Login" button in the header.');
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

  const handleFirstPage = () => {
    if (page === 0 && !lastPage) return;
    setPageHistory((prev) => [...prev, page]);
    setPage(0);
    setLastPage(false);
  };

  const handlePreviousPage = () => {
    if (page === 0 && !lastPage) return;
    if (pageHistory.length > 0) {
      const previousPage = pageHistory[pageHistory.length - 1];
      setPage(previousPage);
      setPageHistory((prev) => prev.slice(0, -1));
      setLastPage(false);
    }
  };

  const handleNextPage = () => {
    if (page >= totalPages - 1) return;
    setPageHistory((prev) => [...prev, page]);
    setPage(page + 1);
    setLastPage(false);
  };

  const handleLastPage = () => {
    if (page >= totalPages - 1) return;
    setPageHistory((prev) => [...prev, page]);
    setPage(totalPages - 1);
    setLastPage(true);
  };

  const handleSizeChange = (e) => {
    setSize(parseInt(e.target.value));
    setPage(0);
    setLastPage(false);
    setPageHistory([]);
  };

  const handleSortByChange = (e) => {
    setSortBy(e.target.value);
    setPage(0);
    setLastPage(false);
    setPageHistory([]);
  };

  const handleDirectionChange = (e) => {
    setDirection(e.target.value);
    setPage(0);
    setLastPage(false);
    setPageHistory([]);
  };

  const startItem = page * size + 1;
  const endItem = Math.min((page + 1) * size, noOfItems);

  return (
    <div>
      {error && (
        <div className="bg-red-500 text-white p-4 text-center">
          {error}
        </div>
      )}
      <h1 className="text-3xl font-bold mb-4">Welcome to AtoZMart</h1>
      <div className="mb-4 flex space-x-4">
        <div>
          <label className="mr-2">Items per page:</label>
          <select
            value={size}
            onChange={handleSizeChange}
            className="p-2 border rounded"
          >
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>
        <div>
          <label className="mr-2">Sort by:</label>
          <select
            value={sortBy}
            onChange={handleSortByChange}
            className="p-2 border rounded"
          >
            <option value="name">Item Name</option>
            <option value="price">Price</option>
          </select>
        </div>
        <div>
          <label className="mr-2">Sort order:</label>
          <select
            value={direction}
            onChange={handleDirectionChange}
            className="p-2 border rounded"
          >
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>
        </div>
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
      <div className="mt-4">
        <div className="text-gray-600 mb-2">
          Showing {startItem}-{endItem} of {noOfItems} items
        </div>
        <div className="flex space-x-2 items-center">
          <button
            onClick={handleFirstPage}
            disabled={loading || (page === 0 && !lastPage)}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
          >
            First
          </button>
          <button
            onClick={handlePreviousPage}
            disabled={loading || (page === 0 && !lastPage)}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
          >
            Previous
          </button>
          <span className="px-4 py-2">
            Page {lastPage ? totalPages : page + 1} of {totalPages}
          </span>
          <button
            onClick={handleNextPage}
            disabled={loading || (page >= totalPages - 1)}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
          >
            Next
          </button>
          <button
            onClick={handleLastPage}
            disabled={loading}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
          >
            Last
          </button>
        </div>
      </div>
    </div>
  );
}

export default HomePage;