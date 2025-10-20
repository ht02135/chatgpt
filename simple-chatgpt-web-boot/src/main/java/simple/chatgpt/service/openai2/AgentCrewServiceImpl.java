package simple.chatgpt.service.openai2;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;

import simple.chatgpt.pojo.openai.Agent;
import simple.chatgpt.pojo.openai.CrewController;
import simple.chatgpt.pojo.openai.Task;

@Service
public class AgentCrewServiceImpl implements AgentCrewService {

    private static final Logger logger = LogManager.getLogger(AgentCrewServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     */
    public AgentCrewServiceImpl(OpenAIClient client) {
        logger.debug("AgentCrewServiceImpl constructor called");
        logger.debug("AgentCrewServiceImpl client param={}", client);
        this.client = client;
    }
    
    /*
     * hung: execute the content creation workflow (previously main)
     */
    @Override
    public void executeCrewWorkflow(String topic) {
        logger.debug("executeCrewWorkflow called");
        logger.debug("executeCrewWorkflow topic={}", topic);

        // Instantiate agents
        Agent planner = new Agent(
                "Content Planner",
                "Plan engaging and factually accurate content on " + topic,
                "You're working on planning a blog article about the topic: " + topic + ".",
                client
        );
        logger.debug("executeCrewWorkflow planner={}", planner);

        Agent writer = new Agent(
                "Content Writer",
                "Write insightful and factually accurate opinion piece about the topic: " + topic,
                "You're working on writing a new opinion piece about the topic: " + topic + ".",
                client
        );
        logger.debug("executeCrewWorkflow writer={}", writer);

        Agent editor = new Agent(
                "Editor",
                "Edit a given blog post to align with the writing style of the organization.",
                "You are an editor who receives a blog post from the Content Writer.",
                client
        );
        logger.debug("executeCrewWorkflow editor={}", editor);

        // Create tasks
        Task planTask = new Task(
                planner,
                "1. Prioritize latest trends, key players and noteworthy news on " + topic + ".\n"
                        + "2. Identify the target audience, considering their interests and pain-points.\n"
                        + "3. Develop a detailed content outline including an introduction, key points, and a call to action.\n"
                        + "4. Include SEO keywords and relevant data or sources.",
                "A comprehensive content plan document with an outline, audience analysis, SEO keywords, and resources."
        );
        logger.debug("executeCrewWorkflow planTask={}", planTask);

        Task writeTask = new Task(
                writer,
                "1. Use the content plan to craft a compelling blog post on " + topic + ".\n"
                        + "2. Incorporate SEO keywords naturally.\n"
                        + "3. Sections/Subtitles properly named in an engaging manner.\n"
                        + "4. Ensure the post is structured with engaging introduction, insightful body, summarizing conclusion.\n"
                        + "5. Proof-read for grammar and alignment with the brand's voice.",
                "A well-written blog post in markdown format, ready for publication, each section with 2-3 paragraphs."
        );
        logger.debug("executeCrewWorkflow writeTask={}", writeTask);

        Task editTask = new Task(
                editor,
                "Proof-read the given blog post for grammatical errors and alignment with the brand's voice.",
                "A well-written blog post in markdown format, ready for publication, each section with 2-3 paragraphs."
        );
        logger.debug("executeCrewWorkflow editTask={}", editTask);

        // Create workflow (crew)
        List<Task> workflow = Arrays.asList(planTask, writeTask, editTask);
        logger.debug("executeCrewWorkflow workflow={}", workflow);

        // Execute workflow
        CrewController controller = new CrewController(workflow);
        logger.debug("executeCrewWorkflow controller={}", controller);

        controller.execute(topic);

        logger.debug("executeCrewWorkflow completed");
    }
}
