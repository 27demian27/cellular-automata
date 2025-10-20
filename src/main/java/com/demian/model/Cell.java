package com.demian.model;

import java.util.Arrays;

public class Cell {

    private int x;
    private int y;

    public int state;
    private int nextState;

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

    public MooreNeighborhood getNeighborhood() {
        return neighborhood;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
