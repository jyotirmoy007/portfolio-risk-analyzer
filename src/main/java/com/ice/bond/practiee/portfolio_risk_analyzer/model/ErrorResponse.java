package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ErrorResponse {
    private String errorMessage;
}
