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

public class CommonMusic {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
    {

        private final static Text musicId = new Text();
        private Text userId = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String line = value.toString();

            try
            {            
                JSONObject json = (JSONObject) new JSONParser().parse(line);

                Iterator iter = json.keySet().iterator();

                String user = (String) iter.next();
                userId.set(user);

                JSONArray jsonMusicList = (JSONArray) json.get(user);

                for(int i=0;i<jsonMusicList.size();i++)
                {

                    String music = jsonMusicList.get(i).toString();

                    musicId.set(music);

                    output.collect(musicId, userId);
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

        private Text userList = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String users = "[";

            while (values.hasNext()) 
            {
                try
                {

                    String userIds = values.next().toString();

                    JSONObject json = (JSONObject) new JSONParser().parse(userIds);

                    Iterator iter = json.keySet().iterator();

                    String musicId = (String) iter.next();

                    JSONArray jsonUserList = (JSONArray) json.get(musicId);

                    for(int i=0;i<jsonUserList.size();i++)
                    {
                        String user = jsonUserList.get(i).toString();
                        users += "\""+user+"\",";
                    }

                }
                catch(ParseException e)
                {
                    e.printStackTrace();
                }

            }

            users = users.substring(0, users.length()-1);
            users += "]";

            userList.set("?"+users);
            key.set(key.toString()+":");
            output.collect(key, userList);

        }

    }

    public static class Combine extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text userList = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String users = "{\""+key+"\":[";

            while (values.hasNext()) 
            {
                String user = values.next().toString();
                users += "\""+user+"\",";
            }

            users = users.substring(0, users.length()-1);
            users += "]}";

            userList.set(users);
            output.collect(key, userList);

        }

    }


    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(CommonMusic.class);
        conf.setJobName("commonmusic");

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