package com.ice.bond.practiee.portfolio_risk_analyzer;

import com.ice.bond.practiee.portfolio_risk_analyzer.controller.PortfolioController;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.BondAddRequest;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.BondAddResponse;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.CreatePortfolioRequest;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.CreatePortfolioResponse;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class PortfolioRiskAnalyzerApplicationTests {
	@Mock
	private PortfolioService portfolioService;

	@InjectMocks
	private PortfolioController portfolioController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createPortfolioReturnsValidResponse() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder().build();
		String portfolioId = "12345";
		when(portfolioService.createPortfolio(request)).thenReturn(portfolioId);

		CreatePortfolioResponse response = portfolioController.createPortfolio(request);

		assertEquals(portfolioId, response.getPortfolioId());
		verify(portfolioService, times(1)).createPortfolio(request);
	}

	@Test
	void addBondToPortfolioReturnsSuccessResponse() {
		BondAddRequest request = BondAddRequest.builder().build();

		BondAddResponse response = portfolioController.addBondToPortfolio(request);

		assertEquals("Success", response.getStatus());
	}

}
