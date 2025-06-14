import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';

function ProfilePage({ accessToken, isAuthenticated, onLogout }) {
  const [profile, setProfile] = useState(null);
  const [orders, setOrders] = useState([]);
  const [ordersNotFound, setOrdersNotFound] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showAddresses, setShowAddresses] = useState(false);
  const [activeTab, setActiveTab] = useState('home');
  const [editingAddress, setEditingAddress] = useState(null);
  const [editedAddress, setEditedAddress] = useState(null);
  const [editingBasicDetails, setEditingBasicDetails] = useState(false);
  const [editedBasicDetails, setEditedBasicDetails] = useState(null);
  const [addingAddress, setAddingAddress] = useState(false);
  const [newAddress, setNewAddress] = useState({
    addressType: null,
    addressDesc: null,
    defaultAddress: false,
    addLine1: null,
    addLine2: null,
    addLine3: null,
    pincode: null,
    country: null,
  });
  const navigate = useNavigate();

  useEffect(() => {
    if (accessToken && accessToken !== currentToken) {
      setCurrentToken(accessToken);
    }
  }, [accessToken]);

  useEffect(() => {
    if (isAuthenticated && currentToken) {
      fetchProfile();
    }
  }, [isAuthenticated, currentToken]);

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const fetchProfile = async () => {
    setLoading(true);
    try {
      let token = currentToken;
      if (!token) {
        token = localStorage.getItem('accessToken');
        if (!token) {
          setError('No access token available. Please log in.');
          onLogout();
          return;
        }
        setCurrentToken(token);
      }

      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      let profileResponse;
      try {
        profileResponse = await axios.get('http://localhost:8072/atozmart/profile/profile', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setProfile(profileResponse.data);
      } catch (profileError) {
        if (profileError.response && profileError.response.status === 404 && profileError.response.data?.errorMsg === 'no profile found') {
          setProfile(null);
        } else {
          throw profileError;
        }
      }

      try {
        const ordersResponse = await axios.get('http://localhost:8072/atozmart/order/', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setOrders(ordersResponse.data);
        setOrdersNotFound(false);
      } catch (ordersError) {
        if (ordersError.response && ordersError.response.status === 404) {
          setOrders([]);
          setOrdersNotFound(true);
        } else {
          throw ordersError;
        }
      }

      setError(null);
    } catch (error) {
      console.error('Error fetching data:', error);
      if (error.response) {
        setError(`Failed to fetch data: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchOrderDetails = async (orderId) => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      const response = await axios.get(`http://localhost:8072/atozmart/order/?orderId=${orderId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setSelectedOrder(response.data[0]);
      setError(null);
    } catch (error) {
      console.error('Error fetching order details:', error);
      if (error.response) {
        setError(`Failed to fetch order details: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const deleteAllAddresses = async () => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      await axios.delete('http://localhost:8072/atozmart/profile/profile/address', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setError(null);
      alert('All addresses deleted successfully');
      fetchProfile();
      setActiveTab('add-address');
      setAddingAddress(true);
    } catch (error) {
      console.error('Error deleting all addresses:', error);
      if (error.response) {
        setError(`Failed to delete all addresses: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const markAsDefault = async (addressType) => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      await axios.patch(
        `http://localhost:8072/atozmart/profile/profile/address?addressType=${addressType}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setError(null);
      alert(`Marked ${addressType} as default address`);
      fetchProfile();
    } catch (error) {
      console.error('Error marking as default:', error);
      if (error.response) {
        setError(`Failed to mark as default: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const removeAddress = async (addressType) => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      await axios.delete(
        `http://localhost:8072/atozmart/profile/profile/address?addressType=${addressType}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setError(null);
      alert(`Removed ${addressType} address`);
      fetchProfile();
      if (profile.addressDetails.length === 1) {
        setActiveTab('add-address');
        setAddingAddress(true);
      }
    } catch (error) {
      console.error('Error removing address:', error);
      if (error.response) {
        setError(`Failed to remove address: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const startEditingAddress = (type, index, address) => {
    setEditingAddress({ type, index });
    setEditedAddress({
      ...address,
      addressType: address.addressType || null,
      addressDesc: address.addressDesc || null,
      defaultAddress: address.defaultAddress || false,
      addLine1: address.addLine1 || null,
      addLine2: address.addLine2 || null,
      addLine3: address.addLine3 || null,
      pincode: address.pincode || null,
      country: address.country || null,
    });
  };

  const cancelEditingAddress = () => {
    setEditingAddress(null);
    setEditedAddress(null);
  };

  const handleAddressChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditedAddress((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (value === '' ? null : value),
    }));
  };

  const saveAddressEdit = async () => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      let addressesToUpdate = [editedAddress];
      const originalAddress = profile.addressDetails.find(
        (addr) => addr.addressType === editedAddress.addressType
      );
      const wasDefault = originalAddress.defaultAddress;
      const isNowDefault = editedAddress.defaultAddress;

      if (!wasDefault && isNowDefault) {
        const existingDefault = profile.addressDetails.find(
          (addr) => addr.defaultAddress && addr.addressType !== editedAddress.addressType
        );
        if (existingDefault) {
          addressesToUpdate.push({
            ...existingDefault,
            defaultAddress: false,
          });
        }
      }

      const { username, ...basicDetailsWithoutUsername } = profile.basicDetails;
      const payload = {
        basicDetails: basicDetailsWithoutUsername,
        addressDetails: addressesToUpdate,
      };

      await axios.patch(
        'http://localhost:8072/atozmart/profile/profile',
        payload,
        {
          headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        }
      );

      setError(null);
      alert('Address updated successfully');
      setEditingAddress(null);
      setEditedAddress(null);
      fetchProfile();
    } catch (error) {
      console.error('Error updating address:', error);
      if (error.response) {
        setError(`Failed to update address: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const startEditingBasicDetails = () => {
    setEditingBasicDetails(true);
    setEditedBasicDetails({
      firstName: profile.basicDetails.firstName || null,
      lastName: profile.basicDetails.lastName || null,
      mail: profile.basicDetails.mail || null,
      mobileNo: profile.basicDetails.mobileNo || null,
      username: profile.basicDetails.username || null,
    });
  };

  const cancelEditingBasicDetails = () => {
    setEditingBasicDetails(false);
    setEditedBasicDetails(null);
  };

  const handleBasicDetailsChange = (e) => {
    const { name, value } = e.target;
    setEditedBasicDetails((prev) => ({
      ...prev,
      [name]: value === '' ? null : value,
    }));
  };

  const saveBasicDetailsEdit = async () => {
    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      const { username, ...basicDetailsWithoutUsername } = editedBasicDetails;
      const payload = {
        basicDetails: basicDetailsWithoutUsername,
        addressDetails: null,
      };

      await axios.patch(
        'http://localhost:8072/atozmart/profile/profile',
        payload,
        {
          headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        }
      );

      setError(null);
      alert('Basic details updated successfully');
      setEditingBasicDetails(false);
      setEditedBasicDetails(null);
      fetchProfile();
    } catch (error) {
      console.error('Error updating basic details:', error);
      if (error.response) {
        setError(`Failed to update basic details: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleNewAddressChange = (e) => {
    const { name, value, type, checked } = e.target;
    setNewAddress((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (value === '' ? null : value),
    }));
  };

  const cancelAddAddress = () => {
    setAddingAddress(false);
    setNewAddress({
      addressType: null,
      addressDesc: null,
      defaultAddress: false,
      addLine1: null,
      addLine2: null,
      addLine3: null,
      pincode: null,
      country: null,
    });
    if (profile.addressDetails.length > 0) {
      const firstAvailableTab = Object.keys(categorizedAddresses).find(key => categorizedAddresses[key].length > 0);
      setActiveTab(firstAvailableTab || 'home');
    } else {
      setActiveTab('add-address');
    }
  };

  const saveNewAddress = async () => {
    if (!newAddress.addressType) {
      alert('Please select an address type.');
      return;
    }

    setLoading(true);
    try {
      let token = currentToken;
      const authType = localStorage.getItem('authType');
      if (authType === 'keycloak' && isTokenExpired()) {
        try {
          token = await refreshAccessToken();
          setCurrentToken(token);
        } catch (refreshError) {
          setError('Failed to refresh token. Please log in again.');
          onLogout();
          return;
        }
      } else if (isTokenExpired()) {
        setError('Session expired. Please log in again.');
        onLogout();
        return;
      }

      let addressesToAdd = [newAddress];
      if (newAddress.defaultAddress) {
        const existingDefault = profile.addressDetails.find((addr) => addr.defaultAddress);
        if (existingDefault) {
          addressesToAdd.push({
            ...existingDefault,
            defaultAddress: false,
          });
        }
      }

      const { username, ...basicDetailsWithoutUsername } = profile.basicDetails;
      const payload = {
        basicDetails: basicDetailsWithoutUsername,
        addressDetails: addressesToAdd,
      };

      await axios.patch(
        'http://localhost:8072/atozmart/profile/profile',
        payload,
        {
          headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        }
      );

      setError(null);
      alert('Address added successfully');
      setAddingAddress(false);
      setNewAddress({
        addressType: null,
        addressDesc: null,
        defaultAddress: false,
        addLine1: null,
        addLine2: null,
        addLine3: null,
        pincode: null,
        country: null,
      });
      fetchProfile();
    } catch (error) {
      console.error('Error adding address:', error);
      if (error.response) {
        setError(`Failed to add address: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div>
        <h1 className="text-3xl font-bold mb-4">Your Profile</h1>
        <p className="text-gray-600 text-center">
          Please log in to view your profile. Click the "Login" button in the header.
        </p>
      </div>
    );
  }

  if (loading) {
    return <p className="text-center text-gray-600">Loading...</p>;
  }

  const { basicDetails, addressDetails } = profile || { basicDetails: {}, addressDetails: [] };

  let initials = '';
  if (basicDetails.firstName || basicDetails.lastName) {
    const firstInitial = basicDetails.firstName ? basicDetails.firstName[0] : '';
    const lastInitial = basicDetails.lastName ? basicDetails.lastName[basicDetails.lastName.length - 1] : '';
    initials = `${firstInitial}${lastInitial}`.toUpperCase();
  } else {
    initials = basicDetails.username ? basicDetails.username[0].toUpperCase() : 'U';
  }

  const categorizedAddresses = addressDetails.reduce((acc, addr) => {
    acc[addr.addressType] = acc[addr.addressType] || [];
    acc[addr.addressType].push(addr);
    return acc;
  }, { home: [], work: [], others: [] });

  const tabs = [
    { id: 'home', label: 'Home', addresses: categorizedAddresses.home },
    { id: 'work', label: 'Work', addresses: categorizedAddresses.work },
    { id: 'others', label: 'Others', addresses: categorizedAddresses.others },
  ].filter(tab => tab.addresses.length > 0);

  tabs.push({ id: 'add-address', label: 'Add Address', addresses: [] });

  return (
    <div className="relative">
      {error && <div className="bg-red-500 text-white p-4 rounded text-center mb-4">{error}</div>}
      <h1 className="text-3xl font-bold mb-6 text-center">Your Profile</h1>

      <div className="mb-8 p-6 border rounded-lg shadow">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">Basic Details</h2>
          {profile ? (
            !editingBasicDetails && (
              <button
                onClick={startEditingBasicDetails}
                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
              >
                Edit Basic Details
              </button>
            )
          ) : (
            <button
              onClick={() => navigate('/create-profile')}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Create New Profile
            </button>
          )}
        </div>
        {profile ? (
          editingBasicDetails ? (
            <div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block mb-1">First Name</label>
                  <input
                    type="text"
                    name="firstName"
                    value={editedBasicDetails.firstName || ''}
                    onChange={handleBasicDetailsChange}
                    className="w-full p-2 border rounded"
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Last Name</label>
                  <input
                    type="text"
                    name="lastName"
                    value={editedBasicDetails.lastName || ''}
                    onChange={handleBasicDetailsChange}
                    className="w-full p-2 border rounded"
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Email</label>
                  <input
                    type="email"
                    name="mail"
                    value={editedBasicDetails.mail || ''}
                    onChange={handleBasicDetailsChange}
                    className="w-full p-2 border rounded"
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Mobile Number</label>
                  <input
                    type="text"
                    name="mobileNo"
                    value={editedBasicDetails.mobileNo || ''}
                    onChange={handleBasicDetailsChange}
                    className="w-full p-2 border rounded"
                    required
                  />
                </div>
              </div>
              <div className="mt-4 space-x-2">
                <button
                  onClick={saveBasicDetailsEdit}
                  className="px-3 py-1 rounded bg-green-500 text-white hover:bg-green-600"
                >
                  Ok
                </button>
                <button
                  onClick={cancelEditingBasicDetails}
                  className="px-3 py-1 rounded bg-gray-500 text-white hover:bg-gray-600"
                >
                  Cancel
                </button>
              </div>
            </div>
          ) : (
            <div className="flex items-center space-x-4">
              <div className="relative">
                <div className="w-16 h-16 bg-gray-300 rounded flex items-center justify-center text-2xl font-bold">
                  {initials}
                </div>
                <button
                  onClick={() => alert('Profile picture editing will be implemented later.')}
                  className="absolute bottom-0 right-0 bg-gray-500 text-white text-xs px-2 py-1 rounded hover:bg-gray-600"
                >
                  Edit Picture
                </button>
              </div>
              <div>
                <p><strong>First Name:</strong> {basicDetails.firstName || 'Not provided'}</p>
                <p><strong>Last Name:</strong> {basicDetails.lastName || 'Not provided'}</p>
                <p><strong>Email:</strong> {basicDetails.mail || 'Not provided'}</p>
                <p><strong>Mobile:</strong> {basicDetails.mobileNo || 'Not provided'}</p>
              </div>
            </div>
          )
        ) : (
          <p className="text-gray-600 text-center mb-4">No profile found.</p>
        )}
      </div>

      <div className="mb-8 p-6 border rounded-lg shadow">
        <h2 className="text-2xl font-semibold mb-4">Order Details</h2>
        {ordersNotFound ? (
          <p className="text-gray-600 text-center">No orders yet.</p>
        ) : orders.length === 0 ? (
          <p className="text-gray-600 text-center">No orders found.</p>
        ) : (
          <div>
            <table className="w-full border-collapse mb-4">
              <thead>
                <tr className="bg-gray-200">
                  <th className="border p-2 text-left">Order ID</th>
                  <th className="border p-2 text-left">Payment Status</th>
                  <th className="border p-2 text-left">Delivery Status</th>
                  <th className="border p-2 text-left">Order Total</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.orderId}>
                    <td className="border p-2">
                      <button
                        onClick={() => fetchOrderDetails(order.orderId)}
                        className="text-blue-500 hover:underline"
                      >
                        {order.orderId}
                      </button>
                    </td>
                    <td className="border p-2">{order.paymentStatus}</td>
                    <td className="border p-2">{order.deliveryStatus}</td>
                    <td className="border p-2">₹{order.orderTotal.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {selectedOrder && (
              <div className="border p-4 rounded-lg">
                <h3 className="text-xl font-semibold mb-2">Order Details for Order ID: {selectedOrder.orderId}</h3>
                {selectedOrder.orderItems && selectedOrder.orderItems.length > 0 ? (
                  <table className="w-full border-collapse">
                    <thead>
                      <tr className="bg-gray-100">
                        <th className="border p-2 text-left">Item</th>
                        <th className="border p-2 text-left">Unit Price</th>
                        <th className="border p-2 text-left">Quantity</th>
                        <th className="border p-2 text-left">Effective Price</th>
                      </tr>
                    </thead>
                    <tbody>
                      {selectedOrder.orderItems.map((item, index) => (
                        <tr key={index}>
                          <td className="border p-2">{item.item}</td>
                          <td className="border p-2">₹{item.unitPrice.toFixed(2)}</td>
                          <td className="border p-2">{item.quantity}</td>
                          <td className="border p-2">₹{item.effectivePrice.toFixed(2)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <p className="text-gray-600">No order items available for this order.</p>
                )}
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="mt-2 px-3 py-1 rounded bg-gray-500 text-white hover:bg-gray-600"
                >
                  Close
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      {profile && (
        <div className="p-6 border rounded-lg shadow">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-2xl font-semibold">Address Details</h2>
            <button
              onClick={() => setShowAddresses(!showAddresses)}
              className="text-blue-500 hover:underline"
            >
              {showAddresses ? 'Collapse' : 'Expand'}
            </button>
          </div>

          {showAddresses && (
            <div>
              {addressDetails.length > 0 && (
                <div className="mb-4 flex justify-end">
                  <button
                    onClick={deleteAllAddresses}
                    className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                  >
                    Delete All Addresses
                  </button>
                </div>
              )}
              <div>
                <div className="flex space-x-2 mb-4">
                  {tabs.map((tab) => (
                    <button
                      key={tab.id}
                      onClick={() => {
                        setActiveTab(tab.id);
                        setAddingAddress(tab.id === 'add-address');
                      }}
                      className={`px-4 py-2 rounded ${activeTab === tab.id ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
                    >
                      {tab.label}
                    </button>
                  ))}
                </div>
                <div>
                  {activeTab === 'add-address' ? (
                    <div className="border p-4 rounded-lg">
                      <h3 className="text-xl font-semibold mb-4">Add New Address</h3>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block mb-1">Address Type</label>
                          <select
                            name="addressType"
                            value={newAddress.addressType || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                            required
                          >
                            <option value="">Select Type</option>
                            <option value="home" disabled={categorizedAddresses.home.length > 0}>Home</option>
                            <option value="work" disabled={categorizedAddresses.work.length > 0}>Work</option>
                            <option value="others">Others</option>
                          </select>
                        </div>
                        <div>
                          <label className="block mb-1">Description</label>
                          <input
                            type="text"
                            name="addressDesc"
                            value={newAddress.addressDesc || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                          />
                        </div>
                        <div>
                          <label className="block mb-1">Address Line 1</label>
                          <input
                            type="text"
                            name="addLine1"
                            value={newAddress.addLine1 || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                            required
                          />
                        </div>
                        <div>
                          <label className="block mb-1">Address Line 2</label>
                          <input
                            type="text"
                            name="addLine2"
                            value={newAddress.addLine2 || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                          />
                        </div>
                        <div>
                          <label className="block mb-1">Address Line 3</label>
                          <input
                            type="text"
                            name="addLine3"
                            value={newAddress.addLine3 || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                          />
                        </div>
                        <div>
                          <label className="block mb-1">Pincode</label>
                          <input
                            type="text"
                            name="pincode"
                            value={newAddress.pincode || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                            required
                          />
                        </div>
                        <div>
                          <label className="block mb-1">Country</label>
                          <input
                            type="text"
                            name="country"
                            value={newAddress.country || ''}
                            onChange={handleNewAddressChange}
                            className="w-full p-2 border rounded"
                            required
                          />
                        </div>
                        <div className="flex items-center">
                          <label className="mr-2">Default Address</label>
                          <input
                            type="checkbox"
                            name="defaultAddress"
                            checked={newAddress.defaultAddress}
                            onChange={handleNewAddressChange}
                          />
                        </div>
                      </div>
                      <div className="mt-4 space-x-2">
                        <button
                          onClick={saveNewAddress}
                          className="px-3 py-1 rounded bg-green-500 text-white hover:bg-green-600"
                        >
                          Add
                        </button>
                        <button
                          onClick={cancelAddAddress}
                          className="px-3 py-1 rounded bg-gray-500 text-white hover:bg-gray-600"
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  ) : (
                    tabs.find((tab) => tab.id === activeTab)?.addresses.map((address, index) => (
                      <div key={index} className="border p-4 rounded-lg mb-4">
                        {editingAddress?.type === activeTab && editingAddress?.index === index ? (
                          <div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                              <div>
                                <label className="block mb-1">Address Line 1</label>
                                <input
                                  type="text"
                                  name="addLine1"
                                  value={editedAddress.addLine1 || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div>
                                <label className="block mb-1">Address Line 2</label>
                                <input
                                  type="text"
                                  name="addLine2"
                                  value={editedAddress.addLine2 || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div>
                                <label className="block mb-1">Address Line 3</label>
                                <input
                                  type="text"
                                  name="addLine3"
                                  value={editedAddress.addLine3 || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div>
                                <label className="block mb-1">Pincode</label>
                                <input
                                  type="text"
                                  name="pincode"
                                  value={editedAddress.pincode || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div>
                                <label className="block mb-1">Country</label>
                                <input
                                  type="text"
                                  name="country"
                                  value={editedAddress.country || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div>
                                <label className="block mb-1">Description</label>
                                <input
                                  type="text"
                                  name="addressDesc"
                                  value={editedAddress.addressDesc || ''}
                                  onChange={handleAddressChange}
                                  className="w-full p-2 border rounded"
                                />
                              </div>
                              <div className="flex items-center">
                                <label className="mr-2">Default Address</label>
                                <input
                                  type="checkbox"
                                  name="defaultAddress"
                                  checked={editedAddress.defaultAddress || false}
                                  onChange={handleAddressChange}
                                />
                              </div>
                            </div>
                            <div className="mt-4 space-x-2">
                              <button
                                onClick={saveAddressEdit}
                                className="px-3 py-1 rounded bg-green-500 text-white hover:bg-green-600"
                              >
                                Ok
                              </button>
                              <button
                                onClick={cancelEditingAddress}
                                className="px-3 py-1 rounded bg-gray-500 text-white hover:bg-gray-600"
                              >
                                Cancel
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div>
                            <p><strong>Address Line 1:</strong> {address.addLine1}</p>
                            <p><strong>Address Line 2:</strong> {address.addLine2}</p>
                            <p><strong>Address Line 3:</strong> {address.addLine3}</p>
                            <p><strong>Pincode:</strong> {address.pincode}</p>
                            <p><strong>Country:</strong> {address.country}</p>
                            {address.addressDesc && <p><strong>Description:</strong> {address.addressDesc}</p>}
                            <p><strong>Default:</strong> {address.defaultAddress ? 'Yes' : 'No'}</p>
                            <div className="mt-2 space-x-2">
                              <button
                                onClick={() => markAsDefault(address.addressType)}
                                disabled={address.defaultAddress}
                                className={`px-3 py-1 rounded ${address.defaultAddress ? 'bg-gray-300' : 'bg-green-500 text-white hover:bg-green-600'}`}
                              >
                                Mark as Default
                              </button>
                              <button
                                onClick={() => startEditingAddress(activeTab, index, address)}
                                className="px-3 py-1 rounded bg-yellow-500 text-white hover:bg-yellow-600"
                              >
                                Edit
                              </button>
                              <button
                                onClick={() => removeAddress(address.addressType)}
                                className="px-3 py-1 rounded bg-red-500 text-white hover:bg-red-600"
                              >
                                Remove
                              </button>
                            </div>
                          </div>
                        )}
                      </div>
                    ))
                  )}
                  {tabs.find((tab) => tab.id === activeTab)?.addresses.length === 0 && activeTab !== 'add-address' && (
                    <p className="text-gray-600">No {activeTab} addresses found.</p>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default ProfilePage;