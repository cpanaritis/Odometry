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
  private double[] lastPosition;

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
      lastPosition = new double[3];
      if (scaledColor <= 300) {	//makes a sound when its not a regular tile (very primitive only used
  	  	Sound.beep();		//for testing. If there's not enough data read for the line it will not beep.
    }
      System.out.println(scaledColor);
      
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
