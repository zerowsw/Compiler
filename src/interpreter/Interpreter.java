package interpreter;

import java.util.ArrayList;
import java.util.LinkedList;





import java.util.Stack;

import javax.swing.JTextArea;

import semantic_analyzer.FourElementFormula;
import semantic_analyzer.SymbolElement;
import semantic_analyzer.SymbolTable;

/**
 * �����м�������
 * @author wsw
 * 
 * ����ִ�������������֮�����ɵ��м����
 * �м�������Ԫʽ������commandλ�õ�������������£�
 * 		int��real��int_array,real_array, +��*��-��/��=,<,>,<=,>=,<>,==,jmp,read,write,in,out��end ,call ,return ,void 
 *
 */

public class Interpreter implements Runnable{
	//�м��������
	private LinkedList<FourElementFormula> intermediateCode;
	//���ű��������ִ�й����У�������ֵ��������ʱ����
	private SymbolTable symbolTable;
	//������
	private int level = 0;
//	//����������
//	private double factorValue;
	/*
	 * ��������Ҫһ��ȫ�ֱ����ˣ��ͻ�ȡ�û������ʱ��������������̻�������Ȼ����Ҫһ��������ȡ�û�
	 * �����룬��ȡ���û�����֮��ỽ�ѽ��ͽ��̡�Ȼ��������Ҫ�Ի�ȡ�����ݽ���һ�£��жϡ�����������
	 * ��Ҫ����һ����������������޷�ʵ����Ϣ�Ĵ��ݣ�����������Ҫһ��ȫ�ֵĹ�����Դ
	 */
	private String input;
	
	private String consoleAreaTextCurrentContentString;
	
	
	private JTextArea consoleTextArea;
	
	/*
	 * ����������ں��������Ĵ�ֵ
	 * 
	 * addLast()
	 * removeFirst()
	 */
	private LinkedList<String> queue;
	
	//��ջ���ڴ洢�������ص�ַ
	private Stack<Integer> stack;
	
	//���ڴ洢�����ķ���ֵ��ʵʱ����
	private Double returnValue;
	
	//�洢��ִ�е��������ı��
	private ArrayList<Integer> declare_number;
	
	
	
	
	
	
	
	public Interpreter(LinkedList<FourElementFormula> intermediateCode,JTextArea consoleTextArea,ArrayList<Integer> declare_number){
		this.intermediateCode = intermediateCode;
		symbolTable = new SymbolTable();	
		this.consoleTextArea = consoleTextArea;
		stack = new Stack<Integer>();
		queue = new LinkedList<String>();
		this.declare_number = declare_number;
		
		
	}
	
	/**
	 * �ж��ַ����Ƿ���int���ͱ���
	 * @param str
	 * @return
	 */
	public static boolean isInt(String str){
		for (int i = 0; i < str.length(); i++){
		   System.out.println(str.charAt(i));
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		}
		return true;
		}
	
	
	public String getCurrentText(){
		return consoleAreaTextCurrentContentString;
	}
	
	
	public void run(){
		
		int count = intermediateCode.size();
		
		int start = 0;
		//int end = 0;
		int amount = 0;
		
		//����Ҫ�õ�������ʼִ�е�λ��
		
		for(int i=0;i<count ; i++){
			FourElementFormula currentFormula = intermediateCode.get(i);
			String command = currentFormula.getCommand();
			String address = currentFormula.getResult();
			if (command.equals("void") &&  address.equals("main")) {
				start = i+1;
				amount++;
			}
		}
		
		
		/*
		 * ��main������ڿ��ܳ��ֵ�һЩ������ж�
		 */
		if (amount > 1) {
			try {
				throw new ExecuteError("ExecuteError: "+"�����ж���һ�����");
			} catch (ExecuteError e) {
				// TODO Auto-generated catch block
				consoleTextArea.setText(e.getMessage());
				return;
			}
		}
		if (start == 0) {
			
			try {
				throw new ExecuteError("ExecuteError: "+"û���ҵ�main����");
			} catch (ExecuteError e) {
				// TODO Auto-generated catch block
				consoleTextArea.setText(e.getMessage());
				return;
			}
			
		}
		
		
		/*
		 * ����������main������ȫ�ֵı����������������Ҫ��main����֮ǰ����
		 */
		
		for(Integer integer : declare_number){
			
		    //consoleTextArea.append("Ŀǰ��integerΪ�� "+integer+"\n");
			FourElementFormula currentFormula = intermediateCode.get(integer-1);
			String command = currentFormula.getCommand();
			String parameter1 = currentFormula.getParameter1();
			String parameter2 = currentFormula.getParameter2();
			String address = currentFormula.getResult();
			
			try{
			if(command.equals("int")){
				//consoleTextArea.append("��ʼint ��������\n");
				
				if (parameter2.equals("$queue")) {
					double value = Double.parseDouble(queue.removeFirst());
					SymbolElement element = new SymbolElement(parameter1, "int", level);
					element.setValue(value);
					symbolTable.add(element);
									
				}else{
					command_Int(parameter1, parameter2);
					
				}
			}else if (command.equals("real")) {
				if (parameter2.equals("$queue")) {
					double value = Double.parseDouble(queue.removeFirst());
					SymbolElement element = new SymbolElement(parameter1, "real", level);
					element.setValue(value);
					symbolTable.add(element);
						
				}else{
				
				command_Real(parameter1, parameter2);
				
				}
				
				
			}else if(command.equals("int_array") || command.equals("real_array")){
				if (parameter2.equals("$queue")) {
					
					SymbolElement element = symbolTable.getPossibleElement(queue.removeFirst(), level);
					SymbolElement newElement = new SymbolElement(parameter1, command, level, element.getLength());
					if (command.equals("int_array")) {
						newElement.setInt_array(element.getInt_array());
					}else {
						newElement.setReal_array(element.getReal_array());
					}
					
					symbolTable.add(newElement);
					
					
				}else{
					if (isInt(parameter2)) {
						SymbolElement element = new SymbolElement(parameter1, command, level,Integer.parseInt(parameter2));
						symbolTable.add(element);
					}else {
						SymbolElement element = new SymbolElement(parameter1, command, level,(int)symbolTable.getPossibleElement(parameter2, level).getValue());
						symbolTable.add(element);
					}	
				
				}
				
				
			}
			}catch(ExecuteError e){
				consoleTextArea.setText(e.getMessage());
			}
					
		}
		
		
		
		
	
		for (int i = start; i < count; i++) {
			
			
			FourElementFormula currentFormula = intermediateCode.get(i);
			String command = currentFormula.getCommand();
			String parameter1 = currentFormula.getParameter1();
			String parameter2 = currentFormula.getParameter2();
			String address = currentFormula.getResult();
			
			//consoleTextArea.append(currentFormula+"\n");
			
			try {
				
			
			if(command.equals("int")){
				
				
				if (parameter2.equals("$queue")) {
					double value = Double.parseDouble(queue.removeFirst());
					SymbolElement element = new SymbolElement(parameter1, "int", level);
					element.setValue(value);
					symbolTable.add(element);
									
				}else if(parameter2.equals("$return")){
					SymbolElement element = new SymbolElement(parameter1, "int", level);
					element.setValue(returnValue);
					symbolTable.add(element);
					
					
					
				}else{
					command_Int(parameter1, parameter2);
					
				}
			}else if (command.equals("real")) {
				if (parameter2.equals("$queue")) {
					double value = Double.parseDouble(queue.removeFirst());
					SymbolElement element = new SymbolElement(parameter1, "real", level);
					element.setValue(value);
					symbolTable.add(element);
					
					//consoleTextArea.append("�ɹ����"+parameter1+"�����ű�,Ŀǰ��levelΪ��"+level+"\n");
						
				}else if(parameter2.equals("$return")){
					
					SymbolElement element = new SymbolElement(parameter1, "real", level);
					element.setValue(returnValue);
					symbolTable.add(element);
					
				}else{
				
				command_Real(parameter1, parameter2);
				
				}
				
				
			}else if(command.equals("int_array") || command.equals("real_array")){
				if (parameter2.equals("$queue")) {
					
					SymbolElement element = symbolTable.getPossibleElement(queue.removeFirst(), level);
					SymbolElement newElement = new SymbolElement(parameter1, command, level, element.getLength());
					if (command.equals("int_array")) {
						newElement.setInt_array(element.getInt_array());
					}else {
						newElement.setReal_array(element.getReal_array());
					}
					
					symbolTable.add(newElement);
					
					
				}else{
					if (isInt(parameter2)) {
						SymbolElement element = new SymbolElement(parameter1, command, level,Integer.parseInt(parameter2));
						symbolTable.add(element);
					}else {
						SymbolElement element = new SymbolElement(parameter1, command, level,(int)symbolTable.getPossibleElement(parameter2, level).getValue());
						symbolTable.add(element);
					}	
				
				}
				
				
			}else if (command.equals("+")) {
				
				//���õģ�����÷���������������ô���¶�
				
				double value1 = getValueofTerm(parameter1);
				
				double value2= getValueofTerm(parameter2);
				
				//consoleTextArea.append("�ɹ��������ӷ�:  "+value1+"    "+value2);
					
				double value3 = value1+value2;
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					
					//consoleTextArea.append("��ʼ������ʱ����"+address+"\n");
					
					SymbolElement element2 = new SymbolElement(address, "real", level);
					element2.setValue(value3);
					symbolTable.add(element2);
					
			    }else{
					//element.setType("int");
					element.setValue(value3);
				}
										
			}else if(command.equals("-")){
				
				
				double value1 = getValueofTerm(parameter1);
				
				double value2= getValueofTerm(parameter2);
					
				double value3 = value1-value2;
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					element2.setValue(value3);
					symbolTable.add(element2);
			    }else{
					//element.setType("int");
					element.setValue(value3);
				}
				
			}else if (command.equals("*")) {
				
				

				double value1 = getValueofTerm(parameter1);
				
				double value2= getValueofTerm(parameter2);
					
				double value3 = value1*value2;
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					element2.setValue(value3);
					symbolTable.add(element2);
			    }else{
					//element.setType("int");
					element.setValue(value3);
				}
					
				
			}else if (command.equals("/")) {
				
				double value1 = getValueofTerm(parameter1);
				
				double value2= getValueofTerm(parameter2);
					
				double value3 = value1/value2;
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					element2.setValue(value3);
					symbolTable.add(element2);
			    }else{
					element.setValue(value3);
				}
				
			}else if(command.equals("=")){
				/*
				 * �����������������ȣ���ֵ�Ļ������Ѿ�����Ҫ�������͵������ˣ���Ϊ�������������ʱ���Ѿ�������
				 * Ҳ����˵���ﲻ��Ҫ�����ж������Ƿ���ȷ��������Ҫȷ��������һЩ�ظ��Ĺ���
				 * Ȼ����ǣ���ȥ�����õģ���ʱ������ʲô���Ͱ����԰�����ʱ�������ù����͵�ѽ������ʱ����ֻ����һ������ֵ
				 * �����ã����ͷ�����֮ǰ�Ѿ������ˡ�
				 * 
				 */
				//consoleTextArea.append("���븳ֵ���,addressֵΪ��"+address);
				
				if(address.equals("$queue")){
					//consoleTextArea.append("�ɹ����뵽��ַΪ$queue�ķ�֧\n");
					
					//����Ǻ������ݹ������漰���������ݵĸ�ֵ 
					SymbolElement element = symbolTable.getPossibleElement(parameter1, level);
					
					
					if ((element != null) && (element.getType().equals("int_array") || element.getType().equals("real_array"))) {
						//��˵���������õ�ʱ�����������
						queue.add(parameter1);
					}else{
						//���������Ļ����ǿ��Ի��ֵ��
						
						//consoleTextArea.append("��ʼ��queue�����������\n");
						double value = getValueofTerm(parameter1);
						//consoleTextArea.append("value��ֵΪ��" +value);
						
						queue.add(value+"");
						
						
						//consoleTextArea.append("�ɹ���ӽ�����"+queue.removeFirst()+"\n");
					}
								
				}else if(address.equals("$return")){
					double value = getValueofTerm(parameter1);
					returnValue = value;
						
					//consoleTextArea.append("returnValue����ֵΪ��"+value);
				}else if(parameter1.equals("$return")){
					
					//�������ķ��ؽ��������
					double value = returnValue;
					
					if (address.contains("[")) {
						//consoleTextArea.append("��ֵ����ɹ�ʶ��Ϊ����");
				
						//String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
						String array_name = address.substring(0, address.indexOf("["));
						SymbolElement element = symbolTable.getPossibleElement(array_name, level);
				
						//consoleTextArea.append("��ֵ���������"+element.getType()+"\n");
				
						if (element.getType().equals("int_array")) {
							setIntArrayValue(address, value);
						}else {
							setRealArrayValue(address, value);
						}		
					}else{
						SymbolElement element = symbolTable.getPossibleElement(address, level);
						element.setValue(value);
					}
								
					
				}else{
					
					 /*
					  * ����ĸ�ֵ���
					  */
						double value = getValueofTerm(parameter1);
					
						if (address.contains("[")) {
							//consoleTextArea.append("��ֵ����ɹ�ʶ��Ϊ����");
					
							//String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
							String array_name = address.substring(0, address.indexOf("["));
							SymbolElement element = symbolTable.getPossibleElement(array_name, level);
					
							//consoleTextArea.append("��ֵ���������"+element.getType()+"\n");
					
							if (element.getType().equals("int_array")) {
								setIntArrayValue(address, value);
							}else {
								setRealArrayValue(address, value);
							}		
						}else{
							SymbolElement element = symbolTable.getPossibleElement(address, level);
							element.setValue(value);
						}
			  }
					
			}else if (command.equals("<")) {
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				double value3 = value2 - value1;
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(value3>0.000001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (value3>0.000001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
				
			}else if (command.equals("<=")) {
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(value2>value1-0.000001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (value2>value1-0.000001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
				
			}else if (command.equals(">")) {
				
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				double value3 = value1 - value2;
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(value3>0.00001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (value3>0.00001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
				
				
			}else if (command.equals(">=")) {
				
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(value1>value2-0.000001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (value1>value2-0.000001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
				
			}else if (command.equals("<>")) {
				
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				double value3 = value2 - value1;
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(Math.abs(value3)>0.000001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (Math.abs(value3)>0.000001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
			}else if(command.equals("==")){
				
				double value1 = getValueofTerm(parameter1);
				double value2 = getValueofTerm(parameter2);
				double value3 = value2 - value1;
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					SymbolElement element2 = new SymbolElement(address, "real", level);
					if(Math.abs(value3)<0.000001){
						element2.setValue(1);
					}else {
						element2.setValue(0);
					}
					symbolTable.add(element2);
			    }else{
					if (Math.abs(value3)<0.000001) {
						element.setValue(1);
					}else {
						element.setValue(0);
					}		
				}
				
				
				
			}else if (command.equals("jmp")) {
				if( !parameter1.equals("")){
					SymbolElement element = symbolTable.getPossibleElement(parameter1, level);
					if (Math.abs(element.getValue())<0.000001) {
						i = Integer.parseInt(address)-2;
					}
				}else{
						i = Integer.parseInt(address)-2;
				}
				
			}else if(command.equals("return")){
				i = Integer.parseInt(address)-2;
				
			}else if (command.equals("in")) {
				level++;
			}else if (command.equals("out")) {
				
				level--;
				symbolTable.remove(level);
				
				
				/*
				 * ����Ǻ����������out���Ļ�����ַ�ϻ���$stack
				 * ��ʱ��Ҫ��ջ��ȡ�������ջ��ĺ������ص�ַ
				 */
				if (address.equals("$stack")) {
					i = stack.pop();
				}
				
			}else if (command.equals("write")) {
				
				//consoleTextArea.append("����write����,addressΪ�� "+address+"level : "+level);
				
				if(address.contains(".")){
					
					  double factorValue = Double.parseDouble(address);
					  consoleTextArea.append(factorValue+"\n");
					 
				}else if(isInt(address)){
					
					 int factorValue = Integer.parseInt(address);
					 
					 consoleTextArea.append(factorValue+"\n");
					 
				}else if(address.contains("[")){
					String array_name = address.substring(0, address.indexOf("["));
					SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
					if (array_element.getType().equals("int_array")) {
						 int factorValue = getIntArrayValue(address);
						 consoleTextArea.append(factorValue+"\n");
					}else {
						
						 double factorValue = getRealArrayValue(address);
						 consoleTextArea.append(factorValue+"\n");
					}
				}else if(address.equals("$return")){
					
					
					double value = returnValue;
					consoleTextArea.append(value+"\n");
					
				
				}else{
					
					//consoleTextArea.append("��ʼȡ������ֵ��,��ǰ��levelΪ�� "+level);
					SymbolElement element  = symbolTable.getPossibleElement(address, level);
					
					//consoleTextArea.append("��ȡ���˱���"+element.getName());
					
					String type =element.getType();
					
					
					if (type.equals("int")) {
						 int factorValue = (int)element.getValue();
						 consoleTextArea.append(factorValue+"\n");
					}else {
						 double factorValue = element.getValue();
						 consoleTextArea.append(factorValue+"\n");
					}
				}
				
			}else if (command.equals("read")) {
				consoleAreaTextCurrentContentString = consoleTextArea.getText();
				String value = readInput();

		
				
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				element.setValue(Double.parseDouble(value));
			}else if (command.equals("end")) {
				consoleTextArea.append("\n\n\n\n");
				return;
			}else if (command.equals("call")) {
				/*
				 *�������õĲ��������ȵĻ����Ǵ洢���ص�ַ
				 *Ȼ��������ת 
				 */
				stack.push(i);
				i = Integer.parseInt(address)-2;
			}
			
			
		
			}catch( NumberFormatException |ExecuteError e){
				consoleTextArea.setText(e.getMessage());
			}
		}
	
	}

	
	/**
	 * ��ȫ�ֱ�����ȡ�û�����
	 * @return
	 * 
	 * ��Ҫ���ǽ���ͬ����Ȼ����������Ҫ��ͣ���ͽ���
	 */
	
	public synchronized String readInput() {
		String value = null;
		try {
			while (input == null) {
				wait(); //���̽�������״̬
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		value = input;
		input = null;
		return value;
	}
	
	
	
	/**
	 * ����������ⲿ��ȡ���룬�����������Ľ���
	 * @param userinput
	 */
	public synchronized void getInput(String userinput){
		input = userinput;
		notify();
	}
	
	
	
	

	private double getValueofTerm(String parameter1) throws NumberFormatException, ExecuteError{
		double factorValue = 0.0;
		if(parameter1.contains(".")){
			 factorValue = Double.parseDouble(parameter1);
			 return factorValue;
			 
		}else if(isInt(parameter1)){
			 factorValue = Integer.parseInt(parameter1);
			 return factorValue;
		}else if(parameter1.contains("[")){
			String array_name = parameter1.substring(0, parameter1.indexOf("["));
			SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
			if (array_element.getType().equals("int_array")) {
				 factorValue = getIntArrayValue(parameter1);
				 return factorValue;
			}else {
				 factorValue = getRealArrayValue(parameter1);
				 return factorValue;
			}
		}else if(parameter1.equals("$return")){
			factorValue = returnValue;
			return factorValue;
			
		}else{
			SymbolElement element  = symbolTable.getPossibleElement(parameter1, level);
			String type =element.getType();
			if (type.equals("int")) {
				 factorValue = element.getValue();
				 return factorValue;
			}else {
				 factorValue = element.getValue();
				 return factorValue;
			}
		}
		
	}
	
	
	
	
	
	
	
	
	

	private void command_Real(String parameter1, String parameter2)
			throws ExecuteError {
		if(parameter2.equals("")){
			symbolTable.add(new SymbolElement(parameter1, "real", level));
		}else if (parameter2.contains(".")) {
			SymbolElement element = new SymbolElement(parameter1, "real", level);
			element.setValue(Double.parseDouble(parameter2));
			symbolTable.add(element);	
			
		}else if(isInt(parameter2)){
			SymbolElement element = new SymbolElement(parameter1, "real", level);
			element.setValue(Double.parseDouble(parameter2));
			symbolTable.add(element);	
			
			
		}else if(parameter2.contains("[")){
			//��Ϊд����������ĸ����ԱȽϸߣ������ع���һ�£�д��һ����ȡ����ֵ�ĺ���
			double real_value = getRealArrayValue(parameter2);
					
			SymbolElement element  = new SymbolElement(parameter1, "real", level);
			element.setValue(real_value);
			symbolTable.add(element);
			
		}else{
			/*
			 * ��ֻʣ�±�����ֵ��
			 */
			
			SymbolElement value_element = symbolTable.getPossibleElement(parameter2, level);
			SymbolElement element = new SymbolElement(parameter1, "real", level);
			element.setValue(value_element.getValue());
			symbolTable.add(element);
			
		}
	}

	
	/**
	 * ��ȡreal����ֵ�ĺ���
	 * @param parameter2
	 * @return
	 * @throws ExecuteError
	 * 
	 */
	
	private double getRealArrayValue(String parameter2) throws ExecuteError {
		String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
		String array_name = parameter2.substring(0, parameter2.indexOf("["));
		SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
		double real_value = 0.0;
		
		
		if(isInt(indexstr)){	
			real_value = array_element.getRealArrayValue(Integer.parseInt(indexstr));
			
		}else{
			SymbolElement value_element = symbolTable.getPossibleElement(indexstr, level);
			real_value = array_element.getRealArrayValue((int)value_element.getValue());
		}
		return real_value;
	}
	
	
	
	private int getIntArrayValue(String parameter2) throws NumberFormatException, ExecuteError{
		String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
		String array_name = parameter2.substring(0, parameter2.indexOf("["));
		SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
		int  int_value = 0;
		
		
		if(isInt(indexstr)){	
			int_value = array_element.getIntArrayValue(Integer.parseInt(indexstr));
			
		}else{
			SymbolElement value_element = symbolTable.getPossibleElement(indexstr, level);
			int_value = array_element.getIntArrayValue((int)value_element.getValue());
		}
		return int_value;
	}
	
	
	private void setIntArrayValue(String address,double value) throws NumberFormatException, ExecuteError{
		String indexstr = address.substring(address.indexOf("[") + 1, address.length() - 1);
		String array_name = address.substring(0, address.indexOf("["));
		SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
		

		if(isInt(indexstr)){	
			array_element.setIntArrayValue(Integer.parseInt(indexstr), (int)value);
			
		}else{
			SymbolElement value_element = symbolTable.getPossibleElement(indexstr, level);
			array_element.setIntArrayValue((int)value_element.getValue(),(int)value);
		}	
	}
	
	
	private void setRealArrayValue(String address,double value) throws NumberFormatException, ExecuteError{
		String indexstr = address.substring(address.indexOf("[") + 1, address.length() - 1);
		String array_name = address.substring(0, address.indexOf("["));
		SymbolElement array_element = symbolTable.getPossibleElement(array_name, level);
		

		if(isInt(indexstr)){	
			array_element.setRealArrayValue(Integer.parseInt(indexstr), value);
			
		}else{
			SymbolElement value_element = symbolTable.getPossibleElement(indexstr, level);
			array_element.setRealArrayValue((int)value_element.getValue(),value);
		}	
		
	}

	
	
	private void command_Int(String parameter1, String parameter2)
			throws ExecuteError {
		/*
		 * ���ǵ�����ʱ��ֵ�����������Ҫ�������¼��������
		 * 		1. ��int��ֵ 2.������ֵ 3.���鸳ֵ �������鸳ֵ������£�����Ҫ���������±�����ͣ�Ҳ����˵��intֵ�����Ǳ�����
		 */
		
		//consoleTextArea.append("��ʼ������з���"+parameter1+"   "+parameter2);
		
		if(parameter2.equals("")){
			
			symbolTable.add(new SymbolElement(parameter1, "int", level));
			
		}else if (isInt(parameter2)) {
			
			//consoleTextArea.append("��ֵ���жϳ�int");
			
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(Integer.parseInt(parameter2));
			symbolTable.add(element);		
			
			
		//	consoleTextArea.append("�ɹ���ӽ����ű�");
			
			
		}else if(parameter2.contains("[")){
			//��������ֵ���и�ֵ������Ҫ����������Լ��±�
			int value = getIntArrayValue(parameter2);
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(value);
			symbolTable.add(element);
		}else{
			/*
			 * ��ֻʣ�±�����ֵ��
			 */
			SymbolElement value_element = symbolTable.getPossibleElement(parameter2, level);
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(value_element.getValue());
			symbolTable.add(element);
		}
	}
	
}
