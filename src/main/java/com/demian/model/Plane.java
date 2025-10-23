package com.demian.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Plane {

    private List<Cell> cells;

    private int sizeX;
    private int sizeY;

    RuleEnforcer ruleEnforcer;

    public Plane() {
        ruleEnforcer = new RuleEnforcer(sizeX, sizeY);
    }

    private void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        ruleEnforcer.setSize(sizeX, sizeY);
    }

    public void initialize(int initialSizeX, int initialSizeY) {
        setSize(initialSizeX, initialSizeY);
        cells = new ArrayList<>(initialSizeX * initialSizeY);

        for (int y = 0; y < initialSizeY; y++) {
            for (int x = 0; x < initialSizeX; x++) {
                cells.add(new Cell(x, y));
            }
        }

        for (Cell c : cells) {
            c.initializeNeighborhood(this);
        }
    }

    public void setState(int state, int xPos, int yPos) {
        int index = yPos * sizeX + xPos;
        if (!checkBounds(index))
            return;

        Cell cell = cells.get(index);
        cell.state = state;
        cells.set(index, cell);
    }

    public Optional<Integer> getState(int xPos, int yPos) {
        int index = yPos * sizeX + xPos;
        if (!checkBounds(index)) {
            System.err.println("getState() called on invalid position.");
            return Optional.empty();
        }

        return Optional.of(cells.get(index).state);
    }

    public void killCells() {
        for (Cell cell : cells) {
            cell.state = 0;
        }
    }

    public void simulateGeneration() {

        ruleEnforcer.changeAlternation();

        for (Cell c : cells) {
            c.computeNextState(ruleEnforcer);
        }

        for (Cell c : cells) {
            c.applyNextState();
        }

    }

    private boolean checkBounds(int index) {
        return (index >= 0 && index < sizeX * sizeY);
    }

    public boolean checkBounds(int x, int y) {
        return (x >= 0 && x < sizeX && y >= 0 && y < sizeY);
    }


    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Cell getCell(int x, int y) {
        return cells.get(y * sizeX + x);
    }

    public void setCurrentRuleSet(RuleSet ruleSet) {
        ruleEnforcer.setRuleSet(ruleSet);
    }

    public void addAlternatingRuleSet(RuleSet ruleSet) {
        ruleEnforcer.addAlternatingRuleSet(ruleSet);
    }

    public void removeAlternatingRuleSet(RuleSet ruleSet) {
        ruleEnforcer.removeAlternatingRuleSet(ruleSet);
    }
}
