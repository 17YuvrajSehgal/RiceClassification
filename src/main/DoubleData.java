package main;

import ec.gp.GPData;

@SuppressWarnings("serial")
public class DoubleData extends GPData {
    public double x;

    @Override
    public void copyTo(final GPData gpd) {
        ((DoubleData)gpd).x = x;
    }
}

