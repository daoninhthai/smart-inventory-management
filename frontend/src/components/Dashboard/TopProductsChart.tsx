import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { ProductMovementSummary } from '../../types';

interface TopProductsChartProps {
  data: ProductMovementSummary[];
  loading: boolean;
}

const TopProductsChart: React.FC<TopProductsChartProps> = ({ data, loading }) => {
  if (loading) {
    return (
      <div className="card">
        <div className="h-64 bg-gray-100 rounded animate-pulse" />
      </div>
    );
  }

  const chartData = data.map((item) => ({
    name: item.productName.length > 20 ? item.productName.substring(0, 20) + '...' : item.productName,
    movements: item.totalIn + item.totalOut,
    fullName: item.productName,
  }));

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Top Moving Products</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData} layout="vertical" margin={{ top: 5, right: 30, left: 80, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis type="number" tick={{ fontSize: 12 }} />
          <YAxis dataKey="name" type="category" tick={{ fontSize: 11 }} width={80} />
          <Tooltip
            formatter={(value: number, _name: string, props: any) => [
              value.toLocaleString(),
              `Total Movements (${props.payload.fullName})`,
            ]}
            contentStyle={{ borderRadius: '8px', border: '1px solid #e5e7eb' }}
          />
          <Bar dataKey="movements" fill="#10b981" radius={[0, 4, 4, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default TopProductsChart;
