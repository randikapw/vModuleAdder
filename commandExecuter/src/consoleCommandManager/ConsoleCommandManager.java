package consoleCommandManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConsoleCommandManager {
	private static ConsoleCommandManager instance;
	private static ProcessBuilder probuilder;
	private ConsoleCommandManager() {
		
	}
	
	public static ConsoleCommandManager getInstance() {
		if (instance == null) {
			instance = new ConsoleCommandManager();
			ConsoleCommandManager.probuilder = new ProcessBuilder(new String[]{"CMD", "/C", "echo Hi"});
		}
		return instance;
	}
	
	private List<String> getCmdCommand(String ... strings ){
		ArrayList<String> list = new ArrayList<String>();
		list.add("CMD");
		list.add("/C");
		for (String string : strings) {
			list.add(string);
		}
		return list;
	}
	
	public void setWorkingDerectory(String path){
		File file = new File(path);
		probuilder.directory(file);
	}
	
	public ConsoleOutput executeCommand(String ... commands ){
		probuilder.command(getCmdCommand(commands));
		ConsoleOutput output = new ConsoleOutput();
		try {
		Process process = probuilder.start();
        
        //Read out dir output
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder outputBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            outputBuilder.append(line);
        }
        
        BufferedReader stdError = new BufferedReader(new 
   		InputStreamReader(process.getErrorStream()));
        int errorLineCount = 0;
		// read any errors from the attempted command
		//System.out.println("Here is the standard error of the command (if any):\n");
		while ((line = stdError.readLine()) != null) {
			++errorLineCount;
		    System.out.println(line);
            outputBuilder.append(line);
		}
		
		output.setOutput(outputBuilder.toString());
		output.setError(errorLineCount > 0);
        
        //Wait to get exit value
        int exitValue = process.waitFor();
        output.setExitCode(exitValue);  

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			output.setOutput(e.getMessage());
			output.setError(true);
	        output.setExitCode(-1);
		}
		
		return output;
		
	}
	

}
