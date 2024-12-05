import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { StockTrend } from '../../api/dashboard';

interface StockTrendChartProps {
  data: StockTrend[];
  loading: boolean;
}

const StockTrendChart: React.FC<StockTrendChartProps> = ({ data, loading }) => {
  if (loading) {
    return (
      <div className="card">
        <div className="h-64 bg-gray-100 rounded animate-pulse" />
      </div>
    );
  }

  const chartData = data.map((item) => ({
    date: item.date,
    'Stock In': item.totalIn,
    'Stock Out': item.totalOut,
  }));

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Stock Movement Trends</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis dataKey="date" tick={{ fontSize: 11 }} />
          <YAxis tick={{ fontSize: 12 }} />
          <Tooltip
            contentStyle={{ borderRadius: '8px', border: '1px solid #e5e7eb' }}
          />
          <Legend />
          <Line
            type="monotone"
            dataKey="Stock In"
            stroke="#3b82f6"
            strokeWidth={2}
            dot={{ r: 3 }}
            activeDot={{ r: 5 }}
          />
          <Line
            type="monotone"
            dataKey="Stock Out"
            stroke="#ef4444"
            strokeWidth={2}
            dot={{ r: 3 }}
            activeDot={{ r: 5 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default StockTrendChart;
