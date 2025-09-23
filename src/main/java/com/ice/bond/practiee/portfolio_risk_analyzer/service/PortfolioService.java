package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.ice.bond.practiee.portfolio_risk_analyzer.exception.PortfolioNotFoundException;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import com.ice.bond.practiee.portfolio_risk_analyzer.utils.BondDurationCalculator;
import com.ice.bond.practiee.portfolio_risk_analyzer.utils.BondUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private List<Portfolio> portfolios;
    private BondService bondService;
    private YieldToMaturityCalculator yieldToMaturityCalculator;
    private Cache<String, Double> durationCache;


    /**
     * Constructs a PortfolioService with required dependencies.
     *
     * @param bondService the bond service for bond lookup
     * @param yieldToMaturityCalculator calculator for yield to maturity
     * @param durationCache cache for storing bond duration calculations
     */
    @Autowired
    public PortfolioService(BondService bondService, YieldToMaturityCalculator yieldToMaturityCalculator,
                            Cache<String, Double> durationCache) {
        this.portfolios = new ArrayList<>();
        this.bondService = bondService;
        this.yieldToMaturityCalculator = yieldToMaturityCalculator;
        this.durationCache = durationCache;
    }

    /**
     * Adds a bond to a portfolio by portfolio ID and bond ISIN.
     *
     * @param portfolioId the portfolio identifier
     * @param isin the bond ISIN
     * @param quantity the quantity of the bond to add
     * @return true if the bond was added successfully
     * @throws PortfolioNotFoundException if the portfolio does not exist
     */
    public boolean addBondToPortfolio(String portfolioId, String isin, int quantity)
            throws PortfolioNotFoundException {
        // Logic to add bond to portfolio
        Portfolio portfolio = lookupPortfolioById(portfolioId);
        bondService.looupBondByIsin(isin);
        portfolio.addBond(isin, quantity);
        System.out.println("### Adding bond " + isin + " with quantity " + quantity + " to portfolio");
        return true; // Assume success for this example
    }

    /**
     * Creates a new portfolio for a customer.
     *
     * @param request the portfolio creation request
     * @return the ID of the newly created portfolio
     */
    public String createPortfolio(CreatePortfolioRequest request) {
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCustomerName(request.getCustomerName());

        portfolios.add(newPortfolio);

        return newPortfolio.getId();
    }


    /**
     * Retrieves detailed information about a portfolio.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio details response
     * @throws PortfolioNotFoundException if the portfolio does not exist
     */
    public PortfolioDetailsResponse getPortfolioDetails(String portfolioId) throws PortfolioNotFoundException {
        Portfolio portfolio = lookupPortfolioById(portfolioId);
        List<BondDTO> bondDTOs = transformToBondDTO(portfolio.getBondQuantities(), portfolioId);

        return PortfolioDetailsResponse.builder()
                .id(portfolio.getId())
                .customerName(portfolio.getCustomerName())
                .totalMarketValue(calculateTotalMarketValue(bondDTOs))
                .weightedAvgDuration(calculateWeightedAvgDuration(bondDTOs))
                .bonds(bondDTOs)
                .build();
    }

    /**
     * Calculates the weighted average duration of bonds in a portfolio.
     *
     * @param bonds the list of bond DTOs
     * @return the weighted average duration, rounded to 2 decimal places
     */
    private double calculateWeightedAvgDuration(List<BondDTO> bonds) {
        if (bonds == null || bonds.isEmpty()) {
            return 0.0;
        }

        double weightedSum = 0.0;
        double totalMarketValue = 0.0;

        for (BondDTO bond : bonds) {
            double marketValue = bond.getMarketPrice() * bond.getQuantity();
            weightedSum += bond.getDuration() * marketValue;
            totalMarketValue += marketValue;
        }

        return totalMarketValue == 0.0 ? 0.0 :
                BigDecimal.valueOf(weightedSum / totalMarketValue)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Calculates the total market value of a list of bonds.
     *
     * @param bondDTOs the list of bond DTOs
     * @return the total market value
     */
    private double calculateTotalMarketValue(List<BondDTO> bondDTOs) {
        if (bondDTOs == null || bondDTOs.isEmpty()) {
            return 0.0;
        }

        double totalValue = 0.0;
        for (BondDTO bondDTO : bondDTOs) {
            totalValue += bondDTO.getMarketPrice() * bondDTO.getQuantity();
        }
        return totalValue;
    }

    /**
     * Looks up a portfolio by its ID.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio object
     * @throws PortfolioNotFoundException if the portfolio does not exist
     */
    private Portfolio lookupPortfolioById(String portfolioId) throws PortfolioNotFoundException {
        return    portfolios.stream()
                .filter(p -> p.getId().equals(portfolioId))
                .findFirst()
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
    }

    /**
     * Transforms a map of bond ISINs and quantities into a list of BondDTOs,
     * calculating yield, duration, and other metrics for each bond.
     *
     * @param bondQuantities map of bond ISINs to quantities
     * @param portfolioId the portfolio identifier
     * @return list of BondDTOs
     */
    private List<BondDTO> transformToBondDTO(Map<String, Integer> bondQuantities, String portfolioId) {
        List<BondDTO> bondDtos = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bondQuantities.entrySet()) {
            String isin = entry.getKey();
            int quantity = entry.getValue();

            Bond bond = bondService.looupBondByIsin(isin);
            int yearsToMaturity = BondUtils.calculateYearsToMaturity(bond.getMaturityDate());
            double yieldToMaturity = yieldToMaturityCalculator.calculateYtmApproximate(bond.getFaceValue(),
                    bond.getCouponRate(), yearsToMaturity, bond.getMarketPrice());

            String cacheKey = portfolioId + "-" + isin;
            double duration = durationCache.get(cacheKey, k -> {
                double dur = BondDurationCalculator.calculateMacaulayDuration(bond.getFaceValue(),
                        bond.getCouponRate(), yearsToMaturity, yieldToMaturity);
                System.out.println("### Caching duration for key: " + cacheKey + " Duration: " + dur);
                return dur;
            });
            double modifiedDuration = BondDurationCalculator.calculateModifiedDuration(duration, yieldToMaturity);

            BondDTO bondDto = BondDTO.builder()
                    .isin(isin)
                    .quantity(quantity)
                    .issuer(bond.getIssuer())
                    .maturityDate(bond.getMaturityDate())
                    .couponRate(bond.getCouponRate())
                    .paymentFrequency(bond.getPaymentFrequency())
                    .marketPrice(bond.getMarketPrice())
                    .faceValue(bond.getFaceValue())
                    .yieldToMaturity(yieldToMaturity)
                    .yearsToMaturity(yearsToMaturity)
                    .duration(duration)
                    .modifiedDuration(modifiedDuration)
                    .build();

            bondDtos.add(bondDto);
        }
        return bondDtos;
    }

}
