package my

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import com.kms.katalon.core.annotation.Keyword

/**
 * Refer to https://www.baeldung.com/java-compress-and-uncompress
 * 
 * @author kazurayam
 */
public class ZipUtil {

	public ZipUtil() {}

	@Keyword
	public static void unzip(String sourceZipFilePath, String destDirPath) throws IOException {
		File sourceZipFile = new File(sourceZipFilePath)
		File destDir = new File(destDirPath)
		this.unzip(sourceZipFile, destDir)
	}

	@Keyword
	public static void unzip(File sourceZipFile, File destDir) throws IOException {
		if (sourceZipFile.isDirectory()) {
			throw new IllegalArgumentException(
			"a file is expected but a directory is given: " + sourceZipFile.getAbsolutePath())
		}
		ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZipFile))
		ZipEntry zipEntry = zis.getNextEntry();
		byte[] buffer = new byte[1024]
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry)
			if (zipEntry.isDirectory()) {
				if (!newFile.exists()) {
					boolean b = newFile.mkdirs();
					if (!b) {
						throw new IOException("Failed to create directory " + newFile)
					}
				}
			} else {
				// fix for Windows-created archives
				File parent = newFile.getParentFile();
				if (!parent.exists()) {
					boolean b = parent.mkdirs();
					if (!b) {
						throw new IOException("Failed to create directory " + parent)
					}
				}
				// write file content
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry()
		}
		zis.closeEntry();
		zis.close();
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry)
	throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName())
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();
		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException(
			"Entry is outside of the target dir: " + zipEntry.getName());
		}
		return destFile;
	}

	@Keyword
	public static void zipDirectory(String sourceDirPath, String outputZipPath) throws IOException {
		File sourceDir = new File(sourceDirPath)
		File outputZip = new File(outputZipPath)
		this.zipDirectory(sourceDir, outputZip)
	}

	@Keyword
	public static void zipDirectory(File sourceDir, File outputZip) throws IOException {
		assert sourceDir.exists()
		if (sourceDir.isFile()) {
			throw new IllegalArgumentException(
			"a directory is expected but a file is given: " + sourceDir.getAbsolutePath())
		}
		if (!outputZip.getParentFile().exists()) {
			outputZip.getParentFile().mkdirs()
		}
		FileOutputStream fos = new FileOutputStream(outputZip)
		ZipOutputStream zipOut = new ZipOutputStream(fos)
		//
		zipFile(sourceDir, sourceDir.getName(), zipOut)
		zipOut.close()
		fos.close()
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" +
						childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
}
