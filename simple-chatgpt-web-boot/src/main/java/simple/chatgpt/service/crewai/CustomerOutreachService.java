package simple.chatgpt.service.crewai;

/*
 hung: service interface for customer outreach campaign
 */
public interface CustomerOutreachService {

    /**
     * hung: kickoff venue coordination tasks
     */
    String kickoffVenueTasks() throws Exception;

    /**
     * hung: kickoff logistics tasks
     */
    String kickoffLogisticsTasks() throws Exception;

    /**
     * hung: kickoff marketing / communication tasks
     */
    String kickoffMarketingTasks() throws Exception;

    /**
     * hung: get status of a task by taskId/kickoffId
     */
    String getStatus(String taskId) throws Exception;
}
