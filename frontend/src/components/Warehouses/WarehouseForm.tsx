import React, { useState, useEffect } from 'react';
import { Warehouse } from '../../types';
import { XMarkIcon } from '@heroicons/react/24/outline';

interface WarehouseFormProps {
  warehouse?: Warehouse | null;
  onSubmit: (data: Partial<Warehouse>) => void;
  onClose: () => void;
}

const WarehouseForm: React.FC<WarehouseFormProps> = ({ warehouse, onSubmit, onClose }) => {
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    address: '',
    capacity: '',
  });

  useEffect(() => {
    if (warehouse) {
      setFormData({
        name: warehouse.name || '',
        code: warehouse.code || '',
        address: warehouse.address || '',
        capacity: warehouse.capacity?.toString() || '',
      });
    }
  }, [warehouse]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      name: formData.name,
      code: formData.code,
      address: formData.address,
      capacity: Number(formData.capacity),
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full mx-4">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-lg font-semibold text-gray-900">
            {warehouse ? 'Edit Warehouse' : 'Add Warehouse'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <XMarkIcon className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="input-field"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Code</label>
              <input
                type="text"
                name="code"
                value={formData.code}
                onChange={handleChange}
                className="input-field"
                required
                disabled={!!warehouse}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
            <textarea
              name="address"
              value={formData.address}
              onChange={handleChange}
              className="input-field"
              rows={2}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Capacity</label>
            <input
              type="number"
              name="capacity"
              value={formData.capacity}
              onChange={handleChange}
              className="input-field"
              min="1"
              required
            />
          </div>

          <div className="flex justify-end space-x-3 pt-2">
            <button type="button" onClick={onClose} className="btn-secondary">Cancel</button>
            <button type="submit" className="btn-primary">
              {warehouse ? 'Update' : 'Create'} Warehouse
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default WarehouseForm;
