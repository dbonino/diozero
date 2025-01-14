package com.diozero.api;

/*-
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - Core
 * Filename:     DigitalOutputDevice.java  
 * 
 * This file is part of the diozero project. More information about this project
 * can be found at http://www.diozero.com/
 * %%
 * Copyright (C) 2016 - 2021 diozero
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

import org.tinylog.Logger;

import com.diozero.api.function.Action;
import com.diozero.internal.spi.GpioDeviceFactoryInterface;
import com.diozero.internal.spi.GpioDigitalOutputDeviceInterface;
import com.diozero.sbc.DeviceFactoryHelper;
import com.diozero.util.DiozeroScheduler;
import com.diozero.util.SleepUtil;

/**
 * Provides generic digital (on/off) output control with support for active high
 * and low logic.
 */
public class DigitalOutputDevice extends GpioDevice implements OutputDeviceInterface {
	public static final int INFINITE_ITERATIONS = -1;

	private boolean activeHigh;
	private boolean running;
	private GpioDigitalOutputDeviceInterface delegate;

	/**
	 * Defaults to active high logic, initial value is off.
	 * 
	 * @param gpio GPIO to which the output device is connected.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public DigitalOutputDevice(int gpio) throws RuntimeIOException {
		this(gpio, true, false);
	}

	/**
	 * @param gpio         GPIO to which the output device is connected.
	 * @param activeHigh   If true then setting the value to true will turn on the
	 *                     connected device.
	 * @param initialValue Initial output value.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public DigitalOutputDevice(int gpio, boolean activeHigh, boolean initialValue) throws RuntimeIOException {
		this(DeviceFactoryHelper.getNativeDeviceFactory(), gpio, activeHigh, initialValue);
	}

	/**
	 * @param deviceFactory Device factory to use to construct the device.
	 * @param gpio          GPIO to which the output device is connected.
	 * @param activeHigh    If true then setting the value to true will turn on the
	 *                      connected device.
	 * @param initialValue  Initial output value.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public DigitalOutputDevice(GpioDeviceFactoryInterface deviceFactory, int gpio, boolean activeHigh,
			boolean initialValue) throws RuntimeIOException {
		this(deviceFactory, deviceFactory.getBoardPinInfo().getByGpioNumberOrThrow(gpio), activeHigh, initialValue);
	}

	/**
	 * @param deviceFactory Device factory to use to construct the device.
	 * @param pinInfo       Information about the GPIO pin to which the output
	 *                      device is connected.
	 * @param activeHigh    If true then setting the value to true will turn on the
	 *                      connected device.
	 * @param initialValue  Initial output value.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public DigitalOutputDevice(GpioDeviceFactoryInterface deviceFactory, PinInfo pinInfo, boolean activeHigh,
			boolean initialValue) throws RuntimeIOException {
		super(pinInfo);

		this.delegate = deviceFactory.provisionDigitalOutputDevice(pinInfo, activeHigh == initialValue);
		this.activeHigh = activeHigh;
	}

	@Override
	public void close() {
		Logger.trace("close()");
		if (delegate.isOpen()) {
			setOn(false);
			delegate.close();
		}
	}

	private void onOffLoop(float onTime, float offTime, int n, Action stopAction) throws RuntimeIOException {
		running = true;
		if (n > 0) {
			for (int i = 0; i < n && running; i++) {
				onOff(onTime, offTime);
			}
		} else if (n == INFINITE_ITERATIONS) {
			while (running) {
				onOff(onTime, offTime);
			}
		}
		if (stopAction != null) {
			stopAction.action();
		}
	}

	private void onOff(float onTime, float offTime) throws RuntimeIOException {
		if (!running) {
			return;
		}
		setValueUnsafe(activeHigh);
		SleepUtil.sleepSeconds(onTime);

		if (!running) {
			return;
		}
		setValueUnsafe(!activeHigh);
		SleepUtil.sleepSeconds(offTime);
	}

	private void stopOnOffLoop() {
		// TODO Interrupt any background threads?
		running = false;
	}

	// Exposed operations

	/**
	 * Turn on the device.
	 * 
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public void on() throws RuntimeIOException {
		stopOnOffLoop();
		setValueUnsafe(activeHigh);
	}

	/**
	 * Turn off the device.
	 * 
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public void off() throws RuntimeIOException {
		stopOnOffLoop();
		setValueUnsafe(!activeHigh);
	}

	/**
	 * Toggle the state of the device.
	 * 
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public void toggle() throws RuntimeIOException {
		stopOnOffLoop();
		setValueUnsafe(!delegate.getValue());
	}

	/**
	 * Get the device on / off status.
	 * 
	 * @return Returns true if the device is currently on.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public boolean isOn() throws RuntimeIOException {
		return activeHigh == delegate.getValue();
	}

	/**
	 * Turn the device on or off.
	 * 
	 * @param on New on/off value.
	 * @throws RuntimeIOException If an I/O error occurred.
	 */
	public void setOn(boolean on) throws RuntimeIOException {
		stopOnOffLoop();
		setValueUnsafe(activeHigh & on);
	}

	/**
	 * Unsafe operation that has no synchronisation checks and doesn't compensate
	 * for active low logic. Included primarily for performance tests.
	 * 
	 * @param value The new value
	 * @throws RuntimeIOException If an I/O error occurs
	 */
	public void setValueUnsafe(boolean value) throws RuntimeIOException {
		delegate.setValue(value);
	}

	/**
	 * Set the output value to true if value != 0, does not compensate for active low logic
	 * @param value The new value
	 */
	@Override
	public void setValue(float value) {
		setValueUnsafe(value != 0);
	}

	/**
	 * Toggle the device on-off.
	 * 
	 * @param onTime     On time in seconds.
	 * @param offTime    Off time in seconds.
	 * @param n          Number of iterations. Set to &lt;0 to blink indefinitely.
	 * @param background If true start a background thread to control the blink and
	 *                   return immediately. If false, only return once the blink
	 *                   iterations have finished.
	 * @param stopAction Action to perform when the loop finishes
	 * @throws RuntimeIOException If an I/O error occurs
	 */
	public void onOffLoop(float onTime, float offTime, int n, boolean background, Action stopAction)
			throws RuntimeIOException {
		stopOnOffLoop();
		if (background) {
			DiozeroScheduler.getDaemonInstance().execute(() -> onOffLoop(onTime, offTime, n, stopAction));
		} else {
			onOffLoop(onTime, offTime, n, stopAction);
		}
	}
}
