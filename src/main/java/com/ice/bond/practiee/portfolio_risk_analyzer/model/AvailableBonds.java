package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class AvailableBonds {
    private List<Bond> availableBonds;

    public AvailableBonds() {
        this.availableBonds = new ArrayList<>();

    }
    public void add(Bond bond) {
        this.availableBonds.add(bond);
    }
}
