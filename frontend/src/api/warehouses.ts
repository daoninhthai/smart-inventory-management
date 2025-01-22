import apiClient from './client';
import { Warehouse, StockLevel } from '../types';

export const getWarehouses = async (): Promise<Warehouse[]> => {
  const response = await apiClient.get<Warehouse[]>('/warehouses');
  return response.data;
};

export const getWarehouseById = async (id: number): Promise<Warehouse> => {
  const response = await apiClient.get<Warehouse>(`/warehouses/${id}`);
  return response.data;
};

export const getWarehouseStock = async (id: number): Promise<StockLevel[]> => {
  const response = await apiClient.get<StockLevel[]>(`/warehouses/${id}/stock`);
  return response.data;
};

export const createWarehouse = async (warehouse: Partial<Warehouse>): Promise<Warehouse> => {
  const response = await apiClient.post<Warehouse>('/warehouses', warehouse);
  return response.data;
};

export const updateWarehouse = async (
  id: number,
  warehouse: Partial<Warehouse>
): Promise<Warehouse> => {
  const response = await apiClient.put<Warehouse>(`/warehouses/${id}`, warehouse);
  return response.data;
};

export const deleteWarehouse = async (id: number): Promise<void> => {
  await apiClient.delete(`/warehouses/${id}`);
};
