import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression
from datetime import date, timedelta
from typing import List, Tuple

from schemas import HistoricalDataPoint, ForecastPrediction


class DemandForecaster:
    """Demand forecasting model using Linear Regression with time-based features."""

    def __init__(self):
        self.model = LinearRegression()
        self.is_fitted = False
        self.residual_std = 0.0

    def _create_features(self, dates: List[date]) -> np.ndarray:
        """Create feature matrix from dates: day_of_week, month, day_of_year, trend."""
        features = []
        base_date = dates[0] if dates else date.today()

        for d in dates:
            day_of_week = d.weekday()
            month = d.month
            day_of_year = d.timetuple().tm_yday
            trend = (d - base_date).days

            dow_sin = np.sin(2 * np.pi * day_of_week / 7)
            dow_cos = np.cos(2 * np.pi * day_of_week / 7)
            month_sin = np.sin(2 * np.pi * month / 12)
            month_cos = np.cos(2 * np.pi * month / 12)

            features.append([
                trend,
                dow_sin,
                dow_cos,
                month_sin,
                month_cos,
                day_of_year
            ])

        return np.array(features)

    def fit(self, historical_data: List[HistoricalDataPoint]) -> float:
        """Train the model on historical data. Returns R-squared score."""
        if len(historical_data) < 7:
            raise ValueError("At least 7 data points are required for forecasting")

        dates = [dp.date for dp in historical_data]
        quantities = np.array([dp.quantity for dp in historical_data])

        X = self._create_features(dates)
        y = quantities

        self.model.fit(X, y)
        self.is_fitted = True

        predictions = self.model.predict(X)
        residuals = y - predictions
        self.residual_std = float(np.std(residuals))

        ss_res = np.sum(residuals ** 2)
        ss_tot = np.sum((y - np.mean(y)) ** 2)
        r_squared = 1 - (ss_res / ss_tot) if ss_tot != 0 else 0.0

        self._base_date = dates[0]

        return max(0.0, min(1.0, r_squared))

    def predict(self, periods_ahead: int,
                last_date: date) -> List[ForecastPrediction]:
        """Generate predictions with confidence intervals."""
        if not self.is_fitted:
            raise ValueError("Model must be fitted before making predictions")

        future_dates = [last_date + timedelta(days=i + 1) for i in range(periods_ahead)]

        X_future = self._create_features(future_dates)

        for i, row in enumerate(X_future):
            row[0] = (future_dates[i] - self._base_date).days

        predicted = self.model.predict(X_future)

        predictions = []
        for i, d in enumerate(future_dates):
            pred_qty = max(0, float(predicted[i]))

            confidence_multiplier = 1.96
            uncertainty_growth = 1 + (i * 0.02)
            margin = self.residual_std * confidence_multiplier * uncertainty_growth

            predictions.append(ForecastPrediction(
                date=d,
                predicted_quantity=round(pred_qty, 2),
                confidence_lower=round(max(0, pred_qty - margin), 2),
                confidence_upper=round(pred_qty + margin, 2)
            ))

        return predictions
