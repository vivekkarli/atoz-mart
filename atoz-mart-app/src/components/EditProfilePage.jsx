import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';

function EditProfilePage({ accessToken, isAuthenticated }) {
  const [profile, setProfile] = useState(null);
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [basicDetails, setBasicDetails] = useState({
    firstName: '',
    lastName: '',
    mail: '',
    mobileNo: '',
  });
  const [addressDetails, setAddressDetails] = useState([]);
  const [newAddress, setNewAddress] = useState({
    addressType: '',
    addressDesc: '',
    defaultAddress: false,
    addLine1: '',
    addLine2: '',
    addLine3: '',
    pincode: '',
    country: '',
  });
  const [showAddAddress, setShowAddAddress] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
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

      const response = await axios.get('http://localhost:8072/atozmart/profile/profile', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setProfile(response.data);
      setBasicDetails(response.data.basicDetails);
      setAddressDetails(response.data.addressDetails || []);
      setIsCreating(false);
      setError(null);
    } catch (error) {
      console.error('Error fetching profile:', error);
      if (error.response && error.response.status === 404 && error.response.data?.errorMsg === 'no profile found') {
        setProfile(null);
        setBasicDetails({ firstName: '', lastName: '', mail: '', mobileNo: '' });
        setAddressDetails([]);
        setIsCreating(true);
        setError(null);
      } else if (error.response) {
        setError(`Failed to fetch profile: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleBasicChange = (e) => {
    const { name, value } = e.target;
    setBasicDetails((prev) => ({ ...prev, [name]: value }));
  };

  const handleNewAddressChange = (e) => {
    const { name, value } = e.target;
    setNewAddress((prev) => ({ ...prev, [name]: value }));
  };

  const addAddress = () => {
    if (!newAddress.addressType) {
      alert('Please select an address type.');
      return;
    }
    const updatedAddresses = [...addressDetails, { ...newAddress, defaultAddress: addressDetails.length === 0 }];
    setAddressDetails(updatedAddresses);
    setNewAddress({
      addressType: '',
      addressDesc: '',
      defaultAddress: false,
      addLine1: '',
      addLine2: '',
      addLine3: '',
      pincode: '',
      country: '',
    });
    setShowAddAddress(false);
  };

  const markAsDefault = async (addressType) => {
    if (isCreating) {
      setAddressDetails((prev) =>
        prev.map((addr) => ({
          ...addr,
          defaultAddress: addr.addressType === addressType,
        }))
      );
    } else {
      setLoading(true);
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
          `http://localhost:8072/atozmart/profile/profile/address?addressType=${addressType}`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        setError(null);
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
    }
  };

  const removeAddress = async (addressType) => {
    if (isCreating) {
      setAddressDetails((prev) => prev.filter((addr) => addr.addressType !== addressType));
    } else {
      setLoading(true);
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

        await axios.delete(
          `http://localhost:8072/atozmart/profile/profile/address?addressType=${addressType}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        setError(null);
        fetchProfile();
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
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
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

      // Exclude username from basicDetails
      const { username, ...basicDetailsWithoutUsername } = basicDetails;

      const payload = {
        basicDetails: basicDetailsWithoutUsername,
        addressDetails,
      };

      if (isCreating) {
        await axios.post(
          'http://localhost:8072/atozmart/profile/profile',
          payload,
          {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
          }
        );
        alert('Profile created successfully');
      } else {
        await axios.patch(
          'http://localhost:8072/atozmart/profile/profile',
          payload,
          {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
          }
        );
        alert('Profile updated successfully');
      }

      setError(null);
      navigate('/profile');
    } catch (error) {
      console.error('Error submitting profile:', error);
      if (error.response) {
        setError(`Failed to submit profile: ${error.response.data?.errorMsg || error.message}`);
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
        <h1 className="text-3xl font-bold mb-4">Edit Profile</h1>
        <p className="text-gray-600 text-center">
          Please log in to edit your profile. Click the "Login" button in the header.
        </p>
      </div>
    );
  }

  if (loading) {
    return <p className="text-center text-gray-600">Loading...</p>;
  }

  const hasHome = addressDetails.some((addr) => addr.addressType === 'home');
  const hasWork = addressDetails.some((addr) => addr.addressType === 'work');

  return (
    <div className="relative">
      {error && <div className="bg-red-500 text-white p-4 rounded text-center mb-4">{error}</div>}
      <h1 className="text-3xl font-bold mb-6 text-center">{isCreating ? 'Create Profile' : 'Edit Profile'}</h1>

      <div className="p-6 border rounded-lg shadow">
        <h2 className="text-2xl font-semibold mb-4">Basic Details</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block mb-1">First Name</label>
            <input
              type="text"
              name="firstName"
              value={basicDetails.firstName}
              onChange={handleBasicChange}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div>
            <label className="block mb-1">Last Name</label>
            <input
              type="text"
              name="lastName"
              value={basicDetails.lastName}
              onChange={handleBasicChange}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div>
            <label className="block mb-1">Email</label>
            <input
              type="email"
              name="mail"
              value={basicDetails.mail}
              onChange={handleBasicChange}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div>
            <label className="block mb-1">Mobile Number</label>
            <input
              type="text"
              name="mobileNo"
              value={basicDetails.mobileNo}
              onChange={handleBasicChange}
              className="w-full p-2 border rounded"
              required
            />
          </div>
        </div>

        <h2 className="text-2xl font-semibold mt-8 mb-4">Address Details</h2>
        {addressDetails.map((address, index) => (
          <div key={index} className="border p-4 rounded-lg mb-4">
            <p><strong>Type:</strong> {address.addressType}</p>
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
                onClick={() => removeAddress(address.addressType)}
                className="px-3 py-1 rounded bg-red-500 text-white hover:bg-red-600"
              >
                Remove
              </button>
            </div>
          </div>
        ))}

        {!showAddAddress && (
          <button
            onClick={() => setShowAddAddress(true)}
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Add Address
          </button>
        )}

        {showAddAddress && (
          <div className="border p-4 rounded-lg mt-4">
            <h3 className="text-xl font-semibold mb-4">Add New Address</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block mb-1">Address Type</label>
                <select
                  name="addressType"
                  value={newAddress.addressType}
                  onChange={handleNewAddressChange}
                  className="w-full p-2 border rounded"
                  required
                >
                  <option value="">Select Type</option>
                  <option value="home" disabled={hasHome}>Home</option>
                  <option value="work" disabled={hasWork}>Work</option>
                  <option value="others">Others</option>
                </select>
              </div>
              <div>
                <label className="block mb-1">Description</label>
                <input
                  type="text"
                  name="addressDesc"
                  value={newAddress.addressDesc}
                  onChange={handleNewAddressChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block mb-1">Address Line 1</label>
                <input
                  type="text"
                  name="addLine1"
                  value={newAddress.addLine1}
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
                  value={newAddress.addLine2}
                  onChange={handleNewAddressChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block mb-1">Address Line 3</label>
                <input
                  type="text"
                  name="addLine3"
                  value={newAddress.addLine3}
                  onChange={handleNewAddressChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block mb-1">Pincode</label>
                <input
                  type="text"
                  name="pincode"
                  value={newAddress.pincode}
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
                  value={newAddress.country}
                  onChange={handleNewAddressChange}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>
            </div>
            <div className="mt-4 space-x-2">
              <button
                onClick={addAddress}
                className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
              >
                Add
              </button>
              <button
                onClick={() => setShowAddAddress(false)}
                className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
              >
                Cancel
              </button>
            </div>
          </div>
        )}

        <div className="mt-8 flex justify-end space-x-2">
          <button
            onClick={() => navigate('/profile')}
            className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );
}

export default EditProfilePage;