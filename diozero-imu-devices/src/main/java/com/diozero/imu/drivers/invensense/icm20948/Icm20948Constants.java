package com.diozero.imu.drivers.invensense.icm20948;

/*
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - IMU device classes
 * Filename:     MPU9150Constants.java  
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

/**
 * Constants for the ICM-20948 MPU from TDK (9-axis).
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class Icm20948Constants
{

    // ICM-20948 main I2C address
    static final int ICM20948_ADDRESS_AD0_LOW = 0x68; // address pin low (GND),
                                                      // default.
    static final int ICM20948_ADDRESS_AD0_HIGH = 0x69; // address pin high (VCC)
    static final int ICM20948_DEFAULT_ADDRESS = ICM20948_ADDRESS_AD0_LOW;

    // ICM-20948 WHOAMI
    static final byte DEVICE_ID = (byte) 0xEA;

    // BIT FLAGS

    // ---- USER_CTRL Register ----
    // enable DMP 1 –Enables DMP features.
    // 0 –DMP features are disabled after the current processing round has
    // completed.
    static final byte BIT_DMP_EN = (byte) 0x80;

    // enable FIFO 1 –Enable FIFO operation mode.
    // 0 – Disable FIFO access from serial interface. To disable FIFO writes by
    // DMA, use FIFO_EN register.
    // To disable possible FIFO writes from DMP, disable the DMP.
    static final byte BIT_FIFO_EN = (byte) 0x40;
    // i2c master/slave enable 1 –Enable the I2C Master I/F module; pins ES_DA
    // and ES_SCL are isolated from pins SDA/SDI and SCL/ SCLK.
    // 0 – Disable I2C Master I/F module; pins ES_DA and ES_SCL are logically
    // driven by pins SDA/SDI and SCL/ SCLK.
    static final byte BIT_I2C_MST_EN = (byte) 0x20;
    // disable I2C bit 1 –Reset I2C Slave module and put the serial interface in
    // SPI mode only.
    static final byte BIT_I2C_IF_DIS = (byte) 0x10;
    // reset DMP 1 –Reset DMP module. Reset is asynchronous. This bit auto
    // clears after one
    // clock cycle of the internal 20MHz clock.
    static final byte BIT_DMP_RST = (byte) 0x08;
    // reset SRAM module 1 –Reset SRAM module. Reset is asynchronous. This bit
    // auto
    // clears after one clock cycle of the internal 20MHz clock.
    static final byte BIT_SRAM_RST = (byte) 0x04;
    // reset I2C Master module 1 –Reset I2C Master module. Reset is
    // asynchronous. This bit
    // auto clears after one clock cycle of the internal 20 MHz clock.NOTE: This
    // bit should
    // only be set when the I2C master has hung. If this bit is set during an
    // active I2C master transaction, the I2C slave will hang, which will
    // require the host to reset the slave.
    static final byte BIT_I2C_MST_RST = (byte) 0x02;

    // ---- LP_CONFIG Register ----
    // Operate I2C master in duty cycled mode
    static final byte BIT_I2C_MST_CYCLE = (byte) 0x40;
    // Operate ACCEL in duty cycled mode
    static final byte BIT_ACCEL_CYCLE = (byte) 0x20;
    // Operate GYRO in duty cycled mode
    static final byte BIT_GYRO_CYCLE = (byte) 0x01;

    // ---- PWR_MGMT_1 ----
    // reset the internal registers end restore the default settings, once set,
    // the bit will auto-clear
    static final byte BIT_DEVICE_RESET = (byte) 0x80;
    // when set the chip is set to sleep mode, clearing the bit wakes the chip
    // from sleep mode.
    static final byte BIT_SLEEP = (byte) 0x40;
    // low power feature
    static final byte BIT_LP_EN = (byte) 0x20;
    // disable the temperature sensor
    static final byte BIT_TEMP_DIS = (byte) 0x08;
    // clock select
    // 0 and 6 -> Internal Oscillator
    static final byte CLKSEL_INTERNAL = (byte) 0x00;
    // 1 to 5 -> PLL if ready, required to achieve full gyroscope performance
    static final byte CLKSEL_PLL = (byte) 0x05;

    // ---- PWR_MGMT_2 ----
    // disable all axes of the accelerometer (0x00) to turn them on
    static final byte BIT_DISABLE_ACCEL = (byte) 0x38;
    // disable all axes of the gyroscope
    static final byte BIT_DISABLE_GYRO = (byte) 0x07;

    // ---- INT_PIN_CFG ---
    // The bit controlling the logic level for the INT1 pin, active low if set
    // at 1, active high if set at 0.
    static final byte BIT_INT1_ACTL = (byte) 0x80;
    // The bit controlling the INT1 pin configuration, open drain if set at 1,
    // push-pull if set at 0
    static final byte BIT_INT1_OPEN = (byte) 0x40;
    // Interrupt latch. if set at 1, the pin level is held until interrupt
    // status is cleared, if 0 the interrupt pulse width is 50us
    static final byte BIT_LATCH_EN = (byte) 0x80;
    // interrupt status configuration. If set at 1, the interrupt status is
    // cleared at any read operation. On the converse, if set at 0 the interrupt
    // status is cleared only by reading the INT_STATUS register.
    static final byte BIT_INT_ANYRD_2CLEAR = (byte) 0x10;
    // FSYNC logic. If set at 1, the locig level of the FSYNC pin is active low,
    // otherwise is active high
    static final byte BIT_ACTL_FSYNC = (byte) 0x08;
    // FSYNC as interrupt. If set at 1, enables the FSYNC pin to be used as
    // interrupt. A transition to the active level described by the ACTL_FSYNC
    // bit will cause an interrupt. The status of the interrupt is read in the
    // I2C Master Status register PASS_THROUGH bit. If set at 0 disables the
    // FSYNC
    // pin from causing an interrupt.
    static final byte BIT_FSYNC_INT_MODE_EN = (byte) 0x04;
    // Disable I2C master: When asserted, the I2C_MASTER interface pins (ES_CL
    // and ES_DA) will go into ‘bypass mode’ when the I2C master interface is
    // disabled.
    static final byte BIT_BYPASS_EN = (byte) 0x02;

    // ---- INT_ENABLE ----
    // Wake on FSYNC interrupt. 1 –Enable wake on FSYNC interrupt.0 –Function is
    // disabled
    static final byte BIT_REG_WOF_EN = (byte) 0x80;
    // Wake on motion propagation. 1 –Enable interrupt for wake on motion to
    // propagate to
    // interrupt pin 1.0 –Function is disabled.
    static final byte BIT_WOM_INT_EN = (byte) 0x08;
    // PLL ready propagation. 1 –Enable PLL RDYinterrupt (PLL RDY means PLL is
    // running and in use as the clock source for the system) to propagate to
    // interrupt pin 1.0 –Function is disabled.
    static final byte BIT_PLL_RDY_EN = (byte) 0x04;
    // DMP interrupt propagation. 1 –Enable DMP interrupt to propagate to
    // interrupt pin 1.0 –Function is disabled.
    static final byte BIT_DMP_INT1_EN = (byte) 0x02;
    // I2C master interrupt propagation. 1 –Enable I2C master interrupt to
    // propagate to interrupt pin 1.0 –Function is disabled.
    static final byte BIT_I2C_MST_INT_EN = (byte) 0x01;

    // ---- INT_ENABLE_1 ----
    // Raw interrupt propagation. 1 –Enable raw data ready interrupt from any
    // sensor to propagate to interrupt pin 1.0 – Function is disabled.
    static final byte BIT_RAW_DATA_0_RDY_EN = (byte) 0x01;

    // ---- INT_ENABLE_2 ----
    // FIFO overflow propagation. 1 –Enable interrupt for FIFO overflow to
    // propagate to interrupt pin 1.0 –Function is disabled
    static final byte BIT_FIFO_OVERFLOW_EN = (byte) 0x1F;

    // ---- INT_ENABLE_2 ----
    // FIFO watermark propagation. 1 –Enable interrupt for FIFO watermark to
    // propagate to interrupt pin 1.0 –Function is disabled
    static final byte BIT_FIFO_WM_EN = (byte) 0x1F;

    // ---- I2C_MST_STATUS ----
    // Status of FSYNC interrupt –used as a way to pass an external interrupt
    // through this chip to the host. If enabled in the INT_PIN_CFG register by
    // asserting bit FSYNC_INT_MODE_EN, this will cause an interrupt. A read of
    // this register clears all status bits in this register.
    static final byte BIT_PASS_THROUGH = (byte) 0x80;
    // Asserted when I2C slave 4’s transfer is complete, will cause an interrupt
    // if bit I2C_MST_INT_EN in the INT_ENABLE register is asserted, and if the
    // SLV4_DONE_INT_EN bit is asserted in theI2C_SLV4_CTRL register.
    static final byte BIT_I2C_SLV4_DONE = (byte) 0x40;
    // Asserted when I2C slave loses arbitration of the I2C bus, will cause an
    // interrupt if bit I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_LOST_ARB = (byte) 0x20;
    // Asserted when slave 4 receives a NACK, will cause an interrupt if bit
    // I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_SLV4_NACK = (byte) 0x10;
    // Asserted when slave 3 receives a NACK, will cause an interrupt if bit
    // I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_SLV3_NACK = (byte) 0x08;
    // Asserted when slave 2 receives a NACK, will cause an interrupt if bit
    // I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_SLV2_NACK = (byte) 0x04;
    // Asserted when slave 1 receives a NACK, will cause an interrupt if bit
    // I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_SLV1_NACK = (byte) 0x02;
    // Asserted when slave 0 receives a NACK, will cause an interrupt if bit
    // I2C_MST_INT_EN in the INT_ENABLE register is asserted.
    static final byte BIT_I2C_SLV0_NACK = (byte) 0x01;

    // ---- INT_STATUS ----
    // Wake on motion interrupt occurred.
    static final byte BIT_WOM_INT = (byte) 0x08;
    // Indicates that the PLL has been enabled and is ready (delay of 4ms
    // ensures lock).
    static final byte BIT_PLL_RDY_INT = (byte) 0x04;
    // Indicates the DMP has generated INT1 interrupt.
    static final byte BIT_DMP_INT1 = (byte) 0x02;
    // IndicatesI2C master has generated an interrupt.
    static final byte BIT_I2C_MST_INT = (byte) 0x01;

    // ---- INT_STATUS_1 ----
    // Sensor Register Raw Data, from all sensors, is updated and ready to be
    // read.
    static final byte BIT_RAW_DATA_0_RDY_INT = (byte) 0x01;

    // ---- INT_STATUS_2 ----
    // FIFO Overflow interrupt occurred.
    static final byte BIT_FIFO_OVERFLOW_INT = (byte) 0x0F;

    // ---- INT_STATUS_3 ----
    // Watermark interrupt for FIFO occurred.
    static final byte BIT_FIFO_WM_INT = (byte) 0x0F;

    // ---- FIFO_EN_1 ----
    // 1 –Write EXT_SENS_DATA registers associated to SLV_3 (as determined by
    // I2C_SLV2_CTRL, I2C_SLV1_CTRL, and I2C_SL20_CTRL) to the FIFO at the
    // sample rate;0 –Function is disabled.
    static final byte BIT_SLV3_FIFO_EN = (byte) 0x08;
    // 1 –Write EXT_SENS_DATA registers associated to SLV_2 (as determined by
    // I2C_SLV0_CTRL, I2C_SLV1_CTRL, and I2C_SL20_CTRL) to the FIFO at the
    // sample rate;0 –Function is disabled.
    static final byte BIT_SLV2_FIFO_EN = (byte) 0x04;
    // 1 –Write EXT_SENS_DATA registers associated to SLV_1 (as determined by
    // I2C_SLV0_CTRL and I2C_SLV1_CTRL) to the FIFO at the sample rate;0
    // –Function is disabled.
    static final byte BIT_SLV1_FIFO_EN = (byte) 0x02;
    // 1 –Write EXT_SENS_DATA registers associated to SLV_0 (as determined by
    // I2C_SLV0_CTRL) to the FIFO at the sample rate;0 –Function is disabled.
    static final byte BIT_SLV0_FIFO_EN = (byte) 0x01;

    // ---- FIFO_EN_2 ----
    // 1 –Write ACCEL_XOUT_H, ACCEL_XOUT_L, ACCEL_YOUT_H, ACCEL_YOUT_L,
    // ACCEL_ZOUT_H, and ACCEL_ZOUT_L to the FIFO at the sample rate;0 –Function
    // is disabled.
    static final byte BIT_ACCEL_FIFO_EN = (byte) 0x10;
    // 1 –Write GYRO_ZOUT_H and GYRO_ZOUT_L to the FIFO at the sample rate.0
    // –Function is disabled.
    static final byte BIT_GYRO_Z_FIFO_EN = (byte) 0x08;
    // 1 –Write GYRO_YOUT_H and GYRO_YOUT_L to the FIFO at the sample rate.0
    // –Function is disabled.
    static final byte BIT_GYRO_Y_FIFO_EN = (byte) 0x04;
    // 1 –Write GYRO_XOUT_H and GYRO_XOUT_L to the FIFO at the sample rate.0
    // –Function is disabled.
    static final byte BIT_GYRO_X_FIFO_EN = (byte) 0x02;
    // 1 –Write TEMP_OUT_H and TEMP_OUT_L to the FIFO at the sample rate.0
    // –Function is disabled.
    static final byte BIT_TEMP_FIFO_EN = (byte) 0x01;

    // ---- FIFO_RST ----
    // S/W FIFO reset. Assert and hold to set FIFO size to 0. Assert and
    // de-assert to reset FIFO.
    static final byte BIT_FIFO_RESET = (byte) 0x1F;

    // ---- FIFO_MODE ----
    // 0 –Stream.1 – Snapshot. When set to ‘1’, when the FIFO is full,
    // additional writes will not be written to FIFO. When set to ‘0’, when the
    // FIFO is full, additional writes will be written to the FIFO, replacing
    // the oldest data.
    static final byte BIT_FIFO_MODE = (byte) 0x1F;

    // ---- FIFO_CNT[12:8] ---
    // High Bits, count indicates the number of written bytes in the
    // FIFO.Reading this byte latches the data for both FIFO_COUNTH, and
    // FIFO_COUNTL.
    static final byte BIT_FIFO_CNT_H = (byte) 0x1F;

    // ---- DATA_RDY_STATUS ----
    // Wake on FSYNC interrupt status. Cleared on read.
    static final byte BIT_WOF_STATUS = (byte) 0x80;
    // Data from sensors is copied to FIFO or SRAM. Set when sequence controller
    // kicks off on a sensor data load. Only bit 0 is relevant in a single FIFO
    // configuration. Cleared on read.
    static final byte BIT_RAW_DATA_RDY = (byte) 0x0F;

    // ---- FIFO_CFG ----
    // This bit should be set to 1 if interrupt status for each sensor is
    // required.
    static final byte BIT_FIFO_CFG = (byte) 0x01;

    // ---- REG_BANK_SEL ---
    static final byte BIT_BANK_SEL_0 = (byte) 0x00;
    static final byte BIT_BANK_SEL_1 = (byte) 0x10;
    static final byte BIT_BANK_SEL_2 = (byte) 0x20;
    static final byte BIT_BANK_SEL_3 = (byte) 0x30;

    // ---- GYRO_CONFIG_1 ----
    // Gyro low pass filter configuration[bit 5:3].
    // Rate: 1125/(1+GYRO_SMPLRT_DIV)Hz
    // where GYRO_SMPLRT_DIV is 0, 1, 2,...255
    // if BIT_GYRO_FCHOICE is 0, than the bandwidth is 12106Hz and the Rate 9000
    static final byte DLPFCFG_196HZ = (byte) 0x01;
    static final byte DLPFCFG_151HZ = (byte) 0x09;
    static final byte DLPFCFG_119HZ = (byte) 0x11;
    static final byte DLPFCFG_51HZ = (byte) 0x19;
    static final byte DLPFCFG_24Hz = (byte) 0x21;
    static final byte DLPFCFG_12HZ = (byte) 0x29;
    static final byte DLPFCFG_6HZ = (byte) 0x31;
    static final byte DLPFCFG_361HZ = (byte) 0x39;

    // Gyro Full Scale Select[bit 2:1]
    // 00 = ±250 dps
    // 01= ±500 dps
    // 10 = ±1000 dps
    // 11 = ±2000 dps
    static final byte GYRO_FS_250DPS = (byte) 0x00;
    static final byte GYRO_FS_500DPS = (byte) 0x02;
    static final byte GYRO_FS_1000DPS = (byte) 0x04;
    static final byte GYRO_FS_2000DPS = (byte) 0x06;

    // The gyro filter.
    // 0 –Bypass gyro DLPF.
    // 1 –Enable gyro DLPF.
    static final byte BIT_GYRO_FCHOICE = (byte) 0x01;

    // ---- GYRO_CONFIG_2 ----
    // bit 7:6 reserved
    // X Gyro self-test enable.
    static final byte BIT_XGYRO_CTEN = (byte) 0x20;
    // Y Gyro self-test enable.
    static final byte BIT_YGYRO_CTEN = (byte) 0x10;
    // Z Gyro self-test enable.
    static final byte BIT_ZGYRO_CTEN = (byte) 0x08;

    // GYRO Averaging filter configuration settings for low-power mode.
    static final byte GYRO_AVGCFG_1X = (byte) 0x00;
    static final byte GYRO_AVGCFG_2X = (byte) 0x01;
    static final byte GYRO_AVGCFG_4X = (byte) 0x02;
    static final byte GYRO_AVGCFG_8X = (byte) 0x03;
    static final byte GYRO_AVGCFG_16X = (byte) 0x04;
    static final byte GYRO_AVGCFG_32X = (byte) 0x05;
    static final byte GYRO_AVGCFG_64X = (byte) 0x06;
    static final byte GYRO_AVGCFG_128X = (byte) 0x07;

    // ---- ODR_ALIGN_EN ----
    // 0: Disables ODR start-time alignment.1: Enables ODR start-time alignment
    // when any of the following registers is written (with the same value or
    // with different values): GYRO_SMPLRT_DIV, ACCEL_SMPLRT_DIV_1,
    // ACCEL_SMPLRT_DIV_2, I2C_MST_ODR_CONFIG.
    static final byte BIT_ODR_ALIGN_EN = (byte) 0x01;

    // ---- ACCEL_INTEL_CTRL ----
    // Enable the WOM (Wake On Motion) logic
    static final byte BIT_ACCEL_INTEL_EN = 0x02;
    // Selects WOM algorithm.1 = Compare the current sample with the previous
    // sample.0 = Initial sample is stored, all future samples are compared to
    // the initial sample.
    static final byte ACCEL_INTEL_MODE_INT_COMPARE = 0x01;
    static final byte ACCEL_INTEL_MODE_INT_INITIALIZE = 0x00;

    // ---- ACCEL_CONFIG ----
    // Accelerometer low pass filter configuration
    // RATE (Hz) = 1125/(1+ACCEL_SMPLRT_DIV)Hz where ACCEL_SMPLRT_DIV is 0, 1,
    // 2,...4095
    static final byte ACCEL_DLPFCFG_246HZ = 0x01;
    static final byte ACCEL_DLPFCFG_112HZ = 0x11;
    static final byte ACCEL_DLPFCFG_50HZ = 0x19;
    static final byte ACCEL_DLPFCFG_24HZ = 0x21;
    static final byte ACCEL_DLPFCFG_12HZ = 0x29;
    static final byte ACCEL_DLPFCFG_6HZ = 0x31;
    static final byte ACCEL_DLPFCFG_473HZ = 0x39;

    // Accelerometer Full Scale Select:
    // 00: ±2g
    // 01: ±4g
    // 10: ±8g
    // 11: ±16g
    static final byte ACCEL_FS_2G = (byte) 0x00;
    static final byte ACCEL_FS_4G = (byte) 0x02;
    static final byte ACCEL_FS_8G = (byte) 0x04;
    static final byte ACCEL_FS_16G = (byte) 0x06;

    // Accelerometer filter
    // 0: Bypass accel DLPF.
    // 1: Enable accel DLPF
    static final byte ACCEL_FCHOICE = (byte) 0x01;

    // ---- ACCEL_CONFIG_2 ----
    // X Accel self-test enable.
    static final byte BIT_AX_ST_EN_REG = (byte) 0x10;
    // Y Accel self-test enable.
    static final byte BIT_AY_ST_EN_REG = (byte) 0x08;
    // Z Accel self-test enable.
    static final byte BIT_AZ_ZT_EN_REG = (byte) 0x04;
    // Controls the number of samples averaged in the accelerometer decimator:0:
    // Average 1 or 4 samples depending on ACCEL_FCHOICE (see Table 19). 1:
    // Average 8 samples.2: Average 16 samples.3: Average 32 samples.
    static final byte DEC3_CFG_0 = (byte) 0x00;
    static final byte DEC3_CFG_8 = (byte) 0x01;
    static final byte DEC3_CFG_16 = (byte) 0x02;
    static final byte DEC3_CFG_32 = (byte) 0x03;

    // ---- FSYNC_CONFIG ----
    // 0: Disables delay time measurement between FSYNC event and the first ODR
    // event (after FSYNC event). 1: Enables delay time measurement between
    // FSYNC event and the first ODR event (after FSYNC event).
    static final byte BIT_DELAY_TIME_EN = (byte) 0x80;
    // Enable digital deglitching of FSYNC input for Wake on FSYNC.
    static final byte BIT_WOF_DEGLITCH_EN = (byte) 0x20;
    // 0: FSYNC is a level interrupt for Wake on FSYNC.1: FSYNC is an edge
    // interrupt for Wake on FSYNC. ACTL_FSYNC is used to set the polarity of
    // the interrupt.
    static final byte BIT_WOF_EDGE_INT = (byte) 0x10;
    // Enables the FSYNC pin data to be sampled.EXT_SYNC_SET FSYNC bit location.
    static final byte EXT_SYNC_SET_DS = (byte) 0x00;
    static final byte EXT_SYNC_SET_TEMP_XOUT_L0 = (byte) 0x01;
    static final byte EXT_SYNC_SET_GYRO_XOUT_L0 = (byte) 0x02;
    static final byte EXT_SYNC_SET_GYRO_YOUT_L0 = (byte) 0x03;
    static final byte EXT_SYNC_SET_GYRO_ZOUT_L0 = (byte) 0x04;
    static final byte EXT_SYNC_SET_ACCEL_XOUT_L0 = (byte) 0x05;
    static final byte EXT_SYNC_SET_ACCEL_YOUT_L0 = (byte) 0x06;
    static final byte EXT_SYNC_SET_ACCEL_ZOUT_L0 = (byte) 0x07;

    // ---- TEMP_CONFIG ----
    // Low pass filter configuration for temperature sensor
    static final byte TEMP_DLPFCFG_7932HZ = (byte) 0x00;
    static final byte TEMP_DLPFCFG_218HZ = (byte) 0x01;
    static final byte TEMP_DLPFCFG_124HZ = (byte) 0x02;
    static final byte TEMP_DLPFCFG_66HZ = (byte) 0x03;
    static final byte TEMP_DLPFCFG_34HZ = (byte) 0x04;
    static final byte TEMP_DLPFCFG_17HZ = (byte) 0x05;
    static final byte TEMP_DLPFCFG_8Hz = (byte) 0x06;

    // ---- MOD_CTRL_USR ----
    // Enable turning on DMP in Low Power Accelerometer mode.
    static final byte BIT_REG_LP_DMP_EN = (byte) 0x01;

    // ---- I2C_MST_CTRL ----
    // Enables multi-master capability. When disabled, clocking to the
    // I2C_MST_IF can be disabled when not in use and the logic to detect lost
    // arbitration is disabled.
    static final byte BIT_MULT_MST_EN = (byte) 0x80;
    // This bit controls the I2C Master’s transition from one slave read to the
    // next slave read. 0 - There is a restart between reads. 1 -There is a stop
    // between reads.
    static final byte BIT_I2C_MST_P_NSR = (byte) 0x10;
    // Sets I2C master clock frequency
    static final byte I2C_MST_CLK_370_29KHZ_50 = (byte) 0x00;
    static final byte I2C_MST_CLK_432_00KHZ_50 = (byte) 0x03;
    static final byte I2C_MST_CLK_370_29KHZ_42_86 = (byte) 0x04;
    // static final byte I2C_MST_CLK_370_29KHZ_50 = (byte) 0x05;
    static final byte I2C_MST_CLK_345_60KHZ_40 = (byte) 0x06;
    static final byte I2C_MST_CLK_345_60KHZ_46_67 = (byte) 0x07;
    static final byte I2C_MST_CLK_304_94KHZ_47_06 = (byte) 0x08;
    // static final byte I2C_MST_CLK_432_00KHZ_50 = (byte) 0x09;
    static final byte I2C_MST_CLK_432_00KHZ_41_67 = (byte) 0x10;
    // static final byte I2C_MST_CLK_432_00KHZ_41_67 = (byte) 0x11;
    static final byte I2C_MST_CLK_471_27KHZ_45_45 = (byte) 0x12;
    // static final byte I2C_MST_CLK_432_00KHZ_50 = (byte) 0x13;
    // static final byte I2C_MST_CLK_345_60KHZ_46_67 = (byte) 0x14;
    // static final byte I2C_MST_CLK_345_60KHZ_46_67 = (byte) 0x15;

    // ---- I2C_MST_DELAY_CTRL ----
    // Delays shadowing of external sensordata until all data is received.
    static final byte BIT_DELAY_ES_SHADOW = (byte) 0x80;
    // When enabled, slave 4 will only be accessed 1/(1+I2C_SLC4_DLY) samples as
    // determined by I2C_MST_ODR_CONFIG.
    static final byte BIT_I2C_SLV4_DELAY_EN = (byte) 0x10;
    // When enabled, slave 3 will only be accessed 1/(1+I2C_SLC4_DLY) samples as
    // determined by I2C_MST_ODR_CONFIG.
    static final byte BIT_I2C_SLV3_DELAY_EN = (byte) 0x08;
    // When enabled, slave 2 will only be accessed 1/(1+I2C_SLC4_DLY) samples as
    // determined by I2C_MST_ODR_CONFIG.
    static final byte BIT_I2C_SLV2_DELAY_EN = (byte) 0x04;
    // When enabled, slave 1 will only be accessed 1/(1+I2C_SLC4_DLY) samples as
    // determined by I2C_MST_ODR_CONFIG.
    static final byte BIT_I2C_SVL1_DELAY_EN = (byte) 0x02;
    // When enabled, slave 0 will only be accessed 1/(1+I2C_SLC4_DLY) samples as
    // determined by I2C_MST_ODR_CONFIG.
    static final byte BIT_I2C_SLV0_DELAY_EN = (byte) 0x01;

    // ---- I2C_SLV0_ADDR ----
    // 1 –Transfer is a read.0 –Transfer is a write.
    static final byte BIT_I2C_SLV0_RNW = (byte) 0x80;

    // ---- I2C_SLV0_CTRL ----
    // 1 –Enable reading data from this slave at the sample rate and storing
    // data at the first available EXT_SENS_DATA register, which is always
    // EXT_SENS_DATA_00 for I2C slave 0.
    // 0 –Function is disabled for this slave.
    static final byte BIT_I2C_SLV0_EN = (byte) 0x80;
    // 1 –Swap bytes when reading both the low and high byte of a word. Note
    // there is nothing to swap after reading the first byte if I2C_SLV0_REG[0]
    // = 1, or if the last byte read has a register address lsb = 0.For example,
    // if I2C_SLV0_REG = 0x1, and I2C_SLV0_LENG = 0x4:1) The first byte read
    // from address 0x1 will be stored at EXT_SENS_DATA_00, 2) the second and
    // third bytes will be read and swapped, so the data read from address 0x2
    // will be stored at EXT_SENS_DATA_02, and the data read from address 0x3
    // will be stored at EXT_SENS_DATA_01,3) The last byte read from address 0x4
    // will be stored at EXT_SENS_DATA_03. 0 –No swapping occurs;bytes are
    // written in order read.
    static final byte BIT_I2C_SLV0_BYTE_SW = (byte) 0x40;
    // When set, the transaction does not write a register value,it will only
    // read data, or write data.
    static final byte BIT_I2C_SLV0_REG_DIS = (byte) 0x20;
    // External sensor data typically comes in as groups of two bytes.This bit
    // is used to determine if the groups are from the slave’s register address
    // 0 and 1, 2 and 3, etc.., or if the groups are address 1 and 2, 3 and 4,
    // etc.0 indicates slave register addresses 0 and 1 are grouped together
    // (odd numbered register ends the group). 1 indicates slave register
    // addresses 1 and 2 are grouped together (even numbered register ends the
    // group). This allows byte swapping of registers that are grouped starting
    // at any address.
    static final byte BIT_I2C_SLV0_GRP = (byte) 0x10;

    // ---- I2C_SLV1_ADDR ----
    // 1 –Transfer is a read.0 –Transfer is a write.
    static final byte BIT_I2C_SLV1_RNW = (byte) 0x80;

    // ---- I2C_SLV1_CTRL ----
    // 1 –Enable reading data from this slave at the sample rate and storing
    // data at the first available EXT_SENS_DATA register, which is always
    // EXT_SENS_DATA_00 for I2C slave 0.
    // 0 –Function is disabled for this slave.
    static final byte BIT_I2C_SLV1_EN = (byte) 0x80;
    // 1 –Swap bytes when reading both the low and high byte of a word. Note
    // there is nothing to swap after reading the first byte if I2C_SLV0_REG[0]
    // = 1, or if the last byte read has a register address lsb = 0.For example,
    // if I2C_SLV0_REG = 0x1, and I2C_SLV0_LENG = 0x4:1) The first byte read
    // from address 0x1 will be stored at EXT_SENS_DATA_00, 2) the second and
    // third bytes will be read and swapped, so the data read from address 0x2
    // will be stored at EXT_SENS_DATA_02, and the data read from address 0x3
    // will be stored at EXT_SENS_DATA_01,3) The last byte read from address 0x4
    // will be stored at EXT_SENS_DATA_03. 0 –No swapping occurs;bytes are
    // written in order read.
    static final byte BIT_I2C_SLV1_BYTE_SW = (byte) 0x40;
    // When set, the transaction does not write a register value,it will only
    // read data, or write data.
    static final byte BIT_I2C_SLV1_REG_DIS = (byte) 0x20;
    // External sensor data typically comes in as groups of two bytes.This bit
    // is used to determine if the groups are from the slave’s register address
    // 0 and 1, 2 and 3, etc.., or if the groups are address 1 and 2, 3 and 4,
    // etc.0 indicates slave register addresses 0 and 1 are grouped together
    // (odd numbered register ends the group). 1 indicates slave register
    // addresses 1 and 2 are grouped together (even numbered register ends the
    // group). This allows byte swapping of registers that are grouped starting
    // at any address.
    static final byte BIT_I2C_SLV1_GRP = (byte) 0x10;

    // ---- I2C_SLV2_ADDR ----
    // 1 –Transfer is a read.0 –Transfer is a write.
    static final byte BIT_I2C_SLV2_RNW = (byte) 0x80;

    // ---- I2C_SLV2_CTRL ----
    // 1 –Enable reading data from this slave at the sample rate and storing
    // data at the first available EXT_SENS_DATA register, which is always
    // EXT_SENS_DATA_00 for I2C slave 0.
    // 0 –Function is disabled for this slave.
    static final byte BIT_I2C_SLV2_EN = (byte) 0x80;
    // 1 –Swap bytes when reading both the low and high byte of a word. Note
    // there is nothing to swap after reading the first byte if I2C_SLV0_REG[0]
    // = 1, or if the last byte read has a register address lsb = 0.For example,
    // if I2C_SLV0_REG = 0x1, and I2C_SLV0_LENG = 0x4:1) The first byte read
    // from address 0x1 will be stored at EXT_SENS_DATA_00, 2) the second and
    // third bytes will be read and swapped, so the data read from address 0x2
    // will be stored at EXT_SENS_DATA_02, and the data read from address 0x3
    // will be stored at EXT_SENS_DATA_01,3) The last byte read from address 0x4
    // will be stored at EXT_SENS_DATA_03. 0 –No swapping occurs;bytes are
    // written in order read.
    static final byte BIT_I2C_SLV2_BYTE_SW = (byte) 0x40;
    // When set, the transaction does not write a register value,it will only
    // read data, or write data.
    static final byte BIT_I2C_SLV2_REG_DIS = (byte) 0x20;
    // External sensor data typically comes in as groups of two bytes.This bit
    // is used to determine if the groups are from the slave’s register address
    // 0 and 1, 2 and 3, etc.., or if the groups are address 1 and 2, 3 and 4,
    // etc.0 indicates slave register addresses 0 and 1 are grouped together
    // (odd numbered register ends the group). 1 indicates slave register
    // addresses 1 and 2 are grouped together (even numbered register ends the
    // group). This allows byte swapping of registers that are grouped starting
    // at any address.
    static final byte BIT_I2C_SLV2_GRP = (byte) 0x10;

    // ---- I2C_SLV3_ADDR ----
    // 1 –Transfer is a read.0 –Transfer is a write.
    static final byte BIT_I2C_SLV3_RNW = (byte) 0x80;

    // ---- I2C_SLV1_CTRL ----
    // 1 –Enable reading data from this slave at the sample rate and storing
    // data at the first available EXT_SENS_DATA register, which is always
    // EXT_SENS_DATA_00 for I2C slave 0.
    // 0 –Function is disabled for this slave.
    static final byte BIT_I2C_SLV3_EN = (byte) 0x80;
    // 1 –Swap bytes when reading both the low and high byte of a word. Note
    // there is nothing to swap after reading the first byte if I2C_SLV0_REG[0]
    // = 1, or if the last byte read has a register address lsb = 0.For example,
    // if I2C_SLV0_REG = 0x1, and I2C_SLV0_LENG = 0x4:1) The first byte read
    // from address 0x1 will be stored at EXT_SENS_DATA_00, 2) the second and
    // third bytes will be read and swapped, so the data read from address 0x2
    // will be stored at EXT_SENS_DATA_02, and the data read from address 0x3
    // will be stored at EXT_SENS_DATA_01,3) The last byte read from address 0x4
    // will be stored at EXT_SENS_DATA_03. 0 –No swapping occurs;bytes are
    // written in order read.
    static final byte BIT_I2C_SLV3_BYTE_SW = (byte) 0x40;
    // When set, the transaction does not write a register value,it will only
    // read data, or write data.
    static final byte BIT_I2C_SLV3_REG_DIS = (byte) 0x20;
    // External sensor data typically comes in as groups of two bytes.This bit
    // is used to determine if the groups are from the slave’s register address
    // 0 and 1, 2 and 3, etc.., or if the groups are address 1 and 2, 3 and 4,
    // etc.0 indicates slave register addresses 0 and 1 are grouped together
    // (odd numbered register ends the group). 1 indicates slave register
    // addresses 1 and 2 are grouped together (even numbered register ends the
    // group). This allows byte swapping of registers that are grouped starting
    // at any address.
    static final byte BIT_I2C_SLV3_GRP = (byte) 0x10;

    // ---- I2C_SLV4_ADDR ----
    // 1 –Transfer is a read.0 –Transfer is a write.
    static final byte BIT_I2C_SLV4_RNW = (byte) 0x80;

    // ---- I2C_SLV1_CTRL ----
    // 1 –Enable reading data from this slave at the sample rate and storing
    // data at the first available EXT_SENS_DATA register, which is always
    // EXT_SENS_DATA_00 for I2C slave 0.
    // 0 –Function is disabled for this slave.
    static final byte BIT_I2C_SLV4_EN = (byte) 0x80;
    // 1 –Swap bytes when reading both the low and high byte of a word. Note
    // there is nothing to swap after reading the first byte if I2C_SLV0_REG[0]
    // = 1, or if the last byte read has a register address lsb = 0.For example,
    // if I2C_SLV0_REG = 0x1, and I2C_SLV0_LENG = 0x4:1) The first byte read
    // from address 0x1 will be stored at EXT_SENS_DATA_00, 2) the second and
    // third bytes will be read and swapped, so the data read from address 0x2
    // will be stored at EXT_SENS_DATA_02, and the data read from address 0x3
    // will be stored at EXT_SENS_DATA_01,3) The last byte read from address 0x4
    // will be stored at EXT_SENS_DATA_03. 0 –No swapping occurs;bytes are
    // written in order read.
    static final byte BIT_I2C_SLV4_BYTE_SW = (byte) 0x40;
    // When set, the transaction does not write a register value,it will only
    // read data, or write data.
    static final byte BIT_I2C_SLV4_REG_DIS = (byte) 0x20;
    // External sensor data typically comes in as groups of two bytes.This bit
    // is used to determine if the groups are from the slave’s register address
    // 0 and 1, 2 and 3, etc.., or if the groups are address 1 and 2, 3 and 4,
    // etc.0 indicates slave register addresses 0 and 1 are grouped together
    // (odd numbered register ends the group). 1 indicates slave register
    // addresses 1 and 2 are grouped together (even numbered register ends the
    // group). This allows byte swapping of registers that are grouped starting
    // at any address.
    static final byte BIT_I2C_SLV4_GRP = (byte) 0x10;

    // Register banks

    // ---- BANK 0 ----

    // Register to indicate to users which device is being accessed. The default
    // value for the ICM-20948 is 0xEA
    static final int WHO_AM_I = 0x00;
    // User control, allows to configure some of the ICM-20948 features:
    // BIT NAME
    // 7 DMP_EN
    // 6 FIFO_EN
    // 5 I2C_MST_EN
    // 4 I2C_IF_DIS
    // 3 DMP_RST
    // 2 SRAM_RST
    // 1 I2C_MST_RST
    static final int USER_CTRL = 0x03;
    // Operates in duty-cycled mode
    static final int LP_CONFIG = 0x05;
    // power management
    static final int PWR_MGMT_1 = 0x06;
    // power management 2
    static final int PWR_MGMT_2 = 0x07;
    // pin configuration
    static final int INT_PIN_CFG = 0x0F;
    // interrupt enable
    static final int INT_ENABLE = 0x10;
    // interrupt enable 1
    static final int INT_ENABLE_1 = 0x11;
    // interrupt enable 2
    static final int INT_ENABLE_2 = 0x12;
    // interrupt enable 3
    static final int INT_ENABLE_3 = 0x13;
    // I2C Master status
    static final int I2C_MST_STATUS = 0x17;
    // I2C Master status
    static final int INT_STATUS = 0x19;
    // Sensor register ready
    static final int INT_STATUS_1 = 0x1A;
    // FIFO overflow
    static final int INT_STATUS_2 = 0x1B;
    // Watermark for FIFO
    static final int INT_STATUS_3 = 0x1C;
    // Delay time H. High-byte of delay time between FSYNC event and the 1st
    // gyro ODR event (after the FSYNC event). Reading DELAY_TIMEH will lock
    // DELAY_TIMEH and DELAY_TIMEL from the next update. Reading DELAY_TIMEL
    // will unlock DELAY_TIMEH and DELAY_TIMEL to take the next update due to an
    // FSYNC event.
    static final int DELAY_TIMEH = 0x28;
    // Delay time L. Low-byte of delay time between FSYNC event and the 1st gyro
    // ODR event (after the FSYNC event). Reading DELAY_TIMEH will lock
    // DELAY_TIMEH and DELAY_TIMEL from the next update. Reading DELAY_TIMEL
    // will unlock DELAY_TIMEH and DELAY_TIMEL to take the next update due to an
    // FSYNC event.Delay time in μs = (DELAY_TIMEH * 256 + DELAY_TIMEL) * 0.9645
    static final int DELAY_TIMEL = 0x29;
    // High Byte of Accelerometer X-axis data
    static final int ACCEL_XOUT_H = 0x2D;
    // Low Byte of Accelerometer X-axis data.To convert the output of the
    // accelerometer to acceleration measurement use the formula
    // below:X_acceleration = ACCEL_XOUT/Accel_Sensitivity.
    static final int ACCEL_XOUT_L = 0x2E;
    // High Byte of Accelerometer Y-axis data
    static final int ACCEL_YOUT_H = 0x2F;
    // Low Byte of Accelerometer Y-axis data.To convert the output of the
    // accelerometer to acceleration measurement use the formula
    // below:Y_acceleration = ACCEL_YOUT/Accel_Sensitivity.
    static final int ACCEL_YOUT_L = 0x30;
    // High Byte of Accelerometer Z-axis data.
    static final int ACCEL_ZOUT_H = 0x31;
    // Low Byte of Accelerometer Z-axis data.To convert the output of the
    // accelerometer to acceleration measurement use the formula
    // below:Z_acceleration = ACCEL_ZOUT/Accel_Sensitivity.
    static final int ACCEL_ZOUT_L = 0x32;
    // High Byte of GyroscopeX-axis data.
    static final int GYRO_XOUT_H = 0x33;
    // Low Byte of Gyroscope X-axis data.To convert the output of the gyroscope
    // to angular rate measurement use the formula below:X_angular_rate =
    // GYRO_XOUT/Gyro_Sensitivity
    static final int GYRO_XOUT_L = 0x34;
    // High Byte of Gyroscope Y-axis data.
    static final int GYRO_YOUT_H = 0x35;
    // Low Byte of Gyroscope Y-axis data.To convert the output of the gyroscope
    // to angular rate measurement use the formula below:Y_angular_rate =
    // GYRO_YOUT/Gyro_Sensitivity.
    static final int GYRO_YOUT_L = 0x36;
    // High Byte of Gyroscope Z-axis data.
    static final int GYRO_ZOUT_H = 0x37;
    // Low Byte of Gyroscope Z-axis data.To convert the output of the gyroscope
    // to angular rate measurement use the formula below:Z_angular_rate =
    // GYRO_ZOUT/Gyro_Sensitivity.
    static final int GYRO_ZOUT_L = 0x38;
    // High Byte of Temp sensor data.
    static final int TEMP_OUT_H = 0x39;
    // Low Byte of Temp sensor data.To convert the output of the temperature
    // sensor to degrees C use the following formula:TEMP_degC = ((TEMP_OUT
    // –RoomTemp_Offset)/Temp_Sensitivity) + 21degC.
    static final int TEMP_OUT_L = 0x3A;
    // Sensor data read from external I2C devices via the I2C master interface.
    // The data stored is controlled by the I2C_SLV(0-4)_ADDR, I2C_SLV(0-4)_REG,
    // and I2C_SLV(0-4)_CTRL registers.
    static final int EXT_SLV_SENS_DATA_00 = 0x3B;
    static final int EXT_SLV_SENS_DATA_01 = 0x3C;
    static final int EXT_SLV_SENS_DATA_02 = 0x3D;
    static final int EXT_SLV_SENS_DATA_03 = 0x3E;
    static final int EXT_SLV_SENS_DATA_04 = 0x3F;
    static final int EXT_SLV_SENS_DATA_05 = 0x40;
    static final int EXT_SLV_SENS_DATA_06 = 0x41;
    static final int EXT_SLV_SENS_DATA_07 = 0x42;
    static final int EXT_SLV_SENS_DATA_08 = 0x43;
    static final int EXT_SLV_SENS_DATA_09 = 0x44;
    static final int EXT_SLV_SENS_DATA_10 = 0x45;
    static final int EXT_SLV_SENS_DATA_11 = 0x46;
    static final int EXT_SLV_SENS_DATA_12 = 0x47;
    static final int EXT_SLV_SENS_DATA_13 = 0x48;
    static final int EXT_SLV_SENS_DATA_14 = 0x49;
    static final int EXT_SLV_SENS_DATA_15 = 0x4A;
    static final int EXT_SLV_SENS_DATA_16 = 0x4B;
    static final int EXT_SLV_SENS_DATA_17 = 0x4C;
    static final int EXT_SLV_SENS_DATA_18 = 0x4D;
    static final int EXT_SLV_SENS_DATA_19 = 0x4E;
    static final int EXT_SLV_SENS_DATA_20 = 0x4F;
    static final int EXT_SLV_SENS_DATA_21 = 0x50;
    static final int EXT_SLV_SENS_DATA_22 = 0x51;
    static final int EXT_SLV_SENS_DATA_23 = 0x52;
    // FIFO Enable 1
    static final int FIFO_EN_1 = 0x66;
    // FIFO Enable 2
    static final int FIFO_EN_2 = 0x67;
    // FIFO reset
    static final int FIFO_RESET = 0x68;
    // FIFO MODE
    static final int FIFO_MODE = 0x69;
    // BIT 7:5 reserved. High Bits, count indicates the number of written bytes
    // in the
    // FIFO.Reading this byte latches the data for both FIFO_COUNTH, and
    // FIFO_COUNTL.
    static final int FIFO_COUNTH = 0x70;
    // Low bits, count indicates the number of written bytes in the FIFO.
    static final int FIFO_COUNTL = 0x71;
    // FIFO_R/W. Reading from or writing to this register actually reads/writes
    // the FIFO. For example, to write a byte to the FIFO, write the desired
    // byte value to FIFO_R_W[7:0]. To read a byte from the FIFO, perform a
    // register read operation and access the result in FIFO_R_W[7:0].
    static final int FIFO_R_W = 0x72;
    // Data ready status
    static final int DATA_RDY_STATUS = 0x74;
    // FIFO_CFG
    static final int FIFO_CFG = 0x76;
    // Bank selection
    static final int REG_BANK_SEL = 0x7F;

    // ---- BANK 1 (USR) ----

    // The value in this register indicates the self-test output generated
    // during manufacturing tests. This value is to be used to check against
    // subsequent self-test outputs performed by the end user.
    static final int SELF_TEST_X_GIRO = 0x02;
    // The value in this register indicates the self-test output generated
    // during manufacturing tests. This value is to be used to check against
    // subsequent self-test outputs performed by the end user.
    static final int SELF_TEST_Y_GYRO = 0x03;
    // The value in this register indicates the self-test output generated
    // during manufacturing tests. This value is to be used to check against
    // subsequent self-test outputs performed by the end user.
    static final int SELF_TEST_Z_GYRO = 0x04;
    // Contains self-test data for the X Accelerometer.
    static final int SELF_TEST_X_ACCEL = 0x0E;
    // Contains self-test data for the Y Accelerometer.
    static final int SELF_TEST_Y_ACCEL = 0x0F;
    // Contains self-test data for the Z Accelerometer.
    static final int SELF_TEST_Z_ACCEL = 0x10;
    // Upper bits of the X accelerometer offset cancellation.
    static final int XA_OFFS_H = 0x14;
    // Lower bits of the X accelerometer offset cancellation. Bit 0 is reserved.
    static final int XA_OFFS_L = 0x15;
    // Upper bits of the Y accelerometer offset cancellation.
    static final int YA_OFFS_H = 0x17;
    // Lower bits of the Y accelerometer offset cancellation. Bit 0 is reserved.
    static final int YA_OFFS_L = 0x18;
    // Upper bits of the Y accelerometer offset cancellation.
    static final int ZA_OFFS_H = 0x1A;
    // Lower bits of the Y accelerometer offset cancellation. Bit 0 is reserved.
    static final int ZA_OFFS_L = 0x1B;
    // System PLL clock period error (signed, [-10%, +10%]).
    static final int TIMEBASE_CORRECTION_PLL = 0x28;

    // ---- BANK 2 (USR) ----
    // Gyro sample rate divider. Divides the internal sample rate to generate
    // the sample rate that controls sensor data output rate, FIFO sample rate,
    // and DMP sequence rate. NOTE: This register is only effective when FCHOICE
    // = 1’b1 (FCHOICE_B register bit is 1’b0), and (0 < DLPF_CFG < 7).ODR is
    // computed as follows:1.1kHz/(1+GYRO_SMPLRT_DIV[7:0].
    static final int GYRO_SMPLRT_DIV = 0x00;
    // GYRO_CONFIG_1
    static final int GYRO_CONFIG_1 = 0x01;
    // GYRO_CONFIG_2
    static final int GYRO_CONFIG_2 = 0x02;
    // Upper byte of X gyro offset cancellation.
    static final int XG_OFFS_USRH = 0x03;
    // Lower byte of X gyro offset cancellation.
    static final int XG_OFFS_USRL = 0x04;
    // Upper byte of Y gyro offset cancellation.
    static final int YG_OFFS_USRH = 0x05;
    // Lower byte of Y gyro offset cancellation.
    static final int YG_OFFS_USRL = 0x06;
    // Upper byte of Z gyro offset cancellation.
    static final int ZG_OFFS_USRH = 0x07;
    // Lower byte of Z gyro offset cancellation
    static final int ZG_OFFS_USRL = 0x08;
    // ODR start time alignment
    static final int ODR_ALIGN_EN = 0x09;
    // MSB for ACCEL sample rate div.
    static final int ACCEL_SMPLRT_DIV_1 = 0x10;
    // LSB for ACCEL sample rate div.ODR is computed as
    // follows:1.125kHz/(1+ACCEL_SMPLRT_DIV[11:0]).
    static final int ACCEL_SMPLRT_DIV_2 = 0x11;
    // Accelerometer "intelligent" mode
    static final int ACCEL_INTEL_CTRL = 0x12;
    // This register holds the threshold value for the Wake on Motion Interrupt
    // for ACCEL x/y/z axes. LSB = 4mg. Range is 0mg to 1020mg.
    static final int ACCEL_WOM_THR = 0x13;
    // Accelerometer configuration
    static final int ACCEL_CONFIG = 0x14;
    static final int ACCEL_CONFIG_2 = 0x15;
    // FSYNC configuration
    static final int FSYNC_CONFIG = 0x52;
    // Temperature configuration
    static final int TEMP_CONFIG = 0x53;
    // User control
    static final int MOD_CTRL_USR = 0x54;

    // ---- BANK 3 (USR) ----
    // [Bit 3:0]. ODR configuration for external sensor when gyroscope and
    // accelerometer
    // are disabled. ODR is computed as follows:1.1 kHz/(2^((odr_config[3:0]))
    // )When gyroscope is enabled, all sensors (including I2C_MASTER) use the
    // gyroscope ODR. If gyroscope is disabled, then all sensors (including
    // I2C_MASTER) use the accelerometer ODR.
    static final int I2C_MST_ODR_CONFIG = 0x00;
    // I2C Master control
    static final int I2C_MST_CTRL = 0x01;
    // I2C Master delay control
    static final int I2C_MST_DELAY_CTRL = 0x02;
    // I2C Slave 0 Address. BIT 6:0 physical address of I2C slave 0. BIT 7 (see
    // BIT_I2C_SLV0_RNW)
    static final int I2C_SLV0_ADDR = 0x03;
    // I2C slave 0 register address from where to begin data transfer.
    static final int I2C_SLV0_REG = 0x04;
    // I2C slave 0 control
    // [Bit 3:0] Number of bytes to be read from the I2C slave 0.
    static final int I2C_SLV0_CTRL = 0x05;
    // Data out when slave 0 is set to write
    static final int I2C_SLV0_DO = 0x06;

    // I2C Slave 1 Address. BIT 6:0 physical address of I2C slave 1. BIT 7 (see
    // BIT_I2C_SLV1_RNW)
    static final int I2C_SLV1_ADDR = 0x07;
    // I2C Slave 1 register address from where to begin data transfer.
    static final int I2C_SLV1_REG = 0x08;
    // I2C Slave 1 control
    // [Bit 3:0] Number of bytes to be read from the I2C slave 1.
    static final int I2C_SLV1_CTRL = 0x09;
    // Data out when slave 1 is set to write
    static final int I2C_SLV1_DO = 0x0A;

    // I2C Slave 2 Address. BIT 6:0 physical address of I2C slave 2. BIT 7 (see
    // BIT_I2C_SLV2_RNW)
    static final int I2C_SLV2_ADDR = 0x0B;
    // I2C Slave 2 register address from where to begin data transfer.
    static final int I2C_SLV2_REG = 0x0C;
    // I2C Slave 2 control
    // [Bit 3:0] Number of bytes to be read from the I2C slave 2.
    static final int I2C_SLV2_CTRL = 0x0D;
    // Data out when slave 2 is set to write
    static final int I2C_SLV2_DO = 0x0E;
    
    // I2C Slave 3 Address. BIT 6:0 physical address of I2C slave 3. BIT 7 (see
    // BIT_I2C_SLV3_RNW)
    static final int I2C_SLV3_ADDR = 0x0F;
    // I2C Slave 3 register address from where to begin data transfer.
    static final int I2C_SLV3_REG = 0x10;
    // I2C Slave 3 control
    // [Bit 3:0] Number of bytes to be read from the I2C slave 3.
    static final int I2C_SLV3_CTRL = 0x11;
    // Data out when slave 3 is set to write
    static final int I2C_SLV3_DO = 0x12;
    
    // I2C Slave 4 Address. BIT 6:0 physical address of I2C slave 4. BIT 7 (see
    // BIT_I2C_SLV4_RNW)
    static final int I2C_SLV4_ADDR = 0x13;
    // I2C Slave 4 register address from where to begin data transfer.
    static final int I2C_SLV4_REG = 0x14;
    // I2C Slave 4 control
    // [Bit 3:0] Number of bytes to be read from the I2C slave 4.
    static final int I2C_SLV4_CTRL = 0x15;
    // Data out when slave 4 is set to write
    static final int I2C_SLV4_DO = 0x16;
    // Data read from I2C Slave 4. 
    static final int I2C_SLV4_DI = 0x17;

}
