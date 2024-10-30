from fastapi import APIRouter, HTTPException
from schemas import (
    ForecastRequest, ForecastResponse,
    ReorderRequest, ReorderSuggestion
)
from models.forecasting import DemandForecaster
from models.reorder import ReorderOptimizer

router = APIRouter(prefix="/api/forecast", tags=["Forecast"])


@router.post("/demand", response_model=ForecastResponse)
async def forecast_demand(request: ForecastRequest):
    """Generate demand forecast for a product based on historical data."""
    try:
        if len(request.historical_data) < 7:
            raise HTTPException(
                status_code=400,
                detail="At least 7 historical data points are required"
            )

        forecaster = DemandForecaster()

        sorted_data = sorted(request.historical_data, key=lambda x: x.date)

        accuracy = forecaster.fit(sorted_data)

        last_date = sorted_data[-1].date
        predictions = forecaster.predict(request.periods_ahead, last_date)

        return ForecastResponse(
            product_id=request.product_id,
            predictions=predictions,
            model_accuracy=round(accuracy, 4)
        )
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Forecasting error: {str(e)}")


@router.post("/reorder-suggestion", response_model=ReorderSuggestion)
async def get_reorder_suggestion(request: ReorderRequest):
    """Calculate optimal reorder point and quantity for a product."""
    try:
        optimizer = ReorderOptimizer()

        suggestion = optimizer.optimize(
            product_id=request.product_id,
            average_daily_demand=request.average_daily_demand,
            demand_std_dev=request.demand_std_dev,
            lead_time_days=request.lead_time_days,
            ordering_cost=request.ordering_cost,
            holding_cost_per_unit=request.holding_cost_per_unit,
            unit_price=request.unit_price,
            service_level=request.service_level
        )

        return suggestion
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Reorder optimization error: {str(e)}")
