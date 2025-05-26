import { useState, useEffect } from 'react';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';

function CartPage({ accessToken, isAuthenticated }) {
  const [cart, setCart] = useState({ items: [], orderAmount: 0 });
  const [coupons, setCoupons] = useState([]);
  const [selectedCoupon, setSelectedCoupon] = useState(null);
  const [showCouponModal, setShowCouponModal] = useState(false);
  const [paymentMode, setPaymentMode] = useState('UPI');
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false); // Add loading state

  useEffect(() => {
    if (accessToken && accessToken !== currentToken) {
      setCurrentToken(accessToken);
    }
  }, [accessToken]);

  useEffect(() => {
    if (isAuthenticated && currentToken) {
      fetchCartItems();
    }
  }, [isAuthenticated, currentToken]);

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const fetchCartItems = async () => {
    setLoading(true); // Set loading to true
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

      const response = await axios.get('http://localhost:8072/atozmart/cart/items', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setCart(response.data || { items: [], orderAmount: 0 });
      setError(null);
    } catch (error) {
      console.error('Error fetching cart items:', error);
      if (error.response && error.response.status === 404) {
        setCart({ items: [], orderAmount: 0 }); // Always clear cart on 404
        setError(null);
      } else if (error.response) {
        setError(`Failed to fetch cart: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false); // Reset loading state
    }
  };

  const deleteFromCart = async (itemName) => {
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

      await axios.delete('http://localhost:8072/atozmart/cart/items', {
        params: { itemName },
        headers: { Authorization: `Bearer ${token}` },
      });

      // Optimistically update the cart state by removing the item
      setCart((prevCart) => {
        const updatedItems = prevCart.items.filter((item) => item.itemName !== itemName);
        const updatedOrderAmount = updatedItems.reduce((total, item) => total + item.effectivePrice, 0);
        return { items: updatedItems, orderAmount: updatedOrderAmount };
      });

      setError(null);
      alert(`Removed ${itemName} from cart`);
      fetchCartItems(); // Refresh the cart to sync with the server
    } catch (error) {
      console.error('Error deleting from cart:', error);
      if (error.response) {
        setError(`Failed to delete from cart: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const clearCart = async () => {
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

      await axios.delete('http://localhost:8072/atozmart/cart/items', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setCart({ items: [], orderAmount: 0 }); // Immediately clear the cart
      setError(null);
      alert('Cart cleared successfully');
      fetchCartItems();
    } catch (error) {
      console.error('Error clearing cart:', error);
      if (error.response) {
        setError(`Failed to clear cart: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const updateQuantity = async (item, newQuantity) => {
    if (newQuantity < 1) return;

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

      await axios.patch(
        'http://localhost:8072/atozmart/cart/items',
        {
          itemName: item.itemName,
          unitPrice: item.unitPrice.toString(),
          quantity: newQuantity.toString(),
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );

      setError(null);
      fetchCartItems();
    } catch (error) {
      console.error('Error updating quantity:', error);
      if (error.response) {
        setError(`Failed to update quantity: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const fetchCoupons = async () => {
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

      const response = await axios.get('http://localhost:8072/atozmart/cart/coupon', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setCoupons(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Error fetching coupons:', error);
      if (error.response) {
        setError(`Failed to fetch coupons: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    }
  };

  const handleCouponSelect = (coupon) => {
    setSelectedCoupon(coupon);
    setShowCouponModal(false);
  };

  const handleRemoveCoupon = () => {
    setSelectedCoupon(null);
  };

  const handleCheckout = async () => {
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

      const orderAmount = cart.orderAmount;
      const discount = selectedCoupon ? selectedCoupon.discount : 0;
      const orderSavings = (discount / 100) * orderAmount;
      const orderTotal = orderAmount - orderSavings;

      const response = await axios.post(
        'http://localhost:8072/atozmart/cart/checkout',
        {
          orderAmount,
          couponCode: selectedCoupon ? selectedCoupon.couponCode : null,
          orderSavings,
          orderTotal,
          paymentMode,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );

      setError(null);
      alert(`Order placed successfully! Order ID: ${response.data.orderId}`);
      fetchCartItems();
    } catch (error) {
      console.error('Error during checkout:', error);
      if (error.response) {
        setError(`Failed to checkout: ${error.response.data?.errorMsg || error.message}`);
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
        <h1 className="text-3xl font-bold mb-4">Your Cart</h1>
        <p className="text-gray-600 text-center">
          Please log in to view your cart. Click the "Login" button in the header.
        </p>
      </div>
    );
  }

  const orderAmount = cart.orderAmount;
  const discount = selectedCoupon ? selectedCoupon.discount : 0;
  const orderSavings = (discount / 100) * orderAmount;
  const orderTotal = orderAmount - orderSavings;

  return (
    <div className="relative">
      {error && (
        <div className="bg-red-500 text-white p-4 text-center">
          {error}
        </div>
      )}
      <h1 className="text-3xl font-bold mb-4">Your Cart</h1>

      {loading ? (
        <p className="text-gray-600">Loading...</p>
      ) : cart.items.length === 0 ? (
        <p className="text-gray-600">Your cart is empty.</p>
      ) : (
        <div className="mb-8">
          <div className="mb-4">
            <button
              onClick={clearCart}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              Clear Cart
            </button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-gray-200">
                  <th className="p-2 text-left">Image</th>
                  <th className="p-2 text-left">Item Name</th>
                  <th className="p-2 text-left">Unit Price</th>
                  <th className="p-2 text-left">Quantity</th>
                  <th className="p-2 text-left">Effective Price</th>
                  <th className="p-2 text-left">Action</th>
                </tr>
              </thead>
              <tbody>
                {cart.items.map((item) => (
                  <tr key={item.itemName} className="border-b">
                    <td className="p-2">
                      <img
                        src="https://via.placeholder.com/50"
                        alt={item.itemName}
                        className="w-12 h-12 object-cover"
                      />
                    </td>
                    <td className="p-2">{item.itemName}</td>
                    <td className="p-2">₹{item.unitPrice.toFixed(2)}</td>
                    <td className="p-2">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => updateQuantity(item, item.quantity - 1)}
                          className="bg-gray-300 text-black px-2 py-1 rounded hover:bg-gray-400"
                          disabled={item.quantity <= 1}
                        >
                          -
                        </button>
                        <span>{item.quantity}</span>
                        <button
                          onClick={() => updateQuantity(item, item.quantity + 1)}
                          className="bg-gray-300 text-black px-2 py-1 rounded hover:bg-gray-400"
                        >
                          +
                        </button>
                      </div>
                    </td>
                    <td className="p-2">₹{item.effectivePrice.toFixed(2)}</td>
                    <td className="p-2">
                      <button
                        onClick={() => deleteFromCart(item.itemName)}
                        className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <div className="border p-4 rounded shadow mb-4">
        <h2 className="text-2xl font-semibold mb-4">Billing Details</h2>
        <div className="flex justify-between mb-2">
          <span>Order Amount:</span>
          <span>₹{orderAmount.toFixed(2)}</span>
        </div>
        <div className="flex justify-between mb-2">
          <span>Coupon:</span>
          <div className="flex items-center space-x-2">
            <span className="mr-2">
              {selectedCoupon ? selectedCoupon.couponCode : 'None'}
            </span>
            <button
              onClick={() => {
                setShowCouponModal(true);
                fetchCoupons();
              }}
              className="text-blue-500 hover:underline"
            >
              {selectedCoupon ? 'Change Coupon' : 'Select Coupon'}
            </button>
            {selectedCoupon && (
              <button
                onClick={handleRemoveCoupon}
                className="text-red-500 hover:underline"
              >
                Remove Coupon
              </button>
            )}
          </div>
        </div>
        <div className="flex justify-between mb-2">
          <span>Order Savings:</span>
          <span>₹{orderSavings.toFixed(2)}</span>
        </div>
        <div className="flex justify-between mb-2">
          <span>Order Total:</span>
          <span>₹{orderTotal.toFixed(2)}</span>
        </div>
        <div className="flex justify-between mb-4">
          <span>Payment Mode:</span>
          <select
            value={paymentMode}
            onChange={(e) => setPaymentMode(e.target.value)}
            className="p-2 border rounded"
          >
            <option value="UPI">UPI</option>
            <option value="COD">COD</option>
          </select>
        </div>
        <button
          onClick={handleCheckout}
          disabled={cart.items.length === 0}
          className="w-full bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 disabled:bg-gray-300"
        >
          Checkout
        </button>
      </div>

      {showCouponModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow-lg w-96">
            <h2 className="text-xl font-semibold mb-4">Select a Coupon</h2>
            {coupons.length === 0 ? (
              <p className="text-gray-600">No coupons available.</p>
            ) : (
              <ul className="space-y-2">
                {coupons.map((coupon) => (
                  <li key={coupon.couponCode}>
                    <button
                      onClick={() => handleCouponSelect(coupon)}
                      className="w-full text-left p-2 border rounded hover:bg-gray-100"
                    >
                      {coupon.couponCode} - {coupon.discount}% Off
                    </button>
                  </li>
                ))}
              </ul>
            )}
            <button
              onClick={() => setShowCouponModal(false)}
              className="mt-4 w-full bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default CartPage;