package simple.chatgpt.util;

public class SafeConverter {

	// ------------------ Helpers ------------------
	public static int getInt(Object object) {
		if (object == null) {
			return 0; // Treat null as 0
		}

		if (object instanceof Number) {
			return ((Number) object).intValue();
		} else if (object instanceof String) {
			String s = (String) object;
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		return 0;
	}

	/**
	 * Attempts to convert an Object (like a String or Integer wrapper) to a
	 * primitive int. Returns a default value if the conversion fails.
	 *
	 * @param value        The object to convert. Can be null or a non-numeric
	 *                     String.
	 * @param defaultValue The int to return if conversion is unsuccessful.
	 * @return The successfully converted int or the defaultValue.
	 */
	public static int toIntOrDefault(Object value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		// 1. If the object is already a Number (Integer, Long, Double, etc.),
		// use its intValue() method.
		if (value instanceof Number) {
			// This is safer and avoids string conversion overhead for numeric types
			return ((Number) value).intValue();
		}

		// 2. If it's not a Number, convert to a String for parsing.
		String str = value.toString();

		try {
			// Attempt to parse the string as an integer.
			// trim() helps handle leading/trailing whitespace like " 123 "
			return Integer.parseInt(str.trim());
		} catch (NumberFormatException e) {
			// This catches exceptions thrown if the string is not a valid integer
			// (e.g., "hello", "1.23", or empty string "").

			// As a final, secondary attempt, try to parse it as a float/double
			// and then cast to int, which handles inputs like "3.14".
			try {
				return (int) Double.parseDouble(str.trim());
			} catch (NumberFormatException ex) {
				// If even the double conversion fails, return the default value.
				return defaultValue;
			}
		}
	}

	/**
	 * Attempts to convert an Object (like a String or Number wrapper) to a
	 * primitive long. Returns a default value if the conversion fails.
	 *
	 * @param value        The object to convert. Can be null or a non-numeric
	 *                     String.
	 * @param defaultValue The long to return if conversion is unsuccessful.
	 * @return The successfully converted long or the defaultValue.
	 */
	public static long toLongOrDefault(Object value, long defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		String str = value.toString();

		try {
			return Long.parseLong(str.trim());
		} catch (NumberFormatException e) {
			try {
				return (long) Double.parseDouble(str.trim());
			} catch (NumberFormatException ex) {
				return defaultValue;
			}
		}
	}

}