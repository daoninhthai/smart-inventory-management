import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { Product, StockLevel } from '../types';
import { getProductById } from '../api/products';
import { getStockByProduct } from '../api/stock';

const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [product, setProduct] = useState<Product | null>(null);
  const [stockLevels, setStockLevels] = useState<StockLevel[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const productData = await getProductById(Number(id));
        setProduct(productData);

        const stockData = await getStockByProduct(Number(id));
        setStockLevels(stockData);
      } catch (error) {
        console.error('Failed to fetch product details:', error);
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

  if (!product) {
    return <div className="text-center py-12 text-gray-500">Product not found</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <button onClick={() => navigate('/products')} className="p-2 hover:bg-gray-100 rounded-lg">
          <ArrowLeftIcon className="h-5 w-5 text-gray-500" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{product.name}</h1>
          <p className="text-sm text-gray-500 font-mono">{product.sku}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 card">
          <h2 className="text-lg font-semibold mb-4">Product Information</h2>
          <dl className="grid grid-cols-2 gap-4">
            <div>
              <dt className="text-sm font-medium text-gray-500">Category</dt>
              <dd className="mt-1 text-sm text-gray-900">{product.categoryName || '-'}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Unit</dt>
              <dd className="mt-1 text-sm text-gray-900">{product.unit}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Unit Price</dt>
              <dd className="mt-1 text-sm text-gray-900">${product.unitPrice?.toLocaleString()}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Reorder Point</dt>
              <dd className="mt-1 text-sm text-gray-900">{product.reorderPoint}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Reorder Quantity</dt>
              <dd className="mt-1 text-sm text-gray-900">{product.reorderQuantity}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Status</dt>
              <dd className="mt-1">
                <span className={`px-2 py-0.5 text-xs font-medium rounded-full ${product.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                  {product.active ? 'Active' : 'Inactive'}
                </span>
              </dd>
            </div>
          </dl>
          {product.description && (
            <div className="mt-4">
              <dt className="text-sm font-medium text-gray-500">Description</dt>
              <dd className="mt-1 text-sm text-gray-700">{product.description}</dd>
            </div>
          )}
        </div>

        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Stock Across Warehouses</h2>
          {stockLevels.length === 0 ? (
            <p className="text-sm text-gray-400">No stock records found</p>
          ) : (
            <div className="space-y-3">
              {stockLevels.map((sl) => (
                <div key={sl.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-gray-900">{sl.warehouseName}</p>
                    <p className="text-xs text-gray-500">
                      Min: {sl.minQuantity ?? '-'} / Max: {sl.maxQuantity ?? '-'}
                    </p>
                  </div>
                  <div className={`text-lg font-bold ${sl.minQuantity && sl.quantity <= sl.minQuantity ? 'text-red-600' : 'text-gray-900'}`}>
                    {sl.quantity}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
