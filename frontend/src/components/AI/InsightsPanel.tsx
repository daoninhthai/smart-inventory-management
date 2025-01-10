import React, { useState } from 'react';
import { SparklesIcon } from '@heroicons/react/24/outline';
import { InsightResponse } from '../../types';

interface InsightsPanelProps {
  insights: InsightResponse | null;
  loading: boolean;
  onAnalyze: (question?: string) => void;
}

const InsightsPanel: React.FC<InsightsPanelProps> = ({ insights, loading, onAnalyze }) => {
  const [question, setQuestion] = useState('');

  const handleAnalyze = () => {
    onAnalyze(question || undefined);
    setQuestion('');
  };

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900 flex items-center">
          <SparklesIcon className="h-5 w-5 mr-2 text-purple-600" />
          AI Inventory Analysis
        </h3>
      </div>

      <div className="flex space-x-2 mb-4">
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleAnalyze()}
          placeholder="Ask about your inventory (optional)..."
          className="input-field flex-1"
        />
        <button
          onClick={handleAnalyze}
          disabled={loading}
          className="btn-primary whitespace-nowrap disabled:opacity-50"
        >
          {loading ? (
            <span className="flex items-center">
              <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              Analyzing...
            </span>
          ) : (
            'Analyze'
          )}
        </button>
      </div>

      {insights ? (
        <div className="space-y-4">
          <div className="p-4 bg-gray-50 rounded-lg">
            <h4 className="text-sm font-medium text-gray-700 mb-2">Analysis</h4>
            <p className="text-sm text-gray-600 leading-relaxed">{insights.insights}</p>
          </div>

          {insights.recommendations.length > 0 && (
            <div>
              <h4 className="text-sm font-medium text-gray-700 mb-2">Recommendations</h4>
              <ul className="space-y-2">
                {insights.recommendations.map((rec, index) => (
                  <li key={index} className="flex items-start p-2 bg-blue-50 rounded-lg">
                    <span className="flex-shrink-0 w-5 h-5 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-xs font-medium mr-2 mt-0.5">
                      {index + 1}
                    </span>
                    <span className="text-sm text-blue-800">{rec}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ) : !loading ? (
        <div className="text-center py-8 text-gray-400">
          <SparklesIcon className="h-12 w-12 mx-auto mb-3 text-gray-300" />
          <p>Click "Analyze" to get AI-powered insights about your inventory</p>
        </div>
      ) : null}
    </div>
  );
};

export default InsightsPanel;
