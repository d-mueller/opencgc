package at.monolith.opencgc.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * A JPanel which can be used to draw a pixel image for densities.
 */
public class PixelPanel extends JPanel
{
    public int nx;
    public int ny;
    public double norm;
    public double[][][] field;
    public BufferedImage image;
    public int[] imagePixelData;

    public PixelPanel(int nx, int ny)
    {
        this.nx = nx;
        this.ny = ny;
        this.field = new double[nx][ny][3];
        this.norm = 0.0000000001;

        setSize(nx, ny);

        for (int i = 0; i < nx; i++)
            for (int j = 0; j < ny; j++)
                for (int k = 0; k < 3; k++)
                    field[i][j][k] = 0.0;

    }

    public void setField(double[][][] field)
    {
        this.field = field;
    }

    public void paintComponent(Graphics g2)
    {
        image = (BufferedImage) createImage(nx, ny);
        imagePixelData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        int r, g, b;
        for (int i = 0; i < nx; i++)
        {
            for (int j = 0; j < ny; j++)
            {
                r = Math.min((int) (255 * field[i][j][0] * norm), 255);
                g = Math.min((int) (255 * field[i][j][1] * norm), 255);
                b = Math.min((int) (255 * field[i][j][2] * norm), 255);
                imagePixelData[j * nx + i] = r << 16 | g << 8 | b;
            }
        }

        g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
}