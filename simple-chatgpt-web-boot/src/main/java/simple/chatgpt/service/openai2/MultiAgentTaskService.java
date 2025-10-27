package simple.chatgpt.service.openai2;

public interface MultiAgentTaskService {

    /*
     * hung: execute multi-agent task workflow
     */
	String executeMultiAgentWorkflow();

    /**
     * hung: get status of a task by taskId/kickoffId
     */
    String getStatus(String taskId) throws Exception;
}
