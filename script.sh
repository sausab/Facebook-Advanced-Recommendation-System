echo "\n\n##### Remove existing local data #####"
rm common_music_input2/*
rm common_music_output/*

echo "\n\n##### Remove existing folders on HDFS #####"
bin/hadoop dfs -rmr common_music/input
bin/hadoop dfs -rmr common_music/input2
bin/hadoop dfs -rmr common_music/output
bin/hadoop dfs -rmr common_music/output2
bin/hadoop dfs -rmr common_music

echo "\n\n##### Create new folders on HDFS #####"
bin/hadoop dfs -mkdir common_music
bin/hadoop dfs -mkdir common_music/input
bin/hadoop dfs -mkdir common_music/input2

echo "\n\n##### Copy input data to HDFS #####"
bin/hadoop dfs -put common_music_input/* common_music/input
echo "\n\n##### Run first Map/Reduce #####"
bin/hadoop jar common_music.jar aic.proj.test.CommonMusic common_music/input common_music/output
echo "\n\n##### Copy result of Map/Reduce to local file system #####"
bin/hadoop dfs -cat common_music/output/part-00000 > common_music_output/result.txt

echo "\n\n##### Process the first output #####"
python moveFromInput1toInput2.py
python divideFile.py
rm common_music_input2/file01

echo "\n\n##### Copy input data to HDFS for second round of Map/Reduce #####"
bin/hadoop dfs -put common_music_input2/* common_music/input2
echo "\n\n##### Start second Map/Reduce #####"
bin/hadoop jar common_music2.jar aic.proj.test.CommonMusic2 common_music/input2 common_music/output2
echo "\n\n##### Copy final result back to local file system #####\n"
bin/hadoop dfs -cat common_music/output2/part-00000 > common_music_output/final_result.txt

rm common_music_input/*

