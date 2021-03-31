/**
 * 
 */
package com.diozero.imu.drivers.invensense.icm20948;

/**
 * An enumeration representing possible cut-off frequencies for the low-pass filter on
 * gyroscope data.
 * 
 * @author bonino
 *
 */
public enum Icm20948GyroLpfFrequency
{
    GYRO_361HZ(Icm20948Constants.DLPFCFG_361HZ),
    GYRO_196HZ(Icm20948Constants.DLPFCFG_196HZ),
    GYRO_151HZ(Icm20948Constants.DLPFCFG_151HZ),
    GYRO_119HZ(Icm20948Constants.DLPFCFG_119HZ),
    GYRO_51HZ(Icm20948Constants.DLPFCFG_51HZ),
    GYRO_24HZ(Icm20948Constants.DLPFCFG_24HZ),
    GYRO_12HZ(Icm20948Constants.DLPFCFG_12HZ),
    GYRO_6HZ(Icm20948Constants.DLPFCFG_6HZ);

    // the byte value corresponding to the enumeration instance.
    private byte value;

    private Icm20948GyroLpfFrequency(byte value)
    {
        this.value = value;
    }

    /**
     * Get the byte value to set on the ICM-20948 GYRO_CONFIG_1 register.
     * @return A byte representing the bits to set on the GYRO_CONFIG_1 register.
     */
    public byte getValue()
    {
        return this.value;
    }
}
