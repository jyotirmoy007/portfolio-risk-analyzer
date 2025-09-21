package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.ice.bond.practiee.portfolio_risk_analyzer.controller.PortfolioController;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.BondAddRequest;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.BondAddResponse;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.CreatePortfolioRequest;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.CreatePortfolioResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PortfolioServiceTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createPortfolioWithValidRequestReturnsPortfolioId() {
        CreatePortfolioRequest request =  CreatePortfolioRequest.builder().build();
        request.setCustomerName("John Doe");
        String portfolioId = "67890";
        when(portfolioService.createPortfolio(request)).thenReturn(portfolioId);

        CreatePortfolioResponse response = portfolioController.createPortfolio(request);

        assertEquals(portfolioId, response.getPortfolioId());
        verify(portfolioService, times(1)).createPortfolio(request);
    }

    @Test
    void addBondToPortfolioWithValidDataReturnsSuccess() {
        BondAddRequest request =  BondAddRequest.builder().build();
        request.setPortfolioId("12345");
        request.setIsin("US1234567890");
        request.setQuantity(10);

        when(portfolioService.addBondToPortfolio(request.getPortfolioId(), request.getIsin(), request.getQuantity()))
                .thenReturn(true);

        BondAddResponse response = portfolioController.addBondToPortfolio(request);

        assertEquals("Success", response.getStatus());
        verify(portfolioService, times(1))
                .addBondToPortfolio(request.getPortfolioId(), request.getIsin(), request.getQuantity());
    }

    @Test
    void addBondToPortfolioWithInvalidPortfolioIdThrowsException() {
        BondAddRequest request = BondAddRequest.builder().build();;
        request.setPortfolioId("invalid");
        request.setIsin("US1234567890");
        request.setQuantity(10);

        when(portfolioService.addBondToPortfolio(request.getPortfolioId(), request.getIsin(), request.getQuantity()))
                .thenThrow(new IllegalArgumentException("Portfolio not found"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolioController.addBondToPortfolio(request);
        });

        assertEquals("Portfolio not found", exception.getMessage());
        verify(portfolioService, times(1))
                .addBondToPortfolio(request.getPortfolioId(), request.getIsin(), request.getQuantity());
    }

    @Test
    void createPortfolioWithNullRequestThrowsException() {
        when(portfolioService.createPortfolio(null)).thenThrow(new IllegalArgumentException("Request cannot be null"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolioController.createPortfolio(null);
        });

        assertEquals("Request cannot be null", exception.getMessage());
        verify(portfolioService, times(1)).createPortfolio(null);
    }
}