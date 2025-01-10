import React from 'react';
import { ReorderSuggestion as ReorderSuggestionType } from '../../types';
import {
  ArrowPathIcon,
  ShieldCheckIcon,
  CalculatorIcon,
  CurrencyDollarIcon,
} from '@heroicons/react/24/outline';

interface ReorderSuggestionProps {
  suggestion: ReorderSuggestionType | null;
  loading: boolean;
}

const ReorderSuggestionCard: React.FC<ReorderSuggestionProps> = ({ suggestion, loading }) => {
  if (loading) {
    return (
      <div className="card">
        <div className="space-y-3">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="h-12 bg-gray-100 rounded animate-pulse" />
          ))}
        </div>
      </div>
    );
  }

  if (!suggestion) {
    return (
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Reorder Suggestion</h3>
        <p className="text-sm text-gray-400">Select a product to get reorder suggestions</p>
      </div>
    );
  }

  const metrics = [
    {
      label: 'Reorder Point',
      value: suggestion.reorderPoint.toLocaleString(),
      description: 'Order when stock reaches this level',
      icon: ArrowPathIcon,
      color: 'text-blue-600 bg-blue-50',
    },
    {
      label: 'Reorder Quantity',
      value: suggestion.reorderQuantity.toLocaleString(),
      description: 'Optimal quantity per order',
      icon: CalculatorIcon,
      color: 'text-purple-600 bg-purple-50',
    },
    {
      label: 'Safety Stock',
      value: suggestion.safetyStock.toLocaleString(),
      description: 'Buffer against demand variability',
      icon: ShieldCheckIcon,
      color: 'text-amber-600 bg-amber-50',
    },
    {
      label: 'Economic Order Quantity',
      value: suggestion.economicOrderQuantity.toLocaleString(),
      description: 'Minimizes total inventory cost',
      icon: CalculatorIcon,
      color: 'text-indigo-600 bg-indigo-50',
    },
  ];

  return (
    <div className="card">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Reorder Suggestion</h3>

      <div className="space-y-3">
        {metrics.map((metric) => (
          <div key={metric.label} className="flex items-center p-3 bg-gray-50 rounded-lg">
            <div className={`p-2 rounded-lg ${metric.color}`}>
              <metric.icon className="h-5 w-5" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-700">{metric.label}</p>
              <p className="text-xs text-gray-400">{metric.description}</p>
            </div>
            <span className="text-lg font-bold text-gray-900">{metric.value}</span>
          </div>
        ))}
      </div>

      <div className="mt-4 p-4 bg-green-50 rounded-lg border border-green-200">
        <div className="flex items-center">
          <CurrencyDollarIcon className="h-5 w-5 text-green-600" />
          <span className="ml-2 text-sm font-medium text-green-700">Estimated Annual Savings</span>
        </div>
        <p className="mt-1 text-2xl font-bold text-green-600">
          ${suggestion.estimatedAnnualSavings.toLocaleString(undefined, { minimumFractionDigits: 2 })}
        </p>
      </div>
    </div>
  );
};

export default ReorderSuggestionCard;
