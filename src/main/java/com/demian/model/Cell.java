package com.demian.model;

import lombok.Getter;


public class Cell {

    @Getter
    private final int x;
    @Getter
    private final int y;

    public int state;
    private int nextState;

    @Getter
    private MooreNeighborhood neighborhood;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Cell(int x, int y, int state) {
        this(x, y);
        this.state = state;
    }

    public void initializeNeighborhood(Plane plane) {
        neighborhood = new MooreNeighborhood(this);
        neighborhood.initialize(plane);
    }

    public void computeNextState(RuleEnforcer ruleEnforcer) {
        nextState = ruleEnforcer.getNextState(this);
    }

    public void applyNextState() {
        state = nextState;
    }

}
