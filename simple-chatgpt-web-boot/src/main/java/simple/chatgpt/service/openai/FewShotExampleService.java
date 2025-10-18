package simple.chatgpt.service.openai;

public interface FewShotExampleService {

    // ======= CORE METHODS =======
    void inferSentiment();
    void inferSentiment(String text);
}
