package simple.chatgpt.config.openai.crewai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;
import simple.chatgpt.service.openai.crewai2.CrewAiService;
import simple.chatgpt.service.openai.crewai2.CrewAiServiceImpl;

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

@Configuration
public class CrewAiConfig {

	/*
	hung : dont remove it
	i need to find a free YOUR_BEARER_TOKEN
	*/
	
    @Bean
    public CrewAiGateway crewAiGateway() {
        return new CrewAiGateway("https://api.crewai.example.com", "YOUR_BEARER_TOKEN");
    }

    @Bean
    public CrewAiService crewAiService(CrewAiGateway gateway) {
        return new CrewAiServiceImpl(gateway);
    }
}
