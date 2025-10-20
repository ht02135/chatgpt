package simple.chatgpt.gateway.crewai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
hung: HTTP gateway to CrewAI AMP endpoints (kickoff / status)
///////////////////////
crewai is mostly python stuff
i force chatgpt to give me a java version using CrewAI AMP endpoints
///////////////////////
https://docs.crewai.com/en/api-reference/introduction
Each deployed crew has its own unique API endpoint:
https://your-crew-name.crewai.com
*/
public class CrewAiGateway {
    private static final Logger logger = LogManager.getLogger(CrewAiGateway.class);

    private final String baseUrl;
    private final String bearerToken;
    private final HttpClient http;

    public CrewAiGateway(String baseUrl, String bearerToken) {
        logger.debug("CrewAiController constructor called");
        logger.debug("CrewAiController baseUrl={}", baseUrl);
        logger.debug("CrewAiController bearerToken={}", bearerToken);

        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        logger.debug("CrewAiController this={}", this);
    }

    private static void init() {
        logger.debug("init called");
    }

    /**
     * Kickoff a CrewAI run. Returns kickoff id (task id) or null.
     * @param taskType one-line identifier of the task (e.g. inquiry_resolution)
     * @param agentRole agent role name (e.g. support_agent)
     * @param jsonInputs JSON string representing inputs object
     * @return kickoff id or null
     * @throws Exception on HTTP error
     */
    public String kickoff(String taskType, String agentRole, String jsonInputs) throws Exception {
        logger.debug("kickoff called");
        logger.debug("kickoff taskType={}", taskType);
        logger.debug("kickoff agentRole={}", agentRole);
        logger.debug("kickoff jsonInputs={}", jsonInputs);

        String kickoffUrl = baseUrl + "/kickoff";
        logger.debug("kickoff kickoffUrl={}", kickoffUrl);

        String payload = String.format(
        	    "{\n" +
        	    "  \"inputs\": %s,\n" +
        	    "  \"metadata\": {\n" +
        	    "    \"task_type\": \"%s\",\n" +
        	    "    \"agent_role\": \"%s\"\n" +
        	    "  }\n" +
        	    "}",
        	    jsonInputs,
        	    escapeJson(taskType),
        	    escapeJson(agentRole)
        	);
        logger.debug("kickoff payload={}", payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(kickoffUrl))
                .timeout(Duration.ofMinutes(1))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        logger.debug("kickoff responseStatus={}", resp.statusCode());
        logger.debug("kickoff responseBody={}", resp.body());

        String kickoffId = extractKickoffIdFromResponse(resp.body());
        logger.debug("kickoff kickoffId={}", kickoffId);
        return kickoffId;
    }

    /**
     * Get status for a kickoff id.
     */
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("getStatus called");
        logger.debug("getStatus kickoffId={}", kickoffId);

        String url = baseUrl + "/status/" + kickoffId;
        logger.debug("getStatus url={}", url);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        logger.debug("getStatus responseStatus={}", resp.statusCode());
        logger.debug("getStatus responseBody={}", resp.body());

        return resp.body();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    private static String extractKickoffIdFromResponse(String body) {
        if (body == null) return null;
        String marker1 = "\"kickoff_id\":\"";
        int idx = body.indexOf(marker1);
        if (idx >= 0) {
            int start = idx + marker1.length();
            int end = body.indexOf("\"", start);
            if (end > start) return body.substring(start, end);
        }
        String marker2 = "\"task_id\":\"";
        idx = body.indexOf(marker2);
        if (idx >= 0) {
            int start = idx + marker2.length();
            int end = body.indexOf("\"", start);
            if (end > start) return body.substring(start, end);
        }
        return null;
    }

    @Override
    public String toString() {
        return "CrewAiController{" + "baseUrl='" + baseUrl + "'}";
    }
}
