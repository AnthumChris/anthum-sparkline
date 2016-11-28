package com.anthum.sparkline.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anthum.sparkline.SparklineGenerator;


public class SparklineExampleServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private final int SPARKLINES_DEFAULT = 100;
  private final int SPARKLINES_MAX = 500;

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // chart options to pass
    Map<String, Object> options = new HashMap<String, Object>();    

    // total graphs to show
    int totalSparklines;
    
    // Random data generator (if id parameter not passed)
    Random rand;
    
    try {
      totalSparklines =  Math.min(Integer.parseInt(request.getParameter("total")), SPARKLINES_MAX);
    } catch (Exception e) {
      totalSparklines = SPARKLINES_DEFAULT;
    }

    // pass numerical id to generate predictable random data
    try {
      rand = new Random(Long.parseLong(request.getParameter("id")));
    } catch (Exception e) {
      rand = new Random();
    }
    
    if (request.getParameter("bg") != null) {
      options.put("bgColor", request.getParameter("bg"));
    }
    
    if (request.getParameter("color") != null) {
      options.put("color", request.getParameter("color"));
    }

    if (request.getParameter("fill") != null) {
      options.put("fillColor", request.getParameter("fill"));
    }

    if (request.getParameter("truecolor") != null) {
      options.put("truecolor", "true");
    }
    
    // generate a random array of numbers to chart with
    int pointsPerGraph = 12;
    int[][] chartData = new int[totalSparklines][pointsPerGraph];
    
    for (int g = 0; g<totalSparklines; g++) {
      for (int p = 0; p<pointsPerGraph; p++) {
        // int val = (int)Math.round(rand.nextDouble()*(SparklineGenerator.GRAPH_H-1));
        int val = (int)Math.round(rand.nextDouble()*(1000));
        chartData[g][p] = val; 
        chartData[g][p] = val; 
      }

      // testing explicit data
//      chartData[g] = new int[] {2,4,6,8,10,12};
//      chartData[g] = new int[] {1201, 1205, 1202, 1205, 1203, 1204};
    }
    
    // write the sparkline using the sample data
    OutputStream out = response.getOutputStream();
    ByteArrayOutputStream imgBytes = SparklineGenerator.createPNG(chartData, options);
    response.setContentType("image/png");
    response.setContentLength(imgBytes.size());
    out.write(imgBytes.toByteArray());
  }
}
