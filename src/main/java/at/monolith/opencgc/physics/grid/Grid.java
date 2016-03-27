package at.monolith.opencgc.physics.grid;

import at.monolith.opencgc.math.AlgebraElement;
import at.monolith.opencgc.math.ElementFactory;
import at.monolith.opencgc.math.GroupElement;

/**
 * The grid class contains all cells in the simulation and is a central piece of the simulation.
 */
public class Grid
{
    public int[] numberOfCells;
    public int totalNumberOfCells;

    public Cell[] cells;

    public int[][][] shiftTable;

    public ElementFactory factory;
    public CellIterator cellIterator;

    public ETSquaredComputation eTSquaredComputation;
    public ELSquaredComputation eLSquaredComputation;
    public BTSquaredComputation bTSquaredComputation;
    public BLSquaredComputation bLSquaredComputation;
    public GaussComputation gaussComputation;

    public double dt;
    public double dx;
    public double a;
    public double t;

    public Grid(int nx, int ny, double dt, double dx, double a, double t0)
    {
        this.numberOfCells = new int[2];
        numberOfCells[0] = nx;
        numberOfCells[1] = ny;

        this.dt = dt;
        this.dx = dx;
        this.a = a;
        this.t = t0;

        this.factory = new ElementFactory(2);

        this.totalNumberOfCells = numberOfCells[0] * numberOfCells[1];

        this.cells = new Cell[totalNumberOfCells];
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            cells[i] = new Cell();
        }

        this.shiftTable = new int[totalNumberOfCells][2][2];
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                for (int k = 0; k < 2; k++)
                {
                    int orientation = 2 * k - 1;
                    int shiftedIndex = GridFunctions.shift(i, j, orientation, numberOfCells);
                    shiftTable[i][j][k] = shiftedIndex;
                }
            }
        }

        this.cellIterator = new CellIterator(this);

        eTSquaredComputation = new ETSquaredComputation();
        eLSquaredComputation = new ELSquaredComputation();
        bTSquaredComputation = new BTSquaredComputation();
        bLSquaredComputation = new BLSquaredComputation();
        gaussComputation = new GaussComputation();
    }

    public int shift(int index, int dir, int or)
    {
        if (dir < 2)
        {
            int k = (or + 1) / 2;
            return shiftTable[index][dir][k];
        } else
        {
            return index;
        }
    }

    public GroupElement getLink(int index, int direction, int orientation, int timeIndex)
    {
        if (timeIndex == 0)
        {
            if (orientation < 0)
            {
                return cells[shift(index, direction, orientation)].U0[direction].adj();
            }
            return cells[index].U0[direction];
        } else
        {
            if (orientation < 0)
            {

                return cells[shift(index, direction, orientation)].U1[direction].adj();
            }
            return cells[index].U1[direction];
        }
    }

    public GroupElement getPlaquette(int index, int d1, int d2, int o1, int o2, int timeIndex)
    {
        /*
			The four lattice indices associated with the plaquette.
		 */
        int x1 = index;
        int x2 = shift(x1, d1, o1);
        int x3 = shift(x2, d2, o2);
        int x4 = shift(x3, d1, -o1);

		/*
			The four gauge links associated with the plaquette.
		 */

        GroupElement U1 = getLink(x1, d1, o1, timeIndex);
        GroupElement U2 = getLink(x2, d2, o2, timeIndex);
        GroupElement U3 = getLink(x3, d1, -o1, timeIndex);
        GroupElement U4 = getLink(x4, d2, -o2, timeIndex);

		/*
			Plaquette calculation
		 */


        GroupElement U = factory.groupIdentity();
        U.multAssign(U1);
        U.multAssign(U2);
        U.multAssign(U3);
        U.multAssign(U4);

        return U;
    }

    // Energy-related methods and classes
    public double getELSquared(int index)
    {
        return cells[index].E[2].square();
    }

    public double getETSquared(int index)
    {
        return (cells[index].E[0].square() + cells[index].E[1].square()) / Math.pow(t, 2);
    }

    public double getBLSquared(int index)
    {
        return getPlaquette(index, 0, 1, 1, 1, 0).proj().square();
    }

    public double getBTSquared(int index)
    {
        double P1 = getPlaquette(index, 2, 0, 1, 1, 0).proj().square();
        double P2 = getPlaquette(index, 2, 1, 1, 1, 0).proj().square();
        return (P1 + P2) / Math.pow(a * t, 2);
    }

    public double getEnergyDensity(int index)
    {
        return getELSquared(index) + getETSquared(index) + getBLSquared(index) + getBTSquared(index);
    }

    public double getETSquared()
    {
        return eTSquaredComputation.getResult(this);
    }

    public double getELSquared()
    {
        return eLSquaredComputation.getResult(this);
    }

    public double getBTSquared()
    {
        return bTSquaredComputation.getResult(this);
    }

    public double getBLSquared()
    {
        return bLSquaredComputation.getResult(this);
    }

    public double getElectricEnergy()
    {
        return getETSquared() + getELSquared();
    }

    public double getMagneticEnergy()
    {
        return getBTSquared() + getBLSquared();
    }

    public double getEnergy()
    {
        double eE = getElectricEnergy();
        double eB = getMagneticEnergy();
        return eE + eB;
    }

    // Gauss constraint methods
    public AlgebraElement getGaussDensity(int index)
    {
        AlgebraElement res = factory.algebraZero();
        for (int i = 0; i < 2; i++)
        {
            AlgebraElement E0 = cells[index].E[i];
            int is = shift(index, i, -1);
            AlgebraElement E1 = cells[is].E[i].act(getLink(index, i, -1, 0));
            res.addAssign(E0.sub(E1));
        }
        AlgebraElement E0 = cells[index].E[2];
        AlgebraElement E1 = cells[index].E[2].act(getLink(index, 2, -1, 0));
        res.addAssign(E0.sub(E1).mult(1.0 / (a)));

        return res;
    }

    public double getGaussDensitySquared(int index)
    {
        return getGaussDensity(index).square();
    }

    public double getGaussSquared()
    {
        return gaussComputation.getResult(this);
    }

    class ETSquaredComputation extends CellSumAction
    {
        @Override
        public double compute(Grid grid, int index)
        {
            return grid.getETSquared(index);
        }
    }

    class ELSquaredComputation extends CellSumAction
    {
        @Override
        public double compute(Grid grid, int index)
        {
            return grid.getELSquared(index);
        }
    }

    class BTSquaredComputation extends CellSumAction
    {
        @Override
        public double compute(Grid grid, int index)
        {
            return grid.getBTSquared(index);
        }
    }

    class BLSquaredComputation extends CellSumAction
    {
        @Override
        public double compute(Grid grid, int index)
        {
            return grid.getBLSquared(index);
        }
    }

    class GaussComputation extends CellSumAction
    {
        @Override
        public double compute(Grid grid, int index)
        {
            return grid.getGaussDensitySquared(index);
        }
    }
}
