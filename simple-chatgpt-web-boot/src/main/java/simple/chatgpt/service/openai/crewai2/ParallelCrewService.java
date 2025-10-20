package simple.chatgpt.service.openai.crewai2;

import simple.chatgpt.pojo.openai.crewai2.AgentRegistry;
import simple.chatgpt.pojo.openai.crewai2.TaskQueue;

/*
 hung: interface for parallel crew execution service
 */
public interface ParallelCrewService {

    /**
     * hung: execute all tasks in parallel using agents from the registry
     */
    void executeAll();

    /**
     * hung: set or update the agent registry
     */
    void setAgentRegistry(AgentRegistry agentRegistry);

    /**
     * hung: set or update the task queue
     */
    void setTaskQueue(TaskQueue taskQueue);
}
