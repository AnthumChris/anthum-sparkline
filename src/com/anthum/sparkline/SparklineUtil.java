package com.anthum.sparkline;

public class SparklineUtil {

  /**
   * Returns normalized chart data to ensure nice-looking sparklines that show relative values based on the sparkline graph's height.
   * For example, if our sparkline's height GRAPH_H is 44px with a STROKE_WEIGHT of 2, our maximum number of pixels to plot on Y axis is 40px.
   * All chartData values must be normalized to fall within a 0-40 range to accommodate that 40px height.  A chart with data
   * 2,4,6,8,10,12 would be normalized to [0,8,16,24,32,40] because 2 and 12 are the min/max values that need to be normalized to the chart
   * height's min/max Y axis values of 0px and 40px.  Values in between are normalized based on a percentage of the deviation between those min/max values.
   * Example 2: [1201, 1205, 1202, 1205, 1203, 1204] is normalized to [0, 40, 10, 40, 20, 30] because 1201/1205 are min/max values, and all values
   * @param chartData
   * @return
   */
  public static int[][] getNormalizedPlotData(int[][] chartData) {
    // normalize all values to be relative to min/max values. Negative numbers are turned to zero
    int[][] normalizedData = new int[chartData.length][];
    int[] chartValues;
    int val, valMin, valMax, valSpread;
    double valPct;

    for (int c=0; c<chartData.length; c++) {
      chartValues = chartData[c];
      normalizedData[c] = new int[chartValues.length];

      // get min/max values
      valMin = -1;
      valMax = -1;
      for (int v=0; v<chartValues.length; v++) {
        // convert negative values to zero
        val = Math.max(0, chartValues[v]);
        normalizedData[c][v] = val;

        if (val < valMin || valMax == -1) {
          // convert negative values to zero
          valMin = val;
        }

        if (val > valMax) {
          valMax = val;
        }
      }

      valSpread = valMax-valMin;
      int heightSpread = (SparklineGenerator.GRAPH_H-SparklineGenerator.CHART_PADDING*2);

      // set normalized values based on percentage of within min-max value range
      for (int i=0; i<normalizedData[c].length; i++) {
        val = normalizedData[c][i];
        valPct = (double)(val-valMin)/valSpread;
        normalizedData[c][i] = (int)Math.round((valPct*heightSpread)); // get percentage and round to an int
//        log("normalized: "+val+' '+normalizedData[c][i]+" "+valPct+" of "+heightSpread);
      }
//      log("original:   "+Arrays.toString(chartData[c]));
//      log("normalized: "+Arrays.toString(normalizedData[c]));
    }

    return normalizedData;
  }

}
