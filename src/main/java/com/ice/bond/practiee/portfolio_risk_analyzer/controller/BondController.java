package com.ice.bond.practiee.portfolio_risk_analyzer.controller;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.BondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing bond-related operations.
 * <p>
 * This controller provides an endpoint to fetch all available bonds.
 * </p>
 */
@RestController
public class BondController {

    @Autowired
    private BondService bondService;
    /**
     * Retrieves a list of all available bonds.
     *
     * @return an `AvailableBonds` object containing the list of bonds
     */
    @GetMapping("/api/bonds")
    public AvailableBonds getAllBonds() {
        System.out.println("### Fetching all available bonds");
        return bondService.getAvailableBonds();
    }
}
