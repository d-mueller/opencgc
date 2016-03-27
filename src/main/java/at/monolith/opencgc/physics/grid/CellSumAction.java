package at.monolith.opencgc.physics.grid;

/**
 * This class can be used to sum over quantities (such as densities) which can be computed at each lattice site.
 */
public abstract class CellSumAction implements CellAction
{
    public double result;

    public void reset()
    {
        result = 0.0;
    }

    public void execute(Grid grid, int index)
    {
        synchronized (this)
        {
            result += compute(grid, index);
        }
    }

    public double compute(Grid grid, int index)
    {
        return 0.0;
    }

    public double getResult(Grid grid)
    {
        reset();
        grid.cellIterator.execute(this);
        return result;
    }
}
