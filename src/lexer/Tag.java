package lexer;



/*
 * 记录关键字相应的值，方便语法分析时类型判断
 */
public class Tag {
	public final static int 
		IF = 1,
		ELSE = 2,
		WHILE = 3,
		READ = 4,
		WRITE = 5,
		INT = 6,
		REAL = 7,
		NUM = 8,
		IDENTIFY = 9,
		EQUAL = 10,
		NOREQUAL = 11,
		ASSIGN = 12,
		FEWER = 13,
		SEMICOLON = 14,
		LEFTBRACE = 15,
		RIGHTBRACE = 16,
		LEFTPARENTHESES =17,
		RIGHTPARENTHESES = 18,
		LEFTBRACKET =19,
		RIGHTBRACKET = 20,
		COMMA = 21,
		WRAP = 22,
		ERROR1 = 23,
		ERROR2 = 24,
		GREATER = 25,
		ADD = 26,
		MINUS = 27,
		MUTIPLY = 28,
		DIVIDE = 29,
		NOMORETHAN = 30,
		NOFEWERTHAN = 31,
		FOR = 32,
		VOID = 33,
		RETURN = 34;

}
