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

public class RecommendFriends {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
    {

        private final static Text userId = new Text();
        private Text friend = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String line = value.toString();

            try
            {            
                JSONObject json = (JSONObject) new JSONParser().parse(line);

                Iterator iter = json.keySet().iterator();

                String userIdPair = (String) iter.next();


                if(Integer.parseInt(json.get(userIdPair).toString()) > 100)
                {
                    String[] users = userIdPair.split(",");

                    userId.set(users[0]);
                    friend.set(users[1]);
                    output.collect(userId, friend);
                    output.collect(friend, userId);
                }

            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static class Combine extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text friends = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String friendList = "{\""+key+"\":[";

            while (values.hasNext()) 
            {

                String friend = values.next().toString();
                friendList += "\""+friend+"\",";

            }

            friendList = friendList.substring(0, friendList.length()-1);
            friendList += "]}";

            friends.set(friendList);
            output.collect(key, friends);

        }

    }


    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text friendList = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String friendIds = "[";

            while (values.hasNext()) 
            {
                try
                {

                    String friends = values.next().toString();

                    JSONObject json = (JSONObject) new JSONParser().parse(friends);

                    Iterator iter = json.keySet().iterator();

                    String userId = (String) iter.next();

                    JSONArray jsonFriendList = (JSONArray) json.get(userId);

                    for(int i=0;i<jsonFriendList.size();i++)
                    {
                        String friend = jsonFriendList.get(i).toString();
                        friendIds += "\""+friend+"\",";
                    }

                }
                catch(ParseException e)
                {
                    e.printStackTrace();
                }


            }

            friendIds = friendIds.substring(0, friendIds.length()-1);
            friendIds += "]";

            friendList.set("?"+friendIds);
            key.set(key.toString()+":");
            output.collect(key, friendList);

        }

    }


    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(RecommendFriends.class);
        conf.setJobName("recommendfriends");

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