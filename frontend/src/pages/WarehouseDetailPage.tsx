import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { Warehouse, StockLevel } from '../types';
import { getWarehouseById, getWarehouseStock } from '../api/warehouses';
import CapacityBar from '../components/Warehouses/CapacityBar';

const WarehouseDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [warehouse, setWarehouse] = useState<Warehouse | null>(null);
  const [stockLevels, setStockLevels] = useState<StockLevel[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const warehouseData = await getWarehouseById(Number(id));
        setWarehouse(warehouseData);

        const stockData = await getWarehouseStock(Number(id));
        setStockLevels(stockData);
      } catch (error) {
        console.error('Failed to fetch warehouse details:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchData();
  }, [id]);

  if (loading) {
    return (
      <div className="animate-pulse space-y-4">
        <div className="h-8 w-48 bg-gray-200 rounded" />
        <div className="h-64 bg-gray-200 rounded" />
      </div>
    );
  }

  if (!warehouse) {
    return <div className="text-center py-12 text-gray-500">Warehouse not found</div>;
  }

  const totalStock = stockLevels.reduce((sum, sl) => sum + sl.quantity, 0);

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <button onClick={() => navigate('/warehouses')} className="p-2 hover:bg-gray-100 rounded-lg">
          <ArrowLeftIcon className="h-5 w-5 text-gray-500" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{warehouse.name}</h1>
          <p className="text-sm text-gray-500 font-mono">{warehouse.code}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-2">Address</h3>
          <p className="text-gray-900">{warehouse.address || 'Not specified'}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-2">Total Products</h3>
          <p className="text-2xl font-bold text-gray-900">{stockLevels.length}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-3">Capacity</h3>
          <CapacityBar current={totalStock} max={warehouse.capacity || 0} />
        </div>
      </div>

      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Stock Levels</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Quantity</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Min</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Max</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Updated</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {stockLevels.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-sm text-gray-400">
                    No stock in this warehouse
                  </td>
                </tr>
              ) : (
                stockLevels.map((sl) => (
                  <tr
                    key={sl.id}
                    className={`hover:bg-gray-50 ${sl.minQuantity && sl.quantity <= sl.minQuantity ? 'bg-red-50' : ''}`}
                  >
                    <td className="px-4 py-3 text-sm font-mono text-primary-600">{sl.productSku}</td>
                    <td className="px-4 py-3 text-sm font-medium text-gray-900">{sl.productName}</td>
                    <td className="px-4 py-3 text-sm text-right font-bold text-gray-900">{sl.quantity}</td>
                    <td className="px-4 py-3 text-sm text-right text-gray-500">{sl.minQuantity ?? '-'}</td>
                    <td className="px-4 py-3 text-sm text-right text-gray-500">{sl.maxQuantity ?? '-'}</td>
                    <td className="px-4 py-3">
                      {sl.minQuantity && sl.quantity <= sl.minQuantity ? (
                        <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-red-100 text-red-700">
                          Low
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-green-100 text-green-700">
                          OK
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500">
                      {sl.lastUpdated ? new Date(sl.lastUpdated).toLocaleDateString() : '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default WarehouseDetailPage;
