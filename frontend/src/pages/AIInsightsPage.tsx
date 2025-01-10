import React, { useState, useEffect } from 'react';
import { Product, ForecastResponse, ReorderSuggestion, InsightResponse } from '../types';
import { getProducts } from '../api/products';
import { getDemandForecast, getReorderSuggestion, getInsights } from '../api/forecast';
import DemandForecastChart from '../components/AI/DemandForecastChart';
import ReorderSuggestionCard from '../components/AI/ReorderSuggestion';
import InsightsPanel from '../components/AI/InsightsPanel';

const AIInsightsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [forecast, setForecast] = useState<ForecastResponse | null>(null);
  const [reorder, setReorder] = useState<ReorderSuggestion | null>(null);
  const [insights, setInsights] = useState<InsightResponse | null>(null);
  const [forecastLoading, setForecastLoading] = useState(false);
  const [reorderLoading, setReorderLoading] = useState(false);
  const [insightsLoading, setInsightsLoading] = useState(false);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const data = await getProducts(0, 100);
        setProducts(data.content);
      } catch (error) {
        console.error('Failed to fetch products:', error);
      }
    };
    fetchProducts();
  }, []);

  const handleProductSelect = async (productId: number) => {
    setSelectedProductId(productId);
    setForecast(null);
    setReorder(null);

    setForecastLoading(true);
    setReorderLoading(true);

    try {
      const forecastData = await getDemandForecast(productId);
      setForecast(forecastData);
    } catch (error) {
      console.error('Failed to get forecast:', error);
    } finally {
      setForecastLoading(false);
    }

    try {
      const reorderData = await getReorderSuggestion(productId);
      setReorder(reorderData);
    } catch (error) {
      console.error('Failed to get reorder suggestion:', error);
    } finally {
      setReorderLoading(false);
    }
  };

  const handleAnalyze = async (question?: string) => {
    setInsightsLoading(true);
    try {
      const data = await getInsights(question);
      setInsights(data);
    } catch (error) {
      console.error('Failed to get insights:', error);
    } finally {
      setInsightsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">AI Insights</h1>
        <p className="mt-1 text-sm text-gray-500">
          AI-powered demand forecasting and inventory optimization
        </p>
      </div>

      <div className="card">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Select Product for Analysis
        </label>
        <select
          value={selectedProductId || ''}
          onChange={(e) => {
            const id = Number(e.target.value);
            if (id) handleProductSelect(id);
          }}
          className="input-field max-w-md"
        >
          <option value="">Choose a product...</option>
          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.sku} - {product.name}
            </option>
          ))}
        </select>

        {forecast && (
          <div className="mt-2 flex items-center space-x-4 text-sm text-gray-500">
            <span>
              Model Accuracy:{' '}
              <span className={`font-medium ${(forecast.modelAccuracy || 0) > 0.7 ? 'text-green-600' : 'text-amber-600'}`}>
                {((forecast.modelAccuracy || 0) * 100).toFixed(1)}%
              </span>
            </span>
            <span>Predictions: {forecast.predictions.length} days</span>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <DemandForecastChart
            historicalData={[]}
            predictions={forecast?.predictions || []}
            loading={forecastLoading}
          />
        </div>
        <div>
          <ReorderSuggestionCard suggestion={reorder} loading={reorderLoading} />
        </div>
      </div>

      <InsightsPanel
        insights={insights}
        loading={insightsLoading}
        onAnalyze={handleAnalyze}
      />
    </div>
  );
};

export default AIInsightsPage;
