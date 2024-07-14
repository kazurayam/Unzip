package my

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import com.kms.katalon.core.annotation.Keyword

/**
 * A utility class that implements deleteDirectoryRecursively method
 */
public class DeleteDir {
	
	@Keyword
	public static void deleteDirectoryRecursively(String dir) throws IOException {
		Path d = Paths.get(dir)
		deleteDirectoryRecursively(d);
	}

	/**
	 * delete the target directory and its content files/directories recursively.
	 *
	 * @param dir directory to delete
	 * @throws IOException any error while deletion
	 */
	@Keyword
	public static void deleteDirectoryRecursively(Path dir) throws IOException {
		Objects.requireNonNull(dir);
		if (!Files.exists(dir)) {
			throw new IOException(dir.toString() + " does not exist");
		}
		Files.walk(dir)
				.sorted(Comparator.reverseOrder())
				.each({ p ->
					try {
						if (Files.exists(p)) {
							Files.delete(p);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}
}