import apiClient from './client';
import { ForecastResponse, ReorderSuggestion, InsightResponse } from '../types';

export const getDemandForecast = async (
  productId: number,
  periodsAhead: number = 30
): Promise<ForecastResponse> => {
  const response = await apiClient.get<ForecastResponse>(
    `/forecast/demand/${productId}`,
    { params: { periodsAhead } }
  );
  return response.data;
};

export const getReorderSuggestion = async (
  productId: number
): Promise<ReorderSuggestion> => {
  const response = await apiClient.get<ReorderSuggestion>(
    `/forecast/reorder/${productId}`
  );
  return response.data;
};

export const getInsights = async (
  question?: string
): Promise<InsightResponse> => {
  const response = await apiClient.post<InsightResponse>('/forecast/insights', {
    question,
  });
  return response.data;
};
