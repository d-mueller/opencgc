package at.monolith.opencgc.physics.grid;

/**
 * An action which can be executed by the CellIterator.
 */
public interface CellAction
{
    void execute(Grid grid, int index);
}
