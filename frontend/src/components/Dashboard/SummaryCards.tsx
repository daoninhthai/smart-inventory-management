import React from 'react';
import {
  CubeIcon,
  BuildingStorefrontIcon,
  ExclamationTriangleIcon,
  ClipboardDocumentListIcon,
} from '@heroicons/react/24/outline';
import { DashboardSummary } from '../../types';

interface SummaryCardsProps {
  summary: DashboardSummary | null;
  loading: boolean;
}

const SummaryCards: React.FC<SummaryCardsProps> = ({ summary, loading }) => {
  const cards = [
    {
      name: 'Total Products',
      value: summary?.totalProducts ?? 0,
      icon: CubeIcon,
      color: 'bg-blue-500',
      bgLight: 'bg-blue-50',
      textColor: 'text-blue-600',
    },
    {
      name: 'Warehouses',
      value: summary?.totalWarehouses ?? 0,
      icon: BuildingStorefrontIcon,
      color: 'bg-green-500',
      bgLight: 'bg-green-50',
      textColor: 'text-green-600',
    },
    {
      name: 'Low Stock Alerts',
      value: summary?.lowStockCount ?? 0,
      icon: ExclamationTriangleIcon,
      color: 'bg-red-500',
      bgLight: 'bg-red-50',
      textColor: 'text-red-600',
    },
    {
      name: 'Pending Orders',
      value: summary?.pendingOrdersCount ?? 0,
      icon: ClipboardDocumentListIcon,
      color: 'bg-amber-500',
      bgLight: 'bg-amber-50',
      textColor: 'text-amber-600',
    },
  ];

  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="card animate-pulse">
            <div className="h-16 bg-gray-200 rounded" />
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
      {cards.map((card) => (
        <div key={card.name} className="card hover:shadow-md transition-shadow">
          <div className="flex items-center">
            <div className={`${card.bgLight} p-3 rounded-lg`}>
              <card.icon className={`h-6 w-6 ${card.textColor}`} />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">{card.name}</p>
              <p className={`text-2xl font-bold ${card.name === 'Low Stock Alerts' && card.value > 0 ? 'text-red-600' : 'text-gray-900'}`}>
                {card.value.toLocaleString()}
              </p>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default SummaryCards;
