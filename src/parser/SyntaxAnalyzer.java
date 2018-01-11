package parser;

import java.util.ArrayList;


import lexer.Tag;
import lexer.Token;

/**
 * CMM语法和语义分析
 * 
 * @author 王绍文
 * 
 *         语法制导翻译是基于属性文法的处理过程，对单词符号串进行语法分析，构造语法分析树，然后根据需要构造属性
 *         依赖图，比那里语法树并在语法树的各结点处按语义规则进行计算。虽然这样说，处于效率和方便性的考虑，一般 在语法分析的同时进行语义分析。
 *         本类也是如此，在进行语法分析的同时，进行语义分析。
 * 
 */
public class SyntaxAnalyzer {

	// 记录词法分析得到的token序列，同时也是语法分析的输入
	private ArrayList<Token> tokens;
	// 当前正在分析的token
	private Token currentToken;
	// 分析的token的标号
	private int index = 0;

	public SyntaxAnalyzer(ArrayList<Token> tokens) {
		this.tokens = tokens;
		if (tokens.size() != 0) {
			currentToken = tokens.get(0);
		}
	}

	private void nextToken() {
		index++;
		if (index > tokens.size() - 1) {

			return;
		}
		currentToken = tokens.get(index);
	}

	public TreeNode start() throws SyntaxError {
		TreeNode root = new TreeNode("program", 0);
		while (index < tokens.size()) {
			root.add(statement());
		}
		return root;
	}

	/**
	 * 错误处理函数
	 * 
	 * @param errorInfo
	 *            具体错误类型
	 * @throws SyntaxError
	 */
	private void throwSyntaxError(String errorInfo) throws SyntaxError {
		String errorstring = "第" + currentToken.getLinenumber() + "行出错： "
				+ errorInfo;
		throw new SyntaxError(errorstring);
	}

	/**
	 * 语句的语法分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode statement() throws SyntaxError {
		TreeNode resultNode = null;
		System.out.println("下面开始判断第一个Token类型");
		if (currentToken.tag == Tag.IDENTIFY) {
			resultNode = assign_statement();
		} else if (currentToken.tag == Tag.INT || currentToken.tag == Tag.REAL) {
			resultNode = declare_statement();
		} else if (currentToken.tag == Tag.IF) {
			resultNode = if_statement();
		} else if (currentToken.tag == Tag.WHILE) {
			resultNode = while_statement();
		} else if (currentToken.tag == Tag.READ) {
			resultNode = read_statement();
		} else if (currentToken.tag == Tag.WRITE) {
			resultNode = write_statement();
		} else if (currentToken.tag == Tag.FOR) {
			resultNode = for_statement();
		}else if(currentToken.tag == Tag.VOID){
			//System.out.println("成功判断成void");
			
			resultNode = function_statement();
		}else if(currentToken.tag == Tag.RETURN){
			System.out.println("成功判断到return");
			resultNode = return_statement();
		} else {
			System.out.println(currentToken.getValue());
			throwSyntaxError("无法识别的语法规则");
		}

		return resultNode;
	}

	
	
	/**
	 * 专门用来判断函数返回语句的
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode return_statement() throws SyntaxError {
		//函数的返回值应该可以是一个expression
		TreeNode resultNode = new TreeNode("return_statement", currentToken.getLinenumber());
		nextToken();
		resultNode.add(expression());
		resultNode.setLinenumber(currentToken.getLinenumber());
		
		if (currentToken.tag != Tag.SEMICOLON) {
			throwSyntaxError("return语句缺少分号");
		}
		nextToken();
		
		return resultNode;
	}
	
	
	

	/**
	 * for循环语法语义分析
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode for_statement() throws SyntaxError {
		TreeNode resultNode = new TreeNode("for_statement",
				currentToken.getLinenumber());
		nextToken();

		// 左括号判断
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			throwSyntaxError("for循环条件缺少左括号");
		}

		// 首先是循环变量的声明及赋值
		if (currentToken.tag != Tag.SEMICOLON) {
			TreeNode initializationNode = new TreeNode("initialization",
					currentToken.getLinenumber());
			resultNode.add(initializationNode);
			initializationNode.add(declare_statement());
		} else {
			resultNode.add(new TreeNode("initialization", currentToken
					.getLinenumber()));
			nextToken();
		}

		// for循环判断条件的语法分析
		TreeNode condition = new TreeNode("condition",
				currentToken.getLinenumber());
		if (currentToken.tag != Tag.SEMICOLON) {
			condition.add(condition());
			resultNode.add(condition);
		} else {
			resultNode.add(condition);
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			throwSyntaxError("for循环判断条件缺少分号");
		}

		// 每次步长变化的语法判断。
		if (currentToken.tag != Tag.RIGHTPARENTHESES) {

			TreeNode for_stepExpressionNode = new TreeNode("for_step",
					currentToken.getLinenumber());
			resultNode.add(for_stepExpressionNode);

			TreeNode assignNode = new TreeNode("assign_statement",
					currentToken.getLinenumber());
			for_stepExpressionNode.add(assignNode);
			assignNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken();
			if (currentToken.tag == Tag.ASSIGN) {
				assignNode.add(new TreeNode("=", currentToken.getLinenumber()));
				nextToken();
			} else {
				// 缺少赋值符号的错误处理
				throwSyntaxError("赋值语句缺少\"=\"");
			}
			assignNode.add(expression());

		} else {
			resultNode.add(new TreeNode("for_step", currentToken
					.getLinenumber()));
		}

		// for循环体的内容分析
		TreeNode for_content = new TreeNode("for_content",
				currentToken.getLinenumber());
		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			resultNode.add(for_content);
			nextToken();
			if (currentToken.tag == Tag.LEFTBRACKET) {
				nextToken();
			} else {
				throwSyntaxError("for循环体 缺少左括号");
			}
		} else {

			throwSyntaxError("for循环条件缺少右括号");
		}

		for_content.add(statement());

		if (currentToken.tag == Tag.RIGHTBRACKET) {
			nextToken();
		} else {

			throwSyntaxError("for循环体 缺少右括号");
		}

		return resultNode;
	}

	/**
	 * if语句语法语义分析
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode if_statement() throws SyntaxError {
		TreeNode resultNode = null;
		/*
		 * 这里需要一个表示是否判断语句的节点。 之前我总是想把Token的数据结构再次运用到语法树中
		 * 现在发现其实没有必要，所以我需要修改一下TreeNode
		 */
		// 首先有一个根节点
		resultNode = new TreeNode("if_statement", currentToken.getLinenumber());
		nextToken();

		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			// 这里的终结符（我就不存储了，对于if语句而言，我要的节点就两个，condition和statements
			nextToken();
		} else {
			// 没有左括号的出错处理
			throwSyntaxError("判断条件缺少左括号");
		}

		resultNode.add(condition());

		// 对右括号进行检测
		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// 没有右括号的出错处理
			throwSyntaxError("判断语句缺少右括号\")\"");

		}
		// 对左大括号的检测
		if (currentToken.tag == Tag.LEFTBRACKET) {
			nextToken();
			TreeNode statements = new TreeNode("statements",
					currentToken.getLinenumber());
			resultNode.add(statements);
			
			// 对括号中可能存在的多条语句进行分析
			while (currentToken != null) {
				if (currentToken.tag != Tag.RIGHTBRACKET) {
					statements.add(statement());
				} else if (statements.getChildCount() == 0) { // 如果括号内没有语句的话
					resultNode.remove(resultNode.getChildCount() - 1);
					resultNode.add(new TreeNode("empty_statement", currentToken
							.getLinenumber()));
					break;
				} else {
					break;
				}
			}

			if (currentToken != null && currentToken.tag == Tag.RIGHTBRACKET) {
				nextToken();
			} else {
				// 缺少右大括号的错误处理
				throwSyntaxError("缺少右大括号\"}\"");
			}
		} else {
			// 没有大括号，即只要一条语句
			if (currentToken != null){
				
				TreeNode statements = new TreeNode("statements",
						currentToken.getLinenumber());
				resultNode.add(statements);
				statements.add(statement());
				
			}
		}

		if (currentToken != null && currentToken.tag == Tag.ELSE) {
			TreeNode elseNode = new TreeNode("else",
					currentToken.getLinenumber());
			resultNode.add(elseNode);
			// 额这里也忘了加nextToken
			nextToken();

			if (currentToken.tag == Tag.LEFTBRACKET) {
				// 这里忘了读取下一个token
				nextToken();
				while (currentToken != null
						&& currentToken.tag != Tag.RIGHTBRACKET) {
					elseNode.add(statement());
				}
				if (currentToken.tag == Tag.RIGHTBRACKET) {
					nextToken();
				} else {
					// 缺少右括号的错误处理
					throwSyntaxError("缺少右大括号\"}\"");
				}
			} else {
				// 如果没有左括号，也就是说，只有一句语句
				elseNode.add(statement());
			}
		}

		return resultNode;
	}

	/**
	 * while语句的分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode while_statement() throws SyntaxError {
		TreeNode resultNode = new TreeNode("while_statement",
				currentToken.getLinenumber());
		nextToken();
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			// 缺少左括号的出错处理
			throwSyntaxError("while循环条件缺少左括号\"（\"");
		}

		// 对while条件语句的判断
		resultNode.add(condition());

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// 缺少条件判断的右括号
			throwSyntaxError("循环条件缺少右括号\")\"");
		}

		// 我定义的while语句必须有大括号的存在
		if (currentToken.tag == Tag.LEFTBRACKET) {
			TreeNode statements = new TreeNode("statements",
					currentToken.getLinenumber());
			resultNode.add(statements);
			nextToken();

			while (currentToken != null && currentToken.tag != Tag.RIGHTBRACKET) {
				statements.add(statement());
			}
			if (currentToken.tag == Tag.RIGHTBRACKET) {
				nextToken();
			} else {
				// 缺少右大括号的出错处理
				throwSyntaxError("缺少右大括号\"}\"");
			}
			if (statements.getChildCount() == 0) {
				resultNode.remove(resultNode.getChildCount() - 1);
				resultNode.add(new TreeNode("empty_statements", currentToken
						.getLinenumber()));
			}
		} else {
			// 缺少左大括号的处理
			throwSyntaxError("循环体缺少左大括号\"{\"");
		}

		return resultNode;
	}

	/**
	 * read语句的分析
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode read_statement() throws SyntaxError {
		TreeNode resultNode = new TreeNode("read", currentToken.getLinenumber());
		nextToken();
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			// read语句缺少左括号的错误处理
			throwSyntaxError("read语句缺少左括号\"(\"");
		}

		if (currentToken.tag == Tag.IDENTIFY) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken();
		} else {
			// 非标识符错误
			throwSyntaxError("read语句里面出现非标识符");
		}
		

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// 缺少右括号错误
			throwSyntaxError("缺少右括号\")\"");
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// 缺少分号错误
			throwSyntaxError("缺少分号");
			
		}

		return resultNode;
	}

	/**
	 * write语句的分析
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode write_statement() throws SyntaxError {

		TreeNode resultNode = new TreeNode("write",
				currentToken.getLinenumber());
		nextToken(); // 靠，又忘了
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			// read语句缺少左括号的错误处理
			throwSyntaxError(" write语句缺少左括号\"(\"");
		}

		resultNode.add(expression());

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// 缺少右括号错误
			throwSyntaxError("write语句缺右括号\")\"");
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// 缺少分号错误
			throwSyntaxError("缺少分号");
		}

		return resultNode;
	}

	
	
	/**
	 * 函数的语法识别
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode function_statement() throws SyntaxError {
		
		TreeNode resultNode  = new TreeNode("function_declare", currentToken.getLinenumber());
		//添加类型结点
		resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		nextToken();
		if (currentToken.tag == Tag.IDENTIFY) {
			//添加函数名称结点
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		}else {
			throwSyntaxError("错误的函数声明，缺少函数正确的函数名");
		}
		
		nextToken();
		if ( currentToken.tag != Tag.LEFTPARENTHESES) {
			throwSyntaxError("函数声明的参数列表缺少左括号");
		}
		
		nextToken();
		//下面开始处理参数列表的声明
		
		TreeNode parameterNode =new TreeNode("parameter_list", currentToken.getLinenumber());
		resultNode.add(parameterNode);
		
		boolean flag = false;
		
		if (currentToken.tag == Tag.INT || currentToken.tag == Tag.REAL) {
			do{
				
			if (flag) {
				nextToken();
			}
			TreeNode typeNode = new TreeNode(currentToken.getValue(), currentToken.getLinenumber());
			parameterNode.add(typeNode);
			nextToken();
			if (currentToken.tag != Tag.IDENTIFY) {
				System.out.println(currentToken.getValue());
				throwSyntaxError("错误的参数列表");
			}
			
			
			typeNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
			nextToken();
			
			//这里是传数组
			if (currentToken.tag == Tag.LEFTBRACE) {
				//Token token = tokens.get(index-1);
				typeNode.setContent(typeNode.getContent()+"_array");
				//typeNode.add(new TreeNode(token.getValue(), token.getLinenumber()));		
				nextToken();
				if (currentToken.tag != Tag.RIGHTBRACE) {
					throwSyntaxError("函数参数列表错误，出现单独的左中括号");
				}
				
				nextToken();
			}	
			
			 flag = true;
			 
			}while(currentToken.tag == Tag.COMMA);
			
			if (currentToken.tag != Tag.RIGHTPARENTHESES) {
				throwSyntaxError("函数声明参数列表缺少右括号");
			}
			
			//开始进入函数体的分析
			nextToken();
	

		}else if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		}else {
			throwSyntaxError("函数声明参数列表错误");
		}
		
		if (currentToken.tag != Tag.LEFTBRACKET) {
			throwSyntaxError("函数体缺少左括号");
		}
		
		nextToken();
		
		
		TreeNode statementNode = new TreeNode("statements", currentToken.getLinenumber());
		resultNode.add(statementNode);
		
	
		while (currentToken != null && currentToken.tag != Tag.RIGHTBRACKET) {
			statementNode.add(statement());
		}
		
		if (currentToken.tag != Tag.RIGHTBRACKET) {
			throwSyntaxError("函数体缺少右括号");
		}
		
		nextToken();
	
		return resultNode;
	}
	
	
	
	
	
	
	
	/**
	 * 声明语句的语法分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode declare_statement() throws SyntaxError {

		// 是否有数组声明
		boolean arrayflag = false;
		
		

		TreeNode resultNode = new TreeNode(currentToken.getValue(),
				currentToken.getLinenumber());

		do {
			nextToken(); // 放错位置了
			if (currentToken.tag == Tag.IDENTIFY) {
				resultNode.add(new TreeNode(currentToken.getValue(),
						currentToken.getLinenumber()));
				nextToken();

			} else {
				// 标识符出错处理
				throwSyntaxError("标识符错误");
			}
		} while (currentToken.tag == Tag.COMMA);

		
		/***********************************************************************************************************/
		/*
		 * 进行函数的分析
		 */
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			//如果分析到函数的话，那么需要将token倒退的类型的部分
			index = index - 3;
			nextToken();
			resultNode = function_statement();
			return resultNode;
		}
		/*************************************************************************************************************/
		
		
		
		// 数组的分析
		if (currentToken.tag == Tag.LEFTBRACE) {
			TreeNode arrayNode = new TreeNode("#array",
					currentToken.getLinenumber());
			resultNode.remove(resultNode.getChildCount() - 1);
			resultNode.add(arrayNode);
			int tg = index - 1;
			Token token = tokens.get(tg);
			arrayNode.add(new TreeNode(token.getValue(), currentToken
					.getLinenumber()));

			nextToken();

			arrayNode.add(expression());

			if (currentToken.tag != Tag.RIGHTBRACE) {
				throwSyntaxError("数组声明缺少右括号");
			} else {
				nextToken();
				arrayflag = true;
			}

		}

		// 进行声明赋值分析
		if (currentToken.tag == Tag.ASSIGN) {
			if (!arrayflag) {
				resultNode.add(new TreeNode(currentToken.getValue(),
						currentToken.getLinenumber()));
				nextToken();
				resultNode.add(expression()); //我去，坑爹了
			} else {
				throwSyntaxError("数组无法在声明时赋值");
			}
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// 声明语句缺少分号
			System.out.println("声明缺分号"+currentToken.getValue());
			throwSyntaxError("缺少分号");
		}

		return resultNode;
	}

	/**
	 * 赋值语句的语法分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode assign_statement() throws SyntaxError {
		TreeNode resultNode = new TreeNode("assign_statement",
				currentToken.getLinenumber());
		
		TreeNode tempNode = new TreeNode(currentToken.getValue(), currentToken.getLinenumber());
		nextToken();
		
	
		/*
		 * 
		 */
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			//这里就应该是函数调用了
			index = index-2;
			nextToken();
			resultNode = function_call(true);
			return resultNode;
		}
		
		// 数组赋值
		if (currentToken.tag == Tag.LEFTBRACE) {

			// 改变挂载结构
			TreeNode arrayNode = new TreeNode("array","#array",
					currentToken.getLinenumber());
			arrayNode.add(tempNode);
			resultNode.add(arrayNode);

//			int tg = index - 1;
//			Token token = tokens.get(tg);
//			arrayNode.add(new TreeNode(token.getValue(), currentToken
//					.getLinenumber()));

			nextToken();
			arrayNode.add(
					expression());
			if (currentToken.tag != Tag.RIGHTBRACE) {
				throwSyntaxError("数组声明缺少右括号");
			} else {
				nextToken();
			}
			
		}else {
			resultNode.add(tempNode);
		}
		
		
		
		if (currentToken.tag == Tag.ASSIGN) {
			resultNode.add(new TreeNode("=", currentToken.getLinenumber()));
			nextToken();
		} else {
			// 缺少赋值符号的错误处理
			throwSyntaxError("：赋值语句缺少\"=\"");
		}
		resultNode.add(expression()); // 有可能是bool变量的赋值
		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// 缺少分号的错误处理
			System.out.println("赋值缺分号"+currentToken.getValue());
			throwSyntaxError("缺少分号");
		}

		return resultNode;
	}

	
	
	
	/**
	 * 函数调用的语法分析
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode function_call(boolean flag) throws SyntaxError {
		TreeNode resultNode = new TreeNode("function_call", currentToken.getLinenumber());
		resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		nextToken();
		//这里因为事先已经判断过了有左括号，所以直接往下执行
		
		
		TreeNode parameter_lisTreeNode = new TreeNode("parameter_list", currentToken.getLinenumber());
		
		nextToken();
		if (currentToken.tag != Tag.RIGHTPARENTHESES) {
			index  = index - 1;
			do{
				nextToken();
				parameter_lisTreeNode.add(expression());
			}while(currentToken.tag == Tag.COMMA);
			
			
		}
		
		
		
		resultNode.add(parameter_lisTreeNode);
		
		if (currentToken.tag != Tag.RIGHTPARENTHESES) {
			throwSyntaxError("函数滴用缺少右括号");
		}
		
		nextToken();
		
		if(flag){
			if (currentToken.tag != Tag.SEMICOLON) {
				throwSyntaxError("函数调用语句缺少分号");
			}
		
			nextToken();
		}
		
		
		return resultNode;
	}

	/**
	 * 条件表达式的语法判断
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode condition() throws SyntaxError {
		TreeNode resultNode = new TreeNode("condition",
				currentToken.getLinenumber());

		/*
		 * *******************************************
		 * 这里还需要修改，需要具体分析变量类型了，也就是需要考虑类型 记住一定要修改哦
		 * ********************************************
		 */

		resultNode.add(expression());
		if (currentToken.tag == Tag.FEWER || currentToken.tag == Tag.GREATER
				|| currentToken.tag == Tag.EQUAL
				|| currentToken.tag == Tag.NOFEWERTHAN
				|| currentToken.tag == Tag.NOMORETHAN
				|| currentToken.tag == Tag.NOREQUAL) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken(); // 坑爹，一开始忘了加，找了好久
			resultNode.add(expression());
		}

		return resultNode;
	}

	/**
	 * 表达式的分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 * 
	 */
	private TreeNode expression() throws SyntaxError {
		TreeNode resultNode = new TreeNode("expression",
				currentToken.getLinenumber());
		resultNode.add(term());
		// 可能通过多个term连接而成
		while (currentToken.tag == Tag.ADD || currentToken.tag == Tag.MINUS) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken(); // 这里一开始也忘了加了
			resultNode.add(term());
		}
		return resultNode;
	}

	/**
	 * term单位的分析
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode term() throws SyntaxError {
		TreeNode resultNode = new TreeNode("term", currentToken.getLinenumber());
		resultNode.add(factor());
		// 可能将多个factor连接而成
		while (currentToken.tag == Tag.MUTIPLY
				|| currentToken.tag == Tag.DIVIDE) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken(); //我去，我居然又忘了加了
			resultNode.add(factor());
		}
		return resultNode;

	}

	/**
	 * 基本项的分析
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode factor() throws SyntaxError {
		TreeNode resultNode = new TreeNode("factor", currentToken.getLinenumber());

		if (currentToken.tag == Tag.NUM) {
			resultNode.add(new TreeNode("int", currentToken.getValue(),
					currentToken.getLinenumber()));
			nextToken();
		} else if (currentToken.tag == Tag.REAL) {
			resultNode.add(new TreeNode("real", currentToken.getValue(),
					currentToken.getLinenumber()));
			nextToken();
		} else if (currentToken.tag == Tag.IDENTIFY) {
			TreeNode temp = new TreeNode("identify", currentToken.getValue(),
					currentToken.getLinenumber());
			nextToken();
			
			/*
			 * 哇哈哈，函数调用应该放在这里呀
			 * 函数调用的分析
			 */
			
			if (currentToken.tag == Tag.LEFTPARENTHESES) {
				//这里就应该是函数调用了
				index = index-2;
				nextToken();
				resultNode.add(function_call(false));
				
			}else if (currentToken.tag == Tag.LEFTBRACE) {
				//数组的分析

				// 改变挂载结构
				TreeNode arrayNode = new TreeNode("array","#array",
						currentToken.getLinenumber());
				arrayNode.add(temp);
				resultNode.add(arrayNode);

//				int tg = index - 1;
//				Token token = tokens.get(tg);
//				arrayNode.add(new TreeNode(token.getValue(), currentToken
//						.getLinenumber()));

				nextToken();
				arrayNode.add(
						expression());
				if (currentToken.tag != Tag.RIGHTBRACE) {
					throwSyntaxError("数组声明缺少右括号");
				} else {
					nextToken();
				}
				
			}else {
				resultNode.add(temp);
			}
			
			
			
		}  else if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
			
			//这里设置一下类型，方便后面的语义分析
			TreeNode temNode = expression();
			temNode.setType("expression");
			
			
			resultNode.add(temNode);
			if (currentToken.tag == Tag.RIGHTPARENTHESES) {
				nextToken();
			} else {
				// 算术因子缺少右括号的错误处理
				throwSyntaxError("算术因子缺少右括号\")\"");
			}
		} else {
			// 无法识别的算术因子错误
			throwSyntaxError("无法识别的算术因子");
		}

		return resultNode;
	}
}
