package at.monolith.opencgc.physics.initial;

import at.monolith.opencgc.math.GroupElement;
import at.monolith.opencgc.math.SU2AlgebraElement;
import at.monolith.opencgc.math.SU2GroupElement;
import at.monolith.opencgc.physics.grid.Cell;
import at.monolith.opencgc.physics.grid.Grid;
import at.monolith.opencgc.physics.grid.GridFunctions;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

import java.util.Random;

/**
 * A class which implements color-glass-condensate initial conditions based on the McLerran-Venugopalan model.
 */
public class MVInitialConditions
{
    public Grid grid;
    public double mu;
    public double irRegulator;
    public double uvRegulator;
    public boolean useSeeds;
    public long seed1;
    public long seed2;
    public int totalNumberOfCells;

    public MVInitialConditions(Grid grid, double mu, double irRegulator, double uvRegulator,
                               boolean useSeeds, long seed1, long seed2)
    {
        this.grid = grid;
        this.mu = mu;
        this.irRegulator = irRegulator;
        this.uvRegulator = uvRegulator;
        this.useSeeds = useSeeds;
        this.seed1 = seed1;
        this.seed2 = seed2;
        this.totalNumberOfCells = grid.totalNumberOfCells;
    }

    public void initializeGrid()
    {
        SU2GroupElement[] V1 = solvePoisson(mu, irRegulator, uvRegulator, useSeeds, seed1);
        SU2GroupElement[] V2 = solvePoisson(mu, irRegulator, uvRegulator, useSeeds, seed2);

        // Initialize longitudinal magnetic field
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            Cell cell = grid.cells[i];

            for (int d = 0; d < 2; d++)
            {
                SU2GroupElement U1 = getU(V1, i, d);
                SU2GroupElement U2 = getU(V2, i, d);

                SU2GroupElement sum = (SU2GroupElement) U1.add(U2);
                SU2GroupElement sumInv = ((SU2GroupElement) sum.adj()).inv();

                cell.U0[d] = sum.mult(sumInv);
                cell.U1[d] = sum.mult(sumInv);
            }
        }

        for (int i = 0; i < totalNumberOfCells; i++)
        {

            Cell cell = grid.cells[i];
            /*
            SU2GroupElement temp = (SU2GroupElement) grid.factory.groupZero();
            for (int d = 0; d < 2; d++)
            {
                int is = grid.shift(i, d, -1);
                SU2GroupElement Um1 = (SU2GroupElement) grid.cells[i].U0[d].sub(grid.factory.groupIdentity());
                SU2GroupElement diff1 = (SU2GroupElement) getU(V2, i, d).adj().sub(getU(V1, i, d)).adj();
                SU2GroupElement Um2 = (SU2GroupElement) grid.cells[is].U0[d].adj().sub(grid.factory.groupIdentity());
                SU2GroupElement diff2 = (SU2GroupElement) getU(V2, is, d).sub(getU(V1, is, d));
                temp.addAssign(Um1.mult(diff1).add(Um2.mult(diff2)));
            }

            cell.E[2] = temp.proj().mult(0.5);
            */
            GroupElement tmp = grid.factory.groupZero();
            for (int d = 0; d < 2; d++)
            {
                GroupElement U1 = getU(V1, i, d);
                GroupElement U2 = getU(V2, i, d);
                GroupElement U = grid.cells[i].U0[d];
                int is = grid.shift(i, d, -1);
                GroupElement U1b = getU(V1, is, d);
                GroupElement U2b = getU(V2, is, d);
                GroupElement Ub = grid.cells[is].U0[d];

                tmp.addAssign(U2);
                tmp.addAssign(U1.mult(U.adj()));
                tmp.subAssign(U2b);
                tmp.subAssign(Ub.adj().mult(U1b));
            }
            grid.cells[i].E[2] = tmp.proj().mult(-1.0);
        }
    }

    public SU2GroupElement getU(SU2GroupElement[] V, int index, int dir)
    {
        int index2 = grid.shift(index, dir, 1);
        return (SU2GroupElement) V[index].mult(V[index2].adj());
    }

    public SU2GroupElement[] solvePoisson(double mu, double m, double L, boolean useSeed, long seed)
    {
        // Initialize random charges
        Random rand;
        if (useSeed)
        {
            rand = new Random(seed);
        } else
        {
            rand = new Random();
        }

        SU2AlgebraElement[] rho = new SU2AlgebraElement[totalNumberOfCells];
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            rho[i] = new SU2AlgebraElement();
            for (int j = 0; j < 3; j++)
            {
                double r = rand.nextGaussian() * mu;
                rho[i].set(j, r);
            }
        }

        // Solve Poisson equation with infrared regulator
        DoubleFFT_2D fft = new DoubleFFT_2D(grid.numberOfCells[0], grid.numberOfCells[1]);
        SU2AlgebraElement[] phi = new SU2AlgebraElement[totalNumberOfCells];
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            phi[i] = new SU2AlgebraElement();
        }

        for (int j = 0; j < 3; j++)
        {
            // fft of charge density
            double[] fftArray = new double[2 * totalNumberOfCells];

            for (int i = 0; i < totalNumberOfCells; i++)
            {
                fftArray[2 * i] = rho[i].get(j);
                fftArray[2 * i + 1] = 0.0;
            }
            fft.complexForward(fftArray);

            // solve poisson
            for (int i = 1; i < totalNumberOfCells; i++)
            {
                double psq = momentumSquared(i);
                double invLaplace = 0.0;
                if (psq < L * L)
                {
                    invLaplace = 1.0 / (m * m + psq);
                }
                fftArray[2 * i] *= invLaplace;
                fftArray[2 * i + 1] *= invLaplace;
            }
            fftArray[0] = 0.0;
            fftArray[1] = 0.0;

            fft.complexInverse(fftArray, true);

            for (int i = 0; i < totalNumberOfCells; i++)
            {
                phi[i].set(j, fftArray[2 * i]);
            }

        }

        SU2GroupElement[] V = new SU2GroupElement[totalNumberOfCells];
        for (int i = 0; i < totalNumberOfCells; i++)
        {
            V[i] = (SU2GroupElement) phi[i].mult(-1.0).getLink();
        }

        return V;
    }

    public double momentumSquared(int index)
    {
        int[] pos = GridFunctions.getCellPos(index, grid.numberOfCells);
        double px2 = 2 * (1 - Math.cos(2 * Math.PI * pos[0] / grid.numberOfCells[0]));
        double py2 = 2 * (1 - Math.cos(2 * Math.PI * pos[1] / grid.numberOfCells[1]));
        return px2 + py2;
    }
}
