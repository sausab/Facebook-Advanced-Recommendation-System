#!/usr/bin/python
# Filename: divideFile.py

f = file('common_music_input2/file01')

count = 0

while True:

	line = f.readline()

	if len(line) == 0:
		break
	else:
		count = count + 1

f.close()

f = file('common_music_input2/file01')

for i in range(0,9):

	fw = file('common_music_input2/file'+str(i),'w')

	for j in range(0,count/10):

		fw.write(f.readline())

	fw.close()

fw = file('common_music_input2/file9','w')

while True:

        line = f.readline()

        if len(line) == 0:
                break
        else:
                fw.write(line)

fw.close()
f.close()
