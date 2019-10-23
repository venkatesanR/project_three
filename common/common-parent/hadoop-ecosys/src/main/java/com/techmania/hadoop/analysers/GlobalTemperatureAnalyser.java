package com.techmania.hadoop.analysers;

import com.techmania.hadoop.reducers.TemperatureDataCombiner;
import com.techmania.hadoop.mappers.TemperatureDataProducer;

public class GlobalTemperatureAnalyser extends JobExecutor {
    private Job job;

    @Override
    public void setConfiguration(String jobName, String input, String output) {
        job = new Job.JobBuilder()
                .setName(jobName)
                .setInput(input)
                .setOutput(output)
                .setInvokerClazz(GlobalTemperatureAnalyser.class)
                .setMapperClazz(TemperatureDataProducer.class)
                .setReducerClazz(TemperatureDataCombiner.class)
                .build();
    }

    @Override
    public Job getJobDetail() {
        return job;
    }

    public static void main(String[] args) {
        args = new String[]{"1", "2"};
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperature <input path> <output path>");
            System.exit(-1);
        }
        GlobalTemperatureAnalyser analyser = new GlobalTemperatureAnalyser();
        analyser.setConfiguration("TempratureAnalyser", args[0], args[1]);
        analyser.execute();
    }
}
