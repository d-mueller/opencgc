package at.monolith.opencgc.physics;

import at.monolith.opencgc.physics.grid.Grid;
import at.monolith.opencgc.physics.initial.MVInitialConditions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * The main simulation class which holds the grid and the field solver class.
 */

public class Simulation
{
    // Parameters
    public int nx;
    public int ny;

    public double dt;
    public double dx;
    public double a;

    public double t0;

    // Instances
    public Grid grid;
    public FieldSolver fieldSolver;

    public Simulation()
    {
        nx = 128;
        ny = 128;
        dx = 1;
        dt = 0.02;
        a = 0.1;
        t0 = 0.00001;

        grid = new Grid(nx, ny, dt, dx, a, t0);
        fieldSolver = new FieldSolver(grid);

        //Initial conditions for MV
        // 10% area Au+Au, 0.2Gev IR, 3GeV UV
        double mu = 0.065;
        double m = 0.025;
        double L = 0.51;
        MVInitialConditions initialConditions = new MVInitialConditions(grid, mu, m, L, false, 1, 1);
        initialConditions.initializeGrid();

        // Flux tube initial conditions
        //ElectricFluxTube fluxTube = new ElectricFluxTube(grid, 2, .1, 0.5, 0.5);
        //fluxTube.initializeGrid();
        //MagneticFluxTube fluxTube2 = new MagneticFluxTube(grid, 1, 1, 0.75, 0.5);
        //fluxTube2.initializeGrid();

    }

    public void step()
    {
        for (int i = 0; i < grid.totalNumberOfCells; i++)
        {
            grid.cells[i].reassign();
        }
        fieldSolver.execute();
    }

    public void energyStats()
    {
        double ET = grid.getETSquared();
        double EL = grid.getELSquared();
        double BT = grid.getBTSquared();
        double BL = grid.getBLSquared();
        double E = ET + EL + BT + BL;
        ET /= E;
        EL /= E;
        BT /= E;
        BL /= E;
        double G = grid.getGaussSquared() / E;


        print("ET", ET);
        print("EL", EL);
        print("BT", BT);
        print("BL", BL);
        print("G", G);
        print("t*E", grid.t * E);
        print("E", E);
        print("t", grid.t);
        System.out.println();
    }

    public void print(String s, double v)
    {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("0.#####E0");

        String n = formatter.format(v);
        System.out.print(s + " = " + n + ";\t");
    }
}
