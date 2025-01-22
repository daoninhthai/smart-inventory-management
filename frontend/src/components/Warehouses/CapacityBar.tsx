import React from 'react';

interface CapacityBarProps {
  current: number;
  max: number;
}

const CapacityBar: React.FC<CapacityBarProps> = ({ current, max }) => {
  const percentage = max > 0 ? Math.min((current / max) * 100, 100) : 0;

  let barColor = 'bg-green-500';
  let textColor = 'text-green-700';
  if (percentage >= 90) {
    barColor = 'bg-red-500';
    textColor = 'text-red-700';
  } else if (percentage >= 70) {
    barColor = 'bg-yellow-500';
    textColor = 'text-yellow-700';
  }

  return (
    <div>
      <div className="flex items-center justify-between text-xs mb-1">
        <span className={`font-medium ${textColor}`}>
          {percentage.toFixed(0)}% used
        </span>
        <span className="text-gray-500">
          {current.toLocaleString()} / {max.toLocaleString()} items
        </span>
      </div>
      <div className="w-full bg-gray-200 rounded-full h-2.5">
        <div
          className={`h-2.5 rounded-full transition-all duration-300 ${barColor}`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
};

export default CapacityBar;
