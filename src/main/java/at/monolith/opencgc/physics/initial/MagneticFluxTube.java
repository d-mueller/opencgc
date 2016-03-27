package at.monolith.opencgc.physics.initial;

import at.monolith.opencgc.math.AlgebraElement;
import at.monolith.opencgc.math.GroupElement;
import at.monolith.opencgc.physics.grid.Grid;
import at.monolith.opencgc.physics.grid.GridFunctions;

/**
 * Initial conditions for a magnetic flux tube.
 */
public class MagneticFluxTube
{
    double width;
    double amplitude;
    int x0;
    int y0;
    Grid grid;

    public MagneticFluxTube(Grid grid, double width, double amplitude, double px, double py)
    {
        this.grid = grid;
        this.width = width;
        this.amplitude = amplitude;
        this.x0 = (int) (px * grid.numberOfCells[0]);
        this.y0 = (int) (py * grid.numberOfCells[1]);
    }

    public void initializeGrid()
    {
        for (int i = 0; i < grid.totalNumberOfCells; i++)
        {
            int[] pos = GridFunctions.getCellPos(i, grid.numberOfCells);
            double x = pos[0] - x0;
            double y = pos[1] - y0;
            double d = x * x + y * y;

            double field = amplitude * width * (1 - Math.exp(-d / (2 * width * width))) / (d + 0.01);


            AlgebraElement Ax = grid.factory.algebraZero();
            Ax.set(0, -y * field / 2);
            AlgebraElement Ay = grid.factory.algebraZero();
            Ay.set(0, x * field / 2);
            GroupElement Ux = Ax.getLink();
            GroupElement Uy = Ay.getLink();

            grid.cells[i].U0[0] = Ux;
            grid.cells[i].U0[1] = Uy;
            grid.cells[i].U1[0] = Ux.copy();
            grid.cells[i].U1[1] = Uy.copy();

            //grid.cells[i].E[2].set(1, grid.cells[i].E[2].get(1) + field);

            //grid.cells[i].pi.set(0, field);
            // grid.cells[i].E[2].set(1, amplitude * Math.exp(- d / ( width * width)));
        }
    }
}
