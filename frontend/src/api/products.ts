import apiClient from './client';
import { Product, Page } from '../types';

export const getProducts = async (
  page: number = 0,
  size: number = 20,
  sort: string = 'name,asc'
): Promise<Page<Product>> => {
  const response = await apiClient.get<Page<Product>>('/products', {
    params: { page, size, sort },
  });
  return response.data;
};

export const searchProducts = async (
  params: {
    name?: string;
    sku?: string;
    categoryId?: number;
    page?: number;
    size?: number;
  }
): Promise<Page<Product>> => {
  const response = await apiClient.get<Page<Product>>('/products/search', { params });
  return response.data;
};

export const getProductById = async (id: number): Promise<Product> => {
  const response = await apiClient.get<Product>(`/products/${id}`);
  return response.data;
};

export const createProduct = async (product: {
  sku: string;
  name: string;
  description: string;
  categoryId: number;
  unit: string;
  unitPrice: number;
  reorderPoint: number;
  reorderQuantity: number;
}): Promise<Product> => {
  const response = await apiClient.post<Product>('/products', product);
  return response.data;
};

export const updateProduct = async (
  id: number,
  product: Partial<Product>
): Promise<Product> => {
  const response = await apiClient.put<Product>(`/products/${id}`, product);
  return response.data;
};

export const deleteProduct = async (id: number): Promise<void> => {
  await apiClient.delete(`/products/${id}`);
};
