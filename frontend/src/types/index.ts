export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
}

export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  categoryId: number;
  categoryName: string;
  unit: string;
  unitPrice: number;
  reorderPoint: number;
  reorderQuantity: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Warehouse {
  id: number;
  name: string;
  code: string;
  address: string;
  capacity: number;
  active: boolean;
}

export interface StockLevel {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  warehouseId: number;
  warehouseName: string;
  quantity: number;
  minQuantity: number;
  maxQuantity: number;
  lastUpdated: string;
}

export interface StockMovement {
  id: number;
  productId: number;
  productName: string;
  warehouseId: number;
  warehouseName: string;
  type: 'IN' | 'OUT' | 'TRANSFER' | 'ADJUSTMENT';
  quantity: number;
  reference: string;
  notes: string;
  createdBy: string;
  createdAt: string;
}

export interface Supplier {
  id: number;
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address: string;
  active: boolean;
}

export interface PurchaseOrder {
  id: number;
  orderNumber: string;
  supplierId: number;
  supplierName: string;
  warehouseId: number;
  warehouseName: string;
  status: 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'RECEIVED' | 'CANCELLED';
  totalAmount: number;
  createdBy: string;
  createdAt: string;
  receivedAt: string;
  items: PurchaseOrderItem[];
}

export interface PurchaseOrderItem {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  quantity: number;
  unitPrice: number;
  receivedQuantity: number;
}

export interface DashboardSummary {
  totalProducts: number;
  totalWarehouses: number;
  lowStockCount: number;
  pendingOrdersCount: number;
}

export interface StockValueReport {
  warehouseId: number;
  warehouseName: string;
  warehouseCode: string;
  totalValue: number;
}

export interface ProductMovementSummary {
  productId: number;
  productName: string;
  sku: string;
  totalIn: number;
  totalOut: number;
  netChange: number;
}

export interface LowStockAlert {
  productId: number;
  productName: string;
  sku: string;
  warehouseId: number;
  warehouseName: string;
  currentQuantity: number;
  minQuantity: number;
}

export interface ForecastPrediction {
  date: string;
  predictedQuantity: number;
  confidenceLower: number;
  confidenceUpper: number;
}

export interface ForecastResponse {
  productId: number;
  predictions: ForecastPrediction[];
  modelAccuracy: number;
}

export interface ReorderSuggestion {
  productId: number;
  reorderPoint: number;
  reorderQuantity: number;
  safetyStock: number;
  economicOrderQuantity: number;
  estimatedAnnualSavings: number;
}

export interface InsightResponse {
  insights: string;
  recommendations: string[];
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
