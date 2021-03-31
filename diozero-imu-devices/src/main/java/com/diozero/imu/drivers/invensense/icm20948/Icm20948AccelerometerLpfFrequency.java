/**
 * 
 */
package com.diozero.imu.drivers.invensense.icm20948;

/**
 * An enumeration representing possible cut-off frequencies for the low-pass filter on
 * accelerometer data.
 * @author bonino
 *
 */
public enum Icm20948AccelerometerLpfFrequency
{
    ACCEL_473HZ(Icm20948Constants.ACCEL_DLPFCFG_473HZ),
    ACCEL_246HZ(Icm20948Constants.ACCEL_DLPFCFG_246HZ),
    ACCEL_112HZ(Icm20948Constants.ACCEL_DLPFCFG_112HZ),
    ACCEL_50HZ(Icm20948Constants.ACCEL_DLPFCFG_50HZ),
    ACCEL_24HZ(Icm20948Constants.ACCEL_DLPFCFG_24HZ),
    ACCEL_12HZ(Icm20948Constants.ACCEL_DLPFCFG_12HZ),
    ACCEL_6HZ(Icm20948Constants.ACCEL_DLPFCFG_6HZ);

    // the byte value corresponding to the enumeration instance.
    private byte value;

    private Icm20948AccelerometerLpfFrequency(byte value)
    {
        this.value = value;
    }

    /**
     * Get the byte value to set on the ICM-20948 ACCEL_CONFIG register.
     * 
     * @return A byte representing the bits to set on the ACCEL_CONFIG register.
     */
    public byte getValue()
    {
        return this.value;
    }
}
