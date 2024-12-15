import apiClient from './client';
import { StockLevel, LowStockAlert } from '../types';

export const getAllStockLevels = async (): Promise<StockLevel[]> => {
  const response = await apiClient.get<StockLevel[]>('/stock');
  return response.data;
};

export const getStockByProduct = async (productId: number): Promise<StockLevel[]> => {
  const response = await apiClient.get<StockLevel[]>(`/stock/product/${productId}`);
  return response.data;
};

export const getLowStockAlerts = async (): Promise<LowStockAlert[]> => {
  const response = await apiClient.get<LowStockAlert[]>('/stock/alerts');
  return response.data;
};

export const adjustStock = async (data: {
  productId: number;
  warehouseId: number;
  quantity: number;
  type: string;
  notes?: string;
}): Promise<StockLevel> => {
  const response = await apiClient.post<StockLevel>('/stock/adjust', data);
  return response.data;
};

export const transferStock = async (data: {
  productId: number;
  fromWarehouseId: number;
  toWarehouseId: number;
  quantity: number;
  notes?: string;
}): Promise<void> => {
  await apiClient.post('/stock/transfer', data);
};
