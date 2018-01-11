package userInterface;

import interpreter.Interpreter;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;



/**
 * 语法高亮实现监听器
 * @author wsw
 * 
 * 真麻烦，好在建立在词法分析的基础上
 *
 */
public class SyntaxHighlighter implements DocumentListener {
	private Set<String> keywordsSet;
	private Set<String> typeSet;
	private Style keywordStyle;
	private Style normalStyle ;
	private Style typeStyle;
	private Style numStyle;
	private Style indentifyStyle;
	

	public SyntaxHighlighter(JTextPane editor){
		//准备着色使用的样式
		keywordStyle = ((StyledDocument)editor.getDocument()).addStyle("Keyword_Style", null);
		typeStyle = ((StyledDocument)editor.getDocument()).addStyle("Type_Style", null);
		normalStyle =  ((StyledDocument)editor.getDocument()).addStyle("Normal_Style", null);
		numStyle = ((StyledDocument)editor.getDocument()).addStyle("Num_Style", null);
		indentifyStyle = ((StyledDocument)editor.getDocument()).addStyle("Indentify_Style", null);
		
		
		StyleConstants.setForeground(keywordStyle, new Color(139,0,139));
		StyleConstants.setForeground(normalStyle, Color.BLACK);
		StyleConstants.setForeground(typeStyle, Color.BLUE);
		StyleConstants.setForeground(numStyle, new Color(139,90,43));
		StyleConstants.setForeground(indentifyStyle, new Color(139,115,85));
		
		//准备关键字
		keywordsSet = new HashSet<String>();
		typeSet = new HashSet<String>();
		
		typeSet.add("int");
		typeSet.add("real");
		typeSet.add("void");
		
		keywordsSet.add("if");
		keywordsSet.add("while");
		keywordsSet.add("for");
		keywordsSet.add("else");
		keywordsSet.add("read");
		keywordsSet.add("write");
		keywordsSet.add("return");
		
		
	}
	
	
	public void colouring(StyledDocument doc,int pos,int len)throws BadLocationException{
		/*
		 * 取得插入或者删除后影响到的单词。
		 * 例如"public"在b后插入一个空格, 就变成了:"pub lic", 这时就有两个单词要处理:"pub"和"lic"
		 * 这时要取得的范围是pub中p前面的位置和lic中c后面的位置
		 */
		int start = indexOfWordStart(doc,pos);
		int end =   indexOfWordEnd(doc,pos+len);
		
		char ch;
		while(start<end){
			ch = getCharAt(doc, start);
			if (Character.isLetter(ch) || ch == '_' || Character.isDigit(ch)) {
				/*
				 * 说明是单词
				 * 调用给单词着色的函数，返回值是单词结束的下标
				 */
				
				start = colouringWord(doc,start);
				
			}else{
				
				SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
				++start;
			}
			
		}
		
		
	}
	
	public int colouringWord(StyledDocument doc,int pos) throws BadLocationException{
		int wordEnd = indexOfWordEnd(doc, pos);
		String word = doc.getText(pos, wordEnd-pos);
		
		if(keywordsSet.contains(word)){
			SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd-pos, keywordStyle));
		}else if (typeSet.contains(word)) {
			SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd-pos, typeStyle));
		}else if (word.contains(".") || Interpreter.isInt(word)) {
			SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd-pos, numStyle));
		}else {
			SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd-pos, indentifyStyle));
		}
	
		return wordEnd;	
	}
	
	
	
	
	/**
	 * 取得下标为pos是，它所在的单词开始的下标
	 * @param doc
	 * @param pos
	 * @return
	 * @throws BadLocationException
	 */
	public int indexOfWordStart(Document doc,int pos)throws BadLocationException{
		
		
		for(;pos>0&&isWordCharacter(doc,pos-1);--pos);
		return pos;
		
	}
	
	/**
	 * 取得下标为pos时，它所在的单词结束的下标
	 * @param doc
	 * @param pos
	 * @return
	 * @throws BadLocationException
	 */
	public int indexOfWordEnd(Document doc,int pos) throws BadLocationException{
		//从pos开始向前找到第一个非单词字符
		for(;isWordCharacter(doc,pos);++pos);
		return pos;
		
	}
	
	
	/**
	 * 如果一个字符是字母、数字、下划线，则返回true
	 * @param doc
	 * @param pos
	 * @return
	 * @throws BadLocationException 
	 */
	public boolean isWordCharacter(Document doc,int pos) throws BadLocationException{
		char ch = getCharAt(doc,pos);
		if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
			return true;
		}
		return false;
		
	}
	
	
	/**
	 * 取得在文档中下标在pos出的字符
	 * 
	 * 
	 * @param doc
	 * @param pos
	 * @return
	 * @throws BadLocationException
	 */
	public char getCharAt(Document doc,int pos) throws BadLocationException{
		return doc.getText(pos, 1).charAt(0);
	}
	
	

	@Override
	public void insertUpdate(DocumentEvent e) {
		
		try {
			colouring((StyledDocument)e.getDocument(), e.getOffset(), e.getLength());
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			colouring((StyledDocument)e.getDocument(), e.getOffset(), 0);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}
	
	
	
	
	
	private class ColouringTask implements Runnable{
		private StyledDocument doc;
		private Style style;
		private int pos;
		private int len;
		
		public ColouringTask(StyledDocument doc,int pos,int len,Style style){
			this.doc = doc;
			this.pos = pos;
			this.len = len;
			this.style = style;
					
		}
		
		public void run(){
			
			doc.setCharacterAttributes(pos, len, style, true);
			
		}	
	}
	
	
}
