package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Builder
public class CreatePortfolioResponse {
    private String portfolioId;
}
