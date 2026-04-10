package com.demian.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class Plane {

    private Cell[][] cells;

    private int sizeX;
    private int sizeY;

    RuleEnforcer ruleEnforcer;

    Random randomizer;

    public Plane() {
        ruleEnforcer = new RuleEnforcer(sizeX, sizeY);
        randomizer = new Random();
    }

    private void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        ruleEnforcer.setSize(sizeX, sizeY);
    }

    public void initialize(int initialSizeX, int initialSizeY) {
        setSize(initialSizeX, initialSizeY);
        cells = new Cell[initialSizeY][initialSizeX];

        for (int y = 0; y < initialSizeY; y++) {
            for (int x = 0; x < initialSizeX; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.initializeNeighborhood(this);
            }
        }
    }

    public void resize(int x1, int x2, int y1, int y2) {
        int newSizeX = x2 - x1;
        int newSizeY = y2 - y1;
        Cell[][] newCells = new Cell[newSizeY][newSizeX];

        int x1old = x1;

        for (int y = 0; y < newSizeY; y++) {
            x1 = x1old;
            for (int x = 0; x < newSizeX; x++) {
                if ((y1 >= 0 && y1 < sizeY) && (x1 >= 0 && x1 < sizeX)) {
                    Cell newCell = new Cell(x, y);
                    newCell.state =  cells[y1][x1].state;
                    newCells[y][x] = newCell;
                } else {
                    newCells[y][x] = new Cell(x, y);
                }
                x1++;
            }
            y1++;
        }

        setSize(newSizeX, newSizeY);
        cells = newCells;

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.initializeNeighborhood(this);
            }
        }
    }

    public void setState(int state, int xPos, int yPos) {
        int index = yPos * sizeX + xPos;
        if (!checkBounds(index))
            return;

        Cell cell = cells[yPos][xPos];
        cell.state = state;
    }

    public Optional<Integer> getState(int xPos, int yPos) {
        int index = yPos * sizeX + xPos;
        if (!checkBounds(index)) {
            System.err.println("getState() called on invalid position.");
            return Optional.empty();
        }

        return Optional.of(cells[yPos][xPos].state);
    }

    public void killCells() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.state = 0;
            }
        }
    }

    public void randomizeCells() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                boolean boolState = randomizer.nextBoolean();
                cell.state = boolState ? 1 : 0;
            }
        }
    }

    public void simulateGeneration() {

        ruleEnforcer.changeAlternation();

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.computeNextState(ruleEnforcer);

            }
        }

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.applyNextState();
            }
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
        return cells[y][x];
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
