import React from 'react';
import {
  LineChart,
  Line,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  ComposedChart,
} from 'recharts';
import { ForecastPrediction } from '../../types';

interface DemandForecastChartProps {
  historicalData: { date: string; quantity: number }[];
  predictions: ForecastPrediction[];
  loading: boolean;
}

const DemandForecastChart: React.FC<DemandForecastChartProps> = ({
  historicalData,
  predictions,
  loading,
}) => {
  if (loading) {
    return (
      <div className="card">
        <div className="h-80 bg-gray-100 rounded animate-pulse" />
      </div>
    );
  }

  const chartData = [
    ...historicalData.map((d) => ({
      date: d.date,
      actual: d.quantity,
      predicted: null as number | null,
      confidenceLower: null as number | null,
      confidenceUpper: null as number | null,
    })),
    ...predictions.map((p) => ({
      date: p.date,
      actual: null as number | null,
      predicted: p.predictedQuantity,
      confidenceLower: p.confidenceLower,
      confidenceUpper: p.confidenceUpper,
    })),
  ];

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Demand Forecast</h3>
      {chartData.length === 0 ? (
        <div className="h-80 flex items-center justify-center text-gray-400">
          Select a product to view demand forecast
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={350}>
          <ComposedChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="date" tick={{ fontSize: 10 }} interval="preserveStartEnd" />
            <YAxis tick={{ fontSize: 12 }} />
            <Tooltip
              contentStyle={{ borderRadius: '8px', border: '1px solid #e5e7eb' }}
            />
            <Legend />
            <Area
              type="monotone"
              dataKey="confidenceUpper"
              stroke="none"
              fill="#9ca3af"
              fillOpacity={0.15}
              name="Confidence Band"
            />
            <Area
              type="monotone"
              dataKey="confidenceLower"
              stroke="none"
              fill="#ffffff"
              fillOpacity={1}
              name=""
            />
            <Line
              type="monotone"
              dataKey="actual"
              stroke="#3b82f6"
              strokeWidth={2}
              dot={{ r: 2 }}
              name="Historical"
              connectNulls={false}
            />
            <Line
              type="monotone"
              dataKey="predicted"
              stroke="#f97316"
              strokeWidth={2}
              strokeDasharray="5 5"
              dot={{ r: 2 }}
              name="Predicted"
              connectNulls={false}
            />
          </ComposedChart>
        </ResponsiveContainer>
      )}
    </div>
  );
};

export default DemandForecastChart;
