package simple.chatgpt.pojo.openai.crewai2;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private final java.util.Queue<Task> queue = new java.util.LinkedList<>();

    // existing single-task enqueue
    public void enqueue(Task task) {
        logger.debug("enqueue called (single)");
        logger.debug("enqueue task={}", task);
        queue.add(task);
        logger.debug("enqueue completed remaining={}", queue.size());
    }

    // new batch enqueue
    public void enqueue(List<Task> tasks) {
        logger.debug("enqueue called (batch)");
        logger.debug("enqueue tasks={}", tasks);
        queue.addAll(tasks);
        logger.debug("enqueue completed remaining={}", queue.size());
    }

    public Task dequeue() {
        Task task = queue.poll();
        logger.debug("dequeue called, returning={}", task);
        return task;
    }

    public int size() {
        return queue.size();
    }
}
