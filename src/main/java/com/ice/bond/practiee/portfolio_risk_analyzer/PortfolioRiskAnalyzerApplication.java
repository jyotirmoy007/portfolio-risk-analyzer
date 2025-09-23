package com.ice.bond.practiee.portfolio_risk_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PortfolioRiskAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioRiskAnalyzerApplication.class, args);
	}

}
