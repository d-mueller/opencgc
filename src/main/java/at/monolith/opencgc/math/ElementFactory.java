package at.monolith.opencgc.math;

/**
 * Factory class to generate static SU(n) algebra and group elements.
 */
public class ElementFactory
{

    public int numberOfColors;
    public int numberOfComponents;
    double[] SU3GroupZero = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    double[] SU3GroupIdentity = new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    double[] SU3AlgebraZero = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
    public ElementFactory(int numberOfColors)
    {
        this.numberOfColors = numberOfColors;
        if (numberOfColors > 1)
        {
            this.numberOfComponents = numberOfColors * numberOfColors - 1;
        } else
        {
            this.numberOfComponents = 1;
        }
    }
    public ElementFactory()
    {
    }

    public GroupElement groupZero(int colors)
    {
        switch (colors)
        {
            case 2:
                return new SU2GroupElement(0, 0, 0, 0);
            default:
                System.out.println("Constructor for SU(" + colors + ") not defined.\n");
                return new SU2GroupElement(0, 0, 0, 0);
        }
    }

    public GroupElement groupIdentity(int colors)
    {
        switch (colors)
        {
            case 2:
                return new SU2GroupElement(1, 0, 0, 0);
            default:
                System.out.println("Constructor for SU(" + colors + ") not defined.\n");
                return new SU2GroupElement(1, 0, 0, 0);
        }
    }

    public AlgebraElement algebraZero(int colors)
    {
        switch (colors)
        {
            case 2:
                return new SU2AlgebraElement(0, 0, 0);
            default:
                System.out.println("Constructor for SU(" + colors + ") not defined.\n");
                return new SU2AlgebraElement(0, 0, 0);
        }
    }


    public GroupElement groupZero()
    {
        return groupZero(numberOfColors);
    }

    public GroupElement groupIdentity()
    {
        return groupIdentity(numberOfColors);
    }

    public AlgebraElement algebraZero()
    {
        return algebraZero(numberOfColors);
    }


}