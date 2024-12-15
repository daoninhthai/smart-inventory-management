import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Product } from '../../types';
import { PencilIcon, TrashIcon } from '@heroicons/react/24/outline';

interface ProductTableProps {
  products: Product[];
  onEdit: (product: Product) => void;
  onDelete: (id: number) => void;
  sortField: string;
  sortDir: string;
  onSort: (field: string) => void;
}

const ProductTable: React.FC<ProductTableProps> = ({
  products,
  onEdit,
  onDelete,
  sortField,
  sortDir,
  onSort,
}) => {
  const navigate = useNavigate();

  const SortHeader: React.FC<{ field: string; label: string }> = ({ field, label }) => (
    <th
      className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:text-gray-700"
      onClick={() => onSort(field)}
    >
      <div className="flex items-center space-x-1">
        <span>{label}</span>
        {sortField === field && (
          <span>{sortDir === 'asc' ? '\u2191' : '\u2193'}</span>
        )}
      </div>
    </th>
  );

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <SortHeader field="sku" label="SKU" />
            <SortHeader field="name" label="Name" />
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Unit</th>
            <SortHeader field="unitPrice" label="Price" />
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reorder Pt</th>
            <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {products.length === 0 ? (
            <tr>
              <td colSpan={7} className="px-4 py-8 text-center text-sm text-gray-400">
                No products found
              </td>
            </tr>
          ) : (
            products.map((product) => (
              <tr
                key={product.id}
                className="hover:bg-gray-50 cursor-pointer"
                onClick={() => navigate(`/products/${product.id}`)}
              >
                <td className="px-4 py-3 text-sm font-mono text-primary-600">{product.sku}</td>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">{product.name}</td>
                <td className="px-4 py-3 text-sm text-gray-500">{product.categoryName || '-'}</td>
                <td className="px-4 py-3 text-sm text-gray-500">{product.unit}</td>
                <td className="px-4 py-3 text-sm text-gray-900">
                  ${product.unitPrice?.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                </td>
                <td className="px-4 py-3 text-sm text-gray-500">{product.reorderPoint}</td>
                <td className="px-4 py-3 text-right">
                  <div className="flex justify-end space-x-2" onClick={(e) => e.stopPropagation()}>
                    <button
                      onClick={() => onEdit(product)}
                      className="p-1.5 text-gray-400 hover:text-primary-600 rounded-lg hover:bg-primary-50"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => onDelete(product.id)}
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
  );
};

export default ProductTable;
