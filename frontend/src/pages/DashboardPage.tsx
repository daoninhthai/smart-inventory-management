import React, { useState, useEffect } from 'react';
import SummaryCards from '../components/Dashboard/SummaryCards';
import StockValueChart from '../components/Dashboard/StockValueChart';
import TopProductsChart from '../components/Dashboard/TopProductsChart';
import StockTrendChart from '../components/Dashboard/StockTrendChart';
import RecentMovements from '../components/Dashboard/RecentMovements';
import {
  getDashboardSummary,
  getStockValue,
  getTopProducts,
  getStockTrends,
  getRecentMovements,
  StockTrend,
} from '../api/dashboard';
import {
  DashboardSummary,
  StockValueReport,
  ProductMovementSummary,
  StockMovement,
} from '../types';

const DashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [stockValue, setStockValue] = useState<StockValueReport[]>([]);
  const [topProducts, setTopProducts] = useState<ProductMovementSummary[]>([]);
  const [trends, setTrends] = useState<StockTrend[]>([]);
  const [movements, setMovements] = useState<StockMovement[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [summaryData, stockData, productsData, trendsData, movementsData] =
          await Promise.allSettled([
            getDashboardSummary(),
            getStockValue(),
            getTopProducts(8),
            getStockTrends(30),
            getRecentMovements(),
          ]);

        if (summaryData.status === 'fulfilled') setSummary(summaryData.value);
        if (stockData.status === 'fulfilled') setStockValue(stockData.value);
        if (productsData.status === 'fulfilled') setTopProducts(productsData.value);
        if (trendsData.status === 'fulfilled') setTrends(trendsData.value);
        if (movementsData.status === 'fulfilled') setMovements(movementsData.value);
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Overview of your inventory management system
        </p>
      </div>

      <SummaryCards summary={summary} loading={loading} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <StockValueChart data={stockValue} loading={loading} />
        <TopProductsChart data={topProducts} loading={loading} />
      </div>

      <StockTrendChart data={trends} loading={loading} />

      <RecentMovements movements={movements} loading={loading} />
    </div>
  );
};

export default DashboardPage;
