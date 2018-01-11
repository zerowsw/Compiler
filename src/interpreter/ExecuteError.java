package interpreter;

public class ExecuteError extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8152307812321535929L;

	public ExecuteError(String errorMessage){
		super(errorMessage);
	}
}

