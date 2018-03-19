package com.addval.quartzutils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.addval.environment.Environment;
import com.addval.esapiutils.validator.AppSecurityValidatorException;
import com.addval.esapiutils.validator.FileSecurityValidator;
import com.addval.esapiutils.validator.FileSecurityValidatorESAPIImpl;
import com.addval.esapiutils.validator.HTMLSecurityValidator;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;


/* Ant Task */
//import org.apache.tools.ant.*;


/*
 * <p> Built in job for executing native executables in a separate process. </p>
 *
 */
public class NativeTask implements Job {
	
	private FileSecurityValidator fileSecurityValidator;
	
	public void setFileSecurityValidator(FileSecurityValidator fileSecurityValidator)
	{
		this.fileSecurityValidator=fileSecurityValidator;
	}

	public FileSecurityValidator fileSecurityValidator()
	{
		if(fileSecurityValidator==null){
			fileSecurityValidator=new FileSecurityValidatorESAPIImpl();
		}	
		
		return fileSecurityValidator;
	}
	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 *
	 * Constants.
	 *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	/**
	 * Required parameter that specifies the name of the command (executable) to
	 * be ran.
	 */
	public static final String PROP_COMMAND = "command";

	/**
	 * Optional parameter that specifies the build.xml file name
	 */
	public static final String PROP_STATICPARAMS = "staticparams";

	/**
	 * parameter that specifies the target to be executed
	 */
	public static final String PROP_TARGET = "target";

	/**
	 * Optional parameter that specifies the location of the stdout for the run
	 */
	public static final String PROP_STDOUT = "stdout";

	/**
	 * Optional parameter that specifies the location of the stderr for the run
	 */
	public static final String PROP_STDERR = "stderr";

	/**
	 * Optional parameter that specifies the location of the stderr for the run
	 */
	private static final String SUBSYSTEM = "avcommon";

	/**
	 * Parameter that specifies the directory where the build file for ANT exists.
	 * This is used if a process is not forked.
	 */
	private static final String PROP_COMMANDDIR = "commanddir";

	private static final String _LAST_UPDATED_BY = "LAST_UPDATED_BY";


	public NativeTask() {
	}

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 *
	 * Interface.
	 *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		//System.out.println("---" + context.getJobDetail().getFullName()
		//		+ " executing.[" + new Date() + "]");

		boolean execute_job = shouldExecute(context);

		if (execute_job == false) {
			//System.out
			//		.println("--- skipping execution as it is not the planned scheduler ---");
			return;
		}

		executeMap(context.getJobDetail().getJobDataMap());
	}

	public void executeMap(java.util.Map map) throws JobExecutionException {

		String command = (String) map.get(PROP_COMMAND);

		String staticparam = (String) map.get(PROP_STATICPARAMS);

		String target = (String) map.get(PROP_TARGET);

		String stdout_file = (String) map.get(PROP_STDOUT);

		String stderr_file = (String) map.get(PROP_STDERR);

		String command_dir = (String) map.get(PROP_COMMANDDIR);

		/*
		 * build dynamic parameters with parameters that start with PARM_ in the
		 * form -DPARM_XXX=YYY -DPARM_YYY=ZZZ
		 */
		String dynamicparam = buildAntParameters(map);

		/*
		 * build environment with parameters that start with PARM_ in the form
		 * -PARM_XXX=YYY PARM_YYY=ZZZ
		 */
		String[] envp = null;

		/*
		 * Replace paths variables for unix and windows for example use \ as
		 * path separator for windows instead of / use / as path separator for
		 * unix instead of \ use %CARGORES_HOME% instead of {CARGORES_HOME} in
		 * Windows use $CARGORES_HOME instead of {CARGORES_HOME} in Unix or
		 * Linux
		 */
		command = replaceNativePath(command);
		stdout_file = replaceNativePath(stdout_file);
		stderr_file = replaceNativePath(stderr_file);

		if ((staticparam == null) || (staticparam.length() == 0)) {
			staticparam = "";
		}

		// build full command.
		// If ant is in the command append the
		// dynamic params in command line including the static params and target
		//
		// If ant is not in the command then it is assumed to be a shell script
		// or batch file
		// The dynamic parameters are passed as environment variables
		//
		String full_command = null;

		//
		// The exec command with a single string value has problems with space
		// as
		// delimiter. So we assume a string array in which the second parameter
		// is
		// always blank
		//
		String parameters = "";
		boolean fork = command != null;

		if (command == null)
		{
			// dynamic parameters are only passed as environment variables
			full_command = staticparam + " " + target;
		}
		else if (command != null && command.indexOf("ant") != -1)
		{
			// dynamic parameters are passed as -D to ant
			full_command = command + " " + staticparam + " " + dynamicparam + " " + target;
		}
		else
		{
			// dynamic parameters are only passed as environment variables
			full_command = command + " " + staticparam + " " + target;
			// build the environment parameters
			envp = buildEnvironment(map);
		}

		if (!fork) {

			Vector antArgs = new Vector();

			antArgs.add( "-dir" );
			antArgs.add( command_dir );

			StringTokenizer tokenizer = new StringTokenizer( full_command );
			while (tokenizer.hasMoreTokens())
				antArgs.add( tokenizer.nextToken() );

			tokenizer = new StringTokenizer( parameters );
			while (tokenizer.hasMoreTokens())
				antArgs.add( tokenizer.nextToken() );

			tokenizer = new StringTokenizer( dynamicparam );
			while (tokenizer.hasMoreTokens())
				antArgs.add( tokenizer.nextToken() );

			// Logging - DO NOT ENABLE LOGGING USING -l parameter
			// Check before doing this.All System out logging will go to this file
			//antArgs.add( "-l" );
			//antArgs.add(stdout_file);

			try {

				if (Environment.getInstance(SUBSYSTEM).getLogFileMgr(getClass().getName()).debugOn()) {

					// Print the args being passed to ANT
					Environment.getInstance(SUBSYSTEM).getLogFileMgr(getClass().getName()).logInfo( "Parameters being passed to AntWrapperMain - " );
					Environment.getInstance(SUBSYSTEM).getLogFileMgr(getClass().getName()).logInfo( antArgs.toString() );
				}

				AntWrapperMain.start((String[])antArgs.toArray(new String[antArgs.size()]), null, null);
			}
			catch (Exception e) {
				//System.out.println("exception happened in native job");
				e.printStackTrace();
				Environment.getInstance(SUBSYSTEM)
						.getLogFileMgr(getClass().getName()).logError(e);
				throw new JobExecutionException( e.getMessage(),
						e, true);
			}
			catch (Throwable x) {
				// This means its an error, just log it
				x.printStackTrace();
				Environment.getInstance(SUBSYSTEM)
						.getLogFileMgr(getClass().getName()).logError(x.getMessage());
				throw new JobExecutionException( x.getMessage(), new Exception( x ), true );

			}
		}
		else {

			// If a file name is specified for standard out and standard error
			// use that
			//
			FileOutputStream stdout_fos = null;
			FileOutputStream stderr_fos = null;
			//System.out.println( "Logging of Native Tasks will happen to - " + stdout_file + " and " + stderr_file );

			try {
				if ((stdout_file == null) || (stdout_file.length() == 0)) stdout_fos = null;
				else stdout_fos = new FileOutputStream(stdout_file, true);

				if ((stderr_file == null) || (stderr_file.length() == 0)) stderr_fos = null;
				else stderr_fos = new FileOutputStream(stderr_file, true);

			} catch (Exception ex) {
				Environment.getInstance(SUBSYSTEM)
						.getLogFileMgr(getClass().getName()).logError(ex);
			}

			// run the command
			this.runNativeCommand(full_command, parameters, envp, stdout_fos, stderr_fos);
		}
	}

	public static Properties getEnvVars() throws Throwable {

		Process p = null;
		Properties envVars = new Properties();
		Runtime r = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();

		// System.out.println(OS);
		if (OS.indexOf("windows 9") > -1) {
			p = r.exec( "command.com /c set" );
		}
  		else if ( (OS.indexOf("nt") > -1)
			|| (OS.indexOf("windows 20") > -1 )  // probably is better since no other OS would return "windows" anyway
			|| (OS.indexOf("windows xp") > -1) ) {
			p = r.exec( "cmd.exe /c set" );
		}
		else {
			// our last hope, we assume Unix (thanks to H. Ware for the fix)
			p = r.exec( "env" );
		}

		java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader( p.getInputStream() ) );
		String line;
		while( (line = br.readLine()) != null ) {
			int idx = line.indexOf( '=' );
			String key = line.substring( 0, idx );
			String value = line.substring( idx+1 );
			envVars.setProperty( key, value );
		}

		return envVars;
	}


	/*
	 * build dynamic parameters with parameters that start with PARM_ in the
	 * form -DPARM_XXX=YYY -DPARM_YYY=ZZZ
	 */
	private String buildAntParameters(java.util.Map map) {
		Object keys[] = (Object[]) map.keySet().toArray();
		String parameter = "";

		for (int i = 0; i < keys.length; ++i) {
			String key = (String) keys[i];
			String value = null;
			Object objvalue = null;

			if (key.startsWith("PARM_")) {
				parameter = parameter + " -D" + key + "=" + map.get(key);
			}else if (_LAST_UPDATED_BY.equalsIgnoreCase(key)) {
				parameter = parameter + " -D" + key + "=" + map.get(key);
			}

		}

		return parameter;
	}

	/*
	 * build environment with parameters that start with PARM_ in the form
	 * -PARM_XXX=YYY PARM_YYY=ZZZ
	 */
	private String[] buildEnvironment(java.util.Map map) {
		Object keys[] = (Object[]) map.keySet().toArray();
		;
		String parameter = "";
		int env_size = 0;

		for (int i = 0; i < keys.length; ++i) {
			String key = (String) keys[i];

			if (key.startsWith("PARM_")) {
				env_size = env_size + 1;
			}else  if (_LAST_UPDATED_BY.equalsIgnoreCase(key)) {
				env_size = env_size + 1;
			}
		}

		String envp[] = new String[env_size];
		int j = 0;

		for (int i = 0; i < keys.length; ++i) {
			String key = (String) keys[i];
			String value = "" + map.get(key);

			if (key.startsWith("PARM_")) {

				//System.out.println( "Build Environment - " + key + ":" + value );
				envp[j] = new String(key + "=" + value);
				j = j + 1;
			}else if (_LAST_UPDATED_BY.equalsIgnoreCase(key)) {
				envp[j] = new String(key + "=" + value);
				j = j + 1;
			}

		}

		return envp;
	}

	/**
	 * runNativeCommand based on OS
	 */
	private void runNativeCommand(String command, String parameters,
			String[] envp, FileOutputStream stdout_fos,
			FileOutputStream stderr_fos) throws JobExecutionException {

		String[] cmd = null;
		String[] args = new String[2];
		args[0] = command;
		args[1] = parameters;

		try {
			//with this variable will be done the swithcing
			String osName = System.getProperty("os.name");

			//only will work with Windows NT
			if (osName.equals("Windows NT")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			//only will work with Windows 95
			else if (osName.equals("Windows 95")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "command.com";
				cmd[1] = "/C";
				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			//only will work with Windows 2000
			else if (osName.equals("Windows 2000")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			//only will work with Windows XP
			else if (osName.equals("Windows XP")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			//only will work with Linux
			else if (osName.equals("Linux")) {
				if (cmd == null)
					cmd = new String[args.length + 2];

				cmd[0] = "sh";
				cmd[1] = "-c";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			//will work with the rest
			else {
				if (cmd == null)
					cmd = new String[args.length + 2];

				cmd[0] = "sh";
				cmd[1] = "-c";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}

			Runtime rt = Runtime.getRuntime();
			Process proc = null;

			// If there is only 1 command array then let Java Runtime execute the command
			// by tokenzing
			if (cmd.length <= 1 || (cmd.length > 1 && com.addval.utils.StrUtl.isEmpty(cmd[1]))) {

				String cmdToExecute = "";
				for (int i = 0; i < cmd.length; i++)
					cmdToExecute += cmd[i] + " ";

				cmdToExecute = cmdToExecute.trim();

				{
					//System.out.println("About to execute command with values - " + cmdToExecute );
				}

				if ((envp != null) && (envp.length > 0)) {
					Environment.getInstance(SUBSYSTEM)
							.getLogFileMgr(getClass().getName()).logInfo(
									"About to run with environment" + cmdToExecute);
					proc = rt.exec(cmdToExecute, envp);
				} else {
					Environment
							.getInstance(SUBSYSTEM)
							.getLogFileMgr(getClass().getName())
							.logInfo(
									"About to run no environment" + cmdToExecute);
					proc = rt.exec(cmdToExecute);
				}
			}
			else {

				{
					//for (int i = 0; i < cmd.length; i++)
						//System.out.println("About to execute command with values - " + cmd[i] );
				}

				if ((envp != null) && (envp.length > 0)) {
					Environment.getInstance(SUBSYSTEM)
							.getLogFileMgr(getClass().getName()).logInfo(
									"About to run with environment" + cmd[0]
											+ cmd[1]);
					proc = rt.exec(cmd, envp);
				} else {
					Environment
							.getInstance(SUBSYSTEM)
							.getLogFileMgr(getClass().getName())
							.logInfo(
									"About to run no environment" + cmd[0] + cmd[1]);
					proc = rt.exec(cmd);
				}
			}

			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "ERROR", stderr_fos);
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "OUTPUT", stdout_fos);

			errorGobbler.start();
			outputGobbler.start();

			// any error message?
			int exitVal = proc.waitFor();
			errorGobbler.join();
			outputGobbler.join();

			Environment.getInstance(SUBSYSTEM)
					.getLogFileMgr(getClass().getName()).logInfo(
							"ExitValue: " + exitVal);

			if (exitVal != 0) {

				throw new XRuntime( "com.addval.quartzutils.NativeTask", "Error executing scheduled task.<br>" + (errorGobbler.getErrorOutput() != null ? errorGobbler.getErrorOutput().toString() : "") );
			}

		} catch (Exception x) {
			//System.out.println("error happened in native job");
			//System.out.println("error happened in native job" + cmd[0] +
			// cmd[1] + cmd[2] + cmd[3]);

			Environment.getInstance(SUBSYSTEM)
					.getLogFileMgr(getClass().getName()).logError(x);
			throw new JobExecutionException( x.getMessage(),
					x, false);
		}
	}

	/*
	 * Replace paths variables for unix and windows for example use \ as path
	 * separator for windows instead of / use / as path separator for unix
	 * instead of \ use %CARGORES_HOME% instead of {CARGORES_HOME} in Windows
	 * use $CARGORES_HOME instead of {CARGORES_HOME} in Unix or Linux
	 */
	private String replaceNativePath(String path) {
		if (path == null)
			return path;

		String osName = System.getProperty("os.name");

		if (osName.indexOf("Windows") != -1) {
			if (path != null) {
				String searchstr = "/";
				String replstr = "\\";

				path = oldReplace(path, searchstr, replstr);

				searchstr = "{";
				replstr = "%";

				path = oldReplace(path, searchstr, replstr);

				searchstr = "}";
				replstr = "%";

				path = oldReplace(path, searchstr, replstr);
			}
		} else {
			if (path != null) {
				String searchstr = "\\";
				String replstr = "/";

				path = oldReplace(path, searchstr, replstr);

				searchstr = "{";
				replstr = "$";

				path = oldReplace(path, searchstr, replstr);

				searchstr = "}";
				replstr = "";

				path = oldReplace(path, searchstr, replstr);
			}
		}

		return path;
	}

	private String oldReplace(final String aInput, final String aOldPattern,
			final String aNewPattern) {

		final StringBuffer result = new StringBuffer();
		//startIdx and idxOld delimit various chunks of aInput; these
		//chunks always end where aOldPattern begins
		int startIdx = 0;
		int idxOld = 0;
		while ((idxOld = aInput.indexOf(aOldPattern, startIdx)) >= 0) {
			//grab a part of aInput which does not include aOldPattern
			result.append(aInput.substring(startIdx, idxOld));
			//add aNewPattern to take place of aOldPattern
			result.append(aNewPattern);

			//reset the startIdx to just after the current match, to see
			//if there are any further matches
			startIdx = idxOld + aOldPattern.length();
		}

		//the final chunk will go to the end of aInput
		result.append(aInput.substring(startIdx));
		return result.toString();
	}

	private boolean shouldExecute(JobExecutionContext context) {
		String run_scheduler_name = null;
		String scheduler_name = null;
		boolean execute_job = false;

		try {
			run_scheduler_name = context.getJobDetail().getJobDataMap()
					.getString("scheduler_name");
			scheduler_name = context.getScheduler().getSchedulerName();

			if (run_scheduler_name != null) {
				if (run_scheduler_name.equals(scheduler_name)) {
					execute_job = true;
				}
			} else {
				execute_job = true;
			}
		} catch (org.quartz.SchedulerException ex) {
			//System.out.println("--Scheduler Exception");
			Environment.getInstance(SUBSYSTEM)
					.getLogFileMgr(getClass().getName()).logError(ex);
		}

		//System.out.println("--- run scheduler=" + run_scheduler_name
		//		+ " current_scheduler=" + scheduler_name);

		return execute_job;
	}

	public static void main(String[] args) throws Exception {
		NativeTask tsk = new NativeTask();
		String path = null;

		if (args.length > 1) {
			path = args[1];
		}

		//System.out.println(path);

		path = tsk.replaceNativePath(path);

		//System.out.println(path);
	}

	//
	//
	// Inner class to capture the standard output and
	// error from the batch program
	//
	//
	public class StreamGobbler extends Thread {

		InputStream is;

		String type;

		OutputStream os;

		StringBuffer errorOutput;

		public StringBuffer getErrorOutput() { return errorOutput; }

		StreamGobbler(InputStream is, String type) {
			this(is, type, null);
		}

		StreamGobbler(InputStream is, String type, OutputStream redirect) {
			this.is = is;
			this.type = type;
			this.os = redirect;
			if (StrUtl.equals( type, "ERROR" ))
				errorOutput = new StringBuffer();
		}

		public void run()  {

			try {
				PrintWriter pw = null;
				if (os != null)
					pw = new PrintWriter(os);
				
				String line;
				fileSecurityValidator=fileSecurityValidator();
				 while ((line=fileSecurityValidator.safeReadLine(is)) != null) {					

					if (pw != null) {
						pw.println(line);
						pw.flush();
					}

					Environment.getInstance(SUBSYSTEM)
							.getLogFileMgr(getClass().getName()).logInfo(
									type + ">" + line);

					if (StrUtl.equals(type, "ERROR")) {
						synchronized(errorOutput) {
							errorOutput.append( line + System.getProperty( "line.separator" ) );
						}
					}
				}

				if (pw != null)
					pw.flush();
				
				
			}
			catch (AppSecurityValidatorException e) {
				// TODO Auto-generated catch block
				e.getMessage();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.getMessage();
			}

			
			
		}
	}

}