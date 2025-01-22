import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusIcon, BuildingStorefrontIcon } from '@heroicons/react/24/outline';
import { Warehouse } from '../types';
import { getWarehouses, createWarehouse } from '../api/warehouses';
import CapacityBar from '../components/Warehouses/CapacityBar';
import WarehouseForm from '../components/Warehouses/WarehouseForm';

const WarehousesPage: React.FC = () => {
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const navigate = useNavigate();

  const fetchWarehouses = async () => {
    setLoading(true);
    try {
      const data = await getWarehouses();
      setWarehouses(data);
    } catch (error) {
      console.error('Failed to fetch warehouses:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const handleCreate = async (data: Partial<Warehouse>) => {
    try {
      await createWarehouse(data);
      setShowForm(false);
      fetchWarehouses();
    } catch (error) {
      console.error('Failed to create warehouse:', error);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Warehouses</h1>
          <p className="mt-1 text-sm text-gray-500">Manage your warehouse locations</p>
        </div>
        <button onClick={() => setShowForm(true)} className="btn-primary flex items-center">
          <PlusIcon className="h-5 w-5 mr-1" />
          Add Warehouse
        </button>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="card animate-pulse">
              <div className="h-32 bg-gray-200 rounded" />
            </div>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {warehouses.length === 0 ? (
            <div className="col-span-full text-center py-12">
              <BuildingStorefrontIcon className="h-12 w-12 mx-auto text-gray-300 mb-3" />
              <p className="text-gray-400">No warehouses found</p>
            </div>
          ) : (
            warehouses.map((warehouse) => (
              <div
                key={warehouse.id}
                className="card hover:shadow-md transition-shadow cursor-pointer"
                onClick={() => navigate(`/warehouses/${warehouse.id}`)}
              >
                <div className="flex items-start justify-between mb-3">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">{warehouse.name}</h3>
                    <p className="text-sm font-mono text-primary-600">{warehouse.code}</p>
                  </div>
                  <div className={`p-2 rounded-lg ${warehouse.active ? 'bg-green-50' : 'bg-gray-100'}`}>
                    <BuildingStorefrontIcon className={`h-5 w-5 ${warehouse.active ? 'text-green-600' : 'text-gray-400'}`} />
                  </div>
                </div>

                {warehouse.address && (
                  <p className="text-sm text-gray-500 mb-3">{warehouse.address}</p>
                )}

                {warehouse.capacity && (
                  <CapacityBar current={0} max={warehouse.capacity} />
                )}
              </div>
            ))
          )}
        </div>
      )}

      {showForm && <WarehouseForm onSubmit={handleCreate} onClose={() => setShowForm(false)} />}
    </div>
  );
};

export default WarehousesPage;
