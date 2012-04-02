#echo "\n##### Removing existing 'data' folder #####"
#rm -rf data

#echo "\n\n##### Running Data Collector. This may take a while #####"
#java -jar data_collector.jar

echo "\n\n##### Call to run.py #####"
python run.py

echo "\n\n##### Calculate Final Scores #####"
python calculateScore.py
python moveToInput.py

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

echo "\n\n##### Copy input data to HDFS #####"
bin/hadoop dfs -put common_music_input/* common_music/input
echo "\n\n##### Run first Map/Reduce #####"
bin/hadoop jar final_scores.jar aic.proj.test.CalculateFinalScores common_music/input common_music/output
echo "\n\n##### Copy result of Map/Reduce to local file system #####"
bin/hadoop dfs -cat common_music/output/part-00000 > common_music_output/final_scores.txt

rm common_music_input/*

python moveFromOutputToInput.py

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

echo "\n\n##### Copy input data to HDFS #####"
bin/hadoop dfs -put common_music_input/* common_music/input
echo "\n\n##### Run first Map/Reduce #####"
bin/hadoop jar recommend_friends.jar aic.proj.test.RecommendFriends common_music/input common_music/output
echo "\n\n##### Copy result of Map/Reduce to local file system #####"
bin/hadoop dfs -cat common_music/output/part-00000 > common_music_output/recommendations.txt



