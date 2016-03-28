package at.monolith.opencgc.ui;

import at.monolith.opencgc.physics.grid.Grid;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * A simple plot which shows energy and pressure components.
 */
public class PressureRatioPanel extends JPanel
{
    Grid grid;

    XYSeriesCollection collection;
    XYSeries PL;
    XYSeries PT;

    public PressureRatioPanel(Grid grid)
    {
        this.grid = grid;

        PL = new XYSeries("PL");
        PT = new XYSeries("PT");

        collection = new XYSeriesCollection();

        collection.addSeries(PL);
        collection.addSeries(PT);


        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "t",
                "",
                collection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart.setBackgroundImageAlpha(0);

        ChartPanel CP = new ChartPanel(chart);
        CP.setPreferredSize(new Dimension(320, 320));
        CP.setSize(320, 320);
        this.add(CP, BorderLayout.CENTER);
        this.validate();

    }

    public void paintComponent(Graphics g2)
    {
        draw();
    }

    public void draw()
    {
        double ETv = grid.getETSquared();
        double ELv = grid.getELSquared();
        double BTv = grid.getBTSquared();
        double BLv = grid.getBLSquared();
        double Ev = (ETv + ELv + BTv + BLv);
        double PTv = (ELv + BLv) / Ev;
        double PLv = (ETv + BTv) / Ev - PTv;
        double t = grid.t;
        PT.add(t, PTv);
        PL.add(t, PLv);
    }
}
