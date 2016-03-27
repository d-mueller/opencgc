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
public class EnergyChartPanel extends JPanel
{
    Grid grid;

    XYSeriesCollection collection;
    XYSeries ET;
    XYSeries EL;
    XYSeries BT;
    XYSeries BL;
    XYSeries E;
    XYSeries PL;
    XYSeries PT;

    public EnergyChartPanel(Grid grid)
    {
        this.grid = grid;

        ET = new XYSeries("ET");
        EL = new XYSeries("EL");
        BT = new XYSeries("BT");
        BL = new XYSeries("BL");
        E = new XYSeries("E");
        PL = new XYSeries("PL");
        PT = new XYSeries("PT");

        collection = new XYSeriesCollection();

        collection.addSeries(ET);
        collection.addSeries(EL);
        collection.addSeries(BT);
        collection.addSeries(BL);
        collection.addSeries(E);
        collection.addSeries(PL);
        collection.addSeries(PT);


        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "",
                "t",
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

    public void draw()
    {
        double ETv = grid.getETSquared();
        double ELv = grid.getELSquared();
        double BTv = grid.getBTSquared();
        double BLv = grid.getBLSquared();
        double Ev = (ETv + ELv + BTv + BLv) / 2;
        double PTv = (ELv + BLv) / 2;
        double PLv = (ETv + BTv) / 2 - PTv;
        double t = grid.t;
        ET.add(t, ETv);
        EL.add(t, ELv);
        BT.add(t, BTv);
        BL.add(t, BLv);
        E.add(t, Ev);
        PT.add(t, PTv);
        PL.add(t, PLv);

    }
}
