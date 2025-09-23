package com.ice.bond.practiee.portfolio_risk_analyzer.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BondDurationCalculatorTest {
    @Test
    void calculateMacaulayDuration_returnsZero_whenYearsToMaturityIsZero() {
        double duration = BondDurationCalculator.calculateMacaulayDuration(1000.0, 0.05, 0, 0.04);
        assertThat(duration).isEqualTo(0.0);
    }

    @Test
    void calculateMacaulayDuration_returnsExpectedValue_forTypicalBond() {
        double duration = BondDurationCalculator.calculateMacaulayDuration(1000.0, 0.05, 5, 0.04);
        assertThat(duration).isGreaterThan(0.0);
    }

    @Test
    void calculateMacaulayDuration_handlesZeroCouponRate() {
        double duration = BondDurationCalculator.calculateMacaulayDuration(1000.0, 0.0, 5, 0.04);
        assertThat(duration).isGreaterThan(0.0);
    }

    @Test
    void calculateMacaulayDuration_handlesNegativeYtm() {
        double duration = BondDurationCalculator.calculateMacaulayDuration(1000.0, 0.05, 5, -0.01);
        assertThat(duration).isGreaterThan(0.0);
    }

    @Test
    void calculateModifiedDuration_returnsExpectedValue_forPositiveInputs() {
        double modDuration = BondDurationCalculator.calculateModifiedDuration(4.5, 0.05);
        assertThat(modDuration).isEqualTo(4.29);
    }

    @Test
    void calculateModifiedDuration_handlesZeroYtm() {
        double modDuration = BondDurationCalculator.calculateModifiedDuration(4.5, 0.0);
        assertThat(modDuration).isEqualTo(4.5);
    }

    @Test
    void calculateModifiedDuration_handlesZeroMacaulayDuration() {
        double modDuration = BondDurationCalculator.calculateModifiedDuration(0.0, 0.05);
        assertThat(modDuration).isEqualTo(0.0);
    }

}