import React, { useEffect, useState } from 'react';

function VehicleReservation({ token }) {
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [availability, setAvailability] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedHour, setSelectedHour] = useState(null);

  useEffect(() => {
    fetch('/vehicles')
      .then(res => res.json())
      .then(setVehicles);
  }, []);

  const loadAvailability = async (vehicleId) => {
    const today = new Date().toISOString().slice(0,10);
    const resp = await fetch(`/vehicles/${vehicleId}/availability?startDate=${today}&days=7`, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    const data = await resp.json();
    setAvailability(data.availability);
  };

  const selectVehicle = (v) => {
    setSelectedVehicle(v);
    setSelectedDate(null);
    setSelectedHour(null);
    loadAvailability(v.id);
  };

  const hours = Array.from({length:24}, (_,i) => i);

  const isPast = (dateStr, hour) => {
    const now = new Date();
    const date = new Date(dateStr + 'T00:00:00');
    if (date.toDateString() !== now.toDateString()) return false;
    return hour <= now.getHours();
  };

  const dayAvailability = availability.find(a => a.date === selectedDate);
  const bookedHours = dayAvailability ? dayAvailability.bookedHours : [];

  return (
    <div>
      <h2>Vehicles</h2>
      <div style={{display:'flex',gap:'1rem'}}>
        {vehicles.map(v => (
          <div key={v.id} onClick={() => selectVehicle(v)} style={{cursor:'pointer', border: v===selectedVehicle ? '2px solid blue' : '1px solid #ccc'}}>
            <img src={v.imageUrl} alt={v.name} width={100} />
            <div>{v.name}</div>
          </div>
        ))}
      </div>

      {selectedVehicle && (
        <div>
          <h3>Select Day</h3>
          <div style={{display:'flex', gap:'0.5rem'}}>
            {availability.map(a => {
              const fullyBooked = a.bookedHours.length === 24;
              return (
                <button key={a.date} onClick={() => setSelectedDate(a.date)} disabled={fullyBooked} style={{background: a.date===selectedDate ? '#add8e6':'white'}}>
                  {a.date}{fullyBooked ? ' (Full)' : ''}
                </button>
              );
            })}
          </div>
        </div>
      )}

      {selectedDate && (
        <div>
          <h3>Select Hour</h3>
          <div style={{display:'flex', flexWrap:'wrap', gap:'0.5rem'}}>
            {hours.map(h => {
              const disabled = bookedHours.includes(h) || isPast(selectedDate, h);
              return (
                <button key={h} onClick={() => setSelectedHour(h)} disabled={disabled} style={{width:'60px', background: selectedHour===h ? '#add8e6' : undefined, cursor: disabled ? 'not-allowed':'pointer', opacity: disabled ? 0.5:1}}>
                  {h}:00
                </button>
              );
            })}
          </div>
        </div>
      )}

      {selectedHour !== null && (
        <button>Next</button>
      )}
    </div>
  );
}

export default VehicleReservation;
