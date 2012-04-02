#!/usr/bin/python
# Filename: calculateScore.py

import os

def calcScore(fname,score):

	f = file("data/"+fname+"/final_result.txt")
	fw = file("data/"+fname+"/score.txt",'w')

	while True:

		line = f.readline()

		if len(line) == 0:
			break
	
		colonPos = line.find(":")
		startPos = line.find("[")
		endPos = line.find("]")
                data = line[startPos+1:endPos]
                interests = data.split(",")

		total_score = score*len(interests)
		
		key = line[0:colonPos]
		data = "{\""+key+"\":\""+str(total_score)+"\"}\n"
		fw.write(data)
		

	fw.close()
	f.close()

path="data/"
dirList=os.listdir(path)
scores = {'music':'8','movies':'6','activities':'4','groups':'8','likes':'9','games':'5','television':'6','events':'4','interests':'3','checkins':'10','books':'7'}

for fname in dirList:
	calcScore(fname,int(scores[fname]))
