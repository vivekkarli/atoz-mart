import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await fetch('http://localhost:8072/atozmart/catalog/items', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            // Uncomment and add your token if authentication is required:
            // 'Authorization': 'Bearer your-token-here'
          },
        });

        if (!response.ok) {
          throw new Error(`Failed to fetch items: ${response.statusText}`);
        }

        const itemList = await response.json();
        setItems(itemList);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchItems();
  }, []);

  if (loading) {
    return <div className="App"><h2>Loading items...</h2></div>;
  }

  if (error) {
    return (
      <div className="App">
        <h2>Error: {error}</h2>
        <button onClick={() => window.location.reload()}>Retry</button>
      </div>
    );
  }

  return (
    <div className="App">
      <h1>Grocery List</h1>
      {items.length === 0 ? (
        <p>No items available.</p>
      ) : (
        <ul className="grocery-list">
          {items.map((item) => (
            <li key={item.id} className="grocery-item">
              {item.name} - ${(parseFloat(item.price)).toFixed(2)}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default App;