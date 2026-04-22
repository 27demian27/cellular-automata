package com.demian.model;

import com.demian.util.Bounds;
import lombok.Getter;

import java.net.Inet4Address;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Plane {

    private final RuleEnforcer ruleEnforcer;
    private final Random randomizer;

    private Cell[][] cells;

    @Getter
    private int aliveCellCount;

    @Getter
    private int sizeX;
    @Getter
    private int sizeY;

    public Plane() {
        ruleEnforcer = new RuleEnforcer(sizeX, sizeY);
        randomizer = new Random();
    }

    public void forEachCell(Consumer<Cell> action) {
        Objects.requireNonNull(action);

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                action.accept(cell);
            }
        }
    }

    private void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        ruleEnforcer.setSize(sizeX, sizeY);
    }

    public void initialize(int initialSizeX, int initialSizeY) {
        setSize(initialSizeX, initialSizeY);
        cells = new Cell[initialSizeY][initialSizeX];
        aliveCellCount = 0;

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

        if (state == 1)  aliveCellCount++; else aliveCellCount--;
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

        recountAliveCells();
    }

    public void randomizeCells() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                boolean boolState = randomizer.nextBoolean();
                cell.state = boolState ? 1 : 0;
            }
        }

        recountAliveCells();
    }

    public void simulateGeneration() {
        long start = System.nanoTime();
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

        recountAliveCells();

        long elapsed = System.nanoTime() - start;
        System.out.printf("Simulating generation took \n%.4f ms\n", elapsed / 1_000_000.0);

    }

    public void simulateGenerationThreaded() {
        ruleEnforcer.changeAlternation();

        int chunkSize = 32;

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        try {
            List<Callable<Void>> computeTasks = new ArrayList<>();

            for (int startY = 0; startY < cells.length; startY+=chunkSize) {
                int endY = Math.min(startY + chunkSize, cells.length);
                int finalStartY = startY;

//                System.out.println("Computing rows " + startY + " - " + endY);
                computeTasks.add(() -> {
                    for (int i = finalStartY; i < endY; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            cells[i][j].computeNextState(ruleEnforcer);

                        }
                    }
                    return null;
                });
            }

            executor.invokeAll(computeTasks);

            for (int startY = 0; startY < cells.length; startY+=chunkSize) {
                int endY = Math.min(startY + chunkSize, cells.length);
                int finalStartY = startY;

//                System.out.println("Applying rows " + startY + " - " + endY);
                computeTasks.add(() -> {
                    for (int i = finalStartY; i < endY; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            cells[i][j].applyNextState();

                        }
                    }
                    return null;
                });
            }
            executor.invokeAll(computeTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        recountAliveCells();
    }

    private void recountAliveCells() {
        aliveCellCount = 0;
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.state == 1) aliveCellCount++;
            }
        }
    }

    private boolean checkBounds(int index) {
        return (index >= 0 && index < sizeX * sizeY);
    }

    public boolean checkBounds(int x, int y) {
        return (x >= 0 && x < sizeX && y >= 0 && y < sizeY);
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

    public void setCustomRuleSet(List<Integer> customRuleBirth, List<Integer> customRuleSurvival) {
       ruleEnforcer.setCustomRuleSet(customRuleBirth, customRuleSurvival);
    }

    public List<Integer> getCustomRuleBirth() {
        return ruleEnforcer.getCustomRuleBirth();
    }

    public List<Integer> getCustomRuleSurvival() {
        return ruleEnforcer.getCustomRuleSurvival();
    }
}
