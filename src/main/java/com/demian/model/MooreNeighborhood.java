package com.demian.model;


public class MooreNeighborhood {

    private final Cell centralCell;
    private Cell tl, tm, tr, ml, mr, bl, bm, br;

    public MooreNeighborhood(Cell centralCell) {
        this.centralCell = centralCell;
    }

    public void initialize(Plane plane) {
        int x = centralCell.getX();
        int y = centralCell.getY();

        tl = plane.checkBounds(x-1, y-1) ? plane.getCell(x-1, y-1) : null;
        tm = plane.checkBounds(x, y-1) ? plane.getCell(x, y-1) : null;
        tr = plane.checkBounds(x+1, y-1) ? plane.getCell(x+1, y-1) : null;
        ml = plane.checkBounds(x-1, y) ? plane.getCell(x-1, y) : null;
        mr  = plane.checkBounds(x+1, y) ? plane.getCell(x+1, y) : null;
        bl = plane.checkBounds(x-1, y+1) ? plane.getCell(x-1, y+1) : null;
        bm = plane.checkBounds(x, y+1) ? plane.getCell(x, y+1) : null;
        br = plane.checkBounds(x+1, y+1) ? plane.getCell(x+1, y+1) : null;
    }

    public int getNumOfAliveNeighbors() {
        int count = 0;

        count +=
                TLstate() +
                TMstate() +
                TRstate() +
                MLstate() +
                MRstate() +
                BLstate() +
                BMstate() +
                BRstate();
        return count;
    }


    public int TLstate() { return (tl != null) ? tl.state : 0; }
    public int TMstate() { return (tm != null) ? tm.state : 0; }
    public int TRstate() { return (tr != null) ? tr.state : 0; }
    public int MLstate() { return (ml != null) ? ml.state : 0; }
    public int MMstate() { return (centralCell != null) ? centralCell.state : 0; }
    public int MRstate() { return (mr != null) ? mr.state : 0; }
    public int BLstate() { return (bl != null) ? bl.state : 0; }
    public int BMstate() { return (bm != null) ? bm.state : 0; }
    public int BRstate() { return (br != null) ? br.state : 0; }
}
