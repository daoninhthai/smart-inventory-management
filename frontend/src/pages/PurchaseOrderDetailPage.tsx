import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { PurchaseOrder } from '../types';
import {
  getPurchaseOrderById,
  submitOrder,
  approveOrder,
  receiveOrder,
  cancelOrder,
} from '../api/purchaseOrders';

const statusBadgeColors: Record<string, string> = {
  DRAFT: 'bg-gray-100 text-gray-700',
  SUBMITTED: 'bg-blue-100 text-blue-700',
  APPROVED: 'bg-green-100 text-green-700',
  RECEIVED: 'bg-purple-100 text-purple-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

const PurchaseOrderDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<PurchaseOrder | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchOrder = async () => {
    try {
      const data = await getPurchaseOrderById(Number(id));
      setOrder(data);
    } catch (error) {
      console.error('Failed to fetch order:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) fetchOrder();
  }, [id]);

  const handleAction = async (action: string) => {
    if (!order) return;
    setActionLoading(true);
    try {
      switch (action) {
        case 'submit':
          await submitOrder(order.id);
          break;
        case 'approve':
          await approveOrder(order.id);
          break;
        case 'receive':
          await receiveOrder(order.id);
          break;
        case 'cancel':
          if (!confirm('Are you sure you want to cancel this order?')) return;
          await cancelOrder(order.id);
          break;
      }
      fetchOrder();
    } catch (error) {
      console.error(`Failed to ${action} order:`, error);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="animate-pulse space-y-4">
        <div className="h-8 w-48 bg-gray-200 rounded" />
        <div className="h-64 bg-gray-200 rounded" />
      </div>
    );
  }

  if (!order) {
    return <div className="text-center py-12 text-gray-500">Order not found</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button onClick={() => navigate('/orders')} className="p-2 hover:bg-gray-100 rounded-lg">
            <ArrowLeftIcon className="h-5 w-5 text-gray-500" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{order.orderNumber}</h1>
            <div className="flex items-center space-x-2 mt-1">
              <span className={`px-2.5 py-0.5 text-xs font-medium rounded-full ${statusBadgeColors[order.status]}`}>
                {order.status}
              </span>
              <span className="text-sm text-gray-500">
                Created {new Date(order.createdAt).toLocaleDateString()}
              </span>
            </div>
          </div>
        </div>

        <div className="flex space-x-2">
          {order.status === 'DRAFT' && (
            <button onClick={() => handleAction('submit')} disabled={actionLoading} className="btn-primary">
              Submit Order
            </button>
          )}
          {order.status === 'SUBMITTED' && (
            <button onClick={() => handleAction('approve')} disabled={actionLoading} className="btn-primary">
              Approve Order
            </button>
          )}
          {order.status === 'APPROVED' && (
            <button onClick={() => handleAction('receive')} disabled={actionLoading} className="btn-primary">
              Receive Order
            </button>
          )}
          {order.status !== 'RECEIVED' && order.status !== 'CANCELLED' && (
            <button onClick={() => handleAction('cancel')} disabled={actionLoading} className="btn-danger">
              Cancel
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-2">Supplier</h3>
          <p className="text-lg font-semibold text-gray-900">{order.supplierName}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-2">Warehouse</h3>
          <p className="text-lg font-semibold text-gray-900">{order.warehouseName}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-gray-500 mb-2">Total Amount</h3>
          <p className="text-lg font-semibold text-gray-900">
            ${order.totalAmount?.toLocaleString(undefined, { minimumFractionDigits: 2 })}
          </p>
        </div>
      </div>

      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Order Items</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Qty</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Unit Price</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Subtotal</th>
                {order.status === 'RECEIVED' && (
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Received</th>
                )}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {order.items?.map((item) => (
                <tr key={item.id}>
                  <td className="px-4 py-3 text-sm font-mono text-primary-600">{item.productSku}</td>
                  <td className="px-4 py-3 text-sm text-gray-900">{item.productName}</td>
                  <td className="px-4 py-3 text-sm text-right text-gray-900">{item.quantity}</td>
                  <td className="px-4 py-3 text-sm text-right text-gray-900">
                    ${item.unitPrice?.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>
                  <td className="px-4 py-3 text-sm text-right font-medium text-gray-900">
                    ${((item.quantity || 0) * (item.unitPrice || 0)).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>
                  {order.status === 'RECEIVED' && (
                    <td className="px-4 py-3 text-sm text-right text-green-600 font-medium">
                      {item.receivedQuantity}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default PurchaseOrderDetailPage;
