package semantic_analyzer;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JTextArea;



import parser.TreeNode;
import semantic_analyzer.FourElementFormula;
import semantic_analyzer.SymbolTable;

public class SemanticAnalyzer {

	// ������������������﷨��
	private TreeNode root;
	// �м�������Ԫʽ����
	private LinkedList<FourElementFormula> intermediateCode;
	// ���ű�ṹ�����ڽ����������
	private SymbolTable symbolTable;
	// ��Ҫһ����������������������жϺ��޸�������
	private int level = 0;
	//����һ����̬��������������Ԫʽ����ʱ����������
	private static int tempNum = 1;
	//����̨����
	private JTextArea consoleTextArea;
	//��¼������������
	private String currentReturnString;
	
	//����һ���ṹ�洢�ں�����֮����������ı��
	private ArrayList<Integer> declare_number;
	
	
	
	
	
	

	public SemanticAnalyzer(TreeNode root,JTextArea consoleTextArea) {
		this.root = root;
		this.consoleTextArea = consoleTextArea;

	}
	
	public ArrayList<Integer> getDeclare_Number(){
		return declare_number;
	}
	
	
	//����һ����ȡ��ʱ�������Ƶĺ���
	private String generateTempName(){
		String name = "#temp"+tempNum;
		tempNum++;
		return name;
		
	}
	
	//��ʱ����������
	private void resetTempName(){
		tempNum = 1;
	}
		
	
	/*
	 * ��ȡ��һ����Ԫʽ����ʱ����������
	 */
	
	private String getLastTempName(){
		return intermediateCode.get(intermediateCode.size()-1).getResult();
	}
	
	
	/**
	 * ��������
	 * @return
	 * 
	 * ��Ϊ������ײ������û������ķ���䣬��������Ҫ��������
	 * ��������������ת��ַ��ʱ��
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
		String errorString = "��"+linenumber+"�г���"+errorInfo;
		throw new SemanticError(errorString);
	}
	
	
	public LinkedList<FourElementFormula> generateCode() throws SemanticError {
		//consoleTextArea.append("��ʼ���ɴ���\n");
		
		intermediateCode = new LinkedList<FourElementFormula>();
		symbolTable = new SymbolTable();
		declare_number = new ArrayList<Integer>();
		
		

		analyze(root);
		
		//consoleTextArea.append("���������������ʼ�����м���룡\n");
		
		//������Ҫ���м�������һ���������û�����������Ԫʽ
		clearUseless();
		

		return intermediateCode;
	}

	
	/**
	 * �������������������Ҫ�������ݹ���ã������﷨����������ȱ���
	 * 
	 * @param root
	 * @throws SemanticError 
	 */
	private void analyze(TreeNode root) throws SemanticError {
		
		//consoleTextArea.append("��ʼ�����֧����\n");
		
		// forѭ������ȱ���
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeNode node = root.getChild(i);
			String content = node.getContent();
			if (content.equals("int") || content.equals("string")
					|| content.equals("bool") || content.equals("real")) {
				
				declare_statement(node);
				
				//consoleTextArea.append("����������������\n");

			} else if (content.equals("if_statement")) {
				// ����if���Ļ���������ı�
				level++;
				if_statement(node);
				level--;
				symbolTable.remove(level);

			} else if (content.equals("while_statement")) {
				level++;
				while_statement(node);
				level--;
				//ÿ���˳�һ���ֲ������򣬸��·��ű� ɾ���ֲ�����
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
				//symbolTable.remove(level); ���õģ����������������Ȼ��ë��,���Ǻ����������������ڵľֲ���������Ҫ�����
				
			}else if (content.equals("function_call")) {
				function_call(node);
			}else if (content.equals("return_statement")) {
				return_statement(node);
			}
			
		}
		
		resetTempName();
	}

	
	/**
	 * return���ķ���
	 * @param node
	 * @throws SemanticError 
	 */
	private void return_statement(TreeNode node) throws SemanticError {
		String returnType = expression(node.getChild(0));
		if ( !returnType.equals(currentReturnString)) {
			throwSemanticError(node.getLinenumber(), "�������Ͳ�һ��");
		}
		
		FourElementFormula formula = new FourElementFormula("=", getLastTempName(), "", "$return");
		intermediateCode.add(formula);
		
		FourElementFormula returnFormula = new FourElementFormula("return", "", "", "");
		intermediateCode.add(returnFormula);
		
	}


	
	/**
	 * ���������������������м��������
	 * @param node
	 * 
	 * �����ˣ���ʵ�ֺ�����ͬʱ���һ�Ҫʵ�ֺ��������أ��Ƚϵķ����Ļ����ҿ���ֱ����һ��parameterListType���ж����
	 * ��������ҪһЩ��Ķ���
	 * @throws SemanticError 
	 * 
	 */
	private String function_call(TreeNode node) throws SemanticError {
		
		//���Ȼ�ȡ��������
		String functionName = node.getChild(0).getContent();
		String parameterListType = "";
		
	
		
		
		for(int i=0;i<node.getChild(1).getChildCount();i++){
			String type = "";
			
			//SymbolElement element = symbolTable.getPossibleElement(node.getChild(1).getContent(), level);
			
			SymbolElement tempElement = symbolTable.getPossibleElement(node.getChild(1).getChild(i).getChild(0).getChild(0).getChild(0).getContent(), level);
			
			/*
			 *�۹��������ñ���ү�����ͽ��Ͱ� 
			 * �����淢����ʲô��Ϊ��ʶ����������������Ҫ������expression�ķ֣���Ϊ��expression�ķ������޷��õ��������ͣ�
			 * ����������ֱ�ӵ���ֱ�ӷ���һ�£��ǲ����������ͣ�������ǵĻ�����ȥ��expression�ķ�����
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
			
			
			
			
			//�����ͻ���˵��ú���ʱ�Ĳ�������
			parameterListType+=type;
		}
		
		SymbolElement element = symbolTable.getFunction(functionName, parameterListType);
		
		if (element == null) {
			//consoleTextArea.append(parameterListType);
			throwSemanticError(node.getChild(0).getLinenumber(), "����δ����������������");
		}
		
//		for (int i = 0; i < node.getChild(1).getChildCount(); i++) {
//			//���������Ѿ��жϹ���
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
	 * ���������������������м��������
	 * @param node
	 * @throws SemanticError 
	 */
	private void function_declare(TreeNode node) throws SemanticError {
		
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		int startPosition = intermediateCode.size();
	
		
		
		String functionName = node.getChild(1).getContent();
		String returnType = node.getChild(0).getContent();
		
		
		/*
		 * ���������main�������ж�
		 */
		
		if (functionName.equals("main")) {
			if ( !returnType.equals("void")) {
				throwSemanticError(node.getChild(1).getLinenumber(), "main������������Ӧ��Ϊ��");
			}
			
			if (node.getChild(2).getChildCount() > 0) {
				throwSemanticError(node.getChild(1).getLinenumber(), "main�����Ĳ����б�Ӧ��Ϊ��");
				
			}
			
		}
		

		currentReturnString = returnType;
		
		//�洢�����б����ʹ�
		String parameterListType = "";
		//��ȡ�����б�����
		for (int i = 0; i < node.getChild(2).getChildCount(); i++) {
			parameterListType += node.getChild(2).getChild(i).getContent();
		}
		
		if (symbolTable.getFunction(functionName, parameterListType) != null) {
			throwSemanticError(node.getChild(1).getLinenumber(), "�����ض������");
		}
		
		
		/*
		 * ��������ӵ����ű�ͬʱ��¼�������ͣ�λ����Ϣ������������Ԫʽ���м�����еĿ�ʼλ��
		 */
		SymbolElement element1 = new SymbolElement(functionName, returnType, level);
		element1.setParameterListTypeString(parameterListType);
		element1.setStartPosition(startPosition);
		symbolTable.add(element1);
		
		
		if(functionName.equals("main") && returnType.equals("void")){
		FourElementFormula fourFormula = new FourElementFormula(returnType, "", "", functionName);
		intermediateCode.add(fourFormula);
		}
		
		/******************************������Ӻ�����������Ԫʽ*****************************/
		
		
		/*************************************************************************/
		
		
		
		//����Ļ���Ҫ��Ϊ������ֲ�����
		level ++;
		
		/*
		 * Ȼ������漰�������������Ԫʽ��ʽ�ˣ�����Ҳ���Ƕ��ĸ�����Ĳ�������ʵ���ĸ���ֵ�������
		 * ��Ȼ���ֵ������ȡ�ͼ��ʼ����ˣ����ǵ���ֵ��ȡֵ���ص�Ļ�������ִ�е�ʱ��ῼ�����ö���
		 * ����Ϊ�������úͺ�������֮��Ĳ������ݡ���ô�������ɵ���Ԫʽ�Ļ��Ϳ�����һ������ı������ʾ��
		 * 
		 */
		for (int i = 0; i < node.getChild(2).getChildCount(); i++) {
			SymbolElement element = symbolTable.getElement(node.getChild(2).getChild(i).getChild(0).getContent(), level);
			if (element != null) {
				throwSemanticError(node.getLinenumber(), "���������б�������ظ�");
			}
			
			//�������Ĳ����б��еı����ӽ����ű�
			symbolTable.add(new SymbolElement(node.getChild(2).getChild(i).getChild(0).getContent(), node.getChild(2).getChild(i).getContent(), level));
			
			//���ɺ��������б����Ԫʽ
			FourElementFormula formula = new FourElementFormula(node.getChild(2).getChild(i).getContent(), node.getChild(2).getChild(i).getChild(0).getContent(), "$queue", "");
			intermediateCode.add(formula);
		}
		
		
		analyze(node.getChild(3));
		
		
		level--;
		symbolTable.remove(level);
		
		
		if (functionName.equals("main")) {
			intermediateCode.add(new FourElementFormula("end", "", "", ""));
		}else{
		//out���������תλ��������ʱ����
		intermediateCode.add(new FourElementFormula("out", "", "", "$stack"));
		}
		
		/*
		 *ɨ��һ��֮ǰ����䣬�����е�return�ĵ�ַ��Ϊout��λ�� 
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
				throwSemanticError(node.getChild(1).getLinenumber(), "ȱ��return���");
		}else if (returnType.equals("void") && countOfReturn > 0) {
				throwSemanticError(node.getChild(1).getLinenumber(), "�޷��ز���������Ҫreturn���");
		}
		
		
		
	}


	/**
	 * �������������м��������
	 * @param node
	 * @throws SemanticError
	 * 
	 * �Ҷ������������У��������������ͬʱ�����������Ļ����ǲ��ܽ��и�ֵ�ġ����Ǻ��������ȵ�
	 * ��ȡ����������������һ�������������Ļ�����ô������һ��ѭ����֮ǰ�ķ���ȫ��������ű���
	 * ������ļ��㲻�����鲢�ҽ��������һ���Ļ�����ô��������ǲ��Ƕ���������������������Ļ��ٷ���
	 * ��û�и�ֵ��䡣��Ϊ��ֵ��䲻�ǵ�������ӽ����ű�������Ҫ����ķ��������Է������
	 * 
	 * 
	 */
	private void declare_statement(TreeNode node) throws SemanticError {
		//consoleTextArea.append("��ʼ��������������\n");
		
		
		//���Ȼ�ȡ�������Ľ�����
		int count = node.getChildCount();
		//���������һ���ǲ��������㣬����ǵĻ�����ô�Ͳ�����ָ�ֵ���
		TreeNode lasTreeNode = node.getChild(count-1);
		
		if (lasTreeNode.getContent().equals("#array")) {
			
			//consoleTextArea.append("����������������\n");
			
			//���������������������
			for(int i=0;i<count;i++){
				
				
				if(i == count-1){
					/*
					 * ����Ĵ����������size�Ĵ���Ҳ����˵����Ҫ����expression
					 */
					
					SymbolElement array_element = symbolTable.getElement(node.getChild(i).getChild(0).getContent(), level);
					
		
					
					if(array_element != null){
						throwSemanticError(node.getChild(i).getLinenumber(), "�ظ���������:"+node.getChild(i).getChild(0).getContent());
					}
				
					String sizeType = expression(node.getChild(i).getChild(1));
					if ( !sizeType.equals("int")) {
						throwSemanticError(node.getChild(i).getChild(1).getLinenumber(), "����Ĵ�С���������������");
					}
					
					/*
					 * ������Ҫע���ˣ�������Ļ�����Ҫ������Ĵ�С
					 * �ðɣ���������ν����Ϊ����Ĵ�СҪ����ִ��ʱ��֪��
					 */
					symbolTable.add(new SymbolElement(node.getChild(i).getChild(0).getContent(), node.getContent()+"_array", level));
										
					
					FourElementFormula formula = new FourElementFormula(node.getContent()+"_array", node.getChild(i).getChild(0).getContent(), getLastTempName(), "");
					intermediateCode.add(formula);
					
					//����Ҫ����main����ִ�е������ı�������Ŵ洢����
					clearUseless();
					if (level == 0) {
						declare_number.add(intermediateCode.size());
					}
					
					
				}else{
					
					SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
					if(element != null){
						throwSemanticError(node.getChild(i).getLinenumber(), "�ظ���������:"+node.getChild(i).getContent());
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
			
			//consoleTextArea.append("����ʱ��ֵ���ķ�����\n");
			
			/*
			 * ����ʱ��ֵ���ķ���
			 */
			
			String sizeType = expression(lasTreeNode);
			if(node.getContent().equals("int") && sizeType.equals("real")){
				throwSemanticError(node.getLinenumber(), "���ܽ�real���͵�ֵ����int���͵ı���");
			}
			
			String lastTempName = getLastTempName();
			
			for(int i=0;i<count-2;i++){
				SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
				if(element != null){
					throwSemanticError(node.getChild(i).getLinenumber(), "�ظ���������:"+node.getChild(i).getContent());
				}
				symbolTable.add(new SymbolElement(node.getChild(i).getContent(), node.getContent(), level));
				FourElementFormula formula = new FourElementFormula(node.getContent(), node.getChild(i).getContent(), lastTempName, "");
				intermediateCode.add(formula);
				
				
				clearUseless();
				if (level == 0) {
					declare_number.add(intermediateCode.size());
				}
				
				
			}		
			
			//������ʱ����
			resetTempName();
			
		}else{
			//consoleTextArea.append("��ͨ�������������������\n");
			
			/*
			 * ��ͨ���������������û��������ʱ���и�ֵ
			 */
			for(int i=0;i<count;i++){
				SymbolElement element = symbolTable.getElement(node.getChild(i).getContent(), level);
				if(element != null){
					throwSemanticError(node.getChild(i).getLinenumber(), "�ظ���������:"+node.getChild(i).getContent());
				}
				
				symbolTable.add(new SymbolElement(node.getChild(i).getContent(), node.getContent(), level));
				
				//consoleTextArea.append("��ͨ��������м��������\n");
				
				FourElementFormula formula = new FourElementFormula(node.getContent(), node.getChild(i).getContent(), "", "");
				intermediateCode.add(formula);
				
				
				clearUseless();
				if (level == 0) {
					declare_number.add(intermediateCode.size());
				}
			}	
			
			
			//����
			
			
			
		}
		
			
		
		
	}

	
	/**
	 * ��ֵ�������������м��������
	 * @param node
	 * 
	 * �������д�ĺ���������
	 * �ţ��ǱȽϼ򵥵ģ���Ҫ����������������ǣ���ֵ���͵����⣬ ���ǣ�ʵ���ϰɣ��㿴����������ֵ��ʱ����ʵ
	 * �Ѿ����������Ĵ����ˣ������������Ƕ���һ������ĸ�ֵ������
	 * @throws SemanticError 
	 */
	private void assign_statement(TreeNode node) throws SemanticError {
		//consoleTextArea.append("��ʼ��ֵ����������");
		
		
		if ( !node.getChild(0).getContent().equals("#array")) {
			
			
		//consoleTextArea.append("��������з�������"+level);
			
		//��ͨ�����ĸ�ֵ
		SymbolElement element = symbolTable.getPossibleElement(node.getChild(0).getContent(), level);
		
		
		if (element == null) {
			throwSemanticError(node.getLinenumber(), "δ�����ı���"+node.getChild(0).getContent());
		}
		
		//�����Ҳ�ı��ʽ������ȡ��������
		String sizeType = expression(node.getChild(2));
		if(element.getType().equals("int") && sizeType.equals("real")){
			throwSemanticError(node.getLinenumber(), "���ܽ�real���͵�ֵ����int���͵ı���");
		}
		
		FourElementFormula  formula = new FourElementFormula("=", getLastTempName(),"",element.getName());
		intermediateCode.add(formula);
		
//		//������ʱ��������
//		resetTempName();
		
	}else {
		
		//consoleTextArea.append("����������ǣ�" + node.getChild(0).getChild(0).getContent());
		
		//����ĸ�ֵ
		SymbolElement element = symbolTable.getPossibleElement(node.getChild(0).getChild(0).getContent(), level);
		if (element == null) {
			throwSemanticError(node.getLinenumber(), "δ�������������");
		}
		
		/*
		 * �����Ҳ�ı��ʽ������ȡ��������
		 * ��Ȼ�����������ԵĻ�������һ���±�Խ������⣬����ִ�й�������Ҫ���ǵ����⣬�����������Ͳ�д��
		 */
		String type = expression(node.getChild(2));
		String temp1 = getLastTempName();
		
		if (element.getType().equals("int_array") && type.equals("real")) {
			throwSemanticError(node.getLinenumber(), "���ܽ�real���͵ı�����ֵΪint���͵�����");
		}
		
		String type2 = expression(node.getChild(0).getChild(1));
		String temp2 = getLastTempName();
		if ( !type2.equals("int")) {
			throwSemanticError(node.getLinenumber(), "�����±����������");
		}
		
		FourElementFormula formula = new FourElementFormula("=", temp1, "", element.getName()+"["+temp2+"]");
		intermediateCode.add(formula);
		
		//������ʱ����
		resetTempName();
	}
		
	}
		

/******************************************************************************************************/	
	

	/**
	 * write������
	 * @param node
	 * @throws SemanticError 
	 *  write������������д���ʽ��������Ҫһ����ʱ�������νӡ�ͬʱ�������������ŵ�expression�����������
	 */
	private void write_statement(TreeNode node) throws SemanticError {
		
		//�����Ǳ��ʽ�ķ���
		TreeNode root = node.getChild(0);
		expression(root);

		FourElementFormula formula = new FourElementFormula("write", "", "", getLastTempName());
		intermediateCode.add(formula);
	}

	
	/**
	 * read����������м��������
	 * @param node
	 * @throws SemanticError
	 * 
	 * ע�������ȡ���ݵ������ж��������û����ɣ��õ�����ִ�еĹ�����
	 */
	private void read_statement(TreeNode node) throws SemanticError {
		TreeNode valNode = node.getChild(0);
		String valname = valNode.getContent();
		
		SymbolElement element = symbolTable.getPossibleElement(valname, level);
		
		if(element == null){
			//����Ϊ����
			throwSemanticError(node.getLinenumber(), "δ�����ı���");
		}else{
			//��ɶ����Ϊ���Ҫ���ⲿ��ȡֵ���������������ж����ﲻ��ʵ��
			FourElementFormula formula = new FourElementFormula("read", "", "", element.getName());
			intermediateCode.add(formula);
		}
	}

	
	/**
	 * for����м��������
	 * @param node
	 * @throws SemanticError
	 */
	private void for_statement(TreeNode node) throws SemanticError {
		
		//�Ӹ���ʶ�������飬�����м����ִ��
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		
		declare_statement(node.getChild(0).getChild(0));
		clearUseless();
		int startposition = intermediateCode.size()+1;
		
		condition(node.getChild(1).getChild(0)); 
		
		FourElementFormula jump1 = new FourElementFormula("jmp",getLastTempName() , "", "");
		intermediateCode.add(jump1);
		//��������ķ������������ټ�һ��
		level++;
		analyze(node.getChild(3));
		level--;
		//��������ÿ��ѭ������֮������㡣
		assign_statement(node.getChild(2).getChild(0));
		intermediateCode.add(new FourElementFormula("jmp", "", "", startposition+""));
		clearUseless();
		jump1.setResult((intermediateCode.size()+1)+"");
		
		intermediateCode.add(new FourElementFormula("out", "", "", ""));
		
	}

		
	/**
	 * while����м��������
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
	 * if����м��������
	 * @param node
	 * @throws SemanticError
	 */
	private void if_statement(TreeNode node) throws SemanticError {
		
		intermediateCode.add(new FourElementFormula("in", "", "", ""));
		
		//��̫����±���������⣬���Ծ������ӽڵ������������Ƿ���else�ڵ�
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
	 * condition����������������м��������
	 * @param node
	 * @throws SemanticError
	 * 
	 * �Ҷ����condition�����������ӽ�㣬expression,�ȽϷ���expression
	 * expression���﷨��������Ҫ�����ߵ�����Ҫһ��
	 */
	private void condition(TreeNode node) throws SemanticError {
		//�Ҷ����condition���﷨��������expression,�ȽϷ���,expression�������
		//���ȷ�����һ��expression��㣬����ȡ�������������
		String type1 = expression(node.getChild(0));
		String temp1 = getLastTempName();
		String type2 = expression(node.getChild(2));
		String temp2 = getLastTempName();
//		if(!type1.equals(type2)){
//			throwSemanticError(node.getLinenumber(), "�Ƚ��������߱������Ͳ�һ��");
//		}
		
		//jump������ݴ洢�ȽϽ������ʱ������ֵ���ж���ת���
		FourElementFormula formula = new FourElementFormula(node.getChild(1).getContent(), temp1, temp2, generateTempName());
		intermediateCode.add(formula);
	
	}

	
	/**
	 * ���ʽ�ķ���
	 * @param node
	 * @return ���ʽ�������������
	 * @throws SemanticError
	 */
	private String expression(TreeNode node) throws SemanticError {
		
		//consoleTextArea.append("���ʽ������ʼ��\n");
		
		int count = node.getChildCount();
		
		//��Ϊֻ�������������ͣ�����Ĭ���������int,���������һ��real,��ô���Ľ������real
		String expressionType = "int";
		String type1 = term(node.getChild(0));
		
		//consoleTextArea.append("term��������\n");
		
		if(type1.equals("real")){
			expressionType = "real";
		}
		
		/*
		 * ���������һ������ʽ�Ļ�����ʵҲ����Ҫ������д��䣬��ά��һ��ĺ�������������ɵ�
		 */
		for(int i=2;i<count;i=i+2){
			String temp1 = getLastTempName();
			String type = term(node.getChild(i));
			
			//����ڷ�����Щ���Ĺ����У����������һ��real���ͱ�������ô���Ľ������real���͵�
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
	 * term��Ԫ������
	 * @param node
	 * @return ���﷨��Ԫ������������
	 * @throws SemanticError
	 * 
	 *����֮ǰ�õķ����������鷳������˼·����֮��һ���ӱ�ü��˺ܶ࣬����Ļ����������Ǻ�expression����ķ�����һ�µ�
	 *����ԣ�Ӧ��˵����һģһ���ġ�
	 *��ײ�ķ����Ļ���Ҫ����factor
	 */
	 
	private String term(TreeNode node) throws SemanticError {
		//consoleTextArea.append("��ʼterm�ķ���\n");
		
		//���Ȼ�ȡ������㵥Ԫ������
		int count = node.getChildCount();
		//Ȼ����Ǻúÿ�����ô����֯һ�����ͷ����ˣ�����ý��factor������
		
		String termType = "int";
		String type1 = factor(node.getChild(0));
		
		//consoleTextArea.append("factor��������\n");
		if(type1.equals("real")){
			termType = "real";
		}
		
		
		for(int i=2;i<count;i = i+2){
			String temp1 = getLastTempName();
			String type = factor(node.getChild(i));
			
			//����ڷ�����Щ���Ĺ����У����������һ��real���ͱ�������ô���Ľ������real���͵�
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
	 * factor�ڵ�ķ���
	 * @param child
	 * @return factor��������
	 * @throws SemanticError 
	 */
	private String factor(TreeNode root) throws SemanticError {
		
		//consoleTextArea.append("��ʼfactor�ķ�����\n");
		/*
		 * ������ײ��ˣ������Ԫ�ص��жϣ���ʵ����֮ǰ���﷨�����й��ˣ������������factor������涨����kind����
		 * �����Ļ����������ھ����׷����ܶ��ˣ���ʵ������û�ж���������ԣ���Ҳ��ȫ������factor����һ������һ������
		 * ��㡣��ʵ���﷨������ʱ������ȫ�������ж����͵ģ��Ϳ����﷨����������û�б���������Ϣ��
		 */
			
		root = root.getChild(0);
		
		//�Ҿ�֪���ǽ���λ��
		//consoleTextArea.append("factor�ڵ�����ͣ�"+root.getContent());
		
         if (root.getContent().equals("function_call")) {
			
			//�������õķ���
			
			String type = function_call(root);
			FourElementFormula formula = new FourElementFormula("", "", "", getLastTempName());
			intermediateCode.add(formula);
			return type;
			
		}else if(root.getType().equals("int")){
			//consoleTextArea.append("����int���ͻ���������Ԫ�ķ�����\n");
			
			String type = "int";
			//ע�⣬Ϊ����һ����ȡ���˸����Ƶķ��㣬���������һ��û��command���Ե���Ԫʽ
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
				throwSemanticError(root.getLinenumber(), "δ�����ı���");
			}
			String type = element.getType();
			FourElementFormula formula = new FourElementFormula("", "", "", element.getName());
			intermediateCode.add(formula);
			return type;
			
		}else if(root.getType().equals("array")){
			String type = "";
			//�������Ƶ��������Ե��ж϶���,������Ҫ����ʱ����֪�����������ﵥ�������м����
			SymbolElement element = symbolTable.getPossibleElement(root.getChild(0).getContent(), level);
			if (element == null) {
				throwSemanticError(root.getLinenumber(), "δ����������");
			}
			
			
			//�����ж�
			String temptype = element.getType();
			if (temptype.equals("int_array")) {
				type = "int";
			}else{
				type = "real";
			}
			
			
			String indextype = expression(root.getChild(1));
			if(indextype.equals("real")){
				throwSemanticError(root.getLinenumber(), "�����±겻������");
			}
			
			//�����м�����ʽ
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
