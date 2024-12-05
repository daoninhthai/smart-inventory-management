import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { StockValueReport } from '../../types';

interface StockValueChartProps {
  data: StockValueReport[];
  loading: boolean;
}

const StockValueChart: React.FC<StockValueChartProps> = ({ data, loading }) => {
  if (loading) {
    return (
      <div className="card">
        <div className="h-64 bg-gray-100 rounded animate-pulse" />
      </div>
    );
  }

  const chartData = data.map((item) => ({
    name: item.warehouseName,
    value: Number(item.totalValue),
  }));

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Stock Value by Warehouse</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis dataKey="name" tick={{ fontSize: 12 }} />
          <YAxis
            tick={{ fontSize: 12 }}
            tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`}
          />
          <Tooltip
            formatter={(value: number) => [`$${value.toLocaleString()}`, 'Total Value']}
            contentStyle={{ borderRadius: '8px', border: '1px solid #e5e7eb' }}
          />
          <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default StockValueChart;
