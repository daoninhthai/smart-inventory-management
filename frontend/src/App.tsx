import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/Layout/MainLayout';
import LoginPage from './pages/LoginPage';

// Lazy load pages
const DashboardPage = React.lazy(() => import('./pages/DashboardPage'));
const ProductsPage = React.lazy(() => import('./pages/ProductsPage'));
const ProductDetailPage = React.lazy(() => import('./pages/ProductDetailPage'));
const WarehousesPage = React.lazy(() => import('./pages/WarehousesPage'));
const WarehouseDetailPage = React.lazy(() => import('./pages/WarehouseDetailPage'));
const StockPage = React.lazy(() => import('./pages/StockPage'));
const PurchaseOrdersPage = React.lazy(() => import('./pages/PurchaseOrdersPage'));
const PurchaseOrderDetailPage = React.lazy(() => import('./pages/PurchaseOrderDetailPage'));
const SuppliersPage = React.lazy(() => import('./pages/SuppliersPage'));
const AIInsightsPage = React.lazy(() => import('./pages/AIInsightsPage'));

const PageLoader = () => (
  <div className="flex items-center justify-center h-64">
    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
  </div>
);

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <React.Suspense fallback={<PageLoader />}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <MainLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<DashboardPage />} />
              <Route path="products" element={<ProductsPage />} />
              <Route path="products/:id" element={<ProductDetailPage />} />
              <Route path="warehouses" element={<WarehousesPage />} />
              <Route path="warehouses/:id" element={<WarehouseDetailPage />} />
              <Route path="stock" element={<StockPage />} />
              <Route path="orders" element={<PurchaseOrdersPage />} />
              <Route path="orders/:id" element={<PurchaseOrderDetailPage />} />
              <Route path="suppliers" element={<SuppliersPage />} />
              <Route path="ai-insights" element={<AIInsightsPage />} />
            </Route>
          </Routes>
        </React.Suspense>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
