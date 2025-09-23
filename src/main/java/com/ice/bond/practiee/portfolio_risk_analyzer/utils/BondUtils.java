package com.ice.bond.practiee.portfolio_risk_analyzer.utils;

import java.time.LocalDate;
import java.time.Period;

public class BondUtils {
    public static int calculateYearsToMaturity(LocalDate maturityDate) {
        LocalDate today = LocalDate.now();
        if (maturityDate.isBefore(today)) {
            throw new IllegalArgumentException("Maturity date is in the past!");
        }
        return Period.between(today, maturityDate).getYears();
    }
}
