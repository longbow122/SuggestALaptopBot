package me.longbow122.SuggestALaptopBot.utils;

/**
 * A basic Utils class that contains some functions that are to be globally used.
 * @author longbow122
 */
public final class Utils {

	/**
	 * Minor utility method to check and validate the name of the command to be added, ensuring that it lies within Discord's limits.
	 * The limits for the name of a command within Discord are that the name must be between 1-32 characters, and lowercase alphabetic, with dashes.
	 * Spaces are also not allowed within these command names.
	 * @param name {@link String} representing the name of the command to be added.
	 * @return {@link Boolean} representing whether the name is valid. Returns True if valid, False otherwise.
	 */
	public static boolean checkCommandName(String name) {
		if (name.length() > 32 || name.isEmpty()) return false;
		char[] nameChars = name.toCharArray();
		for (char i : nameChars) {
			if (i == '-') continue;
			if (!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i)) || i == ' ') {
				return false;
			}
		}
		return true;
	}
}
