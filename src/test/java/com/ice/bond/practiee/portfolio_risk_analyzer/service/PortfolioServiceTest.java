package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class PortfolioServiceTest {

    @Mock
    private BondService bondService;
    @Mock
    private Cache<String, Double> durationCache;

    @Mock
    private YieldToMaturityCalculator yieldToMaturityCalculator;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePortfolio() {
        CreatePortfolioRequest request = CreatePortfolioRequest.builder().customerName("Alice").build();


        String portfolioId = portfolioService.createPortfolio(request);

        assertThat(portfolioId).isNotNull();
    }

    @Test
    void testAddBondToPortfolio() {
        CreatePortfolioRequest request = CreatePortfolioRequest.builder().customerName("Bob").build();
        String portfolioId = portfolioService.createPortfolio(request);

        boolean result = portfolioService.addBondToPortfolio(portfolioId, "US1234567890", 100);

        assertThat(result).isTrue();
    }

    @Test
    void testGetPortfolioDetails() {
        CreatePortfolioRequest request = CreatePortfolioRequest.builder().customerName("Charlie").build();
        String portfolioId = portfolioService.createPortfolio(request);

        portfolioService.addBondToPortfolio(portfolioId, "US1234567890", 10);

        Bond bond = Bond.builder()
                .isin("US1234567890")
                .issuer("US Treasury")
                .maturityDate(LocalDate.now().plusYears(5))
                .couponRate(0.05)
                .paymentFrequency(2)
                .marketPrice(950.0)
                .faceValue(1000.0)
                .build();

        when(bondService.looupBondByIsin("US1234567890")).thenReturn(bond);
        when(yieldToMaturityCalculator.calculateYtmApproximate(1000.0, 0.05, 5, 950.0))
                .thenReturn(0.055);
        when(durationCache.get(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(4.0);

        PortfolioDetailsResponse response = portfolioService.getPortfolioDetails(portfolioId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(portfolioId);
        assertThat(response.getCustomerName()).isEqualTo("Charlie");
        assertThat(response.getBonds()).hasSize(1);

        BondDTO dto = response.getBonds().get(0);
        assertThat(dto.getIsin()).isEqualTo("US1234567890");
        assertThat(dto.getYieldToMaturity()).isEqualTo(0.055);
        assertThat(response.getTotalMarketValue()).isGreaterThan(0);
    }

    // 🔴 Negative Test 1: portfolio not found
    @Test
    void testAddBondToPortfolio_portfolioNotFound_throwsException() {
        assertThatThrownBy(() ->
                portfolioService.addBondToPortfolio("INVALID", "US1234567890", 10)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Portfolio not found");
    }

    // 🔴 Negative Test 2: get details of non-existing portfolio
    @Test
    void testGetPortfolioDetails_portfolioNotFound_throwsException() {
        assertThatThrownBy(() ->
                portfolioService.getPortfolioDetails("MISSING")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Portfolio not found");
    }

    // 🔴 Negative Test 3: Bond service returns null
    @Test
    void testGetPortfolioDetails_bondNotFound() {
        CreatePortfolioRequest request =  CreatePortfolioRequest.builder().customerName("David").build();
        String portfolioId = portfolioService.createPortfolio(request);

        portfolioService.addBondToPortfolio(portfolioId, "BAD_ISIN", 5);

        when(bondService.looupBondByIsin("BAD_ISIN")).thenReturn(null);

        assertThatThrownBy(() ->
                portfolioService.getPortfolioDetails(portfolioId)
        ).isInstanceOf(NullPointerException.class);
    }
}
