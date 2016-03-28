package at.monolith.opencgc.ui;

import at.monolith.opencgc.math.AlgebraElement;
import at.monolith.opencgc.physics.Simulation;
import at.monolith.opencgc.physics.grid.GridFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main class which shows basic stuff like energy densities, pressure etc.
 */
public class Main extends JFrame
{
    public int skippedFrames = 60;
    JFrame parent;
    JPanel container;
    PixelPanel ETFrame;
    PixelPanel ELFrame;
    PixelPanel BTFrame;
    PixelPanel BLFrame;
    PixelPanel energyDensityFrame;
    EnergyChartPanel energyChartPanel;
    Simulation simulation;
    /**
     * Timer for animation
     */
    private Timer timer;

    public Main()
    {
        super("OpenCGC MV-Model");
        setSize(320 * 3, 320 * 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        parent = this;

        skippedFrames = 5;

        simulation = new Simulation();

        ETFrame = new PixelPanel(simulation.nx, simulation.ny, "ET");
        ELFrame = new PixelPanel(simulation.nx, simulation.ny, "EL");
        BTFrame = new PixelPanel(simulation.nx, simulation.ny, "BT");
        BLFrame = new PixelPanel(simulation.nx, simulation.ny, "BL");
        energyDensityFrame = new PixelPanel(simulation.nx, simulation.ny, "E");
        energyChartPanel = new EnergyChartPanel(simulation.grid);

        container = new JPanel();
        container.setLayout(new GridLayout(2, 3));

        container.add(ETFrame);
        container.add(BLFrame);
        container.add(energyDensityFrame);

        container.add(BTFrame);
        container.add(ELFrame);
        container.add(energyChartPanel);

        this.add(container);

        timer = new Timer(1, new TimerListener());
        timer.start();
    }

    public static void main(String[] args)
    {
        System.setProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
        Main main = new Main();
        main.setVisible(true);
    }

    public void draw()
    {
        for (int i = 0; i < ETFrame.nx; i++)
        {
            for (int j = 0; j < ETFrame.ny; j++)
            {
                int[] pos = new int[2];
                pos[0] = i;
                pos[1] = j;

                int index = GridFunctions.getCellIndex(pos, simulation.grid.numberOfCells);

                ETFrame.field[i][j][0] = simulation.grid.getETSquared(index);
                ELFrame.field[i][j][0] = simulation.grid.getELSquared(index);
                BTFrame.field[i][j][2] = simulation.grid.getBTSquared(index);
                BLFrame.field[i][j][2] = simulation.grid.getBLSquared(index);

                // energy density
                double E0, E1, E2;

                E0 = 0.0;
                E0 += Math.pow(simulation.grid.cells[index].E[0].get(0), 2) / Math.pow(simulation.grid.t, 2);
                E0 += Math.pow(simulation.grid.cells[index].E[1].get(0), 2) / Math.pow(simulation.grid.t, 2);
                E0 += Math.pow(simulation.grid.cells[index].E[2].get(0), 2);


                E1 = 0.0;
                E1 += Math.pow(simulation.grid.cells[index].E[0].get(1), 2) / Math.pow(simulation.grid.t, 2);
                E1 += Math.pow(simulation.grid.cells[index].E[1].get(1), 2) / Math.pow(simulation.grid.t, 2);
                E1 += Math.pow(simulation.grid.cells[index].E[2].get(1), 2);


                E2 = 0.0;
                E2 += Math.pow(simulation.grid.cells[index].E[0].get(2), 2) / Math.pow(simulation.grid.t, 2);
                E2 += Math.pow(simulation.grid.cells[index].E[1].get(2), 2) / Math.pow(simulation.grid.t, 2);
                E2 += Math.pow(simulation.grid.cells[index].E[2].get(2), 2);

                //Magnetic contribution
                AlgebraElement PL = simulation.grid.getPlaquette(index, 0, 1, 1, 1, 0).proj();
                AlgebraElement PX = simulation.grid.getPlaquette(index, 2, 1, 1, 1, 0).proj();
                AlgebraElement PY = simulation.grid.getPlaquette(index, 2, 2, 1, 1, 0).proj();

                E0 += Math.pow(PL.get(0), 2);
                E1 += Math.pow(PL.get(1), 2);
                E2 += Math.pow(PL.get(2), 2);

                E0 += Math.pow(PX.get(0), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);
                E1 += Math.pow(PX.get(1), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);
                E2 += Math.pow(PX.get(2), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);

                E0 += Math.pow(PY.get(0), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);
                E1 += Math.pow(PY.get(1), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);
                E2 += Math.pow(PY.get(2), 2) / Math.pow(simulation.grid.a * simulation.grid.t, 2);

                double E = E0 + E1 + E2;

                double energy = simulation.grid.getEnergyDensity(index);

                energyDensityFrame.field[i][j][0] = energy * E0 / E;
                energyDensityFrame.field[i][j][1] = energy * E1 / E;
                energyDensityFrame.field[i][j][2] = energy * E2 / E;
            }
        }
        double E = simulation.grid.getEnergy() / (simulation.grid.totalNumberOfCells);
        double norm = 1.0 / E;
        ETFrame.norm = norm;
        ELFrame.norm = norm;
        BTFrame.norm = norm;
        BLFrame.norm = norm;
        energyDensityFrame.norm = norm;
    }

    /**
     * Listener for timer
     */
    class TimerListener implements ActionListener
    {

        int counter = 0;

        public void actionPerformed(ActionEvent eve)
        {
            simulation.step();

            counter++;
            if (counter >= skippedFrames)
            {
                counter = 0;

                draw();
                energyChartPanel.draw();
                container.repaint();
            }
        }
    }
}
