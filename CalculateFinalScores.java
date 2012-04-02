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

public class CalculateFinalScores {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
    {

        private final static Text userPair = new Text();
        private Text score = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            String line = value.toString();

            try
            {            
                JSONObject json = (JSONObject) new JSONParser().parse(line);

                Iterator iter = json.keySet().iterator();

                String userIdPair = (String) iter.next();

                userPair.set(userIdPair);
                score.set(json.get(userIdPair).toString());

                output.collect(userPair, score);

            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }

        }
    }


    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {

        private Text score = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
        {

            int final_score = 0;

            while (values.hasNext()) 
            {

                final_score += Integer.parseInt(values.next().toString());

            }

            score.set("?"+final_score);
            key.set(key.toString()+":");
            output.collect(key, score);

        }

    }


    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(CalculateFinalScores.class);
        conf.setJobName("calculatefinalscores");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }

}