/**
 * 
 */
package com.diozero.imu.drivers.invensense.icm20948;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.diozero.api.DeviceInterface;
import com.diozero.api.I2CDevice;
import com.diozero.api.RuntimeIOException;
import com.diozero.imu.drivers.invensense.AccelFullScaleRange;
import com.diozero.imu.drivers.invensense.GyroFullScaleRange;
import com.diozero.util.SleepUtil;

/**
 * A driver for the ICM20948 MPU. Written by accounting: 1) the existing MPU9250
 * diozero driver; 2) the ICM20948 datasheet available at
 * https://3cfeqx1hf82y3xcoull08ihx-wpengine.netdna-ssl.com/wp-content/uploads/2016/06/DS-000189-ICM-20948-v1.3.pdf
 * 3) the ICM20948 Python Library by Pimoroni, at
 * https://github.com/pimoroni/icm20948-python
 * 
 * @author bonino
 *
 */
public class Icm20948Driver implements DeviceInterface
{
    // Offset and sensitivity - defined in electrical characteristics, and
    // TEMP_OUT_H/L of datasheet
    // the sensitivity of the temperature sensor in LSB/°C
    public static final double TEMPERATURE_SENSOR_SENSITIVITY = 333.87;
    // the room temperature offset (LSB)
    public static final double TEMPERATURE_SENSOR_ROOM_OFFSET = 0;
    // the roomt temperature offset in degrees
    public static final double TEMPERATURE_SENSOR_DEGREES_OFFSET = 21;
    // the actual device address
    private int deviceAddress;
    // the I2C device instance used to communicate with the real device
    private I2CDevice i2cDevice;

    public Icm20948Driver(int controller) throws RuntimeIOException
    {
        this(controller, Icm20948Constants.ICM20948_DEFAULT_ADDRESS);
    }

    public Icm20948Driver(int controller, int deviceAddress)
            throws RuntimeIOException
    {
        this.i2cDevice = I2CDevice.builder(deviceAddress)
                .setController(controller).build();
        this.deviceAddress = deviceAddress;
    }

    @Override
    public void close() throws RuntimeIOException
    {
        this.i2cDevice.close();
    }

    public void mpuInit() throws RuntimeIOException
    {
        // set bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);

        // check device who I am
        if (this.i2cDevice.readByteData(Icm20948Constants.WHO_AM_I)
                != Icm20948Constants.DEVICE_ID)
        {
            throw new RuntimeIOException(
                    "Invalid WHO-AM-I response, unable to find ICM20948");
        }

        // put the device to sleep
        this.i2cDevice.writeByteData(Icm20948Constants.PWR_MGMT_1,
                Icm20948Constants.BIT_DEVICE_RESET);
        // wait for a while
        SleepUtil.sleepMillis(100);
        // bring the device on
        this.i2cDevice.writeByteData(Icm20948Constants.PWR_MGMT_1,
                Icm20948Constants.CLKSEL_PLL);
        // enable all sensors
        this.i2cDevice.writeByteData(Icm20948Constants.PWR_MGMT_2, (byte) 0x00);

        // gyroscope configuration
        this.setGyroSampleRate(100);
        this.setGyroLowPassFilter(Icm20948GyroLpfFrequency.GYRO_12HZ);
        this.setGyroFsr(GyroFullScaleRange.INV_FSR_250DPS);

        // accelerometer configuration
        this.setAccelerometerSampleRate(100);
        this.setAccelerometerLowPassFilter(
                Icm20948AccelerometerLpfFrequency.ACCEL_12HZ);
        this.setAccelerometerFsr(AccelFullScaleRange.INV_FSR_2G);

        // --- Magnetometer (I2C slave) ----
        // set bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);

        // interrupt latch+interrupt clear
        this.i2cDevice.writeByteData(Icm20948Constants.INT_PIN_CFG,
                (byte) (Icm20948Constants.BIT_INT1_LATCH_EN
                        | Icm20948Constants.BIT_INT_ANYRD_2CLEAR));

        // set bank 3
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_3);
        // set I2C master frequency
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_MST_CTRL,
                Icm20948Constants.I2C_MST_CLK_345_60KHZ_46_67);
        // enable access to slave 0 every 1/(1+I2C_SLC4_DLY) samples
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_MST_DELAY_CTRL,
                Icm20948Constants.BIT_I2C_SLV0_DELAY_EN);

        if (this.readRegisterFromMagnetometer(Ak09916Constants.WIA)
                != Ak09916Constants.CHIP_ID)
        {
            // unable to find magnetometer
            throw new RuntimeIOException(
                    "Unable to find the AK09916 magnetometer.");
        }

        // reset the magnetometer
        this.writeRegisterToMagnetometer(Ak09916Constants.CNTL3,
                Ak09916Constants.BIT_SRST);
        // wait for reset to complete
        while (this.readRegisterFromMagnetometer(Ak09916Constants.CNTL3)
                == 0x01)
        {
            SleepUtil.sleepMillis(1);
        }

    }

    /**
     * Read raw gyro data directly from the registers.
     * 
     * @return Raw data in hardware units.
     */
    public int[] getGyroReg()
    {

        // set bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // read all accelerometer registers (big endian)
        ByteBuffer gyroBuffer = this.i2cDevice
                .readI2CBlockDataByteBuffer(Icm20948Constants.GYRO_XOUT_H, 6);

        // extract the x,y,z values
        short x = gyroBuffer.getShort();
        short y = gyroBuffer.getShort();
        short z = gyroBuffer.getShort();

        // return the raw data
        return new int[] { x, y, z };

    }

    /**
     * Return gyroscope data in Degrees Per Second (dps).
     * 
     * @return The gyroscope data along the three axis (x,y,z) in dps.
     */
    public double[] getGyroData()
    {
        GyroFullScaleRange range = this.getGyroFsr();
        double[] gyroData =
                Arrays.stream(this.getGyroReg()).mapToDouble(gyroReg -> {
                    return gyroReg * range.getScale();
                }).toArray();
        return gyroData;
    }

    /**
     * Returns raw accelerometer data directly from the ICM20948 registers.
     * 
     * @return Raw data in hardware units.
     */
    public int[] getAccelerometerReg()
    {
        // set bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // read all accelerometer registers (big endian)
        ByteBuffer accelBuffer = this.i2cDevice
                .readI2CBlockDataByteBuffer(Icm20948Constants.ACCEL_XOUT_H, 6);

        // extract the x,y,z values
        short x = accelBuffer.getShort();
        short y = accelBuffer.getShort();
        short z = accelBuffer.getShort();

        // return the raw data
        return new int[] { x, y, z };
    }

    /**
     * Returns the accelerometer data in g.
     * 
     * @return The accelerometer data along the three axis (x,y,z) in g.
     */
    public double[] getAccelerometerData()
    {
        AccelFullScaleRange range = this.getAccelerometerFsr();
        double[] accelData = Arrays.stream(this.getAccelerometerReg())
                .mapToDouble(accelReg -> {
                    return accelReg * range.getScale();
                }).toArray();
        return accelData;
    }

    /**
     * Returns the raw temperature sensor data directly from the ICM20948
     * registers.
     * 
     * @return Raw data in hardware units.
     */
    public short getTemperatureReg()
    {
        // set bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // read temperature register
        ByteBuffer tempRegBuffer = this.i2cDevice
                .readI2CBlockDataByteBuffer(Icm20948Constants.TEMP_OUT_H, 2);
        // wrap as a short
        return tempRegBuffer.getShort();
    }

    /**
     * Provides the temperature of the sensor die in Celsius degrees.
     * 
     * @return The temperature of the ICM20948 die, in Celsius degrees.
     */
    public double getTemperature()
    {

        return (((double) this.getTemperatureReg()
                - TEMPERATURE_SENSOR_ROOM_OFFSET)
                / TEMPERATURE_SENSOR_SENSITIVITY)
                + TEMPERATURE_SENSOR_DEGREES_OFFSET;

    }

    /**
     * Provide the raw compass readings directly from the AK09916 registers.
     * Wait at most 1s for the AK09916 to be ready.
     * 
     * @return The raw data in hardware units.
     */
    public int[] getCompassReg()
    {
        return this.getCompassReg(1000);
    }

    /**
     * Provide the raw compass readings directly from the AK09916 registers
     * 
     * @param timeoutMillis
     *            The timeout to wait for data to be ready.
     * @return The raw data in hardware units.
     */
    public int[] getCompassReg(long timeoutMillis)
    {
        // trigger single measurement mode
        this.writeRegisterToMagnetometer(Ak09916Constants.CNTL2, (byte) 0x01);
        // wait for the magnetometer data to be ready
        long startTime = System.currentTimeMillis();
        while (!this.isMagnetometerReady())
        {
            if ((System.currentTimeMillis() - startTime) > timeoutMillis)
            {
                throw new RuntimeIOException(
                        "Timeout waiting for the Magnetometer to be ready");
            }
        }

        // read magnetometer measurements
        ByteBuffer magnetometerRegs =
                this.readRegistersFromMagnetometer(Ak09916Constants.HXL, 6);
        magnetometerRegs.order(ByteOrder.LITTLE_ENDIAN);

        // extract x,y,z
        short x = magnetometerRegs.getShort();
        short y = magnetometerRegs.getShort();
        short z = magnetometerRegs.getShort();

        // return the extracted measures
        return new int[] { x, y, z };

    }

    /**
     * Provides the magnetometer data in uT.
     * 
     * @return The magnetometer data along the 3 axis (x,y,z), in uT.
     */
    public double[] getCompassData()
    {
        double[] compassData =
                Arrays.stream(this.getCompassReg()).mapToDouble(accelReg -> {
                    return accelReg * Ak09916Constants.SENSITIVITY_SCALE_FACTOR;
                }).toArray();
        return compassData;
    }

    /**
     * Set the Gyroscope full-scale range
     * 
     * @param range
     *            The range as one of the {@link GyroFullScaleRange} values.
     *            Actual bits written to the ICM20948 are taken from
     *            {@link Icm20948Constants}.
     */
    public void setGyroFsr(GyroFullScaleRange range)
    {
        // set the scale range
        switch (range)
        {
            case INV_FSR_250DPS:
            {
                this.setAccelGyroFsr(Icm20948Constants.GYRO_FS_250DPS,
                        Icm20948Constants.GYRO_CONFIG_1);
                break;
            }
            case INV_FSR_500DPS:
            {
                this.setAccelGyroFsr(Icm20948Constants.GYRO_FS_500DPS,
                        Icm20948Constants.GYRO_CONFIG_1);
                break;
            }
            case INV_FSR_1000DPS:
            {
                this.setAccelGyroFsr(Icm20948Constants.GYRO_FS_1000DPS,
                        Icm20948Constants.GYRO_CONFIG_1);
                break;
            }
            case INV_FSR_2000DPS:
            default:
            {
                this.setAccelGyroFsr(Icm20948Constants.GYRO_FS_2000DPS,
                        Icm20948Constants.GYRO_CONFIG_1);
                break;
            }
        }
    }

    /**
     * Read the current gyroscope scale range from the ICM20948 GYRO_CONFIG_1
     * register.
     * 
     * @return The gyroscope scale range as a {@link GyroFullScaleRange}
     *         instance.
     */
    public GyroFullScaleRange getGyroFsr()
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // read gyroscope configuration register
        byte gyroConfig =
                this.i2cDevice.readByteData(Icm20948Constants.GYRO_CONFIG_1);
        // extract the gyroscope fsr
        byte gyroFsr = (byte) ((gyroConfig & 0x06) >> 1);
        // the gyro fsr
        GyroFullScaleRange fsr = null;
        // switch based on gyro fsr
        switch (gyroFsr)
        {
            case 0:
            {
                fsr = GyroFullScaleRange.INV_FSR_250DPS;
                break;
            }
            case 1:
            {
                fsr = GyroFullScaleRange.INV_FSR_500DPS;
                break;
            }
            case 2:
            {
                fsr = GyroFullScaleRange.INV_FSR_1000DPS;
                break;
            }
            case 3:
            {
                fsr = GyroFullScaleRange.INV_FSR_2000DPS;
                break;
            }
            default:
            {
                // do nothing
            }
        }

        if (fsr == null)
        {
            throw new RuntimeIOException("Unable to read Gyroscope FSR.");
        }

        return fsr;
    }

    /**
     * Set the gyroscope sample rate in Hz. The sample rate is computed as
     * 1125/(1+GYRO_SMPLRT_DIV)Hz where GYRO_SMPLRT_DIV is 0...255
     * 
     * @param rateHz
     *            The desired rate in Hz.
     */
    public void setGyroSampleRate(int rateHz)
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // unsigned byte
        int gyroSampleRateDivider = 0x00FF & ((1125 / rateHz) - 1);
        this.i2cDevice.writeByteData(Icm20948Constants.GYRO_SMPLRT_DIV,
                (byte) gyroSampleRateDivider);
    }

    /**
     * Set the low-pass filter cut-off frequency and implicitly enables the low
     * pass filter on gyroscope data.
     * 
     * @param frequency
     *            The LPF cut-off frequency (at 3dB).
     */
    public void setGyroLowPassFilter(Icm20948GyroLpfFrequency frequency)
    {
        this.setGyroLowPassFilter(true, frequency);
    }

    /**
     * Enables/ disables the gyroscope low-pass filter, leaving the cut-off
     * frequency unchanged. I used to enable the filter, ensure that the right
     * frequency has been set on the gyro configuration.
     * 
     * @param enable
     *            True if the low-pass filter on gyroscope data should be
     *            enabled, false otherwise.
     */
    public void setGyroLowPassFilter(boolean enable)
    {
        this.setGyroLowPassFilter(enable, null);
    }

    /**
     * Sets the low pass filter on gyroscope data at the given frequency
     * 
     * @param enabled
     *            True if the low-pass filter should be enabled, false
     *            otherwise.
     * @param frequency
     *            The cut-off frequency (at 3dB) of the low-pass filter.
     */
    public void setGyroLowPassFilter(boolean enabled,
            Icm20948GyroLpfFrequency frequency)
    {
        this.setAccGyroLowPassFilter(enabled,
                frequency != null ? frequency.getValue() : 0,
                Icm20948Constants.GYRO_CONFIG_1);
    }

    public void setAccelerometerFsr(AccelFullScaleRange range)
    {
        // set the scale range
        switch (range)
        {
            case INV_FSR_2G:
            {
                this.setAccelGyroFsr(Icm20948Constants.ACCEL_FS_2G,
                        Icm20948Constants.ACCEL_CONFIG);
                break;
            }
            case INV_FSR_4G:
            {
                this.setAccelGyroFsr(Icm20948Constants.ACCEL_FS_4G,
                        Icm20948Constants.ACCEL_CONFIG);
                break;
            }
            case INV_FSR_8G:
            {
                this.setAccelGyroFsr(Icm20948Constants.ACCEL_FS_8G,
                        Icm20948Constants.ACCEL_CONFIG);
                break;
            }
            case INV_FSR_16G:
            default:
            {
                this.setAccelGyroFsr(Icm20948Constants.ACCEL_FS_16G,
                        Icm20948Constants.ACCEL_CONFIG);
                break;
            }
        }
    }

    public AccelFullScaleRange getAccelerometerFsr()
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // read gyroscope configuration register
        byte accelConfig =
                this.i2cDevice.readByteData(Icm20948Constants.ACCEL_CONFIG);
        // extract the gyroscope fsr
        byte accelFsr = (byte) ((accelConfig & 0x06) >> 1);
        // the gyro fsr
        AccelFullScaleRange fsr = null;
        // switch based on gyro fsr
        switch (accelFsr)
        {
            case 0:
            {
                fsr = AccelFullScaleRange.INV_FSR_2G;
                break;
            }
            case 1:
            {
                fsr = AccelFullScaleRange.INV_FSR_4G;
                break;
            }
            case 2:
            {
                fsr = AccelFullScaleRange.INV_FSR_8G;
                break;
            }
            case 3:
            {
                fsr = AccelFullScaleRange.INV_FSR_16G;
                break;
            }
            default:
            {
                // do nothing
            }
        }

        if (fsr == null)
        {
            throw new RuntimeIOException("Unable to read Accelerometer FSR.");
        }

        return fsr;
    }

    /**
     * Set the accelerometer sampling rate in Hz. The sample rate is computed as
     * 1125/(1+ACCEL_SMPLRT_DIV)Hz where ACCEL_SMPLRT_DIV_1 is 0...4096
     * 
     * @param rateHz
     *            The desired rate in Hz.
     */
    public void setAccelerometerSampleRate(int rateHz)
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // 12bit integer
        int gyroSampleRateDivider = 0x0FFF & ((1125 / rateHz) - 1);

        // read the ACCEL_SMPLRT_1 register to set the MSB of the accelerometer
        // sample rate
        byte currentMsb = this.i2cDevice
                .readByteData(Icm20948Constants.ACCEL_SMPLRT_DIV_1);
        // zero the last 4 bits
        currentMsb = (byte) (currentMsb & 0xF0);
        // set the actual msb
        currentMsb = (byte) (currentMsb | (gyroSampleRateDivider >> 8));
        // update the MSB
        this.i2cDevice.writeByteData(Icm20948Constants.ACCEL_SMPLRT_DIV_1,
                currentMsb);
        // update the LSB
        this.i2cDevice.writeByteData(Icm20948Constants.ACCEL_SMPLRT_DIV_2,
                (byte) (0x00FF & gyroSampleRateDivider));
    }

    /**
     * Set the low-pass filter cut-off frequency and implicitly enables the low
     * pass filter on accelerometer data.
     * 
     * @param frequency
     *            The LPF cut-off frequency (at 3dB).
     */
    public void setAccelerometerLowPassFilter(
            Icm20948AccelerometerLpfFrequency frequency)
    {
        this.setAccelerometerLowPassFilter(true, frequency);
    }

    /**
     * Enables/ disables the accelerometer low-pass filter, leaving the cut-off
     * frequency unchanged. I used to enable the filter, ensure that the right
     * frequency has been set on the accelerometer configuration.
     * 
     * @param enable
     *            True if the low-pass filter on accelerometer data should be
     *            enabled, false otherwise.
     */
    public void setAccelerometerLowPassFilter(boolean enable)
    {
        this.setAccelerometerLowPassFilter(enable, null);
    }

    /**
     * Sets the low pass filter on accelerometer data at the given frequency
     * 
     * @param enabled
     *            True if the low-pass filter should be enabled, false
     *            otherwise.
     * @param frequency
     *            The cut-off frequency (at 3dB) of the low-pass filter.
     */
    public void setAccelerometerLowPassFilter(boolean enabled,
            Icm20948AccelerometerLpfFrequency frequency)
    {
        this.setAccGyroLowPassFilter(enabled,
                frequency != null ? frequency.getValue() : 0,
                Icm20948Constants.ACCEL_CONFIG);
    }

    /**
     * Sets the low pass filter on accelerometer data at the given frequency
     * 
     * @param enabled
     *            True if the low-pass filter should be enabled, false
     *            otherwise.
     * @param frequency
     *            The cut-off frequency (at 3dB) of the low-pass filter.
     */
    private void setAccGyroLowPassFilter(boolean enabled, byte frequency,
            int register)
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // read the current configuration value
        byte currentConfig = this.i2cDevice.readByteData(register);
        byte newConfig = currentConfig;
        // if enabled, set the frequency value
        if (enabled)
        {
            if (frequency > 0)
            {
                // zero the bit to modify
                // 11000110
                newConfig = (byte) (newConfig & 0xC6);
                // set the frequency
                newConfig = (byte) (newConfig | frequency);
            }
            else
            {
                // just enable
                // zero the bit to modify
                // 11000110
                newConfig = (byte) (newConfig & 0xFE);
                // set the frequency
                newConfig = (byte) (newConfig | 0x01);
            }
        }
        else
        {
            // zero the enable bit
            newConfig = (byte) (newConfig & 0xFE);
        }

        // store the frequency setting
        if (currentConfig != newConfig)
        {
            this.i2cDevice.writeByteData(register, newConfig);
        }
    }

    private void setAccelGyroFsr(byte fsr, int registerAddress)
    {
        // select bank 2
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_2);
        // get the current gyro configuration
        byte currentConfig = this.i2cDevice.readByteData(registerAddress);
        // zero the 2 bits reserved for gyro config.
        byte newConfig = (byte) (currentConfig & 0xF9);

        // set the scale
        newConfig = (byte) (newConfig | fsr);

        // set the gyro configuration if different from the current one
        if (currentConfig != newConfig)
        {
            this.i2cDevice.writeByteData(registerAddress, newConfig);
        }
    }

    private byte readRegisterFromMagnetometer(int register)
    {
        // select bank 3
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_3);
        // set the slave address to the one of AK09916 (read-mode)
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_ADDR,
                (Ak09916Constants.I2C_ADDR | 0x80));
        // set the register address from which starting to read
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_REG, register);
        // data out
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_DO, 0xFF);
        // enable read 1 byte from the slave
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_CTRL,
                (Icm20948Constants.BIT_I2C_SLV0_EN | 0x01));

        // switch to bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // trigger I/O on the I2C bus
        this.triggerMagnetometerIO();
        // read extracted data
        return this.i2cDevice
                .readByteData(Icm20948Constants.EXT_SLV_SENS_DATA_00);
    }

    private ByteBuffer readRegistersFromMagnetometer(int register, int length)
    {
        // guard condition
        if (length > 15)
        {
            throw new RuntimeIOException(
                    "Exceeded maximum allowed number of registers to be read (15), given: "
                            + length);
        }
        // select bank 3
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_3);
        // enable read n byte from the slave in the library at
        // https: // github.com/pimoroni/icm20948-python/blob/
        // 3250fa88c9c03653c1be05e7a80c95c1329df936/library/icm20948/__init__.py#L157
        // the register content is Icm20948Constants.BIT_I2C_SLV0_EN | 0x08 |
        // length
        // but reading the ICM20948 application notes the 0x08 OR seems wrong.
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_CTRL,
                (Icm20948Constants.BIT_I2C_SLV0_EN | length));
        // set the slave address to the one of AK09916 (read-mode)
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_ADDR,
                (Ak09916Constants.I2C_ADDR | 0x80));
        // set the register address from which starting to read
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_REG, register);
        // data out
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_DO, 0xFF);

        // switch to bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // trigger I/O on the I2C bus
        this.triggerMagnetometerIO();
        // read extracted data
        return this.i2cDevice.readI2CBlockDataByteBuffer(
                Icm20948Constants.EXT_SLV_SENS_DATA_00, length);
    }

    private void writeRegisterToMagnetometer(int register, byte value)
    {
        // select bank 3
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_3);

        // set the slave address to the one of AK09916 (write mode)
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_ADDR,
                (Ak09916Constants.I2C_ADDR));
        // set the register address from which starting to read
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_REG, register);
        // data out
        this.i2cDevice.writeByteData(Icm20948Constants.I2C_SLV0_DO, value);

        // switch to bank 0
        this.i2cDevice.writeByteData(Icm20948Constants.REG_BANK_SEL,
                Icm20948Constants.BANK_SEL_0);
        // trigger I/O on the I2C bus
        this.triggerMagnetometerIO();
    }

    /**
     * Trigger I/O by bringing the I2C_MST_EN at 1 for 5 millis
     */
    private void triggerMagnetometerIO()
    {
        byte userControl =
                this.i2cDevice.readByteData(Icm20948Constants.USER_CTRL);
        this.i2cDevice.writeByteData(Icm20948Constants.USER_CTRL,
                (userControl | Icm20948Constants.BIT_I2C_MST_EN));
        SleepUtil.sleepMillis(5);
        this.i2cDevice.writeByteData(Icm20948Constants.USER_CTRL, userControl);
    }

    /**
     * Check if the magnetometer is ready.
     * 
     * @return True if the magnetometer is ready, false otherwise.
     */
    private boolean isMagnetometerReady()
    {
        return (this.readRegisterFromMagnetometer(Ak09916Constants.ST1) & 0x01)
                > 0;
    }

}
