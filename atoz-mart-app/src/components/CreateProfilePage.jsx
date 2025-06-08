import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { refreshAccessToken } from '../utils/authUtils';

function CreateProfilePage({ accessToken, isAuthenticated }) {
  const [currentToken, setCurrentToken] = useState(accessToken || localStorage.getItem('accessToken'));
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showAddresses, setShowAddresses] = useState(false);
  const [newProfile, setNewProfile] = useState({
    basicDetails: {
      firstName: null,
      lastName: null,
      mail: null,
      mobileNo: null,
    },
    addressDetails: [],
  });
  const [editingAddressIndex, setEditingAddressIndex] = useState(null); // Index of address being edited, null if adding new
  const [editedAddress, setEditedAddress] = useState({
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

  const isTokenExpired = () => {
    const expiration = localStorage.getItem('tokenExpiration');
    if (!expiration) return true;
    return Date.now() >= parseInt(expiration);
  };

  const handleBasicDetailsChange = (e) => {
    const { name, value } = e.target;
    setNewProfile((prev) => ({
      ...prev,
      basicDetails: {
        ...prev.basicDetails,
        [name]: value === '' ? null : value,
      },
    }));
  };

  const handleAddressChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditedAddress((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (value === '' ? null : value),
    }));
  };

  const startEditingAddress = (index = null) => {
    if (index !== null) {
      setEditingAddressIndex(index);
      setEditedAddress({ ...newProfile.addressDetails[index] });
    } else {
      setEditingAddressIndex(null);
      setEditedAddress({
        addressType: null,
        addressDesc: null,
        defaultAddress: false,
        addLine1: null,
        addLine2: null,
        addLine3: null,
        pincode: null,
        country: null,
      });
    }
    setShowAddresses(true);
  };

  const saveAddress = () => {
    if (!editedAddress.addressType) {
      alert('Please select an address type.');
      return;
    }

    // Check for duplicate address type
    const existingAddressIndex = newProfile.addressDetails.findIndex(
      (addr, idx) => addr.addressType === editedAddress.addressType && idx !== editingAddressIndex
    );
    if (existingAddressIndex !== -1) {
      alert(`An address of type "${editedAddress.addressType}" already exists.`);
      return;
    }

    let updatedAddresses = [...newProfile.addressDetails];
    if (editedAddress.defaultAddress) {
      updatedAddresses = updatedAddresses.map((addr) => ({
        ...addr,
        defaultAddress: false,
      }));
    }

    if (editingAddressIndex !== null) {
      updatedAddresses[editingAddressIndex] = editedAddress;
    } else {
      updatedAddresses.push(editedAddress);
    }

    setNewProfile((prev) => ({
      ...prev,
      addressDetails: updatedAddresses,
    }));
    setEditedAddress({
      addressType: null,
      addressDesc: null,
      defaultAddress: false,
      addLine1: null,
      addLine2: null,
      addLine3: null,
      pincode: null,
      country: null,
    });
    setEditingAddressIndex(null);
  };

  const removeAddress = (index) => {
    setNewProfile((prev) => ({
      ...prev,
      addressDetails: prev.addressDetails.filter((_, i) => i !== index),
    }));
    setEditingAddressIndex(null);
  };

  const removeAllAddresses = () => {
    setNewProfile((prev) => ({
      ...prev,
      addressDetails: [],
    }));
    setEditingAddressIndex(null);
    setShowAddresses(false);
  };

  const createProfile = async () => {
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

      const payload = {
        basicDetails: newProfile.basicDetails,
        addressDetails: newProfile.addressDetails.length > 0 ? newProfile.addressDetails : null,
      };

      await axios.post(
        'http://localhost:8072/atozmart/profile/profile',
        payload,
        {
          headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        }
      );

      setError(null);
      alert('Profile created successfully');
      navigate('/profile');
    } catch (error) {
      console.error('Error creating profile:', error);
      if (error.response) {
        setError(`Failed to create profile: ${error.response.data?.errorMsg || error.message}`);
      } else if (error.request) {
        setError('Failed to reach the server. Possible CORS issue or server is down.');
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const cancelCreateProfile = () => {
    navigate('/profile');
  };

  if (!isAuthenticated) {
    return (
      <div>
        <h1 className="text-3xl font-bold mb-4">Create New Profile</h1>
        <p className="text-gray-600 text-center">
          Please log in to create a profile. Click the "Login" button in the header.
        </p>
      </div>
    );
  }

  if (loading) {
    return <p className="text-center text-gray-600">Loading...</p>;
  }

  return (
    <div className="relative">
      {error && <div className="bg-red-500 text-white p-4 rounded text-center mb-4">{error}</div>}
      <h1 className="text-3xl font-bold mb-6 text-center">Create New Profile</h1>

      {/* Section 1: Basic Details */}
      <div className="mb-8 p-6 border rounded-lg shadow">
        <h2 className="text-2xl font-semibold mb-4">Basic Details</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block mb-1">First Name</label>
            <input
              type="text"
              name="firstName"
              value={newProfile.basicDetails.firstName || ''}
              onChange={handleBasicDetailsChange}
              className="w-full p-2 border rounded"
            />
          </div>
          <div>
            <label className="block mb-1">Last Name</label>
            <input
              type="text"
              name="lastName"
              value={newProfile.basicDetails.lastName || ''}
              onChange={handleBasicDetailsChange}
              className="w-full p-2 border rounded"
            />
          </div>
          <div>
            <label className="block mb-1">Email</label>
            <input
              type="email"
              name="mail"
              value={newProfile.basicDetails.mail || ''}
              onChange={handleBasicDetailsChange}
              className="w-full p-2 border rounded"
            />
          </div>
          <div>
            <label className="block mb-1">Mobile Number</label>
            <input
              type="text"
              name="mobileNo"
              value={newProfile.basicDetails.mobileNo || ''}
              onChange={handleBasicDetailsChange}
              className="w-full p-2 border rounded"
            />
          </div>
        </div>
      </div>

      {/* Section 2: Address Details */}
      <div className="mb-8 p-6 border rounded-lg shadow">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">Add Addresses</h2>
          <button
            onClick={() => setShowAddresses(!showAddresses)}
            className="text-blue-500 hover:underline"
          >
            {showAddresses ? 'Collapse' : 'Expand'}
          </button>
        </div>

        {showAddresses && (
          <div>
            {newProfile.addressDetails.length > 0 && (
              <div className="mb-4 flex justify-end">
                <button
                  onClick={removeAllAddresses}
                  className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                >
                  Remove All
                </button>
              </div>
            )}
            {editingAddressIndex !== null || newProfile.addressDetails.length === 0 ? (
              <div className="border p-4 rounded-lg mb-4">
                <h3 className="text-xl font-semibold mb-4">
                  {editingAddressIndex !== null ? 'Edit Address' : 'Add New Address'}
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block mb-1">Address Type</label>
                    <select
                      name="addressType"
                      value={editedAddress.addressType || ''}
                      onChange={handleAddressChange}
                      className="w-full p-2 border rounded"
                    >
                      <option value="">Select Type</option>
                      <option value="home" disabled={newProfile.addressDetails.some(addr => addr.addressType === 'home' && editingAddressIndex !== newProfile.addressDetails.indexOf(addr))}>Home</option>
                      <option value="work" disabled={newProfile.addressDetails.some(addr => addr.addressType === 'work' && editingAddressIndex !== newProfile.addressDetails.indexOf(addr))}>Work</option>
                      <option value="others">Others</option>
                    </select>
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
                  <div className="flex items-center">
                    <label className="mr-2">Default Address</label>
                    <input
                      type="checkbox"
                      name="defaultAddress"
                      checked={editedAddress.defaultAddress}
                      onChange={handleAddressChange}
                    />
                  </div>
                </div>
                <div className="mt-4 space-x-2">
                  <button
                    onClick={saveAddress}
                    className="px-3 py-1 rounded bg-green-500 text-white hover:bg-green-600"
                  >
                    Ok
                  </button>
                  {editingAddressIndex !== null && (
                    <button
                      onClick={() => removeAddress(editingAddressIndex)}
                      className="px-3 py-1 rounded bg-red-500 text-white hover:bg-red-600"
                    >
                      Remove
                    </button>
                  )}
                </div>
              </div>
            ) : (
              newProfile.addressDetails.map((address, index) => (
                <div key={index} className="border p-4 rounded-lg mb-4">
                  <h5 className="text-md font-medium mb-2 capitalize">{address.addressType} Address</h5>
                  <p><strong>Address Line 1:</strong> {address.addLine1 || 'Not provided'}</p>
                  <p><strong>Address Line 2:</strong> {address.addLine2 || 'Not provided'}</p>
                  <p><strong>Address Line 3:</strong> {address.addLine3 || 'Not provided'}</p>
                  <p><strong>Pincode:</strong> {address.pincode || 'Not provided'}</p>
                  <p><strong>Country:</strong> {address.country || 'Not provided'}</p>
                  {address.addressDesc && <p><strong>Description:</strong> {address.addressDesc}</p>}
                  <p><strong>Default:</strong> {address.defaultAddress ? 'Yes' : 'No'}</p>
                  <div className="mt-2">
                    <button
                      onClick={() => startEditingAddress(index)}
                      className="px-3 py-1 rounded bg-yellow-500 text-white hover:bg-yellow-600"
                    >
                      Edit
                    </button>
                  </div>
                </div>
              ))
            )}
            {newProfile.addressDetails.length > 0 && editingAddressIndex === null && (
              <button
                onClick={() => startEditingAddress()}
                className="px-4 py-2 rounded bg-blue-500 text-white hover:bg-blue-600"
              >
                Add Another Address
              </button>
            )}
          </div>
        )}
      </div>

      {/* Submit/Cancel Buttons */}
      <div className="flex justify-center space-x-4">
        <button
          onClick={createProfile}
          className="px-6 py-2 rounded bg-green-500 text-white hover:bg-green-600"
        >
          Submit
        </button>
        <button
          onClick={cancelCreateProfile}
          className="px-6 py-2 rounded bg-gray-500 text-white hover:bg-gray-600"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}

export default CreateProfilePage;