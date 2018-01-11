package lexer;


import java.util.ArrayList;



//词法分析器
public class Lexer {
	// 记录行号
	private int linenumber = 1;

	// 当前正在处理的字符
	private char ch = ' ';
	
	//所要处理的文本
	private String text;
	
	

	public void setText(String text) {
		this.text = text;
	}

	//正在读取的字符的位置
	private int position;
	
	
	/*
	 * 判断源码输入是否结束的标志
	 * 当输入‘#’时，表示输入结束，并输出分析结果
	 */
	//private Boolean endflag = false;
	

	/*
	 * 还是决定用键盘输入了，打开文件看的话不太方便
	 */


	/*
	 * 为了判断字符串是保留字还是标识符，建立一个表记录保留字的词法单元，要判断字符串时 先到该表中查询，如果没有的话就是标识符
	 * 然后又因为是根据字符串查找的，所以要把字符串作为键
	 */
	java.util.Hashtable<String, Token> words = new java.util.Hashtable<String, Token>();


	 /*
	 * 存储词法分析结果的token序列，因为语法分析中不需要知道词素是多少，所以这里存储token就可以了
	 */
	 private ArrayList<Token> tokens = new ArrayList<Token>();

	/*
	 * 构造函数需要做的事 1.确定源码输入流对象的绑定 2.在存储保留字的表中，将现有的保留字以及词素存入其中
	 */
	public Lexer(String text) {

		this.text = text;
		
		words.put("else", new Token(Tag.ELSE, "else",0));
		words.put("if", new Token(Tag.IF, "if",0));
		words.put("int", new Token(Tag.INT, "int",0));
		words.put("read", new Token(Tag.READ, "read",0));
		words.put("real", new Token(Tag.REAL, "real",0));
		words.put("while", new Token(Tag.WHILE, "while",0));
		words.put("write", new Token(Tag.WRITE, "write",0));
		words.put("for",new Token(Tag.FOR, "for", 0));
		words.put("void", new Token(Tag.VOID, "void", 0));
		words.put("return", new Token(Tag.RETURN, "return", 0));
		
	}

	
//	//获取代码输入是否结束的标志
//	Boolean getEndFlag(){
//		return endflag;
//	}
	//获取分析后的tokens结果
	public ArrayList<Token> getTokens(){
		return tokens;
	}
	
	// 读取单个字符的函数
	public void moveOn()  {
		if(position<text.length()-1){
		// 读取一个字符
		ch =text.charAt(++position);
		}else {
			position++;
			ch = '#';
		}
		
	}

	public Boolean moveOn(int s)  {
		moveOn();
		if (ch != s)
			return false;
		return true;
	}

	/*
	 * 然后是词法分析器的核心部分，对输入的源码即一系列字符串进行分析，返回语法分析需要的一系列token
	 * 有这样的一个注意点：就是空字符是词法单元之间的一种有效分割，需要利用起来。
	 * 原本打算先将所有的空字符，制表符，换行符全部过滤掉之后再处理的，但稍微一想就知道这是不行的，很明
	 * 显的会让一些词法单元混淆。所以说空字符的过滤应该每分析一次过滤一个。于是我又想能不能利用空字符直接
	 * 将所有的词法单元分割呢？这对大部分情况是适用的，但对一些情况是不行的，除了空字符的分割，还有一些词法
	 * 意义上的分割，这个需要在判断的过程中决定。（词法分析实际上是不存在什么错误的，一般都能分析得到结果，至于
	 * 这个结果是不是符合语法分析就不是这里需要过问的事了）
	 */
	public  void scan() {
		
		//System.out.println("进入词法分析主体");
		
		//对position进行初始化
		position = -1;
		
		int length = text.length();
		System.out.println("文本长度为："+length);
		 
		while(position< (length-1)){
			
			System.out.println("position: "+position);
			System.out.println("我倒要看看是啥"+ch);
		// 首先是获取第一个非空的字符
		moveOn();
			System.out.println("然后是啥捏"+ch);
		
		// System.out.println("每次开始的ch: "+ch);
		while (true) {
			if (ch == ' ' || ch == '\t' )
				moveOn();
			else if (ch == '\n' ) {
				linenumber++;
				moveOn();
			} else {
				break;
			}
		}
		
		//System.out.println("现在的ch"+ch);
		/*
		 * 对注释的处理
		 */
		if(ch == '/'){
			if (moveOn('/')) {
				while(ch != '\n'  && ch != '\r'){
					moveOn();
				}		
			}
			else if(ch == '*'){  //这个错误让我找了一会儿，我在moveOn(int x)函数中的一条路径没有对ch清空
				/*
				 * 这里我还没有对注释的错误处理
				 */
				while(ch != '/'){
					if(ch=='\n'){
						linenumber++;
					}
					moveOn();
				}	
				moveOn();
			}else{
				 tokens.add(new Token(Tag.DIVIDE,"/",linenumber));
				 position--;
				 ch = ' ';
			}
		}	
		
		/*
		 * 下面开始对保留字等进行判断
		 */
		// 首先对数字进行判断
		if (Character.isDigit(ch)) {
			int value = 0; // 用来计算该数的值
			do {
				value = value * 10 + Character.digit(ch, 10);
				moveOn();
			} while (Character.isDigit(ch));
			// 如果该数值是一个整数则返回当前值即可
			if (ch != '.'){
				tokens.add(new Token(Tag.NUM,value+"",linenumber));
				position--;
				ch = ' ';
			}else{
			// 否则需要对浮点数，也是real类型的数进行计算
			float f = value;
			float l = 10;
			while (true) {
				moveOn();
				if (!Character.isDigit(ch))
					break;
				f += Character.digit(ch, 10) / l;
				l = l * 10;
			}
			tokens.add(new Token(Tag.REAL,f+"",linenumber));
			position--;
			ch = ' ';
			}
		}

		/*
		 * 对一些符合符号的识别
		 * 包括<= ，>=,==,<>
		 */
		switch (ch) {
		case '=':
			if (moveOn('=')){
				tokens.add(new Token(Tag.EQUAL, "==",linenumber));
				ch = ' ';
			}
			else{
				tokens.add(new Token(Tag.ASSIGN, "=",linenumber));
				position--;
				ch = ' ';
			}
				
			break;
		case '<':
			if (moveOn('>')){
				tokens.add(new Token(Tag.NOREQUAL, "<>",linenumber));
				ch = ' ';
			}
			else if(ch == '='){
				tokens.add(new Token(Tag.NOMORETHAN, "<=",linenumber));
				ch = ' ';
			}else{
				tokens.add(new Token(Tag.FEWER, "<",linenumber));
				position--;
				ch = ' ';
			}
			break;
		default:
			break;
		}

		
		
		
		// 对标识符的判断
		if (Character.isLetter(ch)) {
			
			System.out.println("开始标识符判断");
			StringBuilder builder = new StringBuilder();
			do {
				builder.append(ch);
				moveOn();
			} while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
			System.out.println("跳出循环");
			String s = builder.toString();
			Token word = words.get(s);  //在保留字容器中查找是否有这样的一个保留字
			if (word != null){  //如果找到了这样的保留字，将其添加到结果集中即可
				tokens.add(word);
				position--;
				ch = ' ';
			}
			// 判断标识符是否以下划线结尾，如果是的话，报错。
			else if (s.endsWith("_")) {
				tokens.add(new Token(Tag.ERROR1, s,linenumber));
				position--;
				ch = ' ';
			} else {
				word = new Token(Tag.IDENTIFY, s,linenumber);
				tokens.add(word);
				position--;
				ch = ' ';
			}
		}

		//System.out.println("标识符判断结束");
		// 剩下的没有做详细区分，统一归为词法单元
		Token w = null;
		switch (ch) {
		case '+':
			w = new Token(Tag.ADD, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '-':
			w = new Token(Tag.MINUS, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '*':
			w = new Token(Tag.MUTIPLY, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case ';':
			w = new Token(Tag.SEMICOLON, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '(':
			w = new Token(Tag.LEFTPARENTHESES, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case ')':
			w = new Token(Tag.RIGHTPARENTHESES, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '>':
			w = new Token(Tag.GREATER, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '[':
			w = new Token(Tag.LEFTBRACE, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case ']':
			w = new Token(Tag.RIGHTBRACE, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '{':
			w = new Token(Tag.LEFTBRACKET, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '}':
			w = new Token(Tag.RIGHTBRACKET, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case ',':
			w = new Token(Tag.COMMA, ch + "",linenumber);
			ch = ' ';
			tokens.add(w);
			break;
		case '\r':
//			ch = ' ';
//			w = new Token(Tag.ERROR2, "yes",linenumber);
//			tokens.add(w);
			break;
		case '\n':
			break;
		case '#':                 //结束标志的判断
//			endflag = true;
			return;
		case ' ':
			break;
		default:
			w = new Token(Tag.ERROR2, ch + "",linenumber);   //我这个词法分析中定义了两种错误，这里是错误二，即异常字符。
			ch = ' ';
			tokens.add(w);
		}
		
		//System.out.println("第一次循环结束");

	}
	}	
	

}
