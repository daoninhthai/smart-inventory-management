import apiClient from './client';
import { Supplier } from '../types';

export const getSuppliers = async (): Promise<Supplier[]> => {
  const response = await apiClient.get<Supplier[]>('/suppliers');
  return response.data;
};

export const getSupplierById = async (id: number): Promise<Supplier> => {
  const response = await apiClient.get<Supplier>(`/suppliers/${id}`);
  return response.data;
};

export const searchSuppliers = async (name: string): Promise<Supplier[]> => {
  const response = await apiClient.get<Supplier[]>('/suppliers/search', {
    params: { name },
  });
  return response.data;
};

export const createSupplier = async (supplier: Partial<Supplier>): Promise<Supplier> => {
  const response = await apiClient.post<Supplier>('/suppliers', supplier);
  return response.data;
};

export const updateSupplier = async (id: number, supplier: Partial<Supplier>): Promise<Supplier> => {
  const response = await apiClient.put<Supplier>(`/suppliers/${id}`, supplier);
  return response.data;
};

export const deleteSupplier = async (id: number): Promise<void> => {
  await apiClient.delete(`/suppliers/${id}`);
};
