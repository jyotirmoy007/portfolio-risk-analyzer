package com.ice.bond.practiee.portfolio_risk_analyzer.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondDurationCalculator {
    /**
     * Calculates the Macaulay Duration of a bond.
     * The formula is the sum of the present value of each cash flow multiplied by its time,
     * all divided by the bond's market price.
     *
     * @param faceValue The face or par value of the bond.
     * @param couponRate The annual coupon rate (e.g., 0.05 for 5%).
     * @param yearsToMaturity The number of years until the bond matures.
     * @param ytm The Yield to Maturity (YTM) as a decimal.
     * @return The Macaulay Duration.
     */
    public static double calculateMacaulayDuration(double faceValue, double couponRate, int yearsToMaturity, double ytm) {
        if (yearsToMaturity <= 0) {
            return 0;
        }

        double annualCouponPayment = faceValue * couponRate;
        double presentValueSum = 0.0;
        double weightedPresentValueSum = 0.0;

        // Calculate the sum of present values and the weighted sum for each coupon period
        for (int i = 1; i <= yearsToMaturity; i++) {
            double cashFlow = (i == yearsToMaturity) ? (annualCouponPayment + faceValue) : annualCouponPayment;
            double presentValue = cashFlow / Math.pow(1 + ytm, i);
            double weightedPresentValue = presentValue * i;

            presentValueSum += presentValue;
            weightedPresentValueSum += weightedPresentValue;
        }

        // The market price is the sum of all present values of cash flows.
        double marketPrice = presentValueSum;

        return BigDecimal.valueOf(weightedPresentValueSum / marketPrice)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

    }

    /**
     * Calculates the Modified Duration of a bond.
     * The formula is: Modified Duration = Macaulay Duration / (1 + YTM).
     *
     * @param macaulayDuration The Macaulay Duration of the bond.
     * @param ytm The Yield to Maturity (YTM) as a decimal.
     * @return The Modified Duration.
     */
    public static double calculateModifiedDuration(double macaulayDuration, double ytm) {
        return BigDecimal.valueOf(macaulayDuration / (1 + ytm))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
