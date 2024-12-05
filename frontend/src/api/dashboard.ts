import apiClient from './client';
import {
  DashboardSummary,
  StockValueReport,
  ProductMovementSummary,
  StockMovement,
} from '../types';

export const getDashboardSummary = async (): Promise<DashboardSummary> => {
  const response = await apiClient.get<DashboardSummary>('/dashboard/summary');
  return response.data;
};

export const getStockValue = async (): Promise<StockValueReport[]> => {
  const response = await apiClient.get<StockValueReport[]>('/dashboard/stock-value');
  return response.data;
};

export const getTopProducts = async (limit: number = 10): Promise<ProductMovementSummary[]> => {
  const response = await apiClient.get<ProductMovementSummary[]>('/dashboard/top-products', {
    params: { limit },
  });
  return response.data;
};

export interface StockTrend {
  date: string;
  totalIn: number;
  totalOut: number;
}

export const getStockTrends = async (days: number = 30): Promise<StockTrend[]> => {
  const response = await apiClient.get<StockTrend[]>('/dashboard/trends', {
    params: { days },
  });
  return response.data;
};

export const getRecentMovements = async (): Promise<StockMovement[]> => {
  const response = await apiClient.get('/stock/movements', {
    params: { page: 0, size: 10, sort: 'createdAt,desc' },
  });
  return response.data.content || response.data;
};
