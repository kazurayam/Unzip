package my

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import com.kms.katalon.core.annotation.Keyword

public class ZipUtil {

	@Keyword
	public void unzip(String zipFilePath, String destDirPath) throws IOException {
		File inputZipFile = new File(zipFilePath)
		File destDir = new File(destDirPath).getCanonicalFile()
		ZipInputStream zis = new ZipInputStream(new FileInputStream(inputZipFile))
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

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName())
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();
		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}
		return destFile;
	}
}
