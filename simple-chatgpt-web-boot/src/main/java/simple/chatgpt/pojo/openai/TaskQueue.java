package simple.chatgpt.pojo.openai;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Alias("openaiTaskQueue")		// for MyBatis    
@Component("openaiTaskQueue")	// for Spring DI/autowire
public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private final Queue<Task> queue = new ConcurrentLinkedQueue<>();

    public TaskQueue() {
        logger.debug("TaskQueue constructor called");
        logger.debug("TaskQueue this={}", this);
    }
    
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