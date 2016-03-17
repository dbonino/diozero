package com.diozero.sampleapps;

/*
 * #%L
 * Device I/O Zero - Core
 * %%
 * Copyright (C) 2016 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.Closeable;
import java.io.IOException;

import org.pmw.tinylog.Logger;

import com.diozero.CamJamKitDualMotor;
import com.diozero.HCSR04;
import com.diozero.LED;
import com.diozero.api.DigitalInputDevice;
import com.diozero.api.motor.DualMotor;
import com.diozero.util.SleepUtil;;

public class CamJamMotorTest implements Closeable {
	private static final int LED_FRONT_LEFT_PIN = 18;
	private static final int LED_FRONT_RIGHT_PIN = 17;
	private static final int LED_REAR_LEFT_PIN = 22;
	private static final int LED_REAR_RIGHT_PIN = 27;

	private static final int IR_SENSOR_LEFT_PIN = 24;
	private static final int IR_SENSOR_CENTRE_PIN = 23;
	private static final int IR_SENSOR_RIGHT_PIN = 25;
	
	private static final int HCSR04_TRIGGER_PIN = 26;
	private static final int HCSR04_ECHO_PIN = 4;

	public static void main(String[] args) {
		float DELAY = 0.65f;
		float speed = 0.9f;
		
		try (CamJamMotorTest robot = new CamJamMotorTest()) {
			robot.go(Direction.RIGHT, DELAY, speed);
			robot.go(Direction.FORWARDS, DELAY, speed);
			robot.go(Direction.LEFT, DELAY, speed);
			robot.stop(DELAY);
			robot.go(Direction.LEFT_BACKWARDS, DELAY, speed);
			robot.go(Direction.BACKWARDS, DELAY, speed);
			robot.go(Direction.RIGHT_BACKWARDS, DELAY, speed);
			robot.stop(DELAY);
			robot.go(Direction.SHARP_LEFT, DELAY, speed);
			robot.go(Direction.SHARP_RIGHT, DELAY, speed);
			robot.stop(DELAY);
			robot.go(Direction.FORWARDS, DELAY, speed);
			robot.go(Direction.BACKWARDS, DELAY, speed);
			robot.stop(DELAY);
			
			robot.followLine();
		} catch (IOException e) {
			Logger.error(e, "Error: {}", e);
		}
	}
	
	private LED frontLeftLed;
	private LED frontRightLed;
	private LED rearLeftLed;
	private LED rearRightLed;
	private DigitalInputDevice leftIrSensor;
	private DigitalInputDevice centreIrSensor;
	private DigitalInputDevice rightIrSensor;
	private HCSR04 hcsr04;
	private DualMotor dualMotor;

	public CamJamMotorTest() {
		frontLeftLed = new LED(LED_FRONT_LEFT_PIN, true);
		frontRightLed = new LED(LED_FRONT_RIGHT_PIN, true);
		rearLeftLed = new LED(LED_REAR_LEFT_PIN, true);
		rearRightLed = new LED(LED_REAR_RIGHT_PIN, true);
	
		leftIrSensor = new DigitalInputDevice(IR_SENSOR_LEFT_PIN);
		centreIrSensor = new DigitalInputDevice(IR_SENSOR_CENTRE_PIN);
		rightIrSensor = new DigitalInputDevice(IR_SENSOR_RIGHT_PIN);
		
		hcsr04 = new HCSR04(HCSR04_TRIGGER_PIN, HCSR04_ECHO_PIN);
			
		dualMotor = new CamJamKitDualMotor();
		
		frontLeftLed.blink(0.25f, 0.25f, 1, false);
		frontRightLed.blink(0.25f, 0.25f, 1, false);
		rearRightLed.blink(0.25f, 0.25f, 1, false);
		rearLeftLed.blink(0.25f, 0.25f, 1, false);
		
		Logger.info("Ready");
	}
	
	public void followLine() {
		float speed = 0.8f;
		float turn_rate = 0.4f;
		int delay_ms = 20;
		
		while (true) {
			SleepUtil.sleepMillis(delay_ms);
			
			float distance;
			int count = 0;
			do {
				distance = hcsr04.getDistanceCm();
			} while (distance == -1 && ++count < 10);
			
			if (distance != -1) {
				if (distance < 40) {
					Logger.info("Object detected");
				} else if (distance < 5) {
					Logger.info("Object detected - stopping");
					dualMotor.stop();
					continue;
				}
			}
			
			// Is the centre IR sensor detecting the line?
			if (centreIrSensor.isActive()) {
				if (leftIrSensor.isActive() && !rightIrSensor.isActive()) {
					Logger.info("Drifting left");
					dualMotor.circleLeft(speed, 0.2f);
				} else if (rightIrSensor.isActive() && !leftIrSensor.isActive()) {
					Logger.info("Drifting right");
					dualMotor.circleRight(speed, 0.2f);
				} else {
					Logger.info("Straight-on");
					dualMotor.forward(speed);
				}
			} else {
				Logger.info("Centre IR sensor has lost the line");
				if (leftIrSensor.isActive()) {
					Logger.info("Detected line to the left, turning left");
					dualMotor.circleLeft(speed, turn_rate);
				} else if (rightIrSensor.isActive()) {
					Logger.info("Detected line to the right, turning right");
					dualMotor.circleRight(speed, turn_rate);
				} else {
					Logger.info("Lost the line...");
					// TODO Is there anything that can be done? Retrace steps to find the line again?
					dualMotor.stop();
					break;
				}
			}
		}
	}

	public void go(Direction direction, float duration, float speed) {
		switch (direction) {
		case FORWARDS:
			frontLeftLed.on();
			frontRightLed.on();
			rearLeftLed.off();
			rearRightLed.off();
			dualMotor.forward(speed);
			break;
		case BACKWARDS:
			frontLeftLed.off();
	    	frontRightLed.off();
	    	rearLeftLed.on();
	    	rearRightLed.on();
	    	dualMotor.backward(speed);
	    	break;
		case SHARP_LEFT:
			frontLeftLed.off();
			frontRightLed.on();
			rearLeftLed.off();
			rearRightLed.on();
			dualMotor.rotateLeft(speed);
			break;
		case SHARP_RIGHT:
			frontLeftLed.on();
			frontRightLed.off();
			rearLeftLed.on();
			rearRightLed.off();
			dualMotor.rotateRight(speed);
			break;
		case LEFT:
			frontLeftLed.off();
			frontRightLed.on();
			rearLeftLed.off();
			rearRightLed.off();
			dualMotor.forwardLeft(speed);
			break;
		case RIGHT:
			frontLeftLed.on();
			frontRightLed.off();
			rearLeftLed.off();
			rearRightLed.off();
			dualMotor.forwardRight(speed);
			break;
		case LEFT_BACKWARDS:
			frontLeftLed.off();
			frontRightLed.off();
			rearLeftLed.off();
			rearRightLed.on();
			dualMotor.backwardLeft(speed);
			break;
		case RIGHT_BACKWARDS:
			frontLeftLed.off();
			frontRightLed.off();
			rearLeftLed.on();
			rearRightLed.off();
			dualMotor.backwardRight(speed);
		}
		
		if (duration > 0) {
			SleepUtil.sleepSeconds(duration);
		}
	}

	public void stop(float duration) {
		frontLeftLed.off();
		frontRightLed.off();
		rearLeftLed.off();
		rearRightLed.off();
		dualMotor.stop();
		if (duration > 0) {
			SleepUtil.sleepSeconds(duration);
		}
	}

	@Override
	public void close() throws IOException {
		frontLeftLed.close();
		frontRightLed.close();
		rearLeftLed.close();
		rearRightLed.close();
		leftIrSensor.close();
		centreIrSensor.close();
		rightIrSensor.close();
		dualMotor.close();
	}
	
	public static enum Direction {
		FORWARDS, BACKWARDS, LEFT, RIGHT, LEFT_BACKWARDS, RIGHT_BACKWARDS, SHARP_LEFT, SHARP_RIGHT;
	}
}