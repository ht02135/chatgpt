package simple.chatgpt.pojo.openai;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
	
public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private final Queue<Task> queue = new ConcurrentLinkedQueue<>();

    public void enqueue(List<Task> tasks) {
        logger.debug("enqueue tasks={}", tasks);
        queue.addAll(tasks);
    }

    public Task dequeue() {
        logger.debug("dequeue called");
        return queue.poll();
    }

    @Override
    public String toString() {
        return "TaskQueue{" + "remaining=" + queue.size() + '}';
    }
}