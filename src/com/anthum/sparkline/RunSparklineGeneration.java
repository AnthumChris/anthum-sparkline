package com.anthum.sparkline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RunSparklineGeneration {

  private static void log(Object s) {
    //System.out.println(s);
  }
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    generateSparklineTest();
    generateSparklineTest();
  }

  private static void generateSparklineTest() throws Exception {
    // generate a random array of numbers and write the sparklines to a file
    int maxH = 15; // max height of graph
    int totalGraphs = 500;
    int pointsPerGraph = 6;
    int[][] chartData = new int[totalGraphs][pointsPerGraph];
    
    for (int g = 0; g<totalGraphs; g++) {
      for (int p = 0; p<pointsPerGraph; p++) {
        int val = (int)Math.round(Math.random()*maxH);
        chartData[g][p] = val; 
      }
    }
    
    log(Math.random() * maxH);
    generateSparklineFile(chartData);
  }
  
  /**
   * Creates an image of sparkline sparkline graphs and writes to file
   * @param chartData Array of charts with points for the chart series
   * @throws Exception 
   */
  private static void generateSparklineFile(int[][] chartData) throws Exception {
    long start = System.currentTimeMillis();
    int total = chartData.length;

    
    // height/width of each sparkline graph
    final int graphH = 15;
    final int graphW = 35;
    final int maxPerRow = 20;
    
    int totalRows = (int)Math.round(total/maxPerRow);

    int canvasW = (maxPerRow-1)*graphW;
    int canvasH = totalRows*graphH;
    
    BufferedImage image = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    
    //g.setBackground(new Color(255,255,255));
    //g.clearRect(0,0,canvasW, canvasH);
    g.setColor(Color.black);
    g.setStroke(new BasicStroke(1));

    // loop through each chart and plot the points
    for (int i = 0;i<total;i++) {
      int[] points = chartData[i];
      int totalPoints = points.length;
      int lastY = 0;
      int row = ((i+1)/maxPerRow);
      int yOffset = ((i+1)/maxPerRow*graphH); // calculates y position for graphs
      int xOffset = ((i+1)/maxPerRow*graphW*maxPerRow); // calculates y position for graphs
      for (int p=1;p<totalPoints;p++) {
        int x = (graphW/(totalPoints-1)*p) + (graphW*i) - xOffset;
        int prevX = (graphW/(totalPoints-1)*(p-1)) + (graphW*i) - xOffset;
        //log("prevX: "+prevX+ ", x: "+x);
        g.drawLine(prevX, points[p-1]+yOffset, x, points[p]+yOffset);
      }
    }
    
    
    log(System.currentTimeMillis()-start);
    start = System.currentTimeMillis();
    ImageIO.write(image, "png", new File(System.getProperty("user.home")+"/Desktop/anthum-sparkline-test.png"));
    
    log((System.currentTimeMillis()-start));
  }
}
