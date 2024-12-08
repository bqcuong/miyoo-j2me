# miyoo-j2me
This repository is a fork of [freej2me-miyoomini](https://github.com/aweigit/freej2me-miyoomini). It aims to provide full support for ease of development environment and to resolve all existing issues, primarily related to sound effects. The code is optimized for the Onion OS on Miyoo Mini Plus.

## How to build
Docker is the only software required to build this project. Ensure that it is installed first.
```
make build
```

## How to install
1. After the build, the `release/` folder will be generated, containing everything ready to be copied to the SD card except for `jdk`.
2. Download a compatible JDK version to the `jdk` folder. For example, the *Azul Zulu JDK 17* is used for building `freej2me-sdl.jar` (if the above build instructions are followed), and the Miyoo Mini Plus has the ARM Cortex-A7, so an JDK 17 for ARM 32-bit should work.
3. On the SD card, create a folder named `JAVA` inside `Emu/` and copy the contents from the `release/` folder into `JAVA` folder.
4. Copy the ROMs to the respective folders in `roms`

## Keymaps
Below is the default keymap mapping.

|   **Key**   |    **Functions As**    |
|:-----------:|:----------------------:|
|  D-PAD UP   |           Up           |
| D-PAD DOWN  |          Down          |
| D-PAD LEFT  |          Left          |
| D-PAD RIGHT |         Right          |
|      X      |      Left button       |
|      B      |      Right button      |
|      A      |       OK button        |
|      Y      |           0            |
|     L1      |           1            |
|     R1      |           3            |
|     L2      |           7            |
|     R1      |           9            |
|   SELECT    |           *            |
|    START    |           #            |

|    **Key**     | **Functions As** |
|:--------------:|:----------------:|
| SELECT + START |   Change mode    |
|   SELECT + Y   |  Rotate screen   |
|   SELECT + B   |   Enable mouse   |

There are 5 modes for phone key layout. Depending on the ROM, try switching between these modes to make it work correctly. 
* **P** - Standard
* **N** - Nokia N-series
* **E** - Nokia E-series
* **S** - Siemens
* **M** - Motorola
