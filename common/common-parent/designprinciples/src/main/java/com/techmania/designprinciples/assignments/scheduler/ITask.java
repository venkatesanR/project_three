package com.techmania.designprinciples.assignments.scheduler;

import com.techmania.common.exceptions.ProcessException;

public interface ITask<Input, ReadOut, ProcessOut, WriteOut> {
    public ReadOut read(Input input) throws ProcessException;

    public ProcessOut process(ReadOut input) throws ProcessException;

    public WriteOut write(ProcessOut input) throws ProcessException;

    public void onComplete(WriteOut t);

    public void onException(Exception t);
}
