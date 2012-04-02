#!/usr/bin/python
# Filename: moveFromInput1toInput2.py


f = file('common_music_output/result.txt')
fw = file('common_music_input2/file01','w')

while True:

	line = f.readline()

	if len(line) == 0:
		break
	
	colonPos = line.find(":")
	questionPos = line.find("?")

	key = line[0:colonPos]
	value = line[questionPos+1:len(line)]

	json = "{\""+key+"\":"+value
	json = json[:len(json)-1]
	json += "}\n"

	fw.write(json)

f.close()
fw.close()
