package consoleCommandManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Commander {

	public void main(String[] args) throws IOException {
//		Runtime rt = Runtime.getRuntime();
//		//String[] commands = { "cmd", "/c", "copy C:\\output\\html\\*.txt C:\\output\\" };
//		String[] commands = { "cmd", "/c", "explorer %cd%" };
//		Process proc = rt.exec(commands);
//
//		BufferedReader stdInput = new BufferedReader(new 
//		     InputStreamReader(proc.getInputStream()));
//
//		BufferedReader stdError = new BufferedReader(new 
//		     InputStreamReader(proc.getErrorStream()));
//
//		// read the output from the command
//		System.out.println("Here is the standard output of the command:\n");
//		String s = null;
//		while ((s = stdInput.readLine()) != null) {
//		    System.out.println(s);
//		}
//
//		// read any errors from the attempted command
//		System.out.println("Here is the standard error of the command (if any):\n");
//		while ((s = stdError.readLine()) != null) {
//		    System.out.println(s);
//		}

		    String[] command = {"CMD", "/C", "Explorer %cd%"};
//		    String[] command2 = {"CMD", "/C", "ping google.com"};
		    String[] command2 = {"CMD", "/C", "echo Hi"};
		    String[] command3 = {"CMD", "/C", "cnd .."};
	        ProcessBuilder probuilder = new ProcessBuilder( command2 );
	        List<String> commands = probuilder.command();
	        
	        //You can set up your work directory
	        //probuilder.directory(new File("c:\\xyzwsdemo"));
	        
	        Process process = probuilder.start();
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        System.out.printf("Output of running %s is:\n",
	                Arrays.toString(command));
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        
	        BufferedReader stdError = new BufferedReader(new 
	   		     InputStreamReader(process.getErrorStream()));
	        
			// read any errors from the attempted command
			//System.out.println("Here is the standard error of the command (if any):\n");
			while ((line = stdError.readLine()) != null) {
			    System.out.println(line);
			}
	        
	        //Wait to get exit value
	        try {
	            int exitValue = process.waitFor();
	            System.out.println("\n\nExit Value is " + exitValue);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        //probuilder.directory(probuilder.directory().getParentFile());	  
	        System.out.println(probuilder.directory().getAbsolutePath());
	        probuilder.command(command);
	        process = probuilder.start();
	}

}
