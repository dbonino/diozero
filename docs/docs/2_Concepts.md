---
title: Concepts
nav_order: 2
permalink: /concepts.html
has_children: true
---

# Concepts

diozero is a multi-faceted “library” for interacting with low-level devices such as environmental
sensors and GPIOs. It achieves this via object-oriented APIs that abstract developers from the
complexities of low-level device interface code. An initial motivation to developing diozero
was to provide a Java equivalent to the excellent Python [gpiozero](https://gpiozero.readthedocs.io/)
library having found that existing Java libraries didn't offer such a developer-friendly experience.

The aim of this library is to encapsulate real-world devices (LEDs, buttons, sensors, motors,
displays, etc) as classes with meaningful operation names, for example, LED (on / off), LDR
(get luminosity), Button (pressed / released), Motor (forward / backwards / left / right).

This library is known to work on the following boards: all models of the Raspberry Pi, Odroid C2,
BeagleBone (Green and Black), Next Thing C.H.I.P and ASUS Tinker Board and is portable to any
Single Board Computer that can run Linux and Java 8.

{: .note-title }
> Pin Numbering 
>
> All pin numbers are device native, i.e. Broadcom for the Raspberry Pi, ASUS for the Tinker Board. Pin layouts:
> 
> * [Raspberry pi](https://pinout.xyz/).
> * [CHIP pin numbering](http://www.chip-community.org/index.php/Hardware_Information).
> * [Odroid C2 pin layout](http://www.hardkernel.com/main/products/prdt_info.php?tab_idx=2).
> * [BeagleBone Black](http://beagleboard.org/support/bone101).
> * [ASUS Tinker Board](https://www.asus.com/uk/Single-board-Computer/TINKER-BOARD/).

diozero implements a layered architecture so as to provide maximum portability.

Device API
: Classes and interfaces in `com.diozero.api` and `com.diozero.devices` that are to be used by
diozero applications. Note that all devices in `com.diozero.devices` make use of classes in `com.diozero.api`.

Base I/O API
: Interfaces and abstract classes in `com.diozero.internal.spi` that are used by `com.diozero.api` for interfacing with low-level hardware.

Provider
: Actual GPIO / I2C / SPI device communication is delegated to pluggable device providers for
maximum compatibility across different boards.
This is described in more detail in the [Providers](2_concepts/1_Providers.md#providers) section.

![diozero layers](/assets/images/Layers.png "diozero layers")

## Clean-up

The library makes use of [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) -
all devices implement `AutoCloseable` hence will get automatically closed by the
`try (Device d = new Device()) { d.doSomething(); }` statement. This is best illustrated by some 
simple examples.

LED control:

```java
try (LED led = new LED(18)) {
	led.toggle();
	SleepUtil.sleepSeconds(.5);
	led.toggle();
	SleepUtil.sleepSeconds(.5);
}
```

Turn on an LED when you press a button:

```java
try (Button button = new Button(12); LED led = new LED(18)) {
	button.whenPressed(nanoTime -> led.on());
	button.whenReleased(nanoTime -> led.off());
	SleepUtil.sleepSeconds(10);
}
```

Or a random LED flicker effect:

```java
Random random = new Random();
try (PwmLed led = new PwmLed(18)) {
	DioZeroScheduler.getNonDaemonInstance().invokeAtFixedRate(RANDOM::nextFloat, led::setValue, 50, 50, TimeUnit.MILLISECONDS);
}
```

### Shutdown

To protect against unexpected shutdown scenarios, diozero implements a [Shutdown Hook](Runtime.getRuntime().addShutdownHook())
which will [close all device factories and internal devices](https://github.com/mattjlewis/diozero/blob/master/diozero-core/src/main/java/com/diozero/sbc/DeviceFactoryHelper.java#L146).
Custom classes that implement AutoCloseable can also be registered by calling [DeviceFactoryHelper.registerForShutown()](https://github.com/mattjlewis/diozero/blob/master/diozero-core/src/main/java/com/diozero/sbc/DeviceFactoryHelper.java#L124)
and will be called for shutdown prior to closing the device factories and internal devices.

## Package Heirarchy

This following image illustrates the relationships between the core packages within diozero.

![Package Hierarchy](/assets/images/Packages.png "Package Hierarchy") 

## Getting Started

Snapshot builds of the library are available in the [Nexus Repository Manager](https://oss.sonatype.org/index.html#nexus-search;gav~com.diozero~~~~).
A ZIP file of diozero and all dependencies can also be downloaded via the [diozero-distribution artifact on Nexus](https://oss.sonatype.org/index.html#nexus-search;gav~com.diozero~diozero-distribution~~~~kw,versionexpand).

Javadoc for the core library is also available via [javadoc.io](http://www.javadoc.io/doc/com.diozero/diozero-core/). 

Java doesn't provide a convenient deployment-time dependency manager 
such as Python's `pip` therefore you will need to manually download all dependencies 
and setup your classpath correctly. You can do this either via setting the `CLASSPATH` 
environment variable or as a command-line option (`java -cp <jar1>:<jar2>`). 
The dependencies have been deliberately kept to as few libraries as possible -
diozero is only dependent on [tinylog v2](http://www.tinylog.org).

To compile a diozero application you will need 3 JAR files - [tinylog](http://www.tinylog.org/)
(both API and Impl), and diozero-core. 
To run a diozero application, you can also optionally include one of the supported device provider 
libraries and the corresponding diozero provider wrapper library. Note that the built-in default
device provider gives maximum portability but has some limitations such as not supporting hardware
PWM on the Raspberry Pi. If you need hardware PWM on a Raspberry Pi then you must use the pigpio provider.

{: .note-title }
> Memory Mapped GPIO Control (mmap)
>
> Very high performance GPIO control can be achieved by directly accessing the GPIO registers via
> the `/dev/gpiomem` device file.
> The built-in default provider makes use of this feature, allowing it to achieve GPIO toggle
> rates as high as 24MHz on the Raspberry Pi 4.
> 
> mmap is currently supported on the following SBCs:
>
> * Raspberry Pi (all variants)
> * FriendlyArm H3 / Allwinner Sun8i CPU (as used in the NanoPi Duo2 / NanoPi Neo amongst others)
> * ASUS TinkerBoard
> * Odroid C2
> * Next Think Co CHIP (Allwinner sun4i/sun5i)
>
> Some boards / Operating Systems _may_ require you to run as root to access this file.

To get started I recommend first looking at the classes in 
[com.diozero.sampleapps](https://github.com/mattjlewis/diozero/blob/master/diozero-sampleapps/src/main/java/com/diozero/sampleapps/). 
To run the [LEDTest](https://github.com/mattjlewis/diozero/blob/master/diozero-sampleapps/src/main/java/com/diozero/sampleapps/LEDTest.java) 
sample application using the pigpioj provider:

```shell
sudo java -cp tinylog-api-2.2.1.jar:tinylog-impl-2.2.1.jar:diozero-core-{{ site.version }}.jar:diozero-sampleapps-{{ site.version }}.jar:diozero-provider-pigpio-{{ site.version }}.jar:pigpioj-java-2.5.5.jar com.diozero.sampleapps.LEDTest 12
```

For an experience similar to Python where source code is interpreted rather than compiled try 
[Groovy](http://www.groovy-lang.org/) (`sudo apt update && sudo apt install groovy`). 
With the `CLASSPATH` environment variable set as per the instructions above, a simple test 
application can be run via the command `groovy <filename>`. There is also a Groovy shell environment `groovysh`.

A Groovy equivalent of the LED controlled button example:

```groovy
import com.diozero.devices.Button
import com.diozero.devices.LED
import com.diozero.util.SleepUtil

led = new LED(12)
button = new Button(25)

button.whenPressed({ led.on() })
button.whenReleased({ led.off() })

println("Waiting for button presses. Press CTRL-C to quit.")
SleepUtil.sleepSeconds(5)
```

To run:

```shell
sudo groovy -cp $CLASSPATH test.groovy
```
