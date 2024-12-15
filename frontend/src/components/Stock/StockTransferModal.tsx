import React, { useState } from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';

interface StockTransferModalProps {
  onSubmit: (data: {
    productId: number;
    fromWarehouseId: number;
    toWarehouseId: number;
    quantity: number;
    notes: string;
  }) => void;
  onClose: () => void;
}

const StockTransferModal: React.FC<StockTransferModalProps> = ({ onSubmit, onClose }) => {
  const [formData, setFormData] = useState({
    productId: '',
    fromWarehouseId: '',
    toWarehouseId: '',
    quantity: '',
    notes: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      productId: Number(formData.productId),
      fromWarehouseId: Number(formData.fromWarehouseId),
      toWarehouseId: Number(formData.toWarehouseId),
      quantity: Number(formData.quantity),
      notes: formData.notes,
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full mx-4">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-lg font-semibold text-gray-900">Stock Transfer</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <XMarkIcon className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Product ID</label>
            <input
              type="number"
              name="productId"
              value={formData.productId}
              onChange={handleChange}
              className="input-field"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">From Warehouse ID</label>
              <input
                type="number"
                name="fromWarehouseId"
                value={formData.fromWarehouseId}
                onChange={handleChange}
                className="input-field"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">To Warehouse ID</label>
              <input
                type="number"
                name="toWarehouseId"
                value={formData.toWarehouseId}
                onChange={handleChange}
                className="input-field"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
            <input
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              className="input-field"
              min="1"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
            <textarea
              name="notes"
              value={formData.notes}
              onChange={handleChange}
              className="input-field"
              rows={2}
            />
          </div>

          <div className="flex justify-end space-x-3 pt-2">
            <button type="button" onClick={onClose} className="btn-secondary">Cancel</button>
            <button type="submit" className="btn-primary">Transfer Stock</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default StockTransferModal;
