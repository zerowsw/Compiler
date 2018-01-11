package lexer;


import java.util.ArrayList;



//�ʷ�������
public class Lexer {
	// ��¼�к�
	private int linenumber = 1;

	// ��ǰ���ڴ�����ַ�
	private char ch = ' ';
	
	//��Ҫ������ı�
	private String text;
	
	

	public void setText(String text) {
		this.text = text;
	}

	//���ڶ�ȡ���ַ���λ��
	private int position;
	
	
	/*
	 * �ж�Դ�������Ƿ�����ı�־
	 * �����롮#��ʱ����ʾ���������������������
	 */
	//private Boolean endflag = false;
	

	/*
	 * ���Ǿ����ü��������ˣ����ļ����Ļ���̫����
	 */


	/*
	 * Ϊ���ж��ַ����Ǳ����ֻ��Ǳ�ʶ��������һ�����¼�����ֵĴʷ���Ԫ��Ҫ�ж��ַ���ʱ �ȵ��ñ��в�ѯ�����û�еĻ����Ǳ�ʶ��
	 * Ȼ������Ϊ�Ǹ����ַ������ҵģ�����Ҫ���ַ�����Ϊ��
	 */
	java.util.Hashtable<String, Token> words = new java.util.Hashtable<String, Token>();


	 /*
	 * �洢�ʷ����������token���У���Ϊ�﷨�����в���Ҫ֪�������Ƕ��٣���������洢token�Ϳ�����
	 */
	 private ArrayList<Token> tokens = new ArrayList<Token>();

	/*
	 * ���캯����Ҫ������ 1.ȷ��Դ������������İ� 2.�ڴ洢�����ֵı��У������еı������Լ����ش�������
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

	
//	//��ȡ���������Ƿ�����ı�־
//	Boolean getEndFlag(){
//		return endflag;
//	}
	//��ȡ�������tokens���
	public ArrayList<Token> getTokens(){
		return tokens;
	}
	
	// ��ȡ�����ַ��ĺ���
	public void moveOn()  {
		if(position<text.length()-1){
		// ��ȡһ���ַ�
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
	 * Ȼ���Ǵʷ��������ĺ��Ĳ��֣��������Դ�뼴һϵ���ַ������з����������﷨������Ҫ��һϵ��token
	 * ��������һ��ע��㣺���ǿ��ַ��Ǵʷ���Ԫ֮���һ����Ч�ָ��Ҫ����������
	 * ԭ�������Ƚ����еĿ��ַ����Ʊ�������з�ȫ�����˵�֮���ٴ���ģ�����΢һ���֪�����ǲ��еģ�����
	 * �ԵĻ���һЩ�ʷ���Ԫ����������˵���ַ��Ĺ���Ӧ��ÿ����һ�ι���һ���������������ܲ������ÿ��ַ�ֱ��
	 * �����еĴʷ���Ԫ�ָ��أ���Դ󲿷���������õģ�����һЩ����ǲ��еģ����˿��ַ��ķָ����һЩ�ʷ�
	 * �����ϵķָ�����Ҫ���жϵĹ����о��������ʷ�����ʵ�����ǲ�����ʲô����ģ�һ�㶼�ܷ����õ����������
	 * �������ǲ��Ƿ����﷨�����Ͳ���������Ҫ���ʵ����ˣ�
	 */
	public  void scan() {
		
		//System.out.println("����ʷ���������");
		
		//��position���г�ʼ��
		position = -1;
		
		int length = text.length();
		System.out.println("�ı�����Ϊ��"+length);
		 
		while(position< (length-1)){
			
			System.out.println("position: "+position);
			System.out.println("�ҵ�Ҫ������ɶ"+ch);
		// �����ǻ�ȡ��һ���ǿյ��ַ�
		moveOn();
			System.out.println("Ȼ����ɶ��"+ch);
		
		// System.out.println("ÿ�ο�ʼ��ch: "+ch);
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
		
		//System.out.println("���ڵ�ch"+ch);
		/*
		 * ��ע�͵Ĵ���
		 */
		if(ch == '/'){
			if (moveOn('/')) {
				while(ch != '\n'  && ch != '\r'){
					moveOn();
				}		
			}
			else if(ch == '*'){  //���������������һ���������moveOn(int x)�����е�һ��·��û�ж�ch���
				/*
				 * �����һ�û�ж�ע�͵Ĵ�����
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
		 * ���濪ʼ�Ա����ֵȽ����ж�
		 */
		// ���ȶ����ֽ����ж�
		if (Character.isDigit(ch)) {
			int value = 0; // �������������ֵ
			do {
				value = value * 10 + Character.digit(ch, 10);
				moveOn();
			} while (Character.isDigit(ch));
			// �������ֵ��һ�������򷵻ص�ǰֵ����
			if (ch != '.'){
				tokens.add(new Token(Tag.NUM,value+"",linenumber));
				position--;
				ch = ' ';
			}else{
			// ������Ҫ�Ը�������Ҳ��real���͵������м���
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
		 * ��һЩ���Ϸ��ŵ�ʶ��
		 * ����<= ��>=,==,<>
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

		
		
		
		// �Ա�ʶ�����ж�
		if (Character.isLetter(ch)) {
			
			System.out.println("��ʼ��ʶ���ж�");
			StringBuilder builder = new StringBuilder();
			do {
				builder.append(ch);
				moveOn();
			} while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
			System.out.println("����ѭ��");
			String s = builder.toString();
			Token word = words.get(s);  //�ڱ����������в����Ƿ���������һ��������
			if (word != null){  //����ҵ��������ı����֣�������ӵ�������м���
				tokens.add(word);
				position--;
				ch = ' ';
			}
			// �жϱ�ʶ���Ƿ����»��߽�β������ǵĻ�������
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

		//System.out.println("��ʶ���жϽ���");
		// ʣ�µ�û������ϸ���֣�ͳһ��Ϊ�ʷ���Ԫ
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
		case '#':                 //������־���ж�
//			endflag = true;
			return;
		case ' ':
			break;
		default:
			w = new Token(Tag.ERROR2, ch + "",linenumber);   //������ʷ������ж��������ִ��������Ǵ���������쳣�ַ���
			ch = ' ';
			tokens.add(w);
		}
		
		//System.out.println("��һ��ѭ������");

	}
	}	
	

}
