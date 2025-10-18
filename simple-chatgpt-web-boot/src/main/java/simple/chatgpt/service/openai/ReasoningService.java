package simple.chatgpt.service.openai;

public interface ReasoningService {

    // ======= CORE METHODS (on top) =======
    void solveLogicProblem();
    void solveLogicProblem(String problem);
}

/*
| Course Technique   | Example Class            | Description                           |
| ------------------ | ------------------------ | ------------------------------------- |
| Chain-of-thought   | `ReasoningService`       | Ask model to “think step by step”     |
*/