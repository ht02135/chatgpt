package simple.chatgpt.service.openai;

public interface ReviewExtractorService {

	// ======= CORE METHODS (on top) =======
	void extractReviewData();

}

/*
| Course Technique   | Example Class            | Description                           |
| ------------------ | ------------------------ | ------------------------------------- |
| Structured output  | `ReviewExtractorService` | Force model to produce JSON           |
*/