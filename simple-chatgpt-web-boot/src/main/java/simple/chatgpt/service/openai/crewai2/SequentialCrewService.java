package simple.chatgpt.service.openai.crewai2;

import java.util.List;

import simple.chatgpt.pojo.openai.crewai2.Task;

/*
 hung: interface for sequential crew execution service
 */
public interface SequentialCrewService {

    /**
     * hung: execute all tasks in sequence.
     * Each task's output is passed as input to the next.
     */
    void execute(String initialInput);

    /**
     * hung: optional setter to configure tasks dynamically
     */
    void setTasks(List<Task> tasks);
}
