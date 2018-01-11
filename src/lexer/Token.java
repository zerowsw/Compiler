package lexer;

/*
 * 关键字类,包含tag属性，便于语法分析
 */
public class Token {
	public final int tag;
	private String value;
	private final int linenumber;

	public String getValue() {
		return value;
	}

	public int getLinenumber() {
		return linenumber;
	}

	public Token(int tag, String value, int linenumber) {
		this.tag = tag;
		this.value = value;
		this.linenumber = linenumber;
	}

	public String toString() {
		switch (tag) {
		case Tag.ELSE:
			return "ELSE: " + value;
		case Tag.ASSIGN:
			return "ASSIGN: " + value;
		case Tag.EQUAL:
			return "EQUAL: " + value;
		case Tag.IDENTIFY:
			return "IDENTIFY: " + value;
		case Tag.FEWER:
			return "FEWER: " + value;
		case Tag.GREATER:
			return "GREATER: " + value;
		case Tag.NOMORETHAN:
			return "NOMORETHAN: " + value;
		case Tag.NOFEWERTHAN:
			return "NOFEWERTHAN:" + value;
		case Tag.IF:
			return "IF: " + value;
		case Tag.INT:
			return "INT: " + value;
		case Tag.NOREQUAL:
			return "NOREQUAL: " + value;
		case Tag.READ:
			return "READ: " + value;
		case Tag.WHILE:
			return "WHILE: " + value;
		case Tag.WRITE:
			return "WRITE: " + value;
		case Tag.LEFTBRACE:
			return "LEFTBRACE: " + value;
		case Tag.RIGHTBRACE:
			return "RIGHTBRACE: " + value;
		case Tag.LEFTBRACKET:
			return "LEFTBRACKET: " + value;
		case Tag.RIGHTBRACKET:
			return "RIGHTBRACKET: " + value;
		case Tag.LEFTPARENTHESES:
			return "LEFTPARENTHESES: " + value;
		case Tag.RIGHTPARENTHESES:
			return "RIGHTPARENTHESES: " + value;
		case Tag.SEMICOLON:
			return "SEMICOLON: " + value;
		case Tag.COMMA:
			return "COMMA: " + value;
		case Tag.ADD:
			return "ADD: " + value;
		case Tag.MINUS:
			return "MINUS: " + value;
		case Tag.MUTIPLY:
			return "MUTIPLY: " + value;
		case Tag.DIVIDE:
			return "DIVIDE: " + value;
		case Tag.REAL:
			return "REAL: " + value;
		case Tag.NUM:
			return "INTEGER: " + value;
		case Tag.FOR:
			return "FOR : " + value;
		case Tag.VOID:
			return "VOID: " + value;
		case Tag.RETURN:
			return "RETURN: " + value;
		case Tag.WRAP:
			return "-------------------------------";
		case Tag.ERROR1:
			return "Error! The Identify \"" + value + "\" ends with '_'";
		case Tag.ERROR2:
			return "Error!Undefined word : " + value;
		default:
			return "TAG: " + value;
		}
	}

}
