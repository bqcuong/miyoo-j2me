C_COMPILER = /opt/mmiyoo/bin/arm-linux-gnueabihf-gcc
CPP_COMPILER = /opt/mmiyoo/bin/arm-linux-gnueabihf-g++
CFLAGS = -DDIRECTFB -std=c++11 -O3 -fno-strict-aliasing -fPIC -marm -mtune=cortex-a7 -march=armv7ve+simd -mfpu=neon-vfpv4 -mfloat-abi=hard -lSDL2 -lpthread

TARGET = sdl_interface

all:
	$(CPP_COMPILER) $(CFLAGS) miyoomini.cpp cJSON.c -o $(TARGET)

clean:  
	rm -f $(TARGET)
