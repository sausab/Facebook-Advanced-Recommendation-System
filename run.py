#!/usr/bin/python
# Filename: run.py

import os

path="data/"
dirList=os.listdir(path)

for fname in dirList:
	print "\n\n##### Processing interest: "+fname+" #####"
	print "\n\n##### Copy data to input folder #####"
	os.system("cp data/"+fname+"/* common_music_input/")
	print "\n\n##### Run script.sh #####"
	os.system("./script.sh")
	os.system("cp common_music_output/final_result.txt data/"+fname+"/")

