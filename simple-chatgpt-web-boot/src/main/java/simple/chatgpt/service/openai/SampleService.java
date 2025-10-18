package simple.chatgpt.service.openai;

public interface SampleService {

    // ======= CORE METHODS (on top) =======
    void simpleCompletion();
    void chatCompletion(String question);
    void assistantInteraction(String question);
}
