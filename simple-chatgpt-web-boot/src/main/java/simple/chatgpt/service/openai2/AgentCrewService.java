package simple.chatgpt.service.openai2;

public interface AgentCrewService {

    /*
     * hung: execute the content creation crew workflow
     */
	String executeCrewWorkflow(String topic);

    /**
     * hung: get status of a task by taskId/kickoffId
     */
    String getStatus(String taskId) throws Exception;
}

