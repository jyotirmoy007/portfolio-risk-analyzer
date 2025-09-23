package com.ice.bond.practiee.portfolio_risk_analyzer.controller;

import com.ice.bond.practiee.portfolio_risk_analyzer.exception.PortfolioNotFoundException;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing portfolio-related operations.
 * <p>
 * This controller provides endpoints for creating portfolios, adding bonds to portfolios,
 * and retrieving portfolio details.
 * </p>
 */
@RestController
public class PortfolioController {

    private PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Creates a new portfolio based on the provided request.
     *
     * @param request the request containing portfolio creation details
     * @return a response containing the ID of the created portfolio
     */
    @PostMapping("/api/portfolio")
    public CreatePortfolioResponse createPortfolio(@RequestBody CreatePortfolioRequest request) {
        String portfolioId = portfolioService.createPortfolio(request);
        System.out.println("### Created portfolio with ID: " + request.getCustomerName());
        return CreatePortfolioResponse.builder().portfolioId(portfolioId).build();
    }

    /**
     * Adds a bond to an existing portfolio based on the provided request.
     *
     * @param request the request containing bond addition details
     * @return a response indicating the success of the operation
     */
    @PostMapping("/api/portfolio/bond")
    public BondAddResponse addBondToPortfolio(@RequestBody BondAddRequest request) throws PortfolioNotFoundException {
        System.out.println("### Received request to add bond to portfolio: " + request.getPortfolioId()
                + " with quantity: " + request.getQuantity() + " and ISIN: " + request.getIsin());

        portfolioService.addBondToPortfolio(request.getPortfolioId(),
                request.getIsin(), request.getQuantity());
        return BondAddResponse.builder().status("Success").build();
    }
    /**
     * Retrieves the details of a portfolio by its ID.
     *
     * @param portfolioId the ID of the portfolio to retrieve
     * @return a response containing the portfolio details
     */
    @GetMapping("/api/portfolio/{portfolioId}")
    public PortfolioDetailsResponse retrievePortfolio(@PathVariable String portfolioId)
            throws PortfolioNotFoundException {
        return portfolioService.getPortfolioDetails(portfolioId);
    }

}
