package com.demian.model;

import java.util.List;

public class RuleEnforcer {

    int sizeX;
    int sizeY;
    private RuleSet ruleSet;

    private int alternator;

    public RuleEnforcer(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        alternator = 0;

        ruleSet = RuleSet.GAME_OF_LIFE;
    }

    public int getNextState(Cell cell) {
        int state = -1;
        MooreNeighborhood neighborhood = cell.getNeighborhood();
        switch (ruleSet) {
            case GAME_OF_LIFE -> {
                state = gameOfLife(neighborhood);
            }
            case SIMPLE_GROWTH -> {
                state = simpleGrowth(neighborhood);
            }
            case DIAG_GROWTH -> {
                state = diagGrowth(neighborhood);
            }
            case ALTERNATING -> {
                if (alternator == 0) {
                    state =
                            simpleGrowth(neighborhood);
                } else {
                    state = gameOfLife(neighborhood);
                }
            }
            default -> {
                System.err.println("ERROR: NO RULESET SELECTED");
                System.exit(1);
            }
        }
        return state;
    }

    private int gameOfLife(MooreNeighborhood neighborhood) {
        int neighborsCount = 0;

        neighborsCount +=
        neighborhood.TLstate() +
        neighborhood.TMstate() +
        neighborhood.TRstate() +
        neighborhood.MLstate() +
        neighborhood.MRstate() +
        neighborhood.BLstate() +
        neighborhood.BMstate() +
        neighborhood.BRstate();

        int MM = neighborhood.MMstate();

        if (MM == 0 && neighborsCount == 3)
            return 1;

        if (MM == 1 && (neighborsCount == 2 || neighborsCount == 3))
            return 1;

        if (MM == 1 && neighborsCount >= 4)
            return 0;

        return 0;
    }

    private int simpleGrowth(MooreNeighborhood neighborhood) {
        if (neighborhood.MLstate() > 0)
            return 1;
        if (neighborhood.MRstate() > 0)
            return 1;
        if (neighborhood.TMstate()> 0)
            return 1;
        if (neighborhood.BMstate() > 0)
            return 1;

        return neighborhood.MMstate();
    }

    private int diagGrowth(MooreNeighborhood neighborhood) {


        if (neighborhood.TLstate() > 0 && neighborhood.TRstate() > 0 &&
                neighborhood.BLstate() > 0 && neighborhood.BRstate() > 0)
            return 0;

        if (neighborhood.MMstate() > 0)
            return 1;


        return (neighborhood.TLstate() > 0 || neighborhood.TRstate() > 0 ||
                neighborhood.BLstate() > 0 || neighborhood.BRstate() > 0) ? 1 : 0;
    }

    private boolean checkBounds(int index) {
        return (index >= 0 && index < sizeX * sizeY);
    }

    public void setRuleSet(RuleSet newRuleSet) {
        this.ruleSet = newRuleSet;
    }

    public void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void changeAlternator() {
        alternator = (alternator + 1) % 2;
    }
}
