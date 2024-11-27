#!/bin/sh
echo $0 $*
mydir=`dirname "$0"`
javapath="/mnt/SDCARD/Emu/JAVA"

export HOME=$mydir
export JAVA_HOME=$javapath/jdk
export CLASSPATH=$JAVA_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$PATH
export LD_LIBRARY_PATH=$JAVA_HOME/lib:$mydir/lib:$LD_LIBRARY_PATH
cd $mydir

mkdir -p ./.java/.systemPrefs
mkdir ./.java/.userPrefs
chmod -R 755 ./.java

killall audioserver
killall audioserver.mod

export SDL_VIDEODRIVER=directfb
export SDL_AUDIODRIVER=mmiyoo
export EGL_VIDEODRIVER=mmiyoo
export DFBARGS="module-dir=/mnt/SDCARD/Emu/JAVA/lib/directfb-1.7-7,layer-rotate=180,layer-depth=32,layer-format=ABGR"

export JAVA_TOOL_OPTIONS='-Xverify:none -Djava.util.prefs.systemRoot=./.java -Djava.util.prefs.userRoot=./.java/.userPrefs -Djava.awt.headless=true -Djava.library.path=./jlib'

gamedir=`dirname "$1"`


CUST_CPUCLOCK=1
sv=`cat /proc/sys/vm/swappiness`

#60 by default
echo 10 > /proc/sys/vm/swappiness
if [ "$CUST_CPUCLOCK" = "1" ]; then
    echo "set customized cpuspeed"
    ./cpuclock 1600
fi

soundLevel=10

if echo $gamedir | grep "240x320" > /dev/null
then
	java -jar ./freej2me-sdl.jar "$1" 240 320 $soundLevel > j2me.txt

elif echo $gamedir | grep "320x240" > /dev/null
then
	java -jar ./freej2me-sdl.jar "$1" 320 240 $soundLevel > j2me.txt

elif echo $gamedir | grep "128x128" > /dev/null
then
	java -jar ./freej2me-sdl.jar "$1" 128 128 $soundLevel
	
elif echo $gamedir | grep "176x208" > /dev/null
then
	java -jar ./freej2me-sdl.jar "$1" 176 208 $soundLevel

elif echo $gamedir | grep "640x360" > /dev/null
then
	java -jar ./freej2me-sdl.jar "$1" 640 360 $soundLevel
else
	echo "none"
fi


if [ "$CUST_CPUCLOCK" = "1" ]; then
    echo "set customized cpuspeed"
    ./cpuclock 1200
fi


echo $sv > /proc/sys/vm/swappiness



