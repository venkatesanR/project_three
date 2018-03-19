package com.addval.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;

public class CreateTap5Page {
	private FileUtils fileUtils = null;

	public CreateTap5Page() {
		fileUtils = new FileUtils();
	}

	public void execute(CommandLine cl) throws XmlException, IOException, XRuntime {

		String srcDir = cl.hasFlag("-s") ? cl.getFlagArgument("-s") : null;
		String destDir = cl.hasFlag("-t") ? cl.getFlagArgument("-t") : null;
		String pageName = cl.hasFlag("-p") ? cl.getFlagArgument("-p") : null;
		String propFileName = cl.hasFlag("-f") ? cl.getFlagArgument("-f") : null;

		if (StrUtl.isEmptyTrimmed(srcDir) || StrUtl.isEmptyTrimmed(destDir) || StrUtl.isEmptyTrimmed(pageName) || StrUtl.isEmptyTrimmed(propFileName)) {
			printHelp();
			return;
		}

		/* Load properties */
		Properties properties = new Properties();
		InputStream inputStream = fileUtils.openInputStream(new File(propFileName));
		properties.load(inputStream);
		inputStream.close();
		properties.put("SAMPLE", pageName.toUpperCase());
		properties.put("sample", pageName.replace(pageName.charAt(0), (String.valueOf(pageName.charAt(0)).toLowerCase().charAt(0))));

		System.out.println("Properties :" + properties.size());
		String packageName = (String) properties.get("packageName");

		String packagePath = packageName.replace(".", "\\");
		String subpackagePath = (String) properties.get("subpackageName");

		// Read source template files
		String extensions[] = { "xml", "java", "tml", "js", "css" };
		Collection list = fileUtils.listFiles(new File(srcDir), extensions, true);
		if (list != null) {
			File file = null;
			File fileOut = null;
			String fileContent = null;
			String propName = null;
			String propKey = null;
			String propValue = null;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				file = (File) iterator.next();
				fileContent = fileUtils.readFileToString(file);
				for (Enumeration propEnum = properties.propertyNames(); propEnum.hasMoreElements();) {
					propName = (String) propEnum.nextElement();
					propKey = "${" + propName + "}";
					propValue = properties.getProperty(propName);
					fileContent = StringUtils.replace(fileContent, propKey, propValue);
				}

				fileContent = StringUtils.replace(fileContent, "Sample", pageName);
				String fileOutName = file.getAbsolutePath();
				fileOutName = StringUtils.replace(fileOutName, srcDir, destDir);
				fileOutName = StringUtils.replace(fileOutName, "\\archetype-resources", "");

				if (file.getName().endsWith(".java")) {
					fileOutName = StringUtils.replace(fileOutName, "\\java\\", "\\java\\" + packagePath + "\\");
				}
				else if (file.getName().endsWith("PageMetadata.xml")) {
					fileOutName = StringUtils.replace(fileOutName, "\\resources\\", "\\resources\\" + packagePath + "\\");
				}

				if (file.getName().endsWith("applicationContext.xml")) {
					System.out.println(fileContent);
				}
				else {
					int lastIndex = fileOutName.lastIndexOf("\\");
					fileOutName = fileOutName.substring(0, lastIndex) + "\\" + subpackagePath + fileOutName.substring(lastIndex);

					fileOutName = StringUtils.replace(fileOutName, "Sample", pageName);
					file = new File(fileOutName);
					fileUtils.writeStringToFile(file, fileContent);
					System.out.println("Write :" + fileOutName);
				}
			}
		}
	}

	private static void printHelp() {
		System.out.println("USAGE: com.addval.utils.CreateTap5Page -s <SRC_DIR> -t <DEST_DIR> -p <PAGE_NAME> -f <ARCHETYPE_PROPERTIES>");
		System.out.println("  -s: SOURCE DIR");
		System.out.println("  -t: TARGET DIR");
		System.out.println("  -p: PAGE NAME");
		System.out.println("  -f: PROPERTIES");
		System.out.println("  -h:  help");
	}

	public static void main(String[] args) {
		try {
			CommandLine cl = new CommandLine(args);
			System.out.println("Arguments passed: " + cl.toString());

			if (cl.hasFlag("-h") || args.length == 0) {
				printHelp();
				System.exit(0);
			}
			CreateTap5Page client = new CreateTap5Page();
			client.execute(cl);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
