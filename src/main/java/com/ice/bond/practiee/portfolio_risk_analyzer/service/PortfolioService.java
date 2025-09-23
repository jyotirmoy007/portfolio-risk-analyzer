package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.github.benmanes.caffeine.cache.Cache;
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


    @Autowired
    public PortfolioService(BondService bondService, YieldToMaturityCalculator yieldToMaturityCalculator,
                            Cache<String, Double> durationCache) {
        this.portfolios = new ArrayList<>();
        this.bondService = bondService;
        this.yieldToMaturityCalculator = yieldToMaturityCalculator;
        this.durationCache = durationCache;
    }

    public boolean addBondToPortfolio(String portfolioId, String isin, int quantity) {
        // Logic to add bond to portfolio
        Portfolio portfolio = lookupPortfolioById(portfolioId);
        portfolio.addBond(isin, quantity);
        System.out.println("### Adding bond " + isin + " with quantity " + quantity + " to portfolio");
        return true; // Assume success for this example
    }

    public String createPortfolio(CreatePortfolioRequest request) {
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCustomerName(request.getCustomerName());

        portfolios.add(newPortfolio);

        return newPortfolio.getId();
    }



    public PortfolioDetailsResponse getPortfolioDetails(String portfolioId) {
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

    private Portfolio lookupPortfolioById(String portfolioId) {
        return    portfolios.stream()
                .filter(p -> p.getId().equals(portfolioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

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
//             BondDurationCalculator.calculateMacaulayDuration(bond.getFaceValue(),
//                    bond.getCouponRate(), yearsToMaturity, yieldToMaturity);
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
