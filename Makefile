###############################################################################
setup:
	# TODO: install jdk and ant

	export JAVA_HOME=./build_tools/zulu17.54.21-ca-jdk17.0.13-macosx_x64
	export ANT_HOME=./build_tools/apache-ant-1.10.15
	export PATH=$PATH:$JAVA_HOME/bin:$ANT_HOME/bin
###############################################################################


build: setup
	# build SDL2
	cd cpp/sdl2 && make

	# build libaudio, libm3g, libmicro3d
	cd cpp/native && make -f Makefile.libaudio
	cd cpp/native && make -f Makefile.libm3g
	cd cpp/native && make -f Makefile.libmicro3d

	# build FreeJ2ME
	ant

release:
	mkdir release
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
