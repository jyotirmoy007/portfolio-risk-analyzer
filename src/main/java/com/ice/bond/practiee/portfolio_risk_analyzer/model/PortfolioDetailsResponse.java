package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioDetailsResponse {
    private String id;
    private String customerName;
    //private List<Bond> bonds;
    List<BondDTO> bonds; // ISIN to quantity mapping
    private Double totalMarketValue;
    private Double weightedAvgDuration;
    private String errorMessage;

}
