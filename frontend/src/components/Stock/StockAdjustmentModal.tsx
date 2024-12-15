import React, { useState } from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';

interface StockAdjustmentModalProps {
  onSubmit: (data: {
    productId: number;
    warehouseId: number;
    quantity: number;
    type: string;
    notes: string;
  }) => void;
  onClose: () => void;
}

const StockAdjustmentModal: React.FC<StockAdjustmentModalProps> = ({ onSubmit, onClose }) => {
  const [formData, setFormData] = useState({
    productId: '',
    warehouseId: '',
    quantity: '',
    type: 'IN',
    notes: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      productId: Number(formData.productId),
      warehouseId: Number(formData.warehouseId),
      quantity: Number(formData.quantity),
      type: formData.type,
      notes: formData.notes,
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full mx-4">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-lg font-semibold text-gray-900">Stock Adjustment</h2>
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

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Warehouse ID</label>
            <input
              type="number"
              name="warehouseId"
              value={formData.warehouseId}
              onChange={handleChange}
              className="input-field"
              required
            />
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
            <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select name="type" value={formData.type} onChange={handleChange} className="input-field">
              <option value="IN">Stock In</option>
              <option value="OUT">Stock Out</option>
              <option value="ADJUSTMENT">Adjustment</option>
            </select>
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
            <button type="submit" className="btn-primary">Submit Adjustment</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default StockAdjustmentModal;
