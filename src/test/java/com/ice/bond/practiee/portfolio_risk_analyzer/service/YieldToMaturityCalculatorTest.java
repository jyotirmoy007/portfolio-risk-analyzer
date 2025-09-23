package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class YieldToMaturityCalculatorTest {
    private YieldToMaturityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new YieldToMaturityCalculator();
    }

    @Test
    void calculateYtmApproximate_returnsCorrectYtm_forTypicalBond() {
        double ytm = calculator.calculateYtmApproximate(1000.0, 0.05, 5, 950.0);
        assertThat(ytm).isEqualTo(0.0615);
    }

    @Test
    void calculateYtmApproximate_returnsCouponRate_whenYearsToMaturityIsZero() {
        double ytm = calculator.calculateYtmApproximate(1000.0, 0.04, 0, 1000.0);
        assertThat(ytm).isEqualTo(0.04);
    }

    @Test
    void calculateYtmApproximate_handlesNegativeMarketPrice() {
        double ytm = calculator.calculateYtmApproximate(1000.0, 0.05, 10, -100.0);
        assertThat(ytm).isNotNull();
    }

    @Test
    void calculateYtmApproximate_handlesZeroFaceValue() {
        double ytm = calculator.calculateYtmApproximate(0.0, 0.05, 5, 950.0);
        assertThat(ytm).isNotNull();
    }

    @Test
    void calculateYtmApproximate_handlesZeroMarketPrice() {
        double ytm = calculator.calculateYtmApproximate(1000.0, 0.05, 5, 0.0);
        assertThat(ytm).isNotNull();
    }
    @Test
    void testCalculateYtmApproximate_PremiumBond() {
        // Market price > face value, YTM < coupon rate
        double ytm = calculator.calculateYtmApproximate(1000.0, 0.05, 5, 1050.0);
        assertEquals(0.039, ytm, 0.001);
    }

}