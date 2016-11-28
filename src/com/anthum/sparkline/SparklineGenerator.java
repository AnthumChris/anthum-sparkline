package com.anthum.sparkline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.objectplanet.image.PngEncoder;

public abstract class SparklineGenerator {

  protected static final int GRAPH_H = 44; // height should accommodate top/bottom padding calculated from STROKE_WEIGHT;
  private static final int GRAPH_W = 105;
  private static final int MAX_PER_ROW = 10;
  private static final int STROKE_WEIGHT = 2;
  private static final Color DEFAULT_STROKE_COLOR = Color.black;
  
  // pad sparklines to accommodate for stroke width and antialiasing to prevent overlapping or cropping
  protected static final int CHART_PADDING = STROKE_WEIGHT/2+1;

  /**
   * Convenience method to log to console
   * @param o
   */
  public static final void log(Object o) {
    System.out.println(o);
  }

  /**
   * Writes a transparent PNG to the returned output stream
   * @param chartData Multidimensional array of values
   * @return Bytes containing the PNG data
   * @throws IOException 
   */
  public static ByteArrayOutputStream createPNG(int[][] chartData) throws IOException {
    return createPNG(chartData, new HashMap<String, Object>());
  }


  /**
   * Writes a transparent PNG to the returned output stream
   * @param chartData Multidimensional array of values chart values, all values normalized relative to min/max values
   * @return Bytes containing the PNG data
   * @options List for passing option parameters
   * @throws IOException 
   */
  public static ByteArrayOutputStream createPNG(int[][] chartData, Map<String, Object> options) throws IOException {
    int[][] normalizedData = SparklineUtil.getNormalizedPlotData(chartData);
    BufferedImage image = getBufferedImage(normalizedData, BufferedImage.TYPE_INT_ARGB, options);
    long start = System.currentTimeMillis();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_INDEXED_ALPHA);
    pngEncoder.setCompression(PngEncoder.BEST_SPEED);
    pngEncoder.encode(image, out);
    
    log("Generated PNG: "+(System.currentTimeMillis()-start)+"ms");
    return out;
  }

  /**
   * 
   * @param normalizedChartData
   * @param colorModel
   * @param options
   * @return
   */
  private static BufferedImage getBufferedImage(int[][] normalizedChartData, int colorModel, Map<String, Object> options) {
    int total = normalizedChartData.length;
    Color strokeColor, bgColor, fillColor;

    // parse stroke color hex code or set to default
    try {
      strokeColor = new Color(Integer.decode("0x" + options.get("color")));
    } catch (Exception e) {
      strokeColor = DEFAULT_STROKE_COLOR;
    }
    
    // parse background color hex code or set null
    try {
      bgColor = new Color(Integer.decode("0x" + options.get("bgColor")));
    } catch (Exception e) {
      bgColor = null;
    }
    
    // parse fill color hex code or set null
    try {
      fillColor = new Color(Integer.decode("0x" + options.get("fillColor")));
    } catch (Exception e) {
      fillColor = null;
    }
    
    // height/width of each sparkline graph
    
    int totalRows = (int)Math.round(total/MAX_PER_ROW);
    if (total % MAX_PER_ROW != 0) {
      totalRows++;
    }

    int canvasW = (MAX_PER_ROW)*GRAPH_W;
    int canvasH = totalRows*GRAPH_H;
    
    BufferedImage image = new BufferedImage(canvasW, canvasH, colorModel);
    Graphics2D g = (Graphics2D) image.getGraphics();

    // various graphics rendering settings tested. Antialiasing is all that's needed to provide a solid look. Others are left for testing
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
//    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//    g.setRenderingHint(RenderingHints.KEY_DITHERING,           RenderingHints.VALUE_DITHER_ENABLE);
//    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,   RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
//    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//    g.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
//    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);
//    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    // use transparent bkg unless color is set
    if (bgColor != null) {
      g.setBackground(bgColor);
      g.clearRect(0,0,canvasW, canvasH);
    }
    g.setStroke(new BasicStroke(STROKE_WEIGHT));
    g.setColor(strokeColor);

    // loop through each chart and plot the points
    for (int i = 0;i<total;i++) {
      int[] points = normalizedChartData[i];

      int totalPoints = points.length;
      int yOffset = (i/MAX_PER_ROW*GRAPH_H); // calculates y position for graphs
      int xOffset = i % MAX_PER_ROW * GRAPH_W; // calculates x position for graphs
      
      // pixel width of each line, last should be longer if remainder exists
      int avgLineWidth = (GRAPH_W-CHART_PADDING)/(totalPoints-1);
      int lastLineWidth = avgLineWidth + (GRAPH_W-CHART_PADDING)%(totalPoints-1);

      // for each point, store x,y coordinates separately for drawPolyline, which is smoother than separate drawLine calls
      int[] xPoints = new int[totalPoints];
      int[] yPoints = new int[totalPoints];
      int pointIndex = 1;

      // add first point to draw enclosed polygon if fillColor specified
      if (fillColor != null) {
        xPoints = new int[totalPoints+2];
        yPoints = new int[totalPoints+2];
        xPoints[0] = xOffset+CHART_PADDING;
        yPoints[0] = yOffset+GRAPH_H;
        pointIndex++;
      }
      
      for (int p=1;p<totalPoints;p++) {
        // X values must be within chart to prevent cropping or overlapping, last line should fill to right with lastLineWidth
        int
          prevX = Math.max(CHART_PADDING, Math.min((p-1)*avgLineWidth, GRAPH_W-CHART_PADDING)),
          x = Math.max(CHART_PADDING, Math.min(p*((p+1 == totalPoints) ? lastLineWidth : avgLineWidth), GRAPH_W-CHART_PADDING));
        
        // Y values plot from top, not bottom, so invert the Y position
        int
          prevY = GRAPH_H-points[p-1]-CHART_PADDING,
          y = GRAPH_H-points[p]-CHART_PADDING;


        // convert chart points to exact canvas placement
        xPoints[pointIndex-1] = prevX+xOffset;
        xPoints[pointIndex] = x+xOffset;
        yPoints[pointIndex-1] = prevY+yOffset;
        yPoints[pointIndex] = y+yOffset;
        pointIndex++;
      }
      
      // draw filled polygon if specified or draw only a line
      if (fillColor != null) {
        xPoints[pointIndex] = xOffset+GRAPH_W-CHART_PADDING;
        yPoints[pointIndex] = yOffset+GRAPH_H;
        g.setColor(fillColor);
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        
        // create new arrays that exclude start/end points to draw the line
        int[] lineXPoints = new int[xPoints.length-2]; 
        int[] lineYPoints = new int[yPoints.length-2]; 
        System.arraycopy(xPoints, 1, lineXPoints, 0, lineXPoints.length);
        System.arraycopy(yPoints, 1, lineYPoints, 0, lineYPoints.length);

        g.setColor(strokeColor);
        g.drawPolyline(lineXPoints, lineYPoints, lineXPoints.length);

      } else {
        g.setColor(strokeColor);
        g.drawPolyline(xPoints, yPoints, xPoints.length);
      }
      
    }

    return image;
  }
  
}
