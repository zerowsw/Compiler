package parser;

import java.util.ArrayList;


import lexer.Tag;
import lexer.Token;

/**
 * CMM�﷨���������
 * 
 * @author ������
 * 
 *         �﷨�Ƶ������ǻ��������ķ��Ĵ�����̣��Ե��ʷ��Ŵ������﷨�����������﷨��������Ȼ�������Ҫ��������
 *         ����ͼ���������﷨�������﷨���ĸ���㴦�����������м��㡣��Ȼ����˵������Ч�ʺͷ����ԵĿ��ǣ�һ�� ���﷨������ͬʱ�������������
 *         ����Ҳ����ˣ��ڽ����﷨������ͬʱ���������������
 * 
 */
public class SyntaxAnalyzer {

	// ��¼�ʷ������õ���token���У�ͬʱҲ���﷨����������
	private ArrayList<Token> tokens;
	// ��ǰ���ڷ�����token
	private Token currentToken;
	// ������token�ı��
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
	 * ��������
	 * 
	 * @param errorInfo
	 *            �����������
	 * @throws SyntaxError
	 */
	private void throwSyntaxError(String errorInfo) throws SyntaxError {
		String errorstring = "��" + currentToken.getLinenumber() + "�г��� "
				+ errorInfo;
		throw new SyntaxError(errorstring);
	}

	/**
	 * �����﷨����
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode statement() throws SyntaxError {
		TreeNode resultNode = null;
		System.out.println("���濪ʼ�жϵ�һ��Token����");
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
			//System.out.println("�ɹ��жϳ�void");
			
			resultNode = function_statement();
		}else if(currentToken.tag == Tag.RETURN){
			System.out.println("�ɹ��жϵ�return");
			resultNode = return_statement();
		} else {
			System.out.println(currentToken.getValue());
			throwSyntaxError("�޷�ʶ����﷨����");
		}

		return resultNode;
	}

	
	
	/**
	 * ר�������жϺ�����������
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode return_statement() throws SyntaxError {
		//�����ķ���ֵӦ�ÿ�����һ��expression
		TreeNode resultNode = new TreeNode("return_statement", currentToken.getLinenumber());
		nextToken();
		resultNode.add(expression());
		resultNode.setLinenumber(currentToken.getLinenumber());
		
		if (currentToken.tag != Tag.SEMICOLON) {
			throwSyntaxError("return���ȱ�ٷֺ�");
		}
		nextToken();
		
		return resultNode;
	}
	
	
	

	/**
	 * forѭ���﷨�������
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode for_statement() throws SyntaxError {
		TreeNode resultNode = new TreeNode("for_statement",
				currentToken.getLinenumber());
		nextToken();

		// �������ж�
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			throwSyntaxError("forѭ������ȱ��������");
		}

		// ������ѭ����������������ֵ
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

		// forѭ���ж��������﷨����
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
			throwSyntaxError("forѭ���ж�����ȱ�ٷֺ�");
		}

		// ÿ�β����仯���﷨�жϡ�
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
				// ȱ�ٸ�ֵ���ŵĴ�����
				throwSyntaxError("��ֵ���ȱ��\"=\"");
			}
			assignNode.add(expression());

		} else {
			resultNode.add(new TreeNode("for_step", currentToken
					.getLinenumber()));
		}

		// forѭ��������ݷ���
		TreeNode for_content = new TreeNode("for_content",
				currentToken.getLinenumber());
		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			resultNode.add(for_content);
			nextToken();
			if (currentToken.tag == Tag.LEFTBRACKET) {
				nextToken();
			} else {
				throwSyntaxError("forѭ���� ȱ��������");
			}
		} else {

			throwSyntaxError("forѭ������ȱ��������");
		}

		for_content.add(statement());

		if (currentToken.tag == Tag.RIGHTBRACKET) {
			nextToken();
		} else {

			throwSyntaxError("forѭ���� ȱ��������");
		}

		return resultNode;
	}

	/**
	 * if����﷨�������
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode if_statement() throws SyntaxError {
		TreeNode resultNode = null;
		/*
		 * ������Ҫһ����ʾ�Ƿ��ж����Ľڵ㡣 ֮ǰ���������Token�����ݽṹ�ٴ����õ��﷨����
		 * ���ڷ�����ʵû�б�Ҫ����������Ҫ�޸�һ��TreeNode
		 */
		// ������һ�����ڵ�
		resultNode = new TreeNode("if_statement", currentToken.getLinenumber());
		nextToken();

		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			// ������ս�����ҾͲ��洢�ˣ�����if�����ԣ���Ҫ�Ľڵ��������condition��statements
			nextToken();
		} else {
			// û�������ŵĳ�����
			throwSyntaxError("�ж�����ȱ��������");
		}

		resultNode.add(condition());

		// �������Ž��м��
		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// û�������ŵĳ�����
			throwSyntaxError("�ж����ȱ��������\")\"");

		}
		// ��������ŵļ��
		if (currentToken.tag == Tag.LEFTBRACKET) {
			nextToken();
			TreeNode statements = new TreeNode("statements",
					currentToken.getLinenumber());
			resultNode.add(statements);
			
			// �������п��ܴ��ڵĶ��������з���
			while (currentToken != null) {
				if (currentToken.tag != Tag.RIGHTBRACKET) {
					statements.add(statement());
				} else if (statements.getChildCount() == 0) { // ���������û�����Ļ�
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
				// ȱ���Ҵ����ŵĴ�����
				throwSyntaxError("ȱ���Ҵ�����\"}\"");
			}
		} else {
			// û�д����ţ���ֻҪһ�����
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
			// ������Ҳ���˼�nextToken
			nextToken();

			if (currentToken.tag == Tag.LEFTBRACKET) {
				// �������˶�ȡ��һ��token
				nextToken();
				while (currentToken != null
						&& currentToken.tag != Tag.RIGHTBRACKET) {
					elseNode.add(statement());
				}
				if (currentToken.tag == Tag.RIGHTBRACKET) {
					nextToken();
				} else {
					// ȱ�������ŵĴ�����
					throwSyntaxError("ȱ���Ҵ�����\"}\"");
				}
			} else {
				// ���û�������ţ�Ҳ����˵��ֻ��һ�����
				elseNode.add(statement());
			}
		}

		return resultNode;
	}

	/**
	 * while���ķ���
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
			// ȱ�������ŵĳ�����
			throwSyntaxError("whileѭ������ȱ��������\"��\"");
		}

		// ��while���������ж�
		resultNode.add(condition());

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// ȱ�������жϵ�������
			throwSyntaxError("ѭ������ȱ��������\")\"");
		}

		// �Ҷ����while�������д����ŵĴ���
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
				// ȱ���Ҵ����ŵĳ�����
				throwSyntaxError("ȱ���Ҵ�����\"}\"");
			}
			if (statements.getChildCount() == 0) {
				resultNode.remove(resultNode.getChildCount() - 1);
				resultNode.add(new TreeNode("empty_statements", currentToken
						.getLinenumber()));
			}
		} else {
			// ȱ��������ŵĴ���
			throwSyntaxError("ѭ����ȱ���������\"{\"");
		}

		return resultNode;
	}

	/**
	 * read���ķ���
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
			// read���ȱ�������ŵĴ�����
			throwSyntaxError("read���ȱ��������\"(\"");
		}

		if (currentToken.tag == Tag.IDENTIFY) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken();
		} else {
			// �Ǳ�ʶ������
			throwSyntaxError("read���������ַǱ�ʶ��");
		}
		

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// ȱ�������Ŵ���
			throwSyntaxError("ȱ��������\")\"");
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// ȱ�ٷֺŴ���
			throwSyntaxError("ȱ�ٷֺ�");
			
		}

		return resultNode;
	}

	/**
	 * write���ķ���
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private TreeNode write_statement() throws SyntaxError {

		TreeNode resultNode = new TreeNode("write",
				currentToken.getLinenumber());
		nextToken(); // ����������
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
		} else {
			// read���ȱ�������ŵĴ�����
			throwSyntaxError(" write���ȱ��������\"(\"");
		}

		resultNode.add(expression());

		if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		} else {
			// ȱ�������Ŵ���
			throwSyntaxError("write���ȱ������\")\"");
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// ȱ�ٷֺŴ���
			throwSyntaxError("ȱ�ٷֺ�");
		}

		return resultNode;
	}

	
	
	/**
	 * �������﷨ʶ��
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode function_statement() throws SyntaxError {
		
		TreeNode resultNode  = new TreeNode("function_declare", currentToken.getLinenumber());
		//������ͽ��
		resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		nextToken();
		if (currentToken.tag == Tag.IDENTIFY) {
			//��Ӻ������ƽ��
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		}else {
			throwSyntaxError("����ĺ���������ȱ�ٺ�����ȷ�ĺ�����");
		}
		
		nextToken();
		if ( currentToken.tag != Tag.LEFTPARENTHESES) {
			throwSyntaxError("���������Ĳ����б�ȱ��������");
		}
		
		nextToken();
		//���濪ʼ��������б������
		
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
				throwSyntaxError("����Ĳ����б�");
			}
			
			
			typeNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
			nextToken();
			
			//�����Ǵ�����
			if (currentToken.tag == Tag.LEFTBRACE) {
				//Token token = tokens.get(index-1);
				typeNode.setContent(typeNode.getContent()+"_array");
				//typeNode.add(new TreeNode(token.getValue(), token.getLinenumber()));		
				nextToken();
				if (currentToken.tag != Tag.RIGHTBRACE) {
					throwSyntaxError("���������б���󣬳��ֵ�������������");
				}
				
				nextToken();
			}	
			
			 flag = true;
			 
			}while(currentToken.tag == Tag.COMMA);
			
			if (currentToken.tag != Tag.RIGHTPARENTHESES) {
				throwSyntaxError("�������������б�ȱ��������");
			}
			
			//��ʼ���뺯����ķ���
			nextToken();
	

		}else if (currentToken.tag == Tag.RIGHTPARENTHESES) {
			nextToken();
		}else {
			throwSyntaxError("�������������б����");
		}
		
		if (currentToken.tag != Tag.LEFTBRACKET) {
			throwSyntaxError("������ȱ��������");
		}
		
		nextToken();
		
		
		TreeNode statementNode = new TreeNode("statements", currentToken.getLinenumber());
		resultNode.add(statementNode);
		
	
		while (currentToken != null && currentToken.tag != Tag.RIGHTBRACKET) {
			statementNode.add(statement());
		}
		
		if (currentToken.tag != Tag.RIGHTBRACKET) {
			throwSyntaxError("������ȱ��������");
		}
		
		nextToken();
	
		return resultNode;
	}
	
	
	
	
	
	
	
	/**
	 * ���������﷨����
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode declare_statement() throws SyntaxError {

		// �Ƿ�����������
		boolean arrayflag = false;
		
		

		TreeNode resultNode = new TreeNode(currentToken.getValue(),
				currentToken.getLinenumber());

		do {
			nextToken(); // �Ŵ�λ����
			if (currentToken.tag == Tag.IDENTIFY) {
				resultNode.add(new TreeNode(currentToken.getValue(),
						currentToken.getLinenumber()));
				nextToken();

			} else {
				// ��ʶ��������
				throwSyntaxError("��ʶ������");
			}
		} while (currentToken.tag == Tag.COMMA);

		
		/***********************************************************************************************************/
		/*
		 * ���к����ķ���
		 */
		if (currentToken.tag == Tag.LEFTPARENTHESES) {
			//��������������Ļ�����ô��Ҫ��token���˵����͵Ĳ���
			index = index - 3;
			nextToken();
			resultNode = function_statement();
			return resultNode;
		}
		/*************************************************************************************************************/
		
		
		
		// ����ķ���
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
				throwSyntaxError("��������ȱ��������");
			} else {
				nextToken();
				arrayflag = true;
			}

		}

		// ����������ֵ����
		if (currentToken.tag == Tag.ASSIGN) {
			if (!arrayflag) {
				resultNode.add(new TreeNode(currentToken.getValue(),
						currentToken.getLinenumber()));
				nextToken();
				resultNode.add(expression()); //��ȥ���ӵ���
			} else {
				throwSyntaxError("�����޷�������ʱ��ֵ");
			}
		}

		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// �������ȱ�ٷֺ�
			System.out.println("����ȱ�ֺ�"+currentToken.getValue());
			throwSyntaxError("ȱ�ٷֺ�");
		}

		return resultNode;
	}

	/**
	 * ��ֵ�����﷨����
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
			//�����Ӧ���Ǻ���������
			index = index-2;
			nextToken();
			resultNode = function_call(true);
			return resultNode;
		}
		
		// ���鸳ֵ
		if (currentToken.tag == Tag.LEFTBRACE) {

			// �ı���ؽṹ
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
				throwSyntaxError("��������ȱ��������");
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
			// ȱ�ٸ�ֵ���ŵĴ�����
			throwSyntaxError("����ֵ���ȱ��\"=\"");
		}
		resultNode.add(expression()); // �п�����bool�����ĸ�ֵ
		if (currentToken.tag == Tag.SEMICOLON) {
			nextToken();
		} else {
			// ȱ�ٷֺŵĴ�����
			System.out.println("��ֵȱ�ֺ�"+currentToken.getValue());
			throwSyntaxError("ȱ�ٷֺ�");
		}

		return resultNode;
	}

	
	
	
	/**
	 * �������õ��﷨����
	 * @return
	 * @throws SyntaxError 
	 */
	private TreeNode function_call(boolean flag) throws SyntaxError {
		TreeNode resultNode = new TreeNode("function_call", currentToken.getLinenumber());
		resultNode.add(new TreeNode(currentToken.getValue(), currentToken.getLinenumber()));
		nextToken();
		//������Ϊ�����Ѿ��жϹ����������ţ�����ֱ������ִ��
		
		
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
			throwSyntaxError("��������ȱ��������");
		}
		
		nextToken();
		
		if(flag){
			if (currentToken.tag != Tag.SEMICOLON) {
				throwSyntaxError("�����������ȱ�ٷֺ�");
			}
		
			nextToken();
		}
		
		
		return resultNode;
	}

	/**
	 * �������ʽ���﷨�ж�
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode condition() throws SyntaxError {
		TreeNode resultNode = new TreeNode("condition",
				currentToken.getLinenumber());

		/*
		 * *******************************************
		 * ���ﻹ��Ҫ�޸ģ���Ҫ����������������ˣ�Ҳ������Ҫ�������� ��סһ��Ҫ�޸�Ŷ
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
			nextToken(); // �ӵ���һ��ʼ���˼ӣ����˺þ�
			resultNode.add(expression());
		}

		return resultNode;
	}

	/**
	 * ���ʽ�ķ���
	 * 
	 * @return TreeNode
	 * @throws SyntaxError
	 * 
	 */
	private TreeNode expression() throws SyntaxError {
		TreeNode resultNode = new TreeNode("expression",
				currentToken.getLinenumber());
		resultNode.add(term());
		// ����ͨ�����term���Ӷ���
		while (currentToken.tag == Tag.ADD || currentToken.tag == Tag.MINUS) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken(); // ����һ��ʼҲ���˼���
			resultNode.add(term());
		}
		return resultNode;
	}

	/**
	 * term��λ�ķ���
	 * @return TreeNode
	 * @throws SyntaxError
	 */
	private TreeNode term() throws SyntaxError {
		TreeNode resultNode = new TreeNode("term", currentToken.getLinenumber());
		resultNode.add(factor());
		// ���ܽ����factor���Ӷ���
		while (currentToken.tag == Tag.MUTIPLY
				|| currentToken.tag == Tag.DIVIDE) {
			resultNode.add(new TreeNode(currentToken.getValue(), currentToken
					.getLinenumber()));
			nextToken(); //��ȥ���Ҿ�Ȼ�����˼���
			resultNode.add(factor());
		}
		return resultNode;

	}

	/**
	 * ������ķ���
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
			 * �۹�������������Ӧ�÷�������ѽ
			 * �������õķ���
			 */
			
			if (currentToken.tag == Tag.LEFTPARENTHESES) {
				//�����Ӧ���Ǻ���������
				index = index-2;
				nextToken();
				resultNode.add(function_call(false));
				
			}else if (currentToken.tag == Tag.LEFTBRACE) {
				//����ķ���

				// �ı���ؽṹ
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
					throwSyntaxError("��������ȱ��������");
				} else {
					nextToken();
				}
				
			}else {
				resultNode.add(temp);
			}
			
			
			
		}  else if (currentToken.tag == Tag.LEFTPARENTHESES) {
			nextToken();
			
			//��������һ�����ͣ����������������
			TreeNode temNode = expression();
			temNode.setType("expression");
			
			
			resultNode.add(temNode);
			if (currentToken.tag == Tag.RIGHTPARENTHESES) {
				nextToken();
			} else {
				// ��������ȱ�������ŵĴ�����
				throwSyntaxError("��������ȱ��������\")\"");
			}
		} else {
			// �޷�ʶ����������Ӵ���
			throwSyntaxError("�޷�ʶ�����������");
		}

		return resultNode;
	}
}
