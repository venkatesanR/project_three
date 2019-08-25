package com.techmania.hadoop.analysers;

import org.apache.hadoop.mapred.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    public abstract Job getJobDetail();

    public abstract void setConfiguration(String jobName, String input, String output);

    public void execute() {
        logger.info("Started executing JobExecutor>execute()");
        Job job = getJobDetail();
        try {
            Objects.requireNonNull(job, "Input should not be empty");
            JobClient.runJob(job.getJobConf());
        } catch (Exception ex) {
            logger.error("Error while executing job: {} with error message:{} ",
                    job.getJobConf().getJobName(), ex.getMessage());
        }
        logger.info("Completed executing JobExecutor>execute()");
    }
}
