package com.demian.model;

import lombok.Setter;

import java.util.*;

public class RuleEnforcer {

    int sizeX;
    int sizeY;
    @Setter
    private RuleSet ruleSet;

    private Set<RuleSet> alternatingRules;
    private Iterator<RuleSet> iterator;

    private RuleSet alternationRuleSet;

    private List<Integer> customRuleBirth;

    private List<Integer> customRuleSurvival;
    public RuleEnforcer(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        alternationRuleSet = RuleSet.GAME_OF_LIFE;
        ruleSet = RuleSet.GAME_OF_LIFE;
        alternatingRules = new TreeSet<>();
        customRuleBirth = new ArrayList<>();
        customRuleSurvival = new ArrayList<>();
        iterator = alternatingRules.iterator();
    }

    public int getNextState(Cell cell) {
        int state = -1;
        MooreNeighborhood neighborhood = cell.getNeighborhood();

        RuleSet currentRuleSet = ruleSet;


        if (ruleSet == RuleSet.ALTERNATING) {
            currentRuleSet = alternationRuleSet;
        }

        switch (currentRuleSet) {
            case GAME_OF_LIFE -> state = gameOfLife(neighborhood);
            case SIMPLE_GROWTH -> state = simpleGrowth(neighborhood);
            case DIAG_GROWTH -> state = diagGrowth(neighborhood);
            case RULE30 -> state = rule30(neighborhood);
            case RULE110 -> state = rule110(neighborhood);
            case MAZE -> state = maze(neighborhood);
            case CUSTOM -> state = customRule(neighborhood);
            default -> {
                System.err.println("ERROR: NO RULESET SELECTED");
                System.exit(1);
            }
        }
        return state;
    }

    private int gameOfLife(MooreNeighborhood neighborhood) {
        int neighborsCount = neighborhood.getNumOfAliveNeighbors();

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

    private int rule30(MooreNeighborhood neighborhood) {
        int TL = neighborhood.TLstate();
        int TM = neighborhood.TMstate();
        int TR = neighborhood.TRstate();


        if (TL == 1 && TM == 1 && TR == 1)
            return 0;
        if (TL == 1 && TM == 1 && TR == 0)
            return 0;
        if (TL == 1 && TM == 0 && TR == 1)
            return 0;
        if (TL == 1 && TM == 0 && TR == 0)
            return 1;
        if (TL == 0 && TM == 1 && TR == 1)
            return 1;
        if (TL == 0 && TM == 1 && TR == 0)
            return 1;
        if (TL == 0 && TM == 0 && TR == 1)
            return 1;

        return neighborhood.MMstate();
    }

    private int rule110(MooreNeighborhood neighborhood) {
        int TL = neighborhood.TLstate();
        int TM = neighborhood.TMstate();
        int TR = neighborhood.TRstate();
        
        if (TL == 1 && TM == 1 && TR == 1)
            return 0;
        if (TL == 1 && TM == 1 && TR == 0)
            return 1;
        if (TL == 1 && TM == 0 && TR == 1)
            return 1;
        if (TL == 1 && TM == 0 && TR == 0)
            return 0;
        if (TL == 0 && TM == 1 && TR == 1)
            return 1;
        if (TL == 0 && TM == 1 && TR == 0)
            return 1;
        if (TL == 0 && TM == 0 && TR == 1)
            return 1;

        return neighborhood.MMstate();
    }

    private int maze(MooreNeighborhood neighborhood) {
        int count  = neighborhood.getNumOfAliveNeighbors();
        int MM = neighborhood.MMstate();

        if (MM == 0 && count == 3)
            return 1;
        if (MM == 1 && (count >= 1 && count <= 4))
            return 1;

        return 0;
    }

    private int customRule(MooreNeighborhood neighborhood) {
        int count  = neighborhood.getNumOfAliveNeighbors();
        int MM = neighborhood.MMstate();

        if (MM == 0 && customRuleBirth.contains(count))
            return 1;
        if (MM == 1 && customRuleSurvival.contains(count))
            return 1;

        return 0;
    }

    private boolean checkBounds(int index) {
        return (index >= 0 && index < sizeX * sizeY);
    }

    public void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void changeAlternation() {
        if (iterator == null || alternatingRules.isEmpty())
            return;

        if (!iterator.hasNext()) {
            iterator = alternatingRules.iterator();
        }
        alternationRuleSet = iterator.next();
    }

    public void addAlternatingRuleSet(RuleSet ruleSet) {
        alternatingRules.add(ruleSet);
        iterator = alternatingRules.iterator();
    }

    public void removeAlternatingRuleSet(RuleSet ruleSet) {
        alternatingRules.remove(ruleSet);
        iterator = alternatingRules.iterator();
    }

    public void setCustomRuleSet(List<Integer> customRuleBirth, List<Integer> customRuleSurvival) {
        this.customRuleBirth = customRuleBirth;
        this.customRuleSurvival = customRuleSurvival;
    }

    public List<Integer> getCustomRuleBirth() {
        return customRuleBirth;
    }

    public List<Integer> getCustomRuleSurvival() {
        return customRuleSurvival;
    }
}
