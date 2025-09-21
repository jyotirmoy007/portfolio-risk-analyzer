package com.ice.bond.practiee.portfolio_risk_analyzer.controller;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.Bond;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.BondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BondController {

    @Autowired
    private BondService bondService;

    @GetMapping("/api/bonds")
    public AvailableBonds getAllBonds() {
        System.out.println("### Fetching all available bonds");
        return bondService.getAvailableBonds();
    }
}
