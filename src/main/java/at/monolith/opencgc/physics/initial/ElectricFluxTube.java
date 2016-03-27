package at.monolith.opencgc.physics.initial;

import at.monolith.opencgc.physics.grid.Grid;
import at.monolith.opencgc.physics.grid.GridFunctions;

/**
 * Initial conditions for an electric flux tube.
 */
public class ElectricFluxTube
{
    double width;
    double amplitude;
    int x0;
    int y0;
    Grid grid;

    public ElectricFluxTube(Grid grid, double width, double amplitude, double px, double py)
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

            double field = amplitude * Math.exp(-d / (2 * width * width));

            grid.cells[i].E[2].set(0, field);
        }
    }
}
