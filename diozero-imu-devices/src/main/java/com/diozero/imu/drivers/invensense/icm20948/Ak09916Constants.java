/**
 * 
 */
package com.diozero.imu.drivers.invensense.icm20948;

/**
 * @author bonino
 *
 */
public class Ak09916Constants
{
    // The sensitivity scale factor of the magnetometer AK09916 as from Section
    // 3.3 of the ICM20948 datasheet.
    public static final double SENSITIVITY_SCALE_FACTOR = 0.15;
    // +32752 -> 4912uT
    public static final int MAX_RANGE = 0x7ff0;
    // -32752 -> -4912uT
    public static final int MIN_RANGE = 0x8010;

    static final byte I2C_ADDR = 0x0c;
    static final byte CHIP_ID = 0x09;
    // Device ID of AK09916. It is described in one byte and fixed value
    static final int WIA = 0x01;
    // Status 1 register
    static final int ST1 = 0x10;
    // Data overflow bit
    static final byte BIT_ST1_DOR = 0b00000010;
    // Data self.ready bit
    static final byte BIT_ST1_DRDY = 0b00000001;

    // Measurement data from 0x11 to 0x16
    // X-axis LSB
    static final int HXL = 0x11;
    // X-axis MSB
    static final int HXH = 0x12;
    // Y-axis LSB
    static final int HYL = 0x13;
    // Y-axis MSB
    static final int HYH = 0x14;
    // Z-axis LSB
    static final int HZL = 0x15;
    // Z-axis MSB
    static final int HZH = 0x16;

    static final int ST2 = 0x18;

    // Magnetic sensor overflow bit
    static final byte BIT_ST2_HOFL = 0b00001000;

    // AK09916 control
    static final int CNTL2 = 0x31;
    // Mask for Operating mode bits
    static final byte MASK_CNTL2_MODE = 0b00001111;
    // Power down mode
    static final byte CNTL2_MODE_OFF = 0x00;
    // Single measurement mode
    static final byte CNTL2_MODE_SINGLE = 0x01;
    // Continuous measurement mode 1
    static final byte CNTL2_MODE_CONT1 = 0x02;
    // Continuous measurement mode 2
    static final byte CNTL2_MODE_CONT2 = 0x04;
    // Continuous measurement mode 3
    static final byte CNTL2_MODE_CONT3 = 0x06;
    // Continuous measurement mode 4
    static final byte CNTL2_MODE_CONT4 = 0x08;
    // Self-test mode
    static final byte CNTL2_MODE_TEST = 0x10;

    // control 3 register
    static final int CNTL3 = 0x32;
    // the soft reset bit
    static final byte BIT_SRST = 0x01;
}
