import React from 'react';
import { StockMovement } from '../../types';

interface RecentMovementsProps {
  movements: StockMovement[];
  loading: boolean;
}

const typeBadgeColors: Record<string, string> = {
  IN: 'bg-green-100 text-green-700',
  OUT: 'bg-red-100 text-red-700',
  TRANSFER: 'bg-blue-100 text-blue-700',
  ADJUSTMENT: 'bg-amber-100 text-amber-700',
};

const RecentMovements: React.FC<RecentMovementsProps> = ({ movements, loading }) => {
  if (loading) {
    return (
      <div className="card">
        <div className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 bg-gray-100 rounded animate-pulse" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Stock Movements</h3>
      <div className="overflow-hidden">
        <table className="min-w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
              <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase">Warehouse</th>
              <th className="px-3 py-2 text-right text-xs font-medium text-gray-500 uppercase">Qty</th>
              <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {movements.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-3 py-4 text-center text-sm text-gray-400">
                  No recent movements
                </td>
              </tr>
            ) : (
              movements.map((movement, idx) => (
                <tr key={movement.id || idx} className="hover:bg-gray-50">
                  <td className="px-3 py-2">
                    <span className={`inline-flex px-2 py-0.5 text-xs font-medium rounded-full ${typeBadgeColors[movement.type] || 'bg-gray-100 text-gray-700'}`}>
                      {movement.type}
                    </span>
                  </td>
                  <td className="px-3 py-2 text-sm text-gray-900">{movement.productName}</td>
                  <td className="px-3 py-2 text-sm text-gray-500">{movement.warehouseName}</td>
                  <td className="px-3 py-2 text-sm text-right font-medium text-gray-900">
                    {movement.quantity.toLocaleString()}
                  </td>
                  <td className="px-3 py-2 text-sm text-gray-500">
                    {movement.createdAt ? new Date(movement.createdAt).toLocaleDateString() : '-'}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default RecentMovements;
