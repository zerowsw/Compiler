package semantic_analyzer;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JTextArea;



import parser.TreeNode;
import semantic_analyzer.FourElementFormula;
import semantic_analyzer.SymbolTable;

public class SemanticAnalyzer {

	// 用来进行语义分析的语法树
	private TreeNode root;
	// 中间代码的四元式序列
	private LinkedList<FourElementFormula> intermediateCode;
	// 符号表结构，用于进行语义分析
	private SymbolTable symbolTable;
	// 需要一个作用域变量，这样方便判断和修改作用域
	private int level = 0;
	//定义一个静态变量用于生成四元式中临时变量的名字
	private static int tempNum = 1;
	//控制台对象
	private JTextArea consoleTextArea;
	//记录函数返回类型
	private String currentReturnString;
	
	//定义一个结构存储在函数体之外的声明语句的编号
	private ArrayList<Integer> declare_number;
	
	
	
	
	
	

	public SemanticAnalyzer(TreeNode root,JTextArea consoleTextArea) {
		this.root = root;
		this.consoleTextArea = consoleTextArea;

	}
	
	public ArrayList<Integer> getDeclare_Number(){
		return declare_number;
	}
	
	
	//定义一个获取临时变量名称的函数
	private String generateTempName(){
		String name = "#temp"+tempNum;
		tempNum++;
		return name;
		
	}
	
	//临时变量名重置
	private void resetTempName(){
		tempNum = 1;
	}
		
	
	/*
	 * 获取上一句四元式中临时变量的名称
	 */
	
	private String getLastTempName(){
		return intermediateCode.get(intermediateCode.size()-1).getResult();
	}
	
	
	/**
	 * 清理废语句
	 * @return
	 * 
	 * 因为我在最底层加入了没有命令的废语句，所以在需要进行清理
	 * 尤其是在设置跳转地址的时候。
	 */
	private void clearUseless(){
		for(int i=0;i<intermediateCode.size();i++){
			if (intermediateCode.get(i).getCommand().equals("")) {
				intermediateCode.remove(i);
				i--;
			}
		}
	}
	

	private void throwSemanticError(int linenumber,String errorInfo) throws SemanticError{
		String errorString = "第"+linenumber+"行出错："+errorInfo;
		throw new SemanticError(errorString);
	}
	
	
	public LinkedList<FourElementFormula> generateCode() throws SemanticError {
		//consoleTextArea.append("开始生成代码\n");
		
		intermediateCode = new LinkedList<FourElementFormula>();
		symbolTable = new SymbolTable();
		declare_number = new ArrayList<Integer>();
		
		

		analyze(root);
		
		//consoleTextArea.append("语义分析结束，开始清理中间代码！\n");
		
		//这里需要对中间代码进行一下清理，清除没有命令项的四元式
		clearUseless();
		

		return intermediateCode;
	}

	
	/**
	 * 语义分析的主方法，需要经常被递归调用，用于语法树的深度优先遍历
	 * 
	 * @param root
	 * @throws SemanticError 
	 */
	private void analyze(TreeNode root) throws SemanticError {
		
		//consoleTextArea.append("开始语义分支分析\n");
		
		// for循环，广度遍历
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeNode node = root.getChild(i);
			String content = node.getContent();
			if (content.equals("int") || content.equals("string")
					|| content.equals("bool") || content.equals("real")) {
				
				declare_statement(node);
				
				//consoleTextArea.append("声明语句分析结束！\n");

			} else if (content.equals("if_statement")) {
				// 进入if语句的话，作用域改变
				level++;
				if_statement(node);
				level--;
				symbolTable.remove(level);

			} else if (content.equals("while_statement")) {
				level++;
				while_statement(node);
				level--;
				//每次退出一个局部作用域，更新符号表， 删除局部符号
				symbolTable.remove(level);

			} else if (content.equals("for_statement")) {
				level++;
				for_statement(node);
				level--;
				symbolTable.remove(level);

			} else if (content.equals("assign_statement")) {
				assign_statement(node);

			} else if (content.equals("read")) {
				read_statement(node);

			} else if (content.equals("write")) {
				write_statement(node);
			}else if (content.equals("function_declare")) {
				level++;
				function_declare(node);
				level--;
				//symbolTable.remove(level); 你妹的，函数不能清除，不然搞毛想,但是函数不能清理，函数内的局部变量是需要清理的
				
			}else if (content.equals("function_call")) {
				function_call(node);
			}else if (content.equals("return_statement")) {
				return_statement(node);
			}
			
		}
		
		resetTempName();
	}

	
	/**
	 * return语句的分析
	 * @param node
	 * @throws SemanticError 
	 */
	private void return_statement(TreeNode node) throws SemanticError {
		String returnType = expression(node.getChild(0));
		if ( !returnType.equals(currentReturnString)) {
			throwSemanticError(node.getLinenumber(), "返回类型不一致");
		}
		
		FourElementFormula formula = new FourElementFormula("=", getLastTempName(), "", "$return");
		intermediateCode.add(formula);
		
		FourElementFormula returnFormula = new FourElementFormula("return", "", "", "");
		intermediateCode.add(returnFormula);
		
	}


	
	/**
	 * 函数调用语句语义分析和中间代码生成
	 * @param node
	 * 
	 * 决定了，在实现函数的同时，我还要实现函数的重载，比较的方法的话，我可以直接用一个parameterListType来判断类别
	 * 不过还需要一些别的东西
	 * @throws SemanticError 
	 * 
	 */
	private String function_call(TreeNode node) throws SemanticError {
		
		//首先获取函数名称
		String functionName = node.getChild(0).getContent();
		String parameterListType = "";
		
	
		
		
		for(int i=0;i<node.getChild(1).getChildCount();i++){
			String type = "";
			
			//SymbolElement element = symbolTable.getPossibleElement(node.getChild(1).getContent(), level);
			
			SymbolElement tempElement = symbolTable.getPossibleElement(node.getChild(1).getChild(i).getChild(0).getChild(0).getChild(0).getContent(), level);
			
			/*
			 *哇哈哈，就让本大爷来解释解释吧 
			 * 这里面发生了什么，为了识别出数组参数，我需要把跳过expression的分，因为，expression的分析是无法得到数组类型，
			 * 所以我这里直接单独直接分析一下，是不是数组类型，如果不是的话再拿去做expression的分析。
			 * 
			 */
			
			
			if (tempElement != null && (tempElement.getType().equals("int_array") || tempElement.getType().equals("real_array"))) {
				type = tempElement.getType();
				FourElementFormula formula = new FourElementFormula("=", node.getChild(1).getChild(i).getChild(0).getChild(0).getChild(0).getContent(), "", "$queue");
				intermediateCode.add(formula);
				
				
			}else{
				type = expression(node.getChild(1).getChild(i));
				FourElementFormula formula = new FourElementFormula("=", getLastTempName(), "", "$queue");
				intermediateCode.add(formula);
			}
			
			
			
			
			//这样就获得了调用函数时的参数队列
			parameterListType+=type;
		}
		
		SymbolElement element = symbolTable.getFunction(functionName, parameterListType);
		
		if (element == null) {
			//consoleTextArea.append(parameterListType);
			throwSemanticError(node.getChild(0).getLinenumber(), "函数未定义或输入参数错误");
		}
		
//		for (int i = 0; i < node.getChild(1).getChildCount(); i++) {
//			//返回类型已经判断过了
//			String expressionType = expression(node.getChild(1).getChild(i));
//			FourElementFormula formula = new FourElementFormula("=", getLastTempName(), "", "$queue");
//			intermediateCode.add(formula);
//		}
		
		
		FourElementFormula formula = new FourElementFormula("call", "", "", element.getStartPosition()+"");
		intermediateCode.add(formula);
		
		FourElementFormula formula2 = new FourElementFormula("", "", "", "$return");
		intermediateCode.add(formula2);
		
		
		return element.getType();

	}

	
	/**
	 * 函数声明语句语义分析和中间代码生成
	 * @param node
	 * @throws SemanticError 
	 */
	private void function_declare(TreeNode node) throws SemanticError {
		
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		int startPosition = intermediateCode.size();
	
		
		
		String functionName = node.getChild(1).getContent();
		String returnType = node.getChild(0).getContent();
		
		
		/*
		 * 这里是针对main函数的判断
		 */
		
		if (functionName.equals("main")) {
			if ( !returnType.equals("void")) {
				throwSemanticError(node.getChild(1).getLinenumber(), "main函数返回类型应该为空");
			}
			
			if (node.getChild(2).getChildCount() > 0) {
				throwSemanticError(node.getChild(1).getLinenumber(), "main函数的参数列表应该为空");
				
			}
			
		}
		

		currentReturnString = returnType;
		
		//存储参数列表类型串
		String parameterListType = "";
		//获取参数列表类型
		for (int i = 0; i < node.getChild(2).getChildCount(); i++) {
			parameterListType += node.getChild(2).getChild(i).getContent();
		}
		
		if (symbolTable.getFunction(functionName, parameterListType) != null) {
			throwSemanticError(node.getChild(1).getLinenumber(), "函数重定义错误");
		}
		
		
		/*
		 * 将函数添加到符号表，同时记录返回类型，位置信息，声明语句的四元式在中间代码中的开始位置
		 */
		SymbolElement element1 = new SymbolElement(functionName, returnType, level);
		element1.setParameterListTypeString(parameterListType);
		element1.setStartPosition(startPosition);
		symbolTable.add(element1);
		
		
		if(functionName.equals("main") && returnType.equals("void")){
		FourElementFormula fourFormula = new FourElementFormula(returnType, "", "", functionName);
		intermediateCode.add(fourFormula);
		}
		
		/******************************这里添加函数声明的四元式*****************************/
		
		
		/*************************************************************************/
		
		
		
		//这里的话主要是为了清理局部变量
		level ++;
		
		/*
		 * 然后就是涉及到函数定义的四元式格式了，首先也就是对四个传入的参数，其实是四个赋值定义语句
		 * 当然这个值从哪里取就见仁见智了，考虑到传值和取值的特点的话，我在执行的时候会考虑适用队列
		 * 来作为函数调用和函数定义之间的参数传递。那么这里生成的四元式的话就可以用一个特殊的标号来表示了
		 * 
		 */
		for (int i = 0; i < node.getChild(2).getChildCount(); i++) {
			SymbolElement element = symbolTable.getElement(node.getChild(2).getChild(i).getChild(0).getContent(), level);
			if (element != null) {
				throwSemanticError(node.getLinenumber(), "函数参数列表参数名重复");
			}
			
			//将函数的参数列表中的变量加进符号表
			symbolTable.add(new SymbolElement(node.getChild(2).getChild(i).getChild(0).getContent(), node.getChild(2).getChild(i).getContent(), level));
			
			//生成函数参数列表的四元式
			FourElementFormula formula = new FourElementFormula(node.getChild(2).getChild(i).getContent(), node.getChild(2).getChild(i).getChild(0).getContent(), "$queue", "");
			intermediateCode.add(formula);
		}
		
		
		analyze(node.getChild(3));
		
		
		level--;
		symbolTable.remove(level);
		
		
		if (functionName.equals("main")) {
			intermediateCode.add(new FourElementFormula("end", "", "", ""));
		}else{
		//out语句最后的跳转位置有运行时决定
		intermediateCode.add(new FourElementFormula("out", "", "", "$stack"));
		}
		
		/*
		 *扫描一遍之前的语句，将所有的return的地址设为out的位置 
		 */
		
		clearUseless();
		int endPosition = intermediateCode.size();
		
		int countOfReturn = 0;
		for (int i = startPosition; i < endPosition; i++) {
			FourElementFormula  tempFormula = intermediateCode.get(i);
			if (tempFormula.getCommand().equals("return")) {
				tempFormula.setResult(endPosition+"");
				countOfReturn++;
			}
		}
		
		if ( (!returnType.equals("void")) && countOfReturn == 0) {
				throwSemanticError(node.getChild(1).getLinenumber(), "缺少return语句");
		}else if (returnType.equals("void") && countOfReturn > 0) {
				throwSemanticError(node.getChild(1).getLinenumber(), "无返回参数，不需要return语句");
		}
		
		
		
	}


	/**
	 * 声明语句分析和中间代码生成
	 * @param node
	 * @throws SemanticError
	 * 
	 * 我定义的声明语句中，数组必须放在最后，同时，如果有数组的话，是不能进行赋值的。于是乎，我首先得
	 * 获取结点的数量，如果最后一个结点是数组结点的话，那么可以用一个循环将之前的符号全部加入符号表当中
	 * 如果最后的几点不是数组并且结点数多于一个的话，那么看结点数是不是多余三个，如果多余三个的话再分析
	 * 有没有赋值语句。因为赋值语句不是单纯的添加进符号表，所以需要额外的分析，可以放在最后。
	 * 
	 * 
	 */
	private void declare_statement(TreeNode node) throws SemanticError {
		//consoleTextArea.append("开始声明语句语义分析\n");
		
		
		//首先获取声明语句的结点个数
		int count = node.getChildCount();
		//分析，最后一个是不是数组结点，如果是的话，那么就不会出现赋值语句
		TreeNode lasTreeNode = node.getChild(count-1);
		
		if (lasTreeNode.getContent().equals("#array")) {
			
			//consoleTextArea.append("数组声明语句分析！\n");
			
			//包含数组声明的声明语句
			for(int i=0;i<count;i++){
				
				
				if(i == count-1){
					/*
					 * 数组的处理，这里会有size的处理，也就是说首先要分析expression
					 */
					
					SymbolElement array_element = symbolTable.getElement(node.getChild(i).getChild(0).getContent(), level);
					
		
					
					if(array_element != null){
						throwSemanticError(node.getChild(i).getLinenumber(), "重复声明数组:"+node.getChild(i).getChild(0).getContent());
					}
				
					String sizeType = expression(node.getChild(i).getChild(1));
					if ( !sizeType.equals("int")) {
						throwSemanticError(node.getChild(i).getChild(1).getLinenumber(), "数组的大小必须得是整数类型");
					}
					
					/*
					 * 这里又要注意了，存数组的话，还要存数组的大小
					 * 好吧，这里无所谓，因为数组的大小要具体执行时才知道
					 */
					symbolTable.add(new SymbolElement(node.getChild(i).getChild(0).getContent(), node.getContent()+"_array", level));
										
					
					FourElementFormula formula = new FourElementFormula(node.getContent()+"_array", node.getChild(i).getChild(0).getContent(), getLastTempName(), "");
					intermediateCode.add(formula);
					
					//将需要先于main函数执行的声明的变量的序号存储起来
					clearUseless();
					if (level == 0) {
						declare_number.add(intermediateCode.size());
					}
					
					
				}else{
					
					SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
					if(element != null){
						throwSemanticError(node.getChild(i).getLinenumber(), "重复声明变量:"+node.getChild(i).getContent());
					}
					
					
					symbolTable.add(new SymbolElement(node.getChild(i).getContent(), node.getContent(), level));
					FourElementFormula formula = new FourElementFormula(node.getContent(), node.getChild(i).getContent(), "", "");
					intermediateCode.add(formula);
					
					clearUseless();
					if (level == 0) {
						declare_number.add(intermediateCode.size());
					}
					
					
				}
				
			}	
		}else if(lasTreeNode.getContent().equals("expression")){
			
			//consoleTextArea.append("声明时赋值语句的分析！\n");
			
			/*
			 * 声明时赋值语句的分析
			 */
			
			String sizeType = expression(lasTreeNode);
			if(node.getContent().equals("int") && sizeType.equals("real")){
				throwSemanticError(node.getLinenumber(), "不能将real类型的值赋给int类型的变量");
			}
			
			String lastTempName = getLastTempName();
			
			for(int i=0;i<count-2;i++){
				SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
				if(element != null){
					throwSemanticError(node.getChild(i).getLinenumber(), "重复声明变量:"+node.getChild(i).getContent());
				}
				symbolTable.add(new SymbolElement(node.getChild(i).getContent(), node.getContent(), level));
				FourElementFormula formula = new FourElementFormula(node.getContent(), node.getChild(i).getContent(), lastTempName, "");
				intermediateCode.add(formula);
				
				
				clearUseless();
				if (level == 0) {
					declare_number.add(intermediateCode.size());
				}
				
				
			}		
			
			//重置临时变量
			resetTempName();
			
		}else{
			//consoleTextArea.append("普通变量声明的语义分析！\n");
			
			/*
			 * 普通的声明多个变量，没有在声明时进行赋值
			 */
			for(int i=0;i<count;i++){
				SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
				if(element != null){
					throwSemanticError(node.getChild(i).getLinenumber(), "重复声明变量:"+node.getChild(i).getContent());
				}
				
				symbolTable.add(new SymbolElement(node.getChild(i).getContent(), node.getContent(), level));
				
				//consoleTextArea.append("普通声明语句中间代码生成\n");
				
				FourElementFormula formula = new FourElementFormula(node.getContent(), node.getChild(i).getContent(), "", "");
				intermediateCode.add(formula);
				
				
				clearUseless();
				if (level == 0) {
					declare_number.add(intermediateCode.size());
				}
			}	
			
			
			//测试
			
			
			
		}
		
			
		
		
	}

	
	/**
	 * 赋值语句语义分析及中间代码生成
	 * @param node
	 * 
	 * 这是最后写的函数啦啦啦
	 * 嗯，是比较简单的，主要分析的语义问题就是，赋值类型的问题， 可是，实际上吧，你看我在声明赋值的时候其实
	 * 已经做过这样的处理了，这里的区别就是多了一个数组的赋值分析。
	 * @throws SemanticError 
	 */
	private void assign_statement(TreeNode node) throws SemanticError {
		//consoleTextArea.append("开始赋值语句语义分析");
		
		
		if ( !node.getChild(0).getContent().equals("#array")) {
			
			
		//consoleTextArea.append("到这里进行分析啦："+level);
			
		//普通变量的赋值
		SymbolElement element = symbolTable.getPossibleElement(node.getChild(0).getContent(), level);
		
		
		if (element == null) {
			throwSemanticError(node.getLinenumber(), "未声明的变量"+node.getChild(0).getContent());
		}
		
		//计算右侧的表达式，并获取返回类型
		String sizeType = expression(node.getChild(2));
		if(element.getType().equals("int") && sizeType.equals("real")){
			throwSemanticError(node.getLinenumber(), "不能将real类型的值赋给int类型的变量");
		}
		
		FourElementFormula  formula = new FourElementFormula("=", getLastTempName(),"",element.getName());
		intermediateCode.add(formula);
		
//		//重置临时变量生成
//		resetTempName();
		
	}else {
		
		//consoleTextArea.append("数组的名字是：" + node.getChild(0).getChild(0).getContent());
		
		//数组的赋值
		SymbolElement element = symbolTable.getPossibleElement(node.getChild(0).getChild(0).getContent(), level);
		if (element == null) {
			throwSemanticError(node.getLinenumber(), "未声明的数组变量");
		}
		
		/*
		 * 计算右侧的表达式，并获取返回类型
		 * 当然，针对数组而言的话，还有一个下标越界的问题，这是执行过程中需要考虑的问题，语义分析这里就不写了
		 */
		String type = expression(node.getChild(2));
		String temp1 = getLastTempName();
		
		if (element.getType().equals("int_array") && type.equals("real")) {
			throwSemanticError(node.getLinenumber(), "不能将real类型的变量赋值为int类型的数组");
		}
		
		String type2 = expression(node.getChild(0).getChild(1));
		String temp2 = getLastTempName();
		if ( !type2.equals("int")) {
			throwSemanticError(node.getLinenumber(), "数组下标必须是整数");
		}
		
		FourElementFormula formula = new FourElementFormula("=", temp1, "", element.getName()+"["+temp2+"]");
		intermediateCode.add(formula);
		
		//重置临时变量
		resetTempName();
	}
		
	}
		

/******************************************************************************************************/	
	

	/**
	 * write语句分析
	 * @param node
	 * @throws SemanticError 
	 *  write语句分析，可以写表达式，所以需要一个临时变量的衔接。同时这里的语义分析放到expression里面分析即可
	 */
	private void write_statement(TreeNode node) throws SemanticError {
		
		//首先是表达式的分析
		TreeNode root = node.getChild(0);
		expression(root);

		FourElementFormula formula = new FourElementFormula("write", "", "", getLastTempName());
		intermediateCode.add(formula);
	}

	
	/**
	 * read语句的语义和中间代码生成
	 * @param node
	 * @throws SemanticError
	 * 
	 * 注意这里读取数据的类型判断语义分析没有完成，得到解释执行的过程中
	 */
	private void read_statement(TreeNode node) throws SemanticError {
		TreeNode valNode = node.getChild(0);
		String valname = valNode.getContent();
		
		SymbolElement element = symbolTable.getPossibleElement(valname, level);
		
		if(element == null){
			//变量为声明
			throwSemanticError(node.getLinenumber(), "未声明的变量");
		}else{
			//那啥，因为这句要从外部读取值，所以类型语义判断这里不能实现
			FourElementFormula formula = new FourElementFormula("read", "", "", element.getName());
			intermediateCode.add(formula);
		}
	}

	
	/**
	 * for语句中间代码生成
	 * @param node
	 * @throws SemanticError
	 */
	private void for_statement(TreeNode node) throws SemanticError {
		
		//加个标识区分语句块，方便中间代码执行
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		
		declare_statement(node.getChild(0).getChild(0));
		clearUseless();
		int startposition = intermediateCode.size()+1;
		
		condition(node.getChild(1).getChild(0)); 
		
		FourElementFormula jump1 = new FourElementFormula("jmp",getLastTempName() , "", "");
		intermediateCode.add(jump1);
		//进入语句块的分析，作用域再加一级
		level++;
		analyze(node.getChild(3));
		level--;
		//紧接着是每次循环结束之后的运算。
		assign_statement(node.getChild(2).getChild(0));
		intermediateCode.add(new FourElementFormula("jmp", "", "", startposition+""));
		clearUseless();
		jump1.setResult((intermediateCode.size()+1)+"");
		
		intermediateCode.add(new FourElementFormula("out", "", "", ""));
		
	}

		
	/**
	 * while语句中间代码生成
	 * @param node
	 * @throws SemanticError
	 */
	private void while_statement(TreeNode node) throws SemanticError {
		
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		
		clearUseless();
		int startposition = intermediateCode.size()+1;
		
		 condition(node.getChild(0));
		 
		FourElementFormula jump1 = new FourElementFormula("jmp",getLastTempName(), "", "");
		intermediateCode.add(jump1);
		
		level++;
		analyze(node.getChild(1));
		level--;
		symbolTable.remove(level);
		
		
		intermediateCode.add(new FourElementFormula("jmp", "", "", startposition+""));
		
		clearUseless();
		int endposition = intermediateCode.size()+1;
		jump1.setResult(endposition+"");
		
		intermediateCode.add(new FourElementFormula("out", "", "", ""));
		
	}

	
	/**
	 * if语句中间代码生成
	 * @param node
	 * @throws SemanticError
	 */
	private void if_statement(TreeNode node) throws SemanticError {
		
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		
		//不太清楚下标溢出的问题，所以决定用子节点数量来分析是否有else节点
		int count = node.getChildCount();
		
		condition(node.getChild(0));
		
		FourElementFormula jump1 = new FourElementFormula("jmp", getLastTempName(), "", "");
		intermediateCode.add(jump1);
		
		level++;
		analyze(node.getChild(1));
		level--;
		
		symbolTable.remove(level);
		
		
		if(count == 3){
			
			FourElementFormula jump2 = new FourElementFormula("jmp", "", "", "");
			
			intermediateCode.add(jump2);
			
			clearUseless();
			int position = intermediateCode.size()+1;
			jump1.setResult(position+"");
			
			level++;
			analyze(node.getChild(2));
			level--;
			symbolTable.remove(level);
	
			
			clearUseless();
			jump2.setResult((intermediateCode.size()+1)+"");
			
			
		}else{
			clearUseless();
			jump1.setResult((intermediateCode.size()+1)+"");
		}		
		
		intermediateCode.add(new FourElementFormula("out", "", "", ""));
	}
	
	
	
	/**
	 * condition的语义分析，包括中间代码生成
	 * @param node
	 * @throws SemanticError
	 * 
	 * 我定义的condition结点包括三个子结点，expression,比较符，expression
	 * expression的语法分析，主要是两边的类型要一致
	 */
	private void condition(TreeNode node) throws SemanticError {
		//我定义的condition的语法树包括，expression,比较符号,expression三个结点
		//首先分析第一个expression结点，并获取分析结果的类型
		String type1 = expression(node.getChild(0));
		String temp1 = getLastTempName();
		String type2 = expression(node.getChild(2));
		String temp2 = getLastTempName();
//		if(!type1.equals(type2)){
//			throwSemanticError(node.getLinenumber(), "比较运算两边变量类型不一致");
//		}
		
		//jump语句会根据存储比较结果的临时变量的值来判断跳转与否
		FourElementFormula formula = new FourElementFormula(node.getChild(1).getContent(), temp1, temp2, generateTempName());
		intermediateCode.add(formula);
	
	}

	
	/**
	 * 表达式的分析
	 * @param node
	 * @return 表达式分析结果的类型
	 * @throws SemanticError
	 */
	private String expression(TreeNode node) throws SemanticError {
		
		//consoleTextArea.append("表达式分析开始！\n");
		
		int count = node.getChildCount();
		
		//因为只定义了两个类型，所以默认情况下是int,如果遇到了一个real,那么最后的结果就是real
		String expressionType = "int";
		String type1 = term(node.getChild(0));
		
		//consoleTextArea.append("term分析结束\n");
		
		if(type1.equals("real")){
			expressionType = "real";
		}
		
		/*
		 * 这里如果是一个单项式的话，其实也不需要另外再写语句，运维下一层的函数会分析并生成的
		 */
		for(int i=2;i<count;i=i+2){
			String temp1 = getLastTempName();
			String type = term(node.getChild(i));
			
			//如果在分析这些结点的过程中，如果遇到了一个real类型变量，那么最后的结果就是real类型的
			if (type.equals("real")) {
				expressionType = "real";
			}
			String temp2 = getLastTempName();
			FourElementFormula formula = new FourElementFormula(node.getChild(i-1).getContent(), temp1, temp2, generateTempName());
			intermediateCode.add(formula);
		}
		
		return expressionType;
	}
	
	
	/**
	 * term单元的类型
	 * @param node
	 * @return 该语法单元计算结果的类型
	 * @throws SemanticError
	 * 
	 *唉，之前用的方法还真是麻烦，现在思路清晰之后，一下子变得简单了很多，这里的机构基本上是和expression里面的分析是一致的
	 *额，不对，应该说，是一模一样的。
	 *最底层的分析的话还要留给factor
	 */
	 
	private String term(TreeNode node) throws SemanticError {
		//consoleTextArea.append("开始term的分析\n");
		
		//首先获取这个运算单元的项数
		int count = node.getChildCount();
		//然后就是好好看看怎么样组织一下类型分析了，具体得结合factor的类型
		
		String termType = "int";
		String type1 = factor(node.getChild(0));
		
		//consoleTextArea.append("factor分析结束\n");
		if(type1.equals("real")){
			termType = "real";
		}
		
		
		for(int i=2;i<count;i = i+2){
			String temp1 = getLastTempName();
			String type = factor(node.getChild(i));
			
			//如果在分析这些结点的过程中，如果遇到了一个real类型变量，那么最后的结果就是real类型的
			if (type.equals("real")) {
				termType = "real";
			}
			String temp2 = getLastTempName();
			FourElementFormula formula = new FourElementFormula(node.getChild(i-1).getContent(), temp1, temp2, generateTempName());
			intermediateCode.add(formula);
		}
		
		return termType;
	
	}
	
	
	/**
	 * factor节点的分析
	 * @param child
	 * @return factor结点的类型
	 * @throws SemanticError 
	 */
	private String factor(TreeNode root) throws SemanticError {
		
		//consoleTextArea.append("开始factor的分析！\n");
		/*
		 * 到了最底层了，最基本元素的判断，其实和我之前的语法分析有关了，我在最基本的factor结点里面定义了kind属性
		 * 这样的话，到了现在就容易分析很多了，其实，就算没有定义这个属性，我也完全可以在factor的下一级定义一个类型
		 * 结点。其实在语法分析的时候是完全还可以判断类型的，就看你语法分析过后有没有保留类型信息了
		 */
			
		root = root.getChild(0);
		
		//我就知道是结点错位了
		//consoleTextArea.append("factor节点的类型："+root.getContent());
		
         if (root.getContent().equals("function_call")) {
			
			//函数调用的分析
			
			String type = function_call(root);
			FourElementFormula formula = new FourElementFormula("", "", "", getLastTempName());
			intermediateCode.add(formula);
			return type;
			
		}else if(root.getType().equals("int")){
			//consoleTextArea.append("进入int类型基本算术单元的分析！\n");
			
			String type = "int";
			//注意，为了上一步获取变了个名称的方便，这里会生成一条没有command属性的四元式
			FourElementFormula formula = new FourElementFormula("", "", "", root.getContent());
			intermediateCode.add(formula);
			return type;	
			
		}else if(root.getType().equals("real")){
			String type = "real";
			FourElementFormula formula = new FourElementFormula("", "", "", root.getContent());
			intermediateCode.add(formula);
			return type;
			
			
		}else if(root.getType().equals("identify")){
			SymbolElement element = symbolTable.getPossibleElement(root.getContent(), level);
			if (element == null) {
				throwSemanticError(root.getLinenumber(), "未声明的变量");
			}
			String type = element.getType();
			FourElementFormula formula = new FourElementFormula("", "", "", element.getName());
			intermediateCode.add(formula);
			return type;
			
		}else if(root.getType().equals("array")){
			String type = "";
			//这里就设计到数组属性的判断饿了,不过这要运行时才能知道，所以这里单纯生成中间代码
			SymbolElement element = symbolTable.getPossibleElement(root.getChild(0).getContent(), level);
			if (element == null) {
				throwSemanticError(root.getLinenumber(), "未声明的数组");
			}
			
			
			//类型判断
			String temptype = element.getType();
			if (temptype.equals("int_array")) {
				type = "int";
			}else{
				type = "real";
			}
			
			
			String indextype = expression(root.getChild(1));
			if(indextype.equals("real")){
				throwSemanticError(root.getLinenumber(), "数组下标不是整数");
			}
			
			//数组中间代码格式
			FourElementFormula formula = new FourElementFormula("", "", "", element.getName()+"["+getLastTempName()+"]");
			intermediateCode.add(formula);
			
			return type;
			
		}else{
			//expression
			String type = expression(root);
			return type;
			
		}
		
	}

/*****************************************************************************************************/
	

	
}
