package com.addval.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.xmlbeans.XmlException;

public class MultiWarUtils {
	private static final String newLine = System.getProperty("line.separator");

	public MultiWarUtils() {
	}

	public void execute(CommandLine cl) throws XmlException, IOException, XRuntime {
		String earReleaseFolder = cl.hasFlag("-t") ? cl.getFlagArgument("-t") : null;
		String contextRootRename = cl.hasFlag("-c") ? cl.getFlagArgument("-c") : null;
		if (StrUtl.isEmptyTrimmed(earReleaseFolder)) {
			printHelp();
			return;
		}

		Project project = new Project();
		File earFolder = new File(earReleaseFolder);

		File appLibFolder = new File(earReleaseFolder + "/APP-INF/lib");
		File appClassesFolder = new File(earReleaseFolder + "/APP-INF/classes");
		File sharedwebFolder = new File(earReleaseFolder + "/APP-INF/classes/sharedweb");
		File appFile = new File(earReleaseFolder + "/META-INF/application.xml");
		/************************************************************************************************
		 * Collection WAR details
		 *********************************************************************************************/
		IOFileFilter fileFilter = new IOFileFilter() {
			public boolean accept(File file) {
				return file.getName().equals("web.xml");
			}

			public boolean accept(File dir, String name) {
				return true;
			}
		};
		Collection<File> webFiles = FileUtils.listFiles(earFolder, fileFilter, FileFilterUtils.trueFileFilter());
		if (webFiles == null) {
			return;
		}

		List<String> uniqueContext = new ArrayList<String>();
		List<String> uniqueContextPath = new ArrayList<String>();
		List<String> allContext = new ArrayList<String>();

		String webFileContent = null;
		for (File webFile : webFiles) {
			webFileContent = FileUtils.readFileToString(webFile);
			int contextConfigLocationIndex = StringUtils.indexOf(webFileContent, "contextConfigLocation");
			int sharedWebContextIndex = StringUtils.indexOf(webFileContent, "SharedWebContext");
			int contextLoaderListenerIndex = StringUtils.indexOf(webFileContent, "org.springframework.web.context.ContextLoaderListener");
			if (contextConfigLocationIndex != -1 && contextLoaderListenerIndex != -1 && sharedWebContextIndex == -1) {
				System.out.println(webFile.getAbsolutePath());

				String part1 = StringUtils.substring(webFileContent, 0, contextConfigLocationIndex);
				String part2 = StringUtils.substring(webFileContent, contextConfigLocationIndex);

				int startPos = StringUtils.lastIndexOf(part1, "context-param>") - 1;
				int endPos = contextConfigLocationIndex + StringUtils.indexOf(part2, "context-param>") + "context-param>".length();

				String contextParam = StringUtils.substring(webFileContent, startPos, endPos);
				String paramValueStr = StringUtils.substringBetween(contextParam, "<param-value>", "</param-value>");
				String paramValues[] = StringUtils.split(paramValueStr, " ");
				for (String paramValue : paramValues) {
					paramValue = paramValue.substring("WEB-INF/".length());
					if (!uniqueContext.contains(paramValue)) {
						uniqueContext.add(paramValue);
						uniqueContextPath.add(webFile.getParent() + paramValue);
					}
					System.out.println("Context :" + paramValue);
					allContext.add(webFile.getParent() + paramValue);
				}
				StringBuffer updatedWebFileContent = new StringBuffer(webFileContent);
				updatedWebFileContent = updatedWebFileContent.insert(startPos, "<!--");
				updatedWebFileContent = updatedWebFileContent.insert(endPos + "<!--".length(), "-->" + newLine + getSharedWebContext());

				System.out.println("Update :" + StringUtils.substring(webFile.getAbsolutePath(), earReleaseFolder.length()) + newLine);
				FileUtils.writeStringToFile(webFile, updatedWebFileContent.toString());
			}
		}

		Copy copy = null;
		Delete delete = null;
		FileSet fileSet = null;
		PatternSet.NameEntry patternSet = null;
		String baseDir = null;
		String filePattern = null;

		if(uniqueContext.size() >0){
			// Create sharedBeanRefContext.xml
			File sharedBeanRefContext = new File(earReleaseFolder + "/APP-INF/classes/sharedBeanRefContext.xml");
			FileUtils.writeStringToFile(sharedBeanRefContext, getSharedBeanRefContext(uniqueContext));
			System.out.println("Create :" + StringUtils.substring(sharedBeanRefContext.getAbsolutePath(), earReleaseFolder.length())+ newLine);

			// Copy Spring context xml(s) to sharedweb folder
			for (String context : uniqueContextPath) {
				baseDir = StringUtils.substring(context, 0, context.indexOf("WEB-INF") + "WEB-INF".length());
				filePattern = StringUtils.substring(context, context.indexOf("WEB-INF/") + "WEB-INF/".length());
				copy = new Copy();
				copy.setTodir(sharedwebFolder);
				fileSet = new FileSet();
				fileSet.setDir(new File(baseDir));
				patternSet = fileSet.createInclude();
				patternSet.setName(filePattern);
				copy.addFileset(fileSet);
				copy.setProject(project);
				copy.execute();

				System.out.println("Copy :" + filePattern +" to " + StringUtils.substring(sharedwebFolder.getAbsolutePath(), earReleaseFolder.length()));
			}
			System.out.println("");
			// Delete Spring context xml(s)
			for (String context : allContext) {
				baseDir= StringUtils.substring(context, 0, context.indexOf("WEB-INF") + "WEB-INF".length());
				File baseFolder = new File(baseDir);
				filePattern = StringUtils.substring(context, context.indexOf("WEB-INF/") + "WEB-INF/".length());
				delete = new Delete();
				fileSet = new FileSet();
				fileSet.setDir(baseFolder);
				patternSet = fileSet.createInclude();
				patternSet.setName(filePattern);
				delete.addFileset(fileSet);
				delete.setProject(project);
				delete.execute();

				System.out.println("Delete :" + filePattern +" from " + StringUtils.substring(baseFolder.getAbsolutePath(), earReleaseFolder.length()));
			}
			System.out.println("");
		}

		// Create empty applicationContext.xml under WEB-INF folder
		for (File webFile : webFiles) {
			File applicationContext = new File(webFile.getParent() + "/applicationContext.xml");
			FileUtils.writeStringToFile(applicationContext, getApplicationContext());
			System.out.println("Create :" + StringUtils.substring(applicationContext.getAbsolutePath(), earReleaseFolder.length())+ newLine);
		}

		/************************************************************************************************
		 * Copy ALL the war jar(s) from WEB-INF/lib to APP-INF/lib
		 * *********************************************************************************************/
		copy = new Copy();
		copy.setTodir(appLibFolder);
		copy.setOverwrite(false);
		copy.setFlatten(true);
		fileSet = new FileSet();
		fileSet.setDir(earFolder);

		patternSet = fileSet.createInclude();
		patternSet.setName("**/WEB-INF/lib/*.jar");
		copy.addFileset(fileSet);

		copy.setProject(project);
		copy.execute();
		System.out.println("Copy ALL WAR jars from WEB-INF/lib to APP-INF/lib");

		/************************************************************************************************
		 * Copy ALL the properties(s) from WEB-INF/classes to APP-INF/classes
		 * *********************************************************************************************/
		copy = new Copy();
		copy.setTodir(appClassesFolder);
		copy.setOverwrite(false);
		copy.setFlatten(true);
		fileSet = new FileSet();
		fileSet.setDir(earFolder);
		patternSet = fileSet.createInclude();
		patternSet.setName("**/WEB-INF/classes/*.properties");
		//patternSet = fileSet.createInclude();
		//patternSet.setName("**/WEB-INF/classes/*.xsd");
		patternSet = fileSet.createExclude();
		patternSet.setName("**/WEB-INF/classes/application-resources");
		patternSet = fileSet.createExclude();
		patternSet.setName("**/WEB-INF/classes/ApplicationResources.properties");
		copy.addFileset(fileSet);

		copy.setProject(project);
		copy.execute();
		System.out.println("Copy ALL properties from WEB-INF/classes to APP-INF/classes");

		/************************************************************************************************
		 * Copy ALL the java class(s) from WEB-INF/classes to APP-INF/classes
		 * *********************************************************************************************/
		copy = new Copy();
		copy.setTodir(appClassesFolder);
		copy.setOverwrite(false);

		for (File webFile : webFiles) {
			fileSet = new FileSet();
			fileSet.setDir(new File(webFile.getParent() + "/classes"));
			patternSet = fileSet.createInclude();
			patternSet.setName("com/**");
			copy.addFileset(fileSet);
		}

		copy.setProject(project);
		copy.execute();
		System.out.println("Copy ALL classes from WEB-INF/classes to APP-INF/classes");

		/************************************************************************************************
		 * Delete ALL the war jar(s) from WEB-INF/lib
		 * *********************************************************************************************/
		delete = new Delete();
		fileSet = new FileSet();
		fileSet.setDir(earFolder);
		patternSet = fileSet.createInclude();
		patternSet.setName("**/WEB-INF/lib/*.jar");

		for (File webFile : webFiles) {
			String excludeJar = StringUtils.substring(webFile.getParent(), earReleaseFolder.length() + 1);
			excludeJar = StringUtils.substring(excludeJar,0,excludeJar.indexOf(".war"));
			patternSet = fileSet.createExclude();
			patternSet.setName("**/WEB-INF/lib/" + excludeJar +".jar");
		}

		patternSet = fileSet.createInclude();
		patternSet.setName("**/WEB-INF/classes/*.properties");
		//patternSet = fileSet.createInclude();
		//patternSet.setName("**/WEB-INF/classes/*.xsd");
		patternSet = fileSet.createExclude();
		patternSet.setName("**/WEB-INF/classes/application-resources");
		patternSet = fileSet.createExclude();
		patternSet.setName("**/WEB-INF/classes/ApplicationResources.properties");
		delete.addFileset(fileSet);

		delete.setProject(project);
		delete.execute();

		for (File webFile : webFiles) {
			delete = new Delete();
			delete.setDir(new File(webFile.getParent() + "/classes/com"));
			delete.setProject(project);
			delete.execute();
		}
		System.out.println("Delete ALL JARS,Properties and classes from WEB-INF/classes");

		/************************************************************************************************
		 * Rename Web Context Root in META-INF/application.xml
		 * *********************************************************************************************/
		if(!StrUtl.isEmptyTrimmed(contextRootRename)){
			String appFileContent = FileUtils.readFileToString(appFile);
			String pairs[] = StringUtils.split(contextRootRename,",");
			for(String pair: pairs){
				String contextRoot[] = StringUtils.split(pair,"=");
				if(contextRoot.length == 2){
					appFileContent = StringUtils.replace(appFileContent, "<context-root>/" + contextRoot[0] + "</context-root>", "<context-root>/" + contextRoot[1] + "</context-root>");
					System.out.println("Web context " + contextRoot[0] +" renamed to " + contextRoot[1]);
				}
			}
			FileUtils.writeStringToFile(appFile, appFileContent);
		}
		/************************************************************************************************
		 * Remove /WEB-INF/ reference from APP-INF/classes/sharedweb/*.xml
		 * *********************************************************************************************/
        String extensions[] = {"xml"};
        Collection<File> list = FileUtils.listFiles(sharedwebFolder,extensions, true);
        if(list != null){
        	String springContext= null;
    		for (File springFile : list) {
    			springContext = FileUtils.readFileToString(springFile);
    			springContext = StringUtils.replace(springContext, "/WEB-INF/","../");
    			FileUtils.writeStringToFile(springFile, springContext);
    		}
        }
		System.out.println("Remove \"/WEB-INF/\" reference from APP-INF/classes/sharedweb/*.xml" );
	}

	private String getSharedWebContext() {
		StringBuffer sharedWebContext = new StringBuffer(newLine);
		sharedWebContext.append("<context-param>").append(newLine);
		sharedWebContext.append("<param-name>locatorFactorySelector</param-name>").append(newLine);
		sharedWebContext.append("<param-value>classpath*:sharedBeanRefContext.xml</param-value>").append(newLine);
		sharedWebContext.append("</context-param>").append(newLine);
		sharedWebContext.append("<context-param>").append(newLine);
		sharedWebContext.append("<param-name>parentContextKey</param-name>").append(newLine);
		sharedWebContext.append("<param-value>SharedWebContext</param-value>").append(newLine);
		sharedWebContext.append("</context-param>").append(newLine);
		return sharedWebContext.toString();
	}

	private String getSharedBeanRefContext(List<String> uniqueContext) {
		StringBuffer sharedBeanRefContext = new StringBuffer();
		sharedBeanRefContext.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(newLine);
		sharedBeanRefContext.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"").append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").append(" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\">").append(newLine);
		sharedBeanRefContext.append("<bean id=\"SharedWebContext\" class=\"org.springframework.context.support.ClassPathXmlApplicationContext\">").append(newLine);
		sharedBeanRefContext.append("<constructor-arg>").append(newLine);
		sharedBeanRefContext.append("<list>").append(newLine);
		for (String context : uniqueContext) {
			sharedBeanRefContext.append("<value>classpath*:sharedweb" + context + "</value>").append(newLine);
		}
		sharedBeanRefContext.append("</list>").append(newLine);
		sharedBeanRefContext.append("</constructor-arg>").append(newLine);
		sharedBeanRefContext.append("</bean>").append(newLine);
		sharedBeanRefContext.append("</beans>").append(newLine);

		return sharedBeanRefContext.toString();
	}

	private String getApplicationContext() {
		StringBuffer applicationContext = new StringBuffer();
		applicationContext.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(newLine);
		applicationContext.append("<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\">").append(newLine);
		applicationContext.append("</beans>").append(newLine);
		return applicationContext.toString();

	}

	private static void printHelp() {
		System.out.println("USAGE: MultiWarUtils -t <EAR_TARGET_FOLDER> -c <CONTEXT_ROOT_RENAME> -l <SHOW_LOG>");
		System.out.println("  -t:  EAR target folder");
		System.out.println("  -c:  context rename ");
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
			MultiWarUtils client = new MultiWarUtils();
			client.execute(cl);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
