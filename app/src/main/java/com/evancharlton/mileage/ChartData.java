
package com.evancharlton.mileage;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

/** Simple (label, points) holder passed from a ChartGenerator to the chart it feeds. */
public final class ChartData {
    public final String label;

    public final List<Entry> points;

    public ChartData(String label, List<Entry> points) {
        this.label = label;
        this.points = points;
    }
}
