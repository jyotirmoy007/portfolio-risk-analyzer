package com.ice.bond.practiee.portfolio_risk_analyzer.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BondUtilsTest {

    @Test
    void calculateYearsToMaturity_returnsZero_whenMaturityDateIsToday() {
        int years = BondUtils.calculateYearsToMaturity(java.time.LocalDate.now());
        assertThat(years).isEqualTo(0);
    }

    @Test
    void calculateYearsToMaturity_returnsCorrectYears_whenMaturityDateIsInFuture() {
        int years = BondUtils.calculateYearsToMaturity(java.time.LocalDate.now().plusYears(5));
        assertThat(years).isEqualTo(5);
    }

    @Test
    void calculateYearsToMaturity_throwsException_whenMaturityDateIsInPast() {
        assertThatThrownBy(() ->
                        BondUtils.calculateYearsToMaturity(java.time.LocalDate.now().minusDays(1))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maturity date is in the past");
    }

}