#!/usr/bin/python
# Filename: moveFromOutputToInput.py


f = file('common_music_output/final_scores.txt')
fw = file('common_music_input/file01','w')

while True:

	line = f.readline()

	if len(line) == 0:
		break
	
	colonPos = line.find(":")
	questionPos = line.find("?")

	key = line[0:colonPos]
	value = line[questionPos+1:len(line)]

	json = "{\""+key+"\":\""+value
	json = json[:len(json)-1]
	json += "\"}\n"

	fw.write(json)

f.close()
fw.close()
