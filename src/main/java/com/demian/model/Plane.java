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
        cells = new ArrayList<>();
        ruleEnforcer = new RuleEnforcer(sizeX, sizeY);
    }

    private void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        ruleEnforcer.setSize(sizeX, sizeY);
    }

    public void initialize(int initialSizeX, int initialSizeY) {
        setSize(initialSizeX, initialSizeY);
        for (int i=0; i<initialSizeX; i++) {
            for (int j=0; j<initialSizeY; j++) {
                cells.add(new Cell());
            }
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
        List<Cell> newCells = new ArrayList<>();
        for (int i=0; i<sizeX*sizeY; i++) {
            int state = ruleEnforcer.getState(cells, i);


            newCells.add(new Cell(state));
        }
        ruleEnforcer.changeAlternator();
        cells = newCells;
    }

    private boolean checkBounds(int index) {
        return (index >= 0 && index < sizeX * sizeY);
    }


    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }


    public void setCurrentRuleSet(RuleSet ruleSet) {
        ruleEnforcer.setRuleSet(ruleSet);
    }
}
