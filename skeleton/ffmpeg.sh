#!/bin/sh
javapath="/mnt/SDCARD/Emu/JAVA"

export LD_LIBRARY_PATH=$javapath/ffmpeglib:/mnt/SDCARD/miyoo/lib:/config/lib

filename=$1
./ffmpeglib/ffmpeg -hide_banner -loglevel error -y -i $filename.amr -c:a flac $filename.flac 2> ffmpeg.log
mv $filename.flac $filename.wav
