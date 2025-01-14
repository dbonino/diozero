package com.diozero.internal.provider.builtin;

/*
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - Core
 * Filename:     SysFsPwmOutputDevice.java  
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.tinylog.Logger;

import com.diozero.api.PwmPinInfo;
import com.diozero.api.RuntimeIOException;
import com.diozero.internal.spi.AbstractDevice;
import com.diozero.internal.spi.DeviceFactoryInterface;
import com.diozero.internal.spi.PwmOutputDeviceInterface;

public class SysFsPwmOutputDevice extends AbstractDevice implements PwmOutputDeviceInterface {
	private int gpio;
	private int pwmChip;
	private int pwmNum;
	private Path pwmRoot;
	private RandomAccessFile dutyFile;
	private int periodNs;

	public SysFsPwmOutputDevice(String key, DeviceFactoryInterface deviceFactory, int pwmChip, PwmPinInfo pinInfo,
			int frequencyHz, float initialValue) {
		super(key, deviceFactory);

		this.pwmChip = pwmChip;
		gpio = pinInfo.getDeviceNumber();
		pwmNum = pinInfo.getPwmNum();

		Path pwm_chip_root = Paths.get("/sys/class/pwm/pwmchip" + pwmChip);
		pwmRoot = pwm_chip_root.resolve("pwm" + pwmNum);

		try {
			if (!pwmRoot.toFile().exists()) {
				File export_file = pwm_chip_root.resolve("export").toFile();
				try (FileWriter writer = new FileWriter(export_file)) {
					writer.write(String.valueOf(pwmNum));
				}
			}

			dutyFile = new RandomAccessFile(pwmRoot.resolve("duty_cycle").toFile(), "rw");
		} catch (IOException e) {
			throw new RuntimeIOException("Error opening PWM #" + pwmNum, e);
		}

		updatePolarity(Polarity.NORMAL);
		setPwmFrequency(frequencyHz);

		setValue(initialValue);

		updateEnabled(true);
	}

	@Override
	protected void closeDevice() {
		try {
			updateEnabled(false);
		} catch (Exception e) {
			// Ignore
		}
		try {
			dutyFile.close();
		} catch (Exception e) {
			// Ignore
		}
		Path pwm_chip_root = Paths.get("/sys/class/pwm/pwmchip" + pwmChip);
		try (FileWriter writer = new FileWriter(pwm_chip_root.resolve("unexport").toFile())) {
			writer.write(String.valueOf(pwmNum));
		} catch (Exception e) {
			Logger.warn(e, "Error closing pwm #" + pwmNum);
		}
	}

	@Override
	public int getGpio() {
		return gpio;
	}

	@Override
	public int getPwmNum() {
		return pwmNum;
	}

	@Override
	public float getValue() throws RuntimeIOException {
		try {
			dutyFile.seek(0);
			return Integer.parseInt(dutyFile.readLine()) / (float) periodNs;
		} catch (IOException e) {
			close();
			throw new RuntimeIOException("Error getting PWM output value");
		}
	}

	@Override
	public void setValue(float value) throws RuntimeIOException {
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("Invalid value, must be 0..1");
		}

		try {
			// dutyFile.seek(0);
			dutyFile.writeBytes(Integer.toString(Math.round(value * periodNs)));
			// dutyFile.flush();
		} catch (IOException e) {
			close();
			throw new RuntimeIOException("Error setting duty for PWM #" + pwmNum, e);
		}
	}

	@Override
	public int getPwmFrequency() {
		return 1_000_000_000 / periodNs;
	}

	@Override
	public void setPwmFrequency(int frequencyHz) throws RuntimeIOException {
		// The value is represented as duty nanoseconds hence needs to be adjusted if
		// the frequency changes
		float value = getValue();
		setValue(0);
		periodNs = 1_000_000_000 / frequencyHz;
		updatePeriod(periodNs);
		setValue(value);
	}

	private void updateEnabled(boolean enabled) throws RuntimeIOException {
		Logger.debug("updateEnabled(" + enabled + ")");
		try (FileWriter writer = new FileWriter(pwmRoot.resolve("enable").toFile())) {
			writer.write(enabled ? "1" : "0");
		} catch (IOException e) {
			close();
			throw new RuntimeIOException("Error writing to enabled file: " + e, e);
		}
	}

	private void updatePeriod(int periodNs) throws RuntimeIOException {
		Logger.debug("updatePeriod(" + periodNs + ")");
		try (FileWriter writer = new FileWriter(pwmRoot.resolve("period").toFile())) {
			writer.write(Integer.toString(periodNs));
		} catch (IOException e) {
			close();
			throw new RuntimeIOException("Error writing to period file: " + e, e);
		}
	}

	private void updatePolarity(Polarity polarity) throws RuntimeIOException {
		Logger.debug("updatePolarity(" + polarity + ")");
		try (FileWriter writer = new FileWriter(pwmRoot.resolve("polarity").toFile())) {
			writer.write(polarity.getValue());
			writer.flush();
		} catch (IOException e) {
			close();
			throw new RuntimeIOException("Error writing to polarity file: " + e, e);
		}
	}

	public static enum Polarity {
		NORMAL("normal"), INVERSED("inversed");

		private String value;

		private Polarity(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
