package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class BondDTO {
    private String isin;
    private String issuer;
    private LocalDate maturityDate;
    private double couponRate; // as a decimal (e.g., 0.05 for 5%)
    private int paymentFrequency;
    private double marketPrice;
    private double faceValue;
    private int quantity;
    private double yieldToMaturity;
    private int yearsToMaturity;
    private double duration;
    private double modifiedDuration;
}
