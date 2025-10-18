package simple.chatgpt.service.openai;

public interface SummarizationService {

	// ======= CORE METHODS (on top) =======
	void summarizeText();
	void summarizeText(String text);
	
}

/*
| Course Technique   | Example Class            | Description                           |
| ------------------ | ------------------------ | ------------------------------------- |
| Role prompting     | `SummarizationService`   | Define assistant persona              |
*/