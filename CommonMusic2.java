package aic.proj.test;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommonMusic2 {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
    {

        private final static Text userPair = new Text();
        private Text musicId = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String line = value.toString();

            try
            {            
                JSONObject json = (JSONObject) new JSONParser().parse(line);

                Iterator iter = json.keySet().iterator();

                String music = (String) iter.next();
                musicId.set(music);

                JSONArray jsonUserList = (JSONArray) json.get(music);

                for(int i=0;i<jsonUserList.size();i++)
                {

                    for(int j=0;j<jsonUserList.size() && j!=i;j++)
                    {

                        String user1 = jsonUserList.get(i).toString();
                        String user2 = jsonUserList.get(j).toString();
                        String pair="";

                        if(user1.compareTo(user2) <= 0)
                            pair = user1+","+user2;
                        else
                            pair = user2+","+user1;

                        userPair.set(pair);

                        output.collect(userPair, musicId);

                    }
                }

            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text musicList = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String musicIds = "[";

            while (values.hasNext()) 
            {
                try
                {

                    String musics = values.next().toString();

                    JSONObject json = (JSONObject) new JSONParser().parse(musics);

                    Iterator iter = json.keySet().iterator();

                    String userIdPair = (String) iter.next();

                    JSONArray jsonMusicList = (JSONArray) json.get(userIdPair);

                    for(int i=0;i<jsonMusicList.size();i++)
                    {
                        String music = jsonMusicList.get(i).toString();
                        musicIds += "\""+music+"\",";
                    }

                }
                catch(ParseException e)
                {
                    e.printStackTrace();
                }


            }

            musicIds = musicIds.substring(0, musicIds.length()-1);
            musicIds += "]";

            musicList.set("?"+musicIds);
            key.set(key.toString()+":");
            output.collect(key, musicList);

        }

    }

    public static class Combine extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text musicList = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String musicIds = "{\""+key+"\":[";

            while (values.hasNext()) 
            {
                String music = values.next().toString();
                musicIds += "\""+music+"\",";
            }

            musicIds = musicIds.substring(0, musicIds.length()-1);
            musicIds += "]}";

            musicList.set(musicIds);
            output.collect(key, musicList);

        }

    }


    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(CommonMusic2.class);
        conf.setJobName("commonmusic2");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Combine.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }

}