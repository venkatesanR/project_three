package com.techmania.designprinciples.assignments.scheduler;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class TaskBuilder<Input, Output> {
    private Input input;
    private Function<Input, Output> reader;
    private Function<Output, Output> writter;
    private Function<Output, Output> processor;
    private Consumer<Throwable> onError;
    private Consumer<Optional<Output>> onComplete;

    private TaskBuilder() {

    }

    public TaskBuilder setInput(Input input) {
        this.input = input;
        return this;
    }

    public TaskBuilder onRead(Function reader) {
        this.reader = reader;
        return this;
    }

    public TaskBuilder onProcess(Function<Output, Output> processor) {
        this.processor = processor;
        return this;
    }

    public TaskBuilder onWrite(Function writter) {
        this.writter = writter;
        return this;
    }

    public TaskBuilder onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public TaskBuilder onComplete(Consumer<Optional<Output>> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public Task build() {
        Objects.requireNonNull(input, "Input should not be a null value");
        Objects.requireNonNull(reader, "reader should not null value");
        Objects.requireNonNull(processor, "processor should not null");
        Objects.requireNonNull(writter, "writter should not null");
        Objects.requireNonNull(onComplete, "ONComplete function is mandatory");
        Objects.requireNonNull(onError, "Capturing error is a mandatory for a task, This should not be null");
        return new Task<Input, Output>(input,
                reader.
                        andThen(processor).
                        andThen(writter), onComplete, onError);
    }

    public static <Input, Output> TaskBuilder<Input, Output> builder() {
        return new TaskBuilder<Input, Output>();
    }

}
