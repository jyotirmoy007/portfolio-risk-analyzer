package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class YieldToMaturityCalculator {
    /**
     * Calculates the approximate Yield to Maturity (YTM) for a bond using a simplified formula.
     * The formula is: YTM = [Coupon Payment + ((Face Value - Market Price) / Years to Maturity)] / [(Face Value + Market Price) / 2]
     *
     * @param faceValue The face or par value of the bond.
     * @param couponRate The annual coupon rate (e.g., 0.05 for 5%).
     * @param yearsToMaturity The number of years until the bond matures.
     * @param marketPrice The current market price of the bond.
     * @return The calculated approximate YTM as a decimal value.
     */
    public double calculateYtmApproximate(double faceValue, double couponRate, int yearsToMaturity, double marketPrice) {
        if (yearsToMaturity <= 0) {
            return couponRate; // If years to maturity is 0, YTM is the coupon rate.
        }

        double annualCouponPayment = faceValue * couponRate;
        double numerator = annualCouponPayment + ((faceValue - marketPrice) / yearsToMaturity);
        double denominator = (faceValue + marketPrice) / 2.0;

        return BigDecimal.valueOf(numerator / denominator)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
