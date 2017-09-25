/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometerlab;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.*;

public class OdometryCorrection extends Thread {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private SampleProvider colorSample;
  private float scaledColor;
  private float[] LData;
  private double[] lastPosition = new double[3];
  private boolean[] update = new boolean[3];
  private boolean firstX = true;
  private boolean firstY = true;
  private boolean firstNegativeX = true;
  private boolean firstNegativeY = true;



  // constructor
  public OdometryCorrection(Odometer odometer, SampleProvider colorSample) {
    this.odometer = odometer;
    this.colorSample = colorSample;
  }

  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    LData = new float[colorSample.sampleSize()];
    while (true) {
      correctionStart = System.currentTimeMillis();
      //TODO Place correction implementation here
      colorSample.fetchSample(LData, 0); // Get data from color sensor
      scaledColor = LData[0]*1000;
      for(boolean data: update){
    	  data = false;
      }
      
    	if (scaledColor <= 300) {	//makes a sound when its not a regular tile (very primitive only used
  	  	Sound.beep();		//for testing. If there's not enough data read for the line it will not beep.
  	  	if(odometer.getTheta() <= Math.PI/4){ //Update values for first side of square
  	  		update[1] = true;				// want to change y values
  	  		if(firstY){	             		// if first line of y then y = 0
  	  			odometer.setY(0);
  	  			firstY = false;
  	  			odometer.getPosition(lastPosition, update);	// update our last position for next cycle
  	  		}
  	  		else{
  	  			lastPosition[1] += OdometryLab.GRID_LENGTH;
	  			odometer.setY(lastPosition[1]); // update y value
  	  		}
  	  	}
  	  	else if(odometer.getTheta() <= 3*Math.PI/4){
  	  		update[0] = true;			// want to change x values
  	  		if(firstX){			// update values for 2nd side of square
  	  			odometer.setX(0);		// if first line of x then x = 0
  	  			firstX = false; 
  	  			odometer.getPosition(lastPosition, update);	
  	  		}
  	  		else{
  	  			lastPosition[0] += OdometryLab.GRID_LENGTH;
  	  			odometer.setX(lastPosition[0]); // update x value
  	  		}
  	  	}
  	  	else if(odometer.getTheta() <= 5*Math.PI/4){ //3rd side of square
  	  		if(firstNegativeY) {
  	  			odometer.setY(lastPosition[1]);
  	  			firstNegativeY = false;	
  	  		}
  	  		else {
  	  		lastPosition[1] -= OdometryLab.GRID_LENGTH;
			odometer.setY(lastPosition[1]);
  	  		}
  	  	}
  	  	else{
  	  		if(firstNegativeX) {
  	  			odometer.setX(lastPosition[0]);
  	  			firstNegativeX = false;
  	  		}
  	  		else {
  	  		lastPosition[0] -= OdometryLab.GRID_LENGTH;
  	  		odometer.setX(lastPosition[0]);
  	  		}
  	  	}
  	  	
      } 
      
      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here because it is not
          // expected that the odometry correction will be
          // interrupted by another thread
        }
      }
    }
  }
}
