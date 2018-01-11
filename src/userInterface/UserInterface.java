package userInterface;

import interpreter.ExecuteError;
import interpreter.Interpreter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;

import javax.swing.tree.DefaultTreeModel;

import com.alee.laf.WebLookAndFeel;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;

import lexer.*;
import parser.SyntaxAnalyzer;
import parser.SyntaxError;
import parser.TreeNode;
import semantic_analyzer.FourElementFormula;
import semantic_analyzer.SemanticAnalyzer;
import semantic_analyzer.SemanticError;




/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class UserInterface extends javax.swing.JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar jMenuBar1;
	private JMenu jMenu1;
	private JMenu jMenu3;
	private JTabbedPane jTabbedPane3;
	private JScrollPane jScrollPane4;
	private JTabbedPane jTabbedPane2;
	private JScrollPane semanticScrollPane;
	private JScrollPane syntaxScrollPane;
	private JScrollPane lexerScrollPane;
	private JTabbedPane jTabbedPane1;
	private JMenuItem executeMenuItem;
	private JMenuItem intermediateMenuItem;
	private JTextArea intermediateTextArea;
	public JTextArea consoleTextArea;
	private JTextArea lexerTextArea;
	private JTextPane editTextPane;
	private JMenuItem syntaxMenuItem;
	private JMenuItem lexerMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem createMenuItem;
	private JSplitPane jSplitPane3;
	private JSplitPane jSplitPane2;
	private JSplitPane jSplitPane1;
	private JToolBar jToolBar1;
	private JScrollPane jScrollPane6;
	private JTabbedPane jTabbedPane4;
	private JScrollPane fileIndexScrollPane;
	private JMenu jMenu4;
	private JMenu jMenu2;
	
//	private final static FileTree FILETREE = new FileTree(
//			new FileTree.ExtensionFilter("lnk"));
	private FileTree filetree;
	
	
	private Interpreter interpreter;

//	//标志变量，标志第一次输入内容
//	private boolean flag = true;
//	/* 控制台列数 */
//	private static int columnNum;
//	/* 控制台行数 */
//	private static int rowNum;
//	/* 控制台最大行数 */
//	private static int presentMaxRow;
//	private static int[] index = new int[] { 0, 0 };
//	private static StyledDocument doc = null;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				WebLookAndFeel.globalControlFont  = new FontUIResource("微软雅黑",0, 12); 
				WebLookAndFeel.globalTooltipFont = new FontUIResource("微软雅黑",0, 12);
				WebLookAndFeel.globalAlertFont  = new FontUIResource("微软雅黑",0, 12);
				WebLookAndFeel.globalMenuFont  = new FontUIResource("微软雅黑",0, 12);
				WebLookAndFeel.globalAcceleratorFont = new FontUIResource("微软雅黑",0, 12);
				WebLookAndFeel.globalTitleFont  = new FontUIResource("微软雅黑",0, 17);
				WebLookAndFeel.globalTextFont   = new FontUIResource("微软雅黑",0, 13);     
			
			
				WebLookAndFeel.textPaneFont = new Font("Courier New", 0, 12);
				WebLookAndFeel.install();
				
				//WebLookAndFeel.tabbedPaneFont = new Font("微软雅黑", 0, 17);
				//SwingUtilities.updateComponentTreeUI(FILETREE);
				
				
				UserInterface inst = new UserInterface();
				
				inst.setTitle("CMM解释器");
				inst.setExtendedState(JFrame.MAXIMIZED_BOTH);
				int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
				int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
				inst.setSize(width, height);
				
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public UserInterface() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			
			
			{
				jToolBar1 = new JToolBar();
				getContentPane().add(jToolBar1, BorderLayout.NORTH);
				jToolBar1.setPreferredSize(new java.awt.Dimension(722, 33));
				jToolBar1.setFont(new java.awt.Font("Microsoft YaHei",0,24));
			}
			{
				jSplitPane1 = new JSplitPane();
				getContentPane().add(jSplitPane1, BorderLayout.CENTER);
				jSplitPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				jSplitPane1.setDividerSize(3);
				jSplitPane1.setDividerLocation(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*5/6);
				{
					jSplitPane2 = new JSplitPane();
					jSplitPane1.add(jSplitPane2, JSplitPane.LEFT);
					jSplitPane2.setPreferredSize(new java.awt.Dimension(546, 351));
					jSplitPane2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					jSplitPane2.setDividerSize(3);
					jSplitPane2.setDividerLocation(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/6);
					{
						jSplitPane3 = new JSplitPane();
						jSplitPane2.add(jSplitPane3, JSplitPane.RIGHT);
						jSplitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
						jSplitPane3.setAutoscrolls(true);
						jSplitPane3.setDividerSize(3);
						jSplitPane3.setPreferredSize(new java.awt.Dimension(469, 331));
						{
							jTabbedPane2 = new JTabbedPane();
							jSplitPane3.add(jTabbedPane2, JSplitPane.TOP);
							{
								
								editTextPane = new JTextPane();
								editTextPane.getDocument().addDocumentListener(new SyntaxHighlighter(editTextPane));
								
								//editTextPane.setFont(new java.awt.Font("Courier New",0,12));
						
								
								TextLineNumber textLineNumber = new TextLineNumber(editTextPane);
								
								jScrollPane4 = new JScrollPane(editTextPane);
								
								jScrollPane4.setRowHeaderView(textLineNumber);
								
								jTabbedPane2.addTab("编辑区", null, jScrollPane4, null);
								jScrollPane4.setFont(new java.awt.Font("Tahoma",0,16));
								{
									
									
//									editor.addMouseListener(new DefaultMouseAdapter());
//									editor.getDocument().addUndoableEditListener(undoHandler);
									
									
									//jScrollPane4.setViewportView(editTextPane);
								}
							}
						}
						{
							jTabbedPane4 = new JTabbedPane();
							jSplitPane3.add(jTabbedPane4, JSplitPane.BOTTOM);
							{
								jScrollPane6 = new JScrollPane();
								jTabbedPane4.addTab("控制台", null, jScrollPane6, null);
								{
									consoleTextArea = new JTextArea();
									jScrollPane6.setViewportView(consoleTextArea);
						//			consoleTextArea.setPreferredSize(new java.awt.Dimension(460, 282));
									
									//doc = consoleTextArea.getStyledDocument();
									
									consoleTextArea.addKeyListener(new KeyAdapter() {
							
										public void keyReleased(KeyEvent evt) {
											consoleTextAreaKeyReleased(evt);
										}
										
//										public void keyPressed(KeyEvent evt){
//											consoleTextAreaKeyPressed(evt);
//										}
										
									});
								}
							}
						}
						jSplitPane3.setDividerLocation(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*3/5);
					}
					{
						jTabbedPane3 = new JTabbedPane();
						jSplitPane2.add(jTabbedPane3, JSplitPane.LEFT);
						{
							fileIndexScrollPane = new JScrollPane();
							
			/*******************************************************************************************************/	
							filetree =new FileTree(new FileTree.ExtensionFilter("lnk"));
							
							
							filetree.addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent e) {
									if (e.getClickCount() == 2) {
										String str = "", fileName = "";
										StringBuilder text = new StringBuilder();
										File file = filetree.getSelectFile();
										fileName = file.getName();
										if (file.isFile()) {
											if (fileName.endsWith(".txt")
													|| fileName.endsWith("CMM")
													|| fileName.endsWith("cmm")
													|| fileName.endsWith(".TXT")
													|| fileName.endsWith(".java")) {
												try {
													FileReader file_reader = new FileReader(file);
													BufferedReader in = new BufferedReader(
															file_reader);
													while ((str = in.readLine()) != null)
														text.append(str + '\n');
													in.close();
													file_reader.close();
												} catch (IOException e2) {
												}
												
												editTextPane.setText(text.toString());
											}
										}
										setSize(getWidth(), getHeight());
									}
								}
							});
							
						
							fileIndexScrollPane.setViewportView(filetree);
							
			/***************************************************************************************************************/
	
							
							jTabbedPane3.addTab("文件目录", null, fileIndexScrollPane, null);
						}
					}
				}
				{
					jTabbedPane1 = new JTabbedPane();
					jSplitPane1.add(jTabbedPane1, JSplitPane.RIGHT);
					{
						lexerScrollPane = new JScrollPane();
						jTabbedPane1.addTab("词法分析", null, lexerScrollPane, null);
						{
							lexerTextArea = new JTextArea();
							lexerScrollPane.setViewportView(lexerTextArea);
							lexerTextArea.setEditable(false);
						}
					}
					{
						syntaxScrollPane = new JScrollPane();
						jTabbedPane1.addTab("语法树", null, syntaxScrollPane, null);
					}
					{
						semanticScrollPane = new JScrollPane();
						jTabbedPane1.addTab("中间代码", null, semanticScrollPane, null);
						{
							intermediateTextArea = new JTextArea();
							
							intermediateTextArea.setTabSize(9);
							
							semanticScrollPane.setViewportView(intermediateTextArea);
							intermediateTextArea.setEditable(false);
						}
					}
				}
			}
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("\u6587\u4ef6(F)");
					{
						createMenuItem = new JMenuItem();
						jMenu1.add(createMenuItem);
						createMenuItem.setText("\u65b0\u5efa");
					}
					{
						openMenuItem = new JMenuItem();
						jMenu1.add(openMenuItem);
						openMenuItem.setText("\u6253\u5f00");
						openMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									openMenuItemActionPerformed(evt);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});

					}
					{
						saveMenuItem = new JMenuItem();
						jMenu1.add(saveMenuItem);
						saveMenuItem.setText("\u4fdd\u5b58");
					}
				}
				{
					jMenu2 = new JMenu();
					jMenuBar1.add(jMenu2);
					jMenu2.setText("\u7f16\u8f91(E)");
				}
				{
					jMenu3 = new JMenu();
					jMenuBar1.add(jMenu3);
					jMenu3.setText("\u8c03\u8bd5(D)");
				}
				{
					jMenu4 = new JMenu();
					jMenuBar1.add(jMenu4);
					jMenu4.setText("\u8fd0\u884c(R)");
					{
						lexerMenuItem = new JMenuItem();
						jMenu4.add(lexerMenuItem);
						lexerMenuItem.setText("\u8bcd\u6cd5\u5206\u6790");
						lexerMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								lexerMenuItemActionPerformed(evt);
							}
						});
					}
					{
						syntaxMenuItem = new JMenuItem();
						jMenu4.add(syntaxMenuItem);
						syntaxMenuItem.setText("\u8bed\u6cd5\u5206\u6790");
						syntaxMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									syntaxMenuItemActionPerformed(evt);
								} catch (SyntaxError e) {
									// TODO Auto-generated catch block
									consoleTextArea.setText("syntaxError: "+e.getMessage());
								}
							}
						});
					}
					{
						intermediateMenuItem = new JMenuItem();
						jMenu4.add(intermediateMenuItem);
						intermediateMenuItem.setText("\u4e2d\u95f4\u4ee3\u7801");
						intermediateMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									intermediateMenuItemActionPerformed(evt);
								} catch (SyntaxError e) {
									// TODO Auto-generated catch block
									consoleTextArea.setText("SyntaxError: "+e.getMessage());
								}catch(SemanticError e){
									consoleTextArea.append("SemanticError: "+e.getMessage());
								}
							}
						});
					}
					{
						executeMenuItem = new JMenuItem();
						jMenu4.add(executeMenuItem);
						executeMenuItem.setText("\u6267\u884c");
						executeMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									executeMenuItemActionPerformed(evt);
								} catch (  SyntaxError e) {
									// TODO Auto-generated catch block
									consoleTextArea.setText("SyntaxError: "+e.getMessage());	
								}catch (SemanticError e) {
									// TODO: handle exception
									consoleTextArea.append("SemanticError: "+e.getMessage());
								}catch (ExecuteError e) {
									// TODO: handle exception
									consoleTextArea.append("ExecuteError: "+e.getMessage());
								}catch (NumberFormatException e) {
									// TODO: handle exception
									consoleTextArea.append("FormatException: "+e.getMessage());
								}
								
								//consoleTextArea.append("执行结束");
							}
						});
					}
				}
			}
			pack();
			this.setSize(738, 453);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	//打开按钮的响应函数
	private void openMenuItemActionPerformed(ActionEvent evt) throws IOException {
		System.out.println("openMenuItem.actionPerformed, event="+evt);
		//TODO add your code for openMenuItem.actionPerforme
		JFileChooser fd = new JFileChooser(new File("./src/input"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
		fd.setFileFilter(filter);
		fd.changeToParentDirectory();
		
		fd.showOpenDialog(null);
			
		File file = fd.getSelectedFile();
		
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		String line = null;
		editTextPane.setText("");
		String content = "";
		while ((line = buffer.readLine()) != null) {
			content +=(line + System.getProperty("line.separator"));
		}
		
		editTextPane.setText(content);
	
		buffer.close();
	}
	
	private void lexerMenuItemActionPerformed(ActionEvent evt) {
		System.out.println("lexerMenuItem.actionPerformed, event="+evt);
		//TODO add your code for lexerMenuItem.actionPerformed
		String text = editTextPane.getText();
		Lexer lexer = new Lexer(text);
		lexer.scan();
		
		consoleTextArea.setText("词法分析完成！"+System.getProperty("line.separator"));
		
		String ouput = "";
		ArrayList<Token> tokens = lexer.getTokens();
		for (Token token : tokens) {
			ouput+=token.toString()+"\n";
		}
		
		lexerTextArea.setText(ouput);
	
	}
	
	private void syntaxMenuItemActionPerformed(ActionEvent evt) throws SyntaxError {
		System.out.println("syntaxMenuItem.actionPerformed, event="+evt);
		//TODO add your code for syntaxMenuItem.actionPerformed
		String text = editTextPane.getText();
		Lexer lexer = new Lexer(text);
		lexer.scan();
		
		consoleTextArea.setText("词法分析完成！"+System.getProperty("line.separator"));
		
		String ouput = "";
		
		ArrayList<Token> tokens = lexer.getTokens();
		for (Token token : tokens) {
			ouput+="  "+token.toString()+"\n";
		}
		
		lexerTextArea.setText(ouput);
		
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
		TreeNode node = syntaxAnalyzer.start();
		
		consoleTextArea.append("语法分析成功！");
		
		DefaultTreeModel model = new DefaultTreeModel(node);
		JTree parserTree = new JTree(model);
		
		parserTree.setShowsRootHandles(true);
		// 设置节点是否可见,默认是true
		parserTree.setRootVisible(true);
		
		syntaxScrollPane.setViewportView(parserTree);
		
	}
	
	private void intermediateMenuItemActionPerformed(ActionEvent evt) throws SyntaxError, SemanticError {
		System.out.println("intermediateMenuItem.actionPerformed, event="+evt);
		//TODO add your code for intermediateMenuItem.actionPerformed
		
		System.out.println("syntaxMenuItem.actionPerformed, event="+evt);
		//TODO add your code for syntaxMenuItem.actionPerformed
		String text = editTextPane.getText();
		Lexer lexer = new Lexer(text);
		lexer.scan();
		
		consoleTextArea.setText("词法分析完成！"+System.getProperty("line.separator"));
		
		String ouput = "";
		
		ArrayList<Token> tokens = lexer.getTokens();
		for (Token token : tokens) {
			ouput+="  "+token.toString()+"\n";
		}
		
		lexerTextArea.setText(ouput);
		
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
		TreeNode node = syntaxAnalyzer.start();
		
		consoleTextArea.append("语法分析成功！"+System.getProperty("line.separator"));
		
		DefaultTreeModel model = new DefaultTreeModel(node);
		JTree parserTree = new JTree(model);
		
		parserTree.setShowsRootHandles(true);
		// 设置节点是否可见,默认是true
		parserTree.setRootVisible(true);
		
		syntaxScrollPane.setViewportView(parserTree);
		
		
		
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(node,consoleTextArea);
		LinkedList<FourElementFormula> intermediateCode = semanticAnalyzer.generateCode();
		
		
		
		consoleTextArea.append("语义分析成功！\n");
		
		String output = "";
		int index = 1;
		for(FourElementFormula formula : intermediateCode){
			output += index+".     "+formula+System.getProperty("line.separator");
			index++;
		}
		
		//semanticScrollPane.setViewport(null);
		intermediateTextArea.setText(output);
		
		
	}
	
	private void executeMenuItemActionPerformed(ActionEvent evt) throws SyntaxError, SemanticError, NumberFormatException, ExecuteError {
		System.out.println("executeMenuItem.actionPerformed, event="+evt);
		//TODO add your code for executeMenuItem.actionPerformed
		
		String text = editTextPane.getText();
		Lexer lexer = new Lexer(text);
		lexer.scan();
		
		consoleTextArea.setText("词法分析完成！"+System.getProperty("line.separator"));
		
		String ouput = "";
		
		ArrayList<Token> tokens = lexer.getTokens();
		for (Token token : tokens) {
			ouput+="  "+token.toString()+"\n";
		}
		
		lexerTextArea.setText(ouput);
		
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
		TreeNode node = syntaxAnalyzer.start();
		
		consoleTextArea.append("语法分析成功！"+System.getProperty("line.separator"));
		
		DefaultTreeModel model = new DefaultTreeModel(node);
		JTree parserTree = new JTree(model);
		
		parserTree.setShowsRootHandles(true);
		// 设置节点是否可见,默认是true
		parserTree.setRootVisible(true);
		
		syntaxScrollPane.setViewportView(parserTree);
		
		
		
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(node,consoleTextArea);
		LinkedList<FourElementFormula> intermediateCode = semanticAnalyzer.generateCode();
		
		
		
		consoleTextArea.append("语义分析成功！\n");
		
		String output = "";
		int index = 1;
		for(FourElementFormula formula : intermediateCode){
			output += index+".     "+formula+System.getProperty("line.separator");
			index++;
		}
		
		//semanticScrollPane.setViewport(null);
		intermediateTextArea.setText(output);
		
		
		consoleTextArea.append("开始解释执行！\n");
		
		interpreter = new Interpreter(intermediateCode, consoleTextArea,semanticAnalyzer.getDeclare_Number());
		new Thread(interpreter).start();
		
	
		
		
	}
	
	private void consoleTextAreaKeyReleased(KeyEvent e) {
		System.out.println("consoleTextArea.keyReleased, event="+e);
		//TODO add your code for consoleTextArea.keyReleased
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String currentString = consoleTextArea.getText();
						
			String input = currentString.substring(interpreter.getCurrentText().length(),currentString.length()-1);

			interpreter.getInput(input);
		}
		
	}	

}
