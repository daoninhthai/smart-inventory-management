import apiClient from './client';
import { AuthResponse } from '../types';

export const login = async (username: string, password: string): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>('/auth/login', {
    username,
    password,
  });
  return response.data;
};

export const register = async (
  username: string,
  email: string,
  password: string,
  fullName: string
): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>('/auth/register', {
    username,
    email,
    password,
    fullName,
  });
  return response.data;
};
