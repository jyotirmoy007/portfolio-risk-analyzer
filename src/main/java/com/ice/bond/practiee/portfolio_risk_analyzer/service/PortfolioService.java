package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private List<Portfolio> portfolios;
    private BondService bondService;

    @Autowired
    public PortfolioService(BondService bondService) {
        this.portfolios = new ArrayList<>();
        this.bondService = bondService;
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
        List<BondDTO> bondDTOs = transformToBondDTO(portfolio.getBondQuantities());
//        .stream()
//                .map(bond -> BondDTO.builder()
//                        .isin(bond.getIsin())
//                        .name(bond.getName())
//                        .quantity(bond.getQuantity())
//                        .price(bond.getPrice())
//                        .build())
//                .toList();
        return null;
    }

    private Portfolio lookupPortfolioById(String portfolioId) {
        return    portfolios.stream()
                .filter(p -> p.getId().equals(portfolioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    private List<BondDTO> transformToBondDTO(Map<String, Integer> bondQuantities) {
        List<BondDTO> bondDtos = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bondQuantities.entrySet()) {
            String isin = entry.getKey();
            int quantity = entry.getValue();

            Bond bond = bondService.looupBondByIsin(isin);



            BondDTO bondDto = BondDTO.builder()
                    .isin(isin)
                    .quantity(quantity)
                    .build();
            bondDto.setIsin(isin);
            bondDto.setQuantity(quantity);
            bondDtos.add(bondDto); // In a real scenario, you would fetch bond details from a database or another service

        }
        return bondDtos;
    }


}
