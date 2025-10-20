package simple.chatgpt.pojo.openai.crewai2;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 hung: lightweight task queue used by parallel executor
 */
public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private final Queue<Task> queue = new ConcurrentLinkedQueue<>();

    public TaskQueue() {
        logger.debug("TaskQueue constructor called");
        logger.debug("TaskQueue this={}", this);
    }

    public void enqueue(List<Task> tasks) {
        logger.debug("enqueue called");
        logger.debug("enqueue tasks={}", tasks);
        queue.addAll(tasks);
        logger.debug("enqueue completed remaining={}", queue.size());
    }

    public Task dequeue() {
        logger.debug("dequeue called");
        Task t = queue.poll();
        logger.debug("dequeue returned={}", t);
        return t;
    }

    public int size() {
        logger.debug("size called");
        return queue.size();
    }

    @Override
    public String toString() {
        return "TaskQueue{" + "remaining=" + queue.size() + '}';
    }
}
