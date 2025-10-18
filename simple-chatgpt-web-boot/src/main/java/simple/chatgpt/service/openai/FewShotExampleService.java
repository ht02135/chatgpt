package simple.chatgpt.service.openai;

public interface FewShotExampleService {

    // ======= CORE METHODS =======
    void inferSentiment();
    void inferSentiment(String text);
}

/*
| Course Technique   | Example Class            | Description                           |
| ------------------ | ------------------------ | ------------------------------------- |
| Few-shot prompting | `FewShotExampleService`  | Teach by examples                     |
*/