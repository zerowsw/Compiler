package parser;

public class SyntaxError extends Exception{
	
	private static final long serialVersionUID = 2572063543161290123L;

	public SyntaxError(String message){
		super(message);
	}
}
