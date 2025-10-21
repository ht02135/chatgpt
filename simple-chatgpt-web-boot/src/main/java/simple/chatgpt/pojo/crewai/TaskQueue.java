package simple.chatgpt.pojo.crewai;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Alias("crewaiTaskQueue")		// for MyBatis    
@Component("crewaiTaskQueue")	// for Spring DI/autowire
public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private final java.util.Queue<Task> queue = new java.util.LinkedList<>();

    public TaskQueue() {
        logger.debug("TaskQueue constructor called");
        logger.debug("TaskQueue this={}", this);
    }
    
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
