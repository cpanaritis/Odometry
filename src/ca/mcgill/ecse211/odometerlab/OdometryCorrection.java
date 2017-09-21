/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometerlab;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private EV3ColorSensor lightSensor;
  private float[] LData;

  // constructor
  public OdometryCorrection(Odometer odometer, EV3ColorSensor lightSensor) {
    this.odometer = odometer;
    this.lightSensor = lightSensor;
  }

  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    SampleProvider colorSample = lightSensor.getMode("ColorID");
    LData = new float[colorSample.sampleSize()];
    lightSensor.setFloodlight(true);
    while (true) {
      correctionStart = System.currentTimeMillis();
      //TODO Place correction implementation here
      lightSensor.fetchSample(LData, 0); // Get data from color sensor
      System.out.println(LData[0]);
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
