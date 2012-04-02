#!/usr/bin/python
# Filename: moveToInput.py


import os

path="data/"
dirList=os.listdir(path)
count = 0

for fname in dirList:
	os.system("cp data/"+fname+"/score.txt common_music_input/score"+str(count))
	count = count + 1
