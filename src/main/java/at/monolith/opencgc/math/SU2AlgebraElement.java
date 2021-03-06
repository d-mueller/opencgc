package at.monolith.opencgc.math;

public class SU2AlgebraElement implements AlgebraElement
{

    protected double[] v;

    public SU2AlgebraElement()
    {

        v = new double[3];

        v[0] = 0;
        v[1] = 0;
        v[2] = 0;

    }

    public SU2AlgebraElement(double a, double b, double c)
    {

        v = new double[3];

        v[0] = a;
        v[1] = b;
        v[2] = c;

    }

    public void reset()
    {

        v[0] = 0;
        v[1] = 0;
        v[2] = 0;

    }

    public AlgebraElement add(AlgebraElement arg)
    {

        SU2AlgebraElement a = (SU2AlgebraElement) arg;
        SU2AlgebraElement b = new SU2AlgebraElement();

        b.v[0] = v[0] + a.v[0];
        b.v[1] = v[1] + a.v[1];
        b.v[2] = v[2] + a.v[2];
        return b;

    }

    public void addAssign(AlgebraElement arg)
    {

        SU2AlgebraElement a = (SU2AlgebraElement) arg;

        v[0] += a.v[0];
        v[1] += a.v[1];
        v[2] += a.v[2];

    }

    public AlgebraElement sub(AlgebraElement arg)
    {

        SU2AlgebraElement a = (SU2AlgebraElement) arg;

        SU2AlgebraElement b = new SU2AlgebraElement();
        b.v[0] = v[0] - a.v[0];
        b.v[1] = v[1] - a.v[1];
        b.v[2] = v[2] - a.v[2];
        return b;

    }

    public void set(int j, double value)
    {

        v[j] = value;

    }

    public double get(int j)
    {
        return v[j];
    }

    public double square()
    {
        return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
    }

    public AlgebraElement mult(double number)
    {

        SU2AlgebraElement b = new SU2AlgebraElement();
        b.v[0] = v[0] * number;
        b.v[1] = v[1] * number;
        b.v[2] = v[2] * number;
        return b;

    }

    public void multAssign(double number)
    {

        this.v[0] *= number;
        this.v[1] *= number;
        this.v[2] *= number;

    }

    public double mult(AlgebraElement arg)
    {
        SU2AlgebraElement a = (SU2AlgebraElement) arg;
        return v[0] * a.v[0] + v[1] * a.v[1] + v[2] * a.v[2];
    }

    public void set(AlgebraElement arg)
    {

        SU2AlgebraElement a = (SU2AlgebraElement) arg;

        v[0] = a.v[0];
        v[1] = a.v[1];
        v[2] = a.v[2];

    }

    public GroupElement getLinearizedLink()
    {

        double sum = (v[0] * v[0] + v[1] * v[1] + v[2] * v[2]) / 4;
        return new SU2GroupElement(Math.sqrt(1.0 - sum), v[0] / 2, v[1] / 2, v[2] / 2);
    }

    public GroupElement getLink()
    {

        double sum = v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
        double mod = Math.sqrt(sum) / 2;
        double sinfakt;
        if (mod < 1.E-20)
        {
            sinfakt = 0.0;
        } else
        {
            sinfakt = 0.5 / mod * Math.sin(mod);
        }
        return new SU2GroupElement(Math.cos(mod), v[0] * sinfakt, v[1] * sinfakt, v[2] * sinfakt);
    }

    public double proj(int c)
    {
        return 0.5 * v[c];
    }

    public AlgebraElement act(GroupElement g)
    {

        SU2GroupElement u = (SU2GroupElement) g;
        SU2GroupElement Xm = new SU2GroupElement();

        Xm.set(0, 0.0);
        Xm.set(1, v[0] / 2);
        Xm.set(2, v[1] / 2);
        Xm.set(3, v[2] / 2);

        Xm = (SU2GroupElement) u.mult(Xm.mult(u.adj()));
        return Xm.proj();

    }

    public void actAssign(GroupElement g)
    {
        this.set(this.act(g));
    }

    public AlgebraElement copy()
    {
        return new SU2AlgebraElement(v[0], v[1], v[2]);
    }


}
