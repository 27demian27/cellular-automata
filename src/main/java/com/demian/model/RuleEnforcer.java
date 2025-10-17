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

    public int getState(List<Cell> cells, int i) {
        int state = -1;
        switch (ruleSet) {
            case GAME_OF_LIFE -> {
                state =
                        gameOfLife(
                                checkBounds(i - sizeX - 1) ? cells.get(i - sizeX - 1).state : 0, // TL
                                checkBounds(i - sizeX) ? cells.get(i - sizeX).state : 0, // TM
                                checkBounds(i - sizeX + 1) ? cells.get(i - sizeX + 1).state : 0, // TR
                                checkBounds(i - 1) ? cells.get(i - 1).state : 0, // ML
                                checkBounds(i) ? cells.get(i).state : 0, // MM
                                checkBounds(i + 1) ? cells.get(i + 1).state : 0, // MR
                                checkBounds(i + sizeX - 1) ? cells.get(i + sizeX - 1).state : 0, // BL
                                checkBounds(i + sizeX) ? cells.get(i + sizeX).state : 0, // BM
                                checkBounds(i + sizeX + 1) ? cells.get(i + sizeX + 1).state : 0 // BR
                        );
            }
            case SIMPLE_GROWTH -> {
                state =
                        simpleGrowth(
                                checkBounds(i - 1) ? cells.get(i - 1).state : 0, // LEFT
                                checkBounds(i - sizeX) ? cells.get(i - sizeX).state : 0, // TOP
                                checkBounds(i) ? cells.get(i).state : 0, // MIDDLE
                                checkBounds(i + 1) ? cells.get(i + 1).state : 0, // RIGHT
                                checkBounds(i + sizeX) ? cells.get(i + sizeX).state : 0 // BOTTOM
                        );
            }
            case ALTERNATING -> {
                if (alternator == 0) {
                    state =
                            simpleGrowth(
                                checkBounds(i - 1) ? cells.get(i - 1).state : 0, // LEFT
                                checkBounds(i - sizeX) ? cells.get(i - sizeX).state : 0, // TOP
                                checkBounds(i) ? cells.get(i).state : 0, // MIDDLE
                                checkBounds(i + 1) ? cells.get(i + 1).state : 0, // RIGHT
                                checkBounds(i + sizeX) ? cells.get(i + sizeX).state : 0 // BOTTOM
                    );
                } else {
                    state =
                            gameOfLife(
                                    checkBounds(i - sizeX - 1) ? cells.get(i - sizeX - 1).state : 0, // TL
                                    checkBounds(i - sizeX) ? cells.get(i - sizeX).state : 0, // TM
                                    checkBounds(i - sizeX + 1) ? cells.get(i - sizeX + 1).state : 0, // TR
                                    checkBounds(i - 1) ? cells.get(i - 1).state : 0, // ML
                                    checkBounds(i) ? cells.get(i).state : 0, // MM
                                    checkBounds(i + 1) ? cells.get(i + 1).state : 0, // MR
                                    checkBounds(i + sizeX - 1) ? cells.get(i + sizeX - 1).state : 0, // BL
                                    checkBounds(i + sizeX) ? cells.get(i + sizeX).state : 0, // BM
                                    checkBounds(i + sizeX + 1) ? cells.get(i + sizeX + 1).state : 0 // BR
                            );
                }
            }
            default -> {
                System.err.println("ERROR: NO RULESET SELECTED");
                System.exit(1);
            }
        }
        return state;
    }

    private int gameOfLife(int TL, int TM, int TR, int ML, int MM, int MR, int BL, int BM, int BR) {
        int neighborsCount = 0;

        neighborsCount += TL += TM += TR += ML += MR += BL += BM += BR;

        if (MM == 0 && neighborsCount == 3)
            return 1;

        if (MM == 1 && (neighborsCount == 2 || neighborsCount == 3))
            return 1;

        if (MM == 1 && neighborsCount >= 4)
            return 0;

        return 0;
    }

    private int simpleGrowth(int leftState, int topState, int currentState, int rightState, int bottomState) {
        if (leftState > 0)
            return 1;
        if (rightState > 0)
            return 1;
        if (topState > 0)
            return 1;
        if (bottomState > 0)
            return 1;

        return currentState;
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
