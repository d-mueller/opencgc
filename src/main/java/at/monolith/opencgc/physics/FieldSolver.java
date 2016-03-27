package at.monolith.opencgc.physics;

import at.monolith.opencgc.math.AlgebraElement;
import at.monolith.opencgc.math.GroupElement;
import at.monolith.opencgc.physics.grid.CellAction;
import at.monolith.opencgc.physics.grid.Grid;

/**
 * This class implements the boost-invariant lattice equations of motion.
 */
public class FieldSolver
{
    public Grid grid;
    public CellAction momentumAction;
    public CellAction fieldAction;
    public double a;

    public FieldSolver(Grid grid)
    {
        this.a = grid.a;
        this.grid = grid;
        this.momentumAction = new MomentumUpdate();
        this.fieldAction = new FieldUpdate();
    }

    public void execute()
    {
        grid.cellIterator.execute(fieldAction);
        grid.cellIterator.execute(momentumAction);
        grid.t += grid.dt;
    }


    class FieldUpdate implements CellAction
    {
        public void execute(Grid grid, int index)
        {

            // U_T update
            for (int i = 0; i < 2; i++)
            {
                GroupElement VT = grid.cells[index].E[i].mult(-grid.dt / grid.t).getLink();
                grid.cells[index].U1[i] = VT.mult(grid.cells[index].U0[i]);
            }

            // U_L update
            GroupElement VL = grid.cells[index].E[2].mult(-grid.dt * grid.t * a).getLink();
            grid.cells[index].U1[2] = VL.mult(grid.cells[index].U0[2]);
        }
    }

    class MomentumUpdate implements CellAction
    {
        public void execute(Grid grid, int index)
        {
            // E_T update

            double g = 1.0; // Guess factor

            AlgebraElement Px1 = grid.getPlaquette(index, 0, 1, 1, 1, 0).add(grid.getPlaquette(index, 0, 1, 1, -1, 0)).proj().mult(g * grid.t * grid.dt);
            AlgebraElement Px2 = grid.getPlaquette(index, 0, 2, 1, 1, 0).add(grid.getPlaquette(index, 0, 2, 1, -1, 0)).proj().mult(g * grid.dt / (grid.t * a * a));
            grid.cells[index].E[0].addAssign(Px1.add(Px2));

            AlgebraElement Py1 = grid.getPlaquette(index, 1, 0, 1, 1, 0).add(grid.getPlaquette(index, 1, 0, 1, -1, 0)).proj().mult(g * grid.t * grid.dt);
            AlgebraElement Py2 = grid.getPlaquette(index, 1, 2, 1, 1, 0).add(grid.getPlaquette(index, 1, 2, 1, -1, 0)).proj().mult(g * grid.dt / (grid.t * a * a));
            grid.cells[index].E[1].addAssign(Py1.add(Py2));

            // E_L update
            AlgebraElement PL1 = grid.getPlaquette(index, 2, 0, 1, 1, 0).add(grid.getPlaquette(index, 2, 0, 1, -1, 0)).proj().mult(g * grid.dt / (grid.t * a));
            AlgebraElement PL2 = grid.getPlaquette(index, 2, 1, 1, 1, 0).add(grid.getPlaquette(index, 2, 1, 1, -1, 0)).proj().mult(g * grid.dt / (grid.t * a));
            grid.cells[index].E[2].addAssign(PL1.add(PL2));
        }
    }

}