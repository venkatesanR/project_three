package com.techmania.hadoop.analysers;

import org.apache.hadoop.mapred.JobConf;
import com.techmania.common.interfaces.Builder;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;

import java.util.Objects;

public class Job {
    private final JobConf jobConf;
    private String input;
    private String output;

    private Job(final JobConf jobConf) {
        this.jobConf = jobConf;
    }

    public JobConf getJobConf() {
        return jobConf;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    static class JobBuilder implements Builder<Job> {
        private String name;
        private String input;
        private String output;
        private Class invokerClazz;
        private Class<? extends Mapper> mapperClazz;
        private Class<? extends Reducer> reducerClazz;

        public JobBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public JobBuilder setInvokerClazz(Class invokerClazz) {
            this.invokerClazz = invokerClazz;
            return this;
        }


        public JobBuilder setInput(String input) {
            this.input = input;
            return this;
        }

        public JobBuilder setOutput(String output) {
            this.output = output;
            return this;
        }

        public JobBuilder setMapperClazz(Class<? extends Mapper> mapperClazz) {
            this.mapperClazz = mapperClazz;
            return this;
        }

        public JobBuilder setReducerClazz(Class<? extends Reducer> reducerClazz) {
            this.reducerClazz = reducerClazz;
            return this;
        }

        @Override
        public Job build() {
            validate();
            return prepareJobConf();
        }

        private Job prepareJobConf() {
            JobConf jobConf = new JobConf(invokerClazz);
            jobConf.setJobName(name);
            jobConf.setMapperClass(mapperClazz);
            jobConf.setReducerClass(reducerClazz);
            Job job = new Job(jobConf);
            job.input = input;
            job.output = output;
            return job;
        }

        private void validate() {
            Objects.requireNonNull(name, "Job name should not be null");
            Objects.requireNonNull(input, "Input directory must not be empty");
            Objects.requireNonNull(output, "Output directory must not be empty");
            Objects.requireNonNull(invokerClazz, "Application class should not empty");
            Objects.requireNonNull(mapperClazz, "Cannot prepare hadoop JobParams without Mapper class");
            Objects.requireNonNull(reducerClazz, "Cannot prepare hadoop JobParams without Reducer class");
        }
    }
}
