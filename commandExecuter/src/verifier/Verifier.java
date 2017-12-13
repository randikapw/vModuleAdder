package verifier;

import java.io.File;

public class Verifier {
	
	public Verifier verifyPathForInoicProject(String path) throws Exception{
		File f = new File(path + File.separator + "ionic.config.json");
		if(f.exists() && !f.isDirectory()) {
			System.out.println("Ionic project verified.");
		    return this;
		} else {
			throw new Exception("File does not exist : ionic.config.json");
		}
	}
	

	public Verifier verifyFolderExist(String path) throws Exception{
		File f = new File(path);
		if(f.exists() && f.isDirectory()) {
			System.out.println("path verified: " + path );
		    return this;
		} else {
			throw new Exception("Path does not exist: " + path);
		}
	}
	
	public boolean isFileExist(String path){
		File f = new File(path);
		return f.exists();
	}
}
