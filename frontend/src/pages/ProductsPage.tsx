import React, { useState, useEffect } from 'react';
import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import { Product, Page } from '../types';
import { getProducts, searchProducts, createProduct, updateProduct, deleteProduct } from '../api/products';
import ProductTable from '../components/Products/ProductTable';
import ProductForm from '../components/Products/ProductForm';

const ProductsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchName, setSearchName] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [sortField, setSortField] = useState('name');
  const [sortDir, setSortDir] = useState('asc');
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      let data: Page<Product>;
      if (searchName || categoryFilter) {
        data = await searchProducts({
          name: searchName || undefined,
          categoryId: categoryFilter ? Number(categoryFilter) : undefined,
          page,
          size: 20,
        });
      } else {
        data = await getProducts(page, 20, `${sortField},${sortDir}`);
      }
      setProducts(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to fetch products:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [page, sortField, sortDir]);

  const handleSearch = () => {
    setPage(0);
    fetchProducts();
  };

  const handleSort = (field: string) => {
    if (sortField === field) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  };

  const handleCreate = async (data: any) => {
    try {
      await createProduct(data);
      setShowForm(false);
      fetchProducts();
    } catch (error) {
      console.error('Failed to create product:', error);
    }
  };

  const handleUpdate = async (data: any) => {
    if (!editingProduct) return;
    try {
      await updateProduct(editingProduct.id, data);
      setEditingProduct(null);
      fetchProducts();
    } catch (error) {
      console.error('Failed to update product:', error);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this product?')) return;
    try {
      await deleteProduct(id);
      fetchProducts();
    } catch (error) {
      console.error('Failed to delete product:', error);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Products</h1>
          <p className="mt-1 text-sm text-gray-500">Manage your product catalog</p>
        </div>
        <button onClick={() => setShowForm(true)} className="btn-primary flex items-center">
          <PlusIcon className="h-5 w-5 mr-1" />
          Add Product
        </button>
      </div>

      <div className="card">
        <div className="flex flex-wrap gap-3 mb-4">
          <div className="flex-1 min-w-[200px]">
            <div className="relative">
              <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Search products..."
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                className="input-field pl-9"
              />
            </div>
          </div>
          <input
            type="number"
            placeholder="Category ID"
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="input-field w-32"
          />
          <button onClick={handleSearch} className="btn-primary">Search</button>
        </div>

        {loading ? (
          <div className="animate-pulse space-y-3">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-12 bg-gray-100 rounded" />
            ))}
          </div>
        ) : (
          <ProductTable
            products={products}
            onEdit={(p) => setEditingProduct(p)}
            onDelete={handleDelete}
            sortField={sortField}
            sortDir={sortDir}
            onSort={handleSort}
          />
        )}

        {totalPages > 1 && (
          <div className="flex items-center justify-between mt-4 pt-4 border-t">
            <button
              onClick={() => setPage(Math.max(0, page - 1))}
              disabled={page === 0}
              className="btn-secondary disabled:opacity-50"
            >
              Previous
            </button>
            <span className="text-sm text-gray-600">
              Page {page + 1} of {totalPages}
            </span>
            <button
              onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
              disabled={page >= totalPages - 1}
              className="btn-secondary disabled:opacity-50"
            >
              Next
            </button>
          </div>
        )}
      </div>

      {showForm && (
        <ProductForm onSubmit={handleCreate} onClose={() => setShowForm(false)} />
      )}

      {editingProduct && (
        <ProductForm
          product={editingProduct}
          onSubmit={handleUpdate}
          onClose={() => setEditingProduct(null)}
        />
      )}
    </div>
  );
};

export default ProductsPage;
