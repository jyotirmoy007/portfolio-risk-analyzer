package com.ice.bond.practiee.portfolio_risk_analyzer.controller;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController {

    private PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/api/portfolio")
    public CreatePortfolioResponse createPortfolio(@RequestBody CreatePortfolioRequest request) {
        String portfolioId = portfolioService.createPortfolio(request);
        System.out.println("### Created portfolio with ID: " + request.getCustomerName());
        return CreatePortfolioResponse.builder().portfolioId(portfolioId).build();
    }

    @PostMapping("/api/portfolio/bond")
    public BondAddResponse addBondToPortfolio(@RequestBody BondAddRequest request) {
        System.out.println("### Received request to add bond to portfolio: " + request.getPortfolioId());
        System.out.println("### Received request to add bond to bond: " + request.getIsin());
        System.out.println("### Received request to add bond to portfolio: " + request.getQuantity());
        portfolioService.addBondToPortfolio(request.getPortfolioId(),
                request.getIsin(), request.getQuantity());
        return BondAddResponse.builder().status("Success").build();
    }

    @GetMapping("/api/portfolio/{portfolioId}")
    public PortfolioDetailsResponse retrievePortfolio(@PathVariable String portfolioId) {
        return portfolioService.getPortfolioDetails(portfolioId);
    }

}
