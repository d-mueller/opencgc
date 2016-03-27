package at.monolith.opencgc.physics.grid;

import at.monolith.opencgc.math.AlgebraElement;
import at.monolith.opencgc.math.GroupElement;
import at.monolith.opencgc.math.SU2AlgebraElement;
import at.monolith.opencgc.math.SU2GroupElement;

/**
 * The cell contains all fields at a lattice site.
 */
public class Cell
{
    public GroupElement[] U0;
    public GroupElement[] U1;
    public AlgebraElement[] E;

    public Cell()
    {
        U0 = new GroupElement[3];
        U1 = new GroupElement[3];
        E = new AlgebraElement[3];

        for (int i = 0; i < 3; i++)
        {
            U0[i] = new SU2GroupElement(1, 0, 0, 0);
            U1[i] = new SU2GroupElement(1, 0, 0, 0);
            E[i] = new SU2AlgebraElement(0, 0, 0);
        }
    }

    public void reassign()
    {
        GroupElement[] tmp = U0;
        U0 = U1;
        U1 = tmp;
    }
}
