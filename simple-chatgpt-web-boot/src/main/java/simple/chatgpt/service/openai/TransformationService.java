package simple.chatgpt.service.openai;

public interface TransformationService {

	// ======= CORE METHODS (on top) =======
	void transformTone();
	void transformTone(String text, String tone);
	
}