package simple.chatgpt.controller.openai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.service.openai2.AgentCrewService;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/openai/agentcrew")
public class AgentCrewController {

    private static final Logger logger = LogManager.getLogger(AgentCrewController.class);

    private final AgentCrewService agentCrewService;

    /*
     * hung: constructor injection using openaiAgentCrewService
     */
    @Autowired
    public AgentCrewController(@Qualifier("openaiAgentCrewService") AgentCrewService agentCrewService) {
        logger.debug("AgentCrewController constructor called");
        logger.debug("AgentCrewController agentCrewService={}", agentCrewService);

        this.agentCrewService = agentCrewService;
    }

    /*
     * hung: proof-read method exposed to frontend
     */
    @PostMapping("/proofread")
    public ResponseEntity<Response<String>> proofRead(@RequestBody String input) {
        logger.debug("proofRead called");
        logger.debug("proofRead input={}", input);

        try {
            String taskId = agentCrewService.executeCrewWorkflow(input);
            logger.debug("proofRead taskId={}", taskId);

            String result = agentCrewService.getStatus(taskId);
            logger.debug("proofRead result={}", result);

            Response<String> response = Response.success("Proof-read completed", result, 200);
            logger.debug("proofRead response={}", response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("proofRead exception", e);
            Response<String> response = Response.error("Error during proof-read", null, 500);
            logger.debug("proofRead error response={}", response);
            return ResponseEntity.status(500).body(response);
        }
    }
}
