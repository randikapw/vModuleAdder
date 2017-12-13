package Commander;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import consoleCommandManager.ConsoleCommandManager;
import consoleCommandManager.ConsoleOutput;
import unzip.UnZip;
import verifier.Verifier;

public class Commander {
	// This a temp location which reprocent repo for now.
	private static String REPOSITORY_FOLDER = "C:\\Users\\Randika\\eclipse-workspace\\commandExecuter\\output\\";
	private static String ionicProjectPath;

	public static void main(String[] args) {
		setRepoPathFromFile();
		// Test console manager.
		ConsoleCommandManager ccm = ConsoleCommandManager.getInstance();
		ConsoleOutput out = null;
		
		System.out.print("Current directory: ");
		out = ccm.executeCommand("echo %cd%");
		// ccm.executeCommand("mkdir yo");
		//ccm.executeCommand("explorer %cd%");

		Verifier v = new Verifier();

		try {
			v.verifyPathForInoicProject(out.getOutput());
			ionicProjectPath = out.getOutput();
			requestComponent();
		} catch (Exception e) {
			try {
				Scanner sc = new Scanner(System.in);
				System.out.print("> Plese enter path for ionic project : ");
				String path = sc.next();
				// sc.close();
				v = v.verifyPathForInoicProject(path);
				ionicProjectPath = path;
				requestComponent();
			} catch (Exception e1) {
				System.out.println("Path you have entred is invalid or not a path for ionic project");
			}
		}
	}

	public static void requestComponent() {
		ConsoleCommandManager.getInstance().setWorkingDerectory(ionicProjectPath);
		Scanner sc = new Scanner(System.in);
		System.out.print("> Plese enter modulename you want to add : ");
		String module = sc.next();
		addComponent(module);
		System.out.print("\n> Do you want to add another module? (Y) : ");
		String answer = sc.next();
		if (answer.equalsIgnoreCase("Y")) {
			requestComponent();
		}
		sc.close();
	}

	// Temp function; need create separate manager for this.
	private static boolean addComponent(String componendName) {
		Verifier v = new Verifier();
		if (componendName.equalsIgnoreCase("vLogger")) {
			String srcFolder = ionicProjectPath + File.separator + "src";
			try {
				System.out.println("Adding '" + componendName + "' to the project.");
				v.verifyFolderExist(srcFolder);
				if (v.isFileExist(
						String.format("%2$s%1$sproviders%1$sv-logger%1$sv-logger.ts", File.separator, srcFolder))) {
					System.out.println("Module '" + componendName + "' already exist.");
					return true;
				} else {
					ConsoleOutput out = ConsoleCommandManager.getInstance().executeCommand("ionic g provider vLogger");
					UnZip.unZipIt(REPOSITORY_FOLDER + "v-logger.zip", srcFolder + File.separator + "providers");
					System.out.println("Module '" + componendName + "' added successfully.");
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Cannot find a component with the name: " + componendName);
			return false;
		}
	}

	private static void setRepoPathFromFile() {

		try {
			File file = new File("repo_local_path.txt");
			if (file.exists()) {
				// Creates a FileReader Object
				FileReader fr = new FileReader(file);
				char[] a = new char[200];
				fr.read(a); // reads the content to the array

				REPOSITORY_FOLDER = String.copyValueOf(a).trim();
				System.out.println("REPOSITORY_FOLDER set to : " + REPOSITORY_FOLDER);
				fr.close();
			} else {
				// creates the file
				file.createNewFile();

				// creates a FileWriter Object
				FileWriter writer = new FileWriter(file);

				// Writes the content to the file
				writer.write(REPOSITORY_FOLDER);
				writer.flush();
				writer.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
