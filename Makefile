.PHONY: build release

###############################################################################
HOST_WORKSPACE=$(shell pwd)
GUEST_WORKSPACE=/root/workspace
TOOLCHAIN_IMAGE=bqcuongas/sdl2-miyoo
###############################################################################
build:
	docker run -it --rm -v $(HOST_WORKSPACE):$(GUEST_WORKSPACE) $(TOOLCHAIN_IMAGE) /bin/bash -c 'cd /root/workspace && make build-cpp build-jar release'

build-cpp:
	# build SDL2
	cd cpp/sdl2 && make

	# build libaudio, libm3g, libmicro3d
	cd cpp/native && make -f Makefile.libaudio
	cd cpp/native && make -f Makefile.libm3g
	cd cpp/native && make -f Makefile.libmicro3d

build-jar:
	# build FreeJ2ME
	ant

release:
	rm -rf release && mkdir release
	cp -r skeleton/* release/

	cp cpp/sdl2/sdl_interface release/
	cp cpp/sdl2/keymap.cfg release/

	cp cpp/native/libaudio.so release/jlib/
	cp cpp/native/libm3g.so release/jlib/
	cp cpp/native/libmicro3d.so release/jlib/

	cp -r shaders release/

	# TODO: copy jdk

clean:
	rm -rf build/
	cd cpp/sdl2 && make clean
	cd cpp/native && make -f Makefile.libaudio clean
	cd cpp/native && make -f Makefile.libm3g clean
	cd cpp/native && make -f Makefile.libmicro3d clean
