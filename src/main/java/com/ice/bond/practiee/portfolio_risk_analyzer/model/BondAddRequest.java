package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BondAddRequest {
    private String portfolioId;
    private String isin;
    private Integer quantity;
}
