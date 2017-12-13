package consoleCommandManager;

public class ConsoleOutput {
	private String output;
	private boolean isError;
	private int exitCode;
	
	ConsoleOutput() {
	}

	public String getOutput() {
		return output;
	}

	public boolean isError() {
		return isError;
	}

	public int getExitCode() {
		return exitCode;
	}

	void setOutput(String output) {
		this.output = output;
	}

	void setError(boolean isError) {
		this.isError = isError;
	}

	void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	
	

}
