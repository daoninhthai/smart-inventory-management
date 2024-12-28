import React, { useState, useEffect } from 'react';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import { Supplier } from '../types';
import { getSuppliers, createSupplier, updateSupplier, deleteSupplier } from '../api/suppliers';
import SupplierForm from '../components/Suppliers/SupplierForm';

const SuppliersPage: React.FC = () => {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null);

  const fetchSuppliers = async () => {
    setLoading(true);
    try {
      const data = await getSuppliers();
      setSuppliers(data);
    } catch (error) {
      console.error('Failed to fetch suppliers:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const handleCreate = async (data: Partial<Supplier>) => {
    try {
      await createSupplier(data);
      setShowForm(false);
      fetchSuppliers();
    } catch (error) {
      console.error('Failed to create supplier:', error);
    }
  };

  const handleUpdate = async (data: Partial<Supplier>) => {
    if (!editingSupplier) return;
    try {
      await updateSupplier(editingSupplier.id, data);
      setEditingSupplier(null);
      fetchSuppliers();
    } catch (error) {
      console.error('Failed to update supplier:', error);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this supplier?')) return;
    try {
      await deleteSupplier(id);
      fetchSuppliers();
    } catch (error) {
      console.error('Failed to delete supplier:', error);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Suppliers</h1>
          <p className="mt-1 text-sm text-gray-500">Manage your supplier network</p>
        </div>
        <button onClick={() => setShowForm(true)} className="btn-primary flex items-center">
          <PlusIcon className="h-5 w-5 mr-1" />
          Add Supplier
        </button>
      </div>

      <div className="card">
        {loading ? (
          <div className="animate-pulse space-y-3">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-12 bg-gray-100 rounded" />
            ))}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Contact</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Phone</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Address</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {suppliers.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-sm text-gray-400">
                      No suppliers found
                    </td>
                  </tr>
                ) : (
                  suppliers.map((supplier) => (
                    <tr key={supplier.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{supplier.name}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{supplier.contactName || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{supplier.email || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{supplier.phone || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-500 max-w-xs truncate">{supplier.address || '-'}</td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex justify-end space-x-2">
                          <button
                            onClick={() => setEditingSupplier(supplier)}
                            className="p-1.5 text-gray-400 hover:text-primary-600 rounded-lg hover:bg-primary-50"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(supplier.id)}
                            className="p-1.5 text-gray-400 hover:text-red-600 rounded-lg hover:bg-red-50"
                          >
                            <TrashIcon className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showForm && <SupplierForm onSubmit={handleCreate} onClose={() => setShowForm(false)} />}
      {editingSupplier && (
        <SupplierForm
          supplier={editingSupplier}
          onSubmit={handleUpdate}
          onClose={() => setEditingSupplier(null)}
        />
      )}
    </div>
  );
};

export default SuppliersPage;
