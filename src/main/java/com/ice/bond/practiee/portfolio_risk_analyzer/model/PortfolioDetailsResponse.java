package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class PortfolioDetailsResponse {
    private String id;
    private String customerName;
    //private List<Bond> bonds;
    List<BondDTO> bonds; // ISIN to quantity mapping
    private double totalMarketValue;
    private double totalFaceValue;
    private double averageCouponRate;
    private double duration;
    private double convexity;
}
