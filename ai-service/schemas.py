from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date


class HistoricalDataPoint(BaseModel):
    date: date
    quantity: float


class ForecastRequest(BaseModel):
    product_id: int
    historical_data: List[HistoricalDataPoint]
    periods_ahead: int = Field(default=30, ge=1, le=365)


class ForecastPrediction(BaseModel):
    date: date
    predicted_quantity: float
    confidence_lower: float
    confidence_upper: float


class ForecastResponse(BaseModel):
    product_id: int
    predictions: List[ForecastPrediction]
    model_accuracy: Optional[float] = None


class ReorderRequest(BaseModel):
    product_id: int
    average_daily_demand: float
    demand_std_dev: float
    lead_time_days: int
    ordering_cost: float
    holding_cost_per_unit: float
    unit_price: float
    service_level: float = Field(default=0.95, ge=0.5, le=0.99)


class ReorderSuggestion(BaseModel):
    product_id: int
    reorder_point: int
    reorder_quantity: int
    safety_stock: int
    economic_order_quantity: int
    estimated_annual_savings: float


class InsightRequest(BaseModel):
    stock_data: dict
    question: Optional[str] = None


class InsightResponse(BaseModel):
    insights: str
    recommendations: List[str]
