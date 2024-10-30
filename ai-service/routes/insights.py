import json
from fastapi import APIRouter, HTTPException
from openai import OpenAI
from schemas import InsightRequest, InsightResponse
from config import settings

router = APIRouter(prefix="/api/insights", tags=["Insights"])


@router.post("/analyze", response_model=InsightResponse)
async def analyze_inventory(request: InsightRequest):
    """Analyze inventory data using GPT-4 and return actionable insights."""
    try:
        stock_summary = json.dumps(request.stock_data, indent=2, default=str)

        system_prompt = """You are an expert inventory management analyst.
Analyze the provided stock data and generate actionable insights.
Focus on:
1. Stock health and balance across warehouses
2. Products that need attention (low stock, overstocking)
3. Trends and patterns in inventory movement
4. Cost optimization opportunities
5. Demand patterns and seasonality

Provide your response as:
- A detailed analysis paragraph (insights)
- A list of specific, actionable recommendations

Be concise but thorough. Use data from the provided information to support your analysis."""

        user_prompt = f"Inventory Data:\n{stock_summary}"
        if request.question:
            user_prompt += f"\n\nSpecific Question: {request.question}"

        if not settings.OPENAI_API_KEY:
            return _generate_fallback_insights(request.stock_data)

        client = OpenAI(api_key=settings.OPENAI_API_KEY)

        response = client.chat.completions.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            temperature=0.7,
            max_tokens=1500
        )

        content = response.choices[0].message.content

        parts = content.split("Recommendations:", 1)
        if len(parts) == 2:
            insights_text = parts[0].replace("Analysis:", "").strip()
            recommendations_text = parts[1].strip()
            recommendations = [
                r.strip().lstrip("- ").lstrip("0123456789.").strip()
                for r in recommendations_text.split("\n")
                if r.strip() and len(r.strip()) > 5
            ]
        else:
            insights_text = content
            recommendations = [
                "Review low-stock items and consider reordering",
                "Analyze slow-moving inventory for potential markdowns",
                "Optimize warehouse distribution to reduce transfer costs"
            ]

        return InsightResponse(
            insights=insights_text,
            recommendations=recommendations[:8]
        )

    except Exception as e:
        if "api_key" in str(e).lower() or "authentication" in str(e).lower():
            return _generate_fallback_insights(request.stock_data)
        raise HTTPException(status_code=500, detail=f"AI analysis error: {str(e)}")


def _generate_fallback_insights(stock_data: dict) -> InsightResponse:
    """Generate basic insights without AI when API key is not available."""
    total_items = stock_data.get("total_products", 0)
    low_stock = stock_data.get("low_stock_count", 0)
    warehouses = stock_data.get("total_warehouses", 0)

    insights = (
        f"Your inventory currently consists of {total_items} active products "
        f"distributed across {warehouses} warehouses. "
        f"There are {low_stock} items flagged as low stock that require immediate attention. "
        f"Consider reviewing reorder points and safety stock levels to prevent stockouts."
    )

    recommendations = [
        "Review and replenish low-stock items immediately to prevent stockouts",
        "Analyze demand patterns to optimize reorder quantities",
        "Consider redistributing stock across warehouses for better coverage",
        "Set up automated alerts for critical stock levels",
        "Review supplier lead times and adjust safety stock accordingly"
    ]

    return InsightResponse(insights=insights, recommendations=recommendations)
