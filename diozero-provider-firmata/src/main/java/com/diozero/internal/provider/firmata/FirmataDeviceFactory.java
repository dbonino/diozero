package com.diozero.internal.provider.firmata;

/*
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - Firmata
 * Filename:     FirmataDeviceFactory.java  
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.Pin.Mode;
import org.firmata4j.firmata.FirmataDevice;
import org.tinylog.Logger;

import com.diozero.api.DeviceAlreadyOpenedException;
import com.diozero.api.DeviceMode;
import com.diozero.api.GpioEventTrigger;
import com.diozero.api.GpioPullUpDown;
import com.diozero.api.I2CConstants;
import com.diozero.api.InvalidModeException;
import com.diozero.api.PinInfo;
import com.diozero.api.RuntimeIOException;
import com.diozero.api.SerialDevice;
import com.diozero.api.SpiClockMode;
import com.diozero.internal.spi.AnalogInputDeviceInterface;
import com.diozero.internal.spi.AnalogOutputDeviceInterface;
import com.diozero.internal.spi.BaseNativeDeviceFactory;
import com.diozero.internal.spi.GpioDigitalInputDeviceInterface;
import com.diozero.internal.spi.GpioDigitalInputOutputDeviceInterface;
import com.diozero.internal.spi.GpioDigitalOutputDeviceInterface;
import com.diozero.internal.spi.InternalI2CDeviceInterface;
import com.diozero.internal.spi.InternalSerialDeviceInterface;
import com.diozero.internal.spi.InternalSpiDeviceInterface;
import com.diozero.internal.spi.PwmOutputDeviceInterface;
import com.diozero.sbc.BoardInfo;
import com.diozero.util.PropertyUtil;

public class FirmataDeviceFactory extends BaseNativeDeviceFactory {
	public static final String DEVICE_NAME = "Firmata";

	private String portName;
	private IODevice ioDevice;

	public FirmataDeviceFactory() {
		Logger.warn("*** Do NOT use this device factory for servo control; not yet implemented!");

		portName = PropertyUtil.getProperty("FIRMATA_SERIAL_PORT", null);
		if (portName == null) {
			throw new IllegalArgumentException("Error, FIRMATA_SERIAL_PORT not set");
		}
		ioDevice = new FirmataDevice(portName);
	}

	@Override
	public void start() {
		try {
			ioDevice.start();
			Logger.info("Waiting for Firmata device '" + portName + "' to initialise");
			ioDevice.ensureInitializationIsDone();
			Logger.info("Firmata device '" + portName + "' successfully initialised");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeIOException(e);
		}
	}

	@Override
	public void shutdown() {
		Logger.trace("shutdown()");

		if (ioDevice != null) {
			try {
				ioDevice.stop();
			} catch (Exception e) {
			}
		}
	}

	IODevice getIoDevice() {
		return ioDevice;
	}

	@Override
	public String getName() {
		return DEVICE_NAME;
	}

	@Override
	protected BoardInfo lookupBoardInfo() {
		return new FirmataBoardInfo(ioDevice);
	}

	@Override
	public int getBoardPwmFrequency() {
		/*
		 * https://www.arduino.cc/en/Tutorial/SecretsOfArduinoPWM // Note that the base
		 * frequency for pins 3, 9, 10, and 11 is 31250 Hz and for pins 5 and 6 is 62500
		 * Hz switch (gpio) { case 3: case 9: case 10: case 11: return 31250; case 5:
		 * case 6: return 62500; } return -1;
		 */
		//
		return 62500;
	}

	@Override
	public void setBoardPwmFrequency(int frequency) {
		// Ignore
		Logger.warn("Not implemented");
	}

	@Override
	public AnalogInputDeviceInterface provisionAnalogInputDevice(PinInfo pinInfo) {
		// Special case - The Arduino can switch between digital and analog input hence
		// use of gpio rather than adc
		if (!pinInfo.isSupported(DeviceMode.ANALOG_INPUT)) {
			throw new InvalidModeException("Invalid mode (analog input) for pin " + pinInfo);
		}

		String key = createPinKey(pinInfo);

		// Check if this pin is already provisioned
		if (isDeviceOpened(key)) {
			throw new DeviceAlreadyOpenedException("Device " + key + " is already in use");
		}

		AnalogInputDeviceInterface device = createAnalogInputDevice(key, pinInfo);
		deviceOpened(device);

		return device;
	}

	@Override
	public GpioDigitalInputDeviceInterface createDigitalInputDevice(String key, PinInfo pinInfo, GpioPullUpDown pud,
			GpioEventTrigger trigger) throws RuntimeIOException {
		return new FirmataDigitalInputDevice(this, key, pinInfo.getDeviceNumber(), pud, trigger);
	}

	@Override
	public GpioDigitalOutputDeviceInterface createDigitalOutputDevice(String key, PinInfo pinInfo, boolean initialValue)
			throws RuntimeIOException {
		return new FirmataDigitalOutputDevice(this, key, pinInfo.getDeviceNumber(), initialValue);
	}

	@Override
	public GpioDigitalInputOutputDeviceInterface createDigitalInputOutputDevice(String key, PinInfo pinInfo,
			DeviceMode mode) throws RuntimeIOException {
		return new FirmataDigitalInputOutputDevice(this, key, pinInfo.getDeviceNumber(), mode);
	}

	@Override
	public PwmOutputDeviceInterface createPwmOutputDevice(String key, PinInfo pinInfo, int pwmFrequency,
			float initialValue) throws RuntimeIOException {
		Logger.warn("PWM frequency will be ignored - Firmata does not allow this to be specified");
		return new FirmataPwmOutputDevice(this, key, pinInfo.getDeviceNumber(), initialValue);
	}

	@Override
	public AnalogInputDeviceInterface createAnalogInputDevice(String key, PinInfo pinInfo) throws RuntimeIOException {
		return new FirmataAnalogInputDevice(this, key, pinInfo.getDeviceNumber());
	}

	@Override
	public AnalogOutputDeviceInterface createAnalogOutputDevice(String key, PinInfo pinInfo, float initialValue)
			throws RuntimeIOException {
		throw new UnsupportedOperationException("Analog output not supported by device factory '"
				+ getClass().getSimpleName() + "' on device '" + getBoardInfo().getName() + "'");
	}

	@Override
	public InternalSpiDeviceInterface createSpiDevice(String key, int controller, int chipSelect, int frequency,
			SpiClockMode spiClockMode, boolean lsbFirst) throws RuntimeIOException {
		throw new UnsupportedOperationException("SPI is not supported by device factory '" + getClass().getSimpleName()
				+ "' on device '" + getBoardInfo().getName() + "'");
	}

	@Override
	public InternalI2CDeviceInterface createI2CDevice(String key, int controller, int address,
			I2CConstants.AddressSize addressSize) throws RuntimeIOException {
		return new FirmataI2CDevice(this, key, controller, address, addressSize);
	}

	@Override
	public InternalSerialDeviceInterface createSerialDevice(String key, String deviceFile, int baud,
			SerialDevice.DataBits dataBits, SerialDevice.StopBits stopBits, SerialDevice.Parity parity,
			boolean readBlocking, int minReadChars, int readTimeoutMillis) throws RuntimeIOException {
		throw new UnsupportedOperationException("Serial communication not available in the device factory");
	}

	public static class FirmataBoardInfo extends BoardInfo {
		private IODevice ioDevice;

		public FirmataBoardInfo(IODevice ioDevice) {
			super("Firmata", "Unknown", -1, "firmata");
			this.ioDevice = ioDevice;
		}

		@Override
		public void populateBoardPinInfo() {
			for (Pin pin : ioDevice.getPins()) {
				int pin_number = pin.getIndex();
				Set<DeviceMode> supported_modes = convertModes(pin.getSupportedModes());
				addGpioPinInfo(pin_number, pin_number, supported_modes);
				if (supported_modes.contains(DeviceMode.ANALOG_INPUT)) {
					addAdcPinInfo(pin_number, pin_number);
				}
				if (supported_modes.contains(DeviceMode.ANALOG_OUTPUT)) {
					addDacPinInfo(pin_number, pin_number);
				}
			}
		}

		private static Set<DeviceMode> convertModes(Set<Mode> firmataModes) {
			Set<DeviceMode> modes = new HashSet<>();

			for (Mode firmata_mode : firmataModes) {
				switch (firmata_mode) {
				case INPUT:
					modes.add(DeviceMode.DIGITAL_INPUT);
					break;
				case OUTPUT:
					modes.add(DeviceMode.DIGITAL_OUTPUT);
					break;
				case ANALOG:
					modes.add(DeviceMode.ANALOG_INPUT);
					break;
				case PWM:
					modes.add(DeviceMode.PWM_OUTPUT);
					break;
				default:
					// Ignore
				}
			}

			return modes;
		}
	}
}
