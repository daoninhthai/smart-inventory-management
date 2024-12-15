import React, { useState, useEffect } from 'react';
import { ArrowsRightLeftIcon, AdjustmentsHorizontalIcon } from '@heroicons/react/24/outline';
import { StockLevel } from '../types';
import { getAllStockLevels, adjustStock, transferStock } from '../api/stock';
import StockAdjustmentModal from '../components/Stock/StockAdjustmentModal';
import StockTransferModal from '../components/Stock/StockTransferModal';

const StockPage: React.FC = () => {
  const [stockLevels, setStockLevels] = useState<StockLevel[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAdjustment, setShowAdjustment] = useState(false);
  const [showTransfer, setShowTransfer] = useState(false);

  const fetchStock = async () => {
    setLoading(true);
    try {
      const data = await getAllStockLevels();
      setStockLevels(data);
    } catch (error) {
      console.error('Failed to fetch stock levels:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStock();
  }, []);

  const handleAdjust = async (data: any) => {
    try {
      await adjustStock(data);
      setShowAdjustment(false);
      fetchStock();
    } catch (error) {
      console.error('Failed to adjust stock:', error);
    }
  };

  const handleTransfer = async (data: any) => {
    try {
      await transferStock(data);
      setShowTransfer(false);
      fetchStock();
    } catch (error) {
      console.error('Failed to transfer stock:', error);
    }
  };

  const getRowColor = (sl: StockLevel) => {
    if (sl.minQuantity && sl.quantity <= sl.minQuantity) return 'bg-red-50';
    if (sl.minQuantity && sl.quantity <= sl.minQuantity * 1.5) return 'bg-yellow-50';
    return '';
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Stock Levels</h1>
          <p className="mt-1 text-sm text-gray-500">Monitor inventory across all warehouses</p>
        </div>
        <div className="flex space-x-3">
          <button onClick={() => setShowAdjustment(true)} className="btn-primary flex items-center">
            <AdjustmentsHorizontalIcon className="h-5 w-5 mr-1" />
            Adjust Stock
          </button>
          <button onClick={() => setShowTransfer(true)} className="btn-secondary flex items-center">
            <ArrowsRightLeftIcon className="h-5 w-5 mr-1" />
            Transfer
          </button>
        </div>
      </div>

      <div className="card">
        {loading ? (
          <div className="animate-pulse space-y-3">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="h-10 bg-gray-100 rounded" />
            ))}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Warehouse</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Quantity</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Min Qty</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Max Qty</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {stockLevels.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="px-4 py-8 text-center text-sm text-gray-400">
                      No stock levels found
                    </td>
                  </tr>
                ) : (
                  stockLevels.map((sl) => (
                    <tr key={sl.id} className={getRowColor(sl)}>
                      <td className="px-4 py-3 text-sm font-mono text-primary-600">{sl.productSku}</td>
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{sl.productName}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{sl.warehouseName}</td>
                      <td className="px-4 py-3 text-sm text-right font-bold text-gray-900">{sl.quantity}</td>
                      <td className="px-4 py-3 text-sm text-right text-gray-500">{sl.minQuantity ?? '-'}</td>
                      <td className="px-4 py-3 text-sm text-right text-gray-500">{sl.maxQuantity ?? '-'}</td>
                      <td className="px-4 py-3">
                        {sl.minQuantity && sl.quantity <= sl.minQuantity ? (
                          <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-red-100 text-red-700">
                            Low Stock
                          </span>
                        ) : sl.minQuantity && sl.quantity <= sl.minQuantity * 1.5 ? (
                          <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-yellow-100 text-yellow-700">
                            Warning
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-green-100 text-green-700">
                            Normal
                          </span>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showAdjustment && (
        <StockAdjustmentModal onSubmit={handleAdjust} onClose={() => setShowAdjustment(false)} />
      )}
      {showTransfer && (
        <StockTransferModal onSubmit={handleTransfer} onClose={() => setShowTransfer(false)} />
      )}
    </div>
  );
};

export default StockPage;
