import math
from schemas import ReorderSuggestion


class ReorderOptimizer:
    """Calculates optimal reorder points and quantities using EOQ model."""

    # Z-scores for common service levels
    Z_SCORES = {
        0.50: 0.00,
        0.80: 0.84,
        0.85: 1.04,
        0.90: 1.28,
        0.95: 1.65,
        0.97: 1.88,
        0.98: 2.05,
        0.99: 2.33,
    }

    def _get_z_score(self, service_level: float) -> float:
        """Get z-score for given service level, interpolating if necessary."""
        if service_level in self.Z_SCORES:
            return self.Z_SCORES[service_level]

        levels = sorted(self.Z_SCORES.keys())
        for i in range(len(levels) - 1):
            if levels[i] <= service_level <= levels[i + 1]:
                ratio = (service_level - levels[i]) / (levels[i + 1] - levels[i])
                return self.Z_SCORES[levels[i]] + ratio * (
                    self.Z_SCORES[levels[i + 1]] - self.Z_SCORES[levels[i]]
                )
        return 1.65  # Default to 95% service level

    def calculate_safety_stock(
        self,
        demand_std_dev: float,
        lead_time_days: int,
        service_level: float
    ) -> int:
        """Calculate safety stock based on demand variability and service level."""
        z_score = self._get_z_score(service_level)
        safety_stock = z_score * demand_std_dev * math.sqrt(lead_time_days)
        return max(0, math.ceil(safety_stock))

    def calculate_reorder_point(
        self,
        average_demand: float,
        lead_time_days: int,
        safety_stock: int
    ) -> int:
        """Calculate reorder point: average demand during lead time + safety stock."""
        demand_during_lead_time = average_demand * lead_time_days
        return math.ceil(demand_during_lead_time + safety_stock)

    def calculate_eoq(
        self,
        annual_demand: float,
        ordering_cost: float,
        holding_cost_per_unit: float
    ) -> int:
        """Calculate Economic Order Quantity using the Wilson formula.

        EOQ = sqrt(2 * D * S / H)
        where D = annual demand, S = ordering cost, H = holding cost per unit
        """
        if holding_cost_per_unit <= 0 or annual_demand <= 0:
            return 1

        eoq = math.sqrt(
            (2 * annual_demand * ordering_cost) / holding_cost_per_unit
        )
        return max(1, math.ceil(eoq))

    def optimize(
        self,
        product_id: int,
        average_daily_demand: float,
        demand_std_dev: float,
        lead_time_days: int,
        ordering_cost: float,
        holding_cost_per_unit: float,
        unit_price: float,
        service_level: float = 0.95
    ) -> ReorderSuggestion:
        """Calculate optimal reorder parameters and estimated savings."""
        annual_demand = average_daily_demand * 365

        safety_stock = self.calculate_safety_stock(
            demand_std_dev, lead_time_days, service_level
        )

        reorder_point = self.calculate_reorder_point(
            average_daily_demand, lead_time_days, safety_stock
        )

        eoq = self.calculate_eoq(
            annual_demand, ordering_cost, holding_cost_per_unit
        )

        # Estimate annual savings compared to ordering monthly
        monthly_order_qty = annual_demand / 12
        monthly_ordering_cost = 12 * ordering_cost
        monthly_holding_cost = (monthly_order_qty / 2) * holding_cost_per_unit

        eoq_ordering_cost = (annual_demand / eoq) * ordering_cost
        eoq_holding_cost = (eoq / 2) * holding_cost_per_unit

        baseline_cost = monthly_ordering_cost + monthly_holding_cost
        optimized_cost = eoq_ordering_cost + eoq_holding_cost
        estimated_savings = max(0, baseline_cost - optimized_cost)

        return ReorderSuggestion(
            product_id=product_id,
            reorder_point=reorder_point,
            reorder_quantity=eoq,
            safety_stock=safety_stock,
            economic_order_quantity=eoq,
            estimated_annual_savings=round(estimated_savings, 2)
        )
