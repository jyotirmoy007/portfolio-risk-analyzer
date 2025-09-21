package com.ice.bond.practiee.portfolio_risk_analyzer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Getter
@Setter
public class Portfolio {
    private String id;
    private String customerName;
    //private List<Bond> bonds;
    Map<String, Integer> bondQuantities; // ISIN to quantity mapping
    private double totalMarketValue;
    private double totalFaceValue;
    private double averageCouponRate;
    private double duration;
    private double convexity;

    public Portfolio() {
        bondQuantities = new HashMap<>();
        id = UUID.randomUUID().toString();
    }

    public void addBond(String isin, int quantity) {
        bondQuantities.put(isin, bondQuantities.getOrDefault(isin, 0) + quantity);
    }


}
