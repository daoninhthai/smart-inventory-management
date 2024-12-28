import apiClient from './client';
import { PurchaseOrder, Page } from '../types';

export const getPurchaseOrders = async (
  page: number = 0,
  size: number = 20,
  status?: string
): Promise<Page<PurchaseOrder>> => {
  const params: any = { page, size, sort: 'createdAt,desc' };
  if (status) params.status = status;
  const response = await apiClient.get<Page<PurchaseOrder>>('/purchase-orders', { params });
  return response.data;
};

export const getPurchaseOrderById = async (id: number): Promise<PurchaseOrder> => {
  const response = await apiClient.get<PurchaseOrder>(`/purchase-orders/${id}`);
  return response.data;
};

export const createPurchaseOrder = async (data: {
  supplierId: number;
  warehouseId: number;
  items: { productId: number; quantity: number; unitPrice: number }[];
}): Promise<PurchaseOrder> => {
  const response = await apiClient.post<PurchaseOrder>('/purchase-orders', data);
  return response.data;
};

export const submitOrder = async (id: number): Promise<PurchaseOrder> => {
  const response = await apiClient.post<PurchaseOrder>(`/purchase-orders/${id}/submit`);
  return response.data;
};

export const approveOrder = async (id: number): Promise<PurchaseOrder> => {
  const response = await apiClient.post<PurchaseOrder>(`/purchase-orders/${id}/approve`);
  return response.data;
};

export const receiveOrder = async (
  id: number,
  items?: { productId: number; receivedQuantity: number }[]
): Promise<PurchaseOrder> => {
  const response = await apiClient.post<PurchaseOrder>(`/purchase-orders/${id}/receive`, {
    items,
  });
  return response.data;
};

export const cancelOrder = async (id: number): Promise<PurchaseOrder> => {
  const response = await apiClient.post<PurchaseOrder>(`/purchase-orders/${id}/cancel`);
  return response.data;
};
