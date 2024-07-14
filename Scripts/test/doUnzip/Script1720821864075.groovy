String inputZip = "./sample.zip"
String outputDir = "./build/unzipped"

CustomKeywords."my.DeleteDir.deleteDirectoryRecursively"(outputDir)  // delete the outputDir
CustomKeywords."my.ZipUtil.unzip"(inputZip, outputDir)
