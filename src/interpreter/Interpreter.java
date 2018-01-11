package interpreter;

import java.util.ArrayList;
import java.util.LinkedList;





import java.util.Stack;

import javax.swing.JTextArea;

import semantic_analyzer.FourElementFormula;
import semantic_analyzer.SymbolElement;
import semantic_analyzer.SymbolTable;

/**
 * 解释中间代码的类
 * @author wsw
 * 
 * 解释执行语义分析结束之后生成的中间代码
 * 中间代码的四元式出现在command位置的命令可能有以下：
 * 		int，real，int_array,real_array, +，*，-，/，=,<,>,<=,>=,<>,==,jmp,read,write,in,out，end ,call ,return ,void 
 *
 */

public class Interpreter implements Runnable{
	//中间代码序列
	private LinkedList<FourElementFormula> intermediateCode;
	//符号表，管理解释执行过程中，变量的值，包括临时变量
	private SymbolTable symbolTable;
	//作用域
	private int level = 0;
//	//计算用算子
//	private double factorValue;
	/*
	 * 这里又需要一个全局变量了，就获取用户输入的时候，这个解释器进程会阻塞，然后需要一个函数获取用户
	 * 的输入，获取了用户输入之后会唤醒解释进程。然而，还需要对获取的数据进行一下，判断。这两个函数
	 * 需要共享一个输入变量，否则无法实现信息的传递，所以这里需要一个全局的共享资源
	 */
	private String input;
	
	private String consoleAreaTextCurrentContentString;
	
	
	private JTextArea consoleTextArea;
	
	/*
	 * 这个队列用于函数参数的传值
	 * 
	 * addLast()
	 * removeFirst()
	 */
	private LinkedList<String> queue;
	
	//堆栈用于存储函数返回地址
	private Stack<Integer> stack;
	
	//用于存储函数的返回值，实时更新
	private Double returnValue;
	
	//存储事执行的声明语句的编号
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
	 * 判断字符串是否是int类型变量
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
		
		//首先要得到函数开始执行的位置
		
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
		 * 对main函数入口可能出现的一些错误的判断
		 */
		if (amount > 1) {
			try {
				throw new ExecuteError("ExecuteError: "+"程序有多于一个入口");
			} catch (ExecuteError e) {
				// TODO Auto-generated catch block
				consoleTextArea.setText(e.getMessage());
				return;
			}
		}
		if (start == 0) {
			
			try {
				throw new ExecuteError("ExecuteError: "+"没有找到main函数");
			} catch (ExecuteError e) {
				// TODO Auto-generated catch block
				consoleTextArea.setText(e.getMessage());
				return;
			}
			
		}
		
		
		/*
		 * 下面是先于main函数的全局的变量的声明，这个需要在main函数之前处理
		 */
		
		for(Integer integer : declare_number){
			
		    //consoleTextArea.append("目前的integer为： "+integer+"\n");
			FourElementFormula currentFormula = intermediateCode.get(integer-1);
			String command = currentFormula.getCommand();
			String parameter1 = currentFormula.getParameter1();
			String parameter2 = currentFormula.getParameter2();
			String address = currentFormula.getResult();
			
			try{
			if(command.equals("int")){
				//consoleTextArea.append("开始int 变量声明\n");
				
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
					
					//consoleTextArea.append("成功添加"+parameter1+"进符号表,目前的level为："+level+"\n");
						
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
				
				//你妹的，这里好繁琐啊，明明就那么回事儿
				
				double value1 = getValueofTerm(parameter1);
				
				double value2= getValueofTerm(parameter2);
				
				//consoleTextArea.append("成功分析到加法:  "+value1+"    "+value2);
					
				double value3 = value1+value2;
				SymbolElement element = symbolTable.getPossibleElement(address, level);
				if (element == null) {
					
					//consoleTextArea.append("开始创建临时变量"+address+"\n");
					
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
				 * 我们来看看啊，首先，赋值的话，我已经不需要考虑类型的问题了，因为这在语义分析的时候已经做过了
				 * 也就是说这里不需要你来判断类型是否正确，现在我要确保不会做一些重复的工作
				 * 然后就是，我去，你妹的，临时变量管什么类型啊，对啊，临时变量不用管类型的呀，我临时变量只是起到一个传递值
				 * 的作用，类型分析，之前已经做过了。
				 * 
				 */
				//consoleTextArea.append("进入赋值语句,address值为："+address);
				
				if(address.equals("$queue")){
					//consoleTextArea.append("成功进入到地址为$queue的分支\n");
					
					//如果是函数传递过程中涉及到参数传递的赋值 
					SymbolElement element = symbolTable.getPossibleElement(parameter1, level);
					
					
					if ((element != null) && (element.getType().equals("int_array") || element.getType().equals("real_array"))) {
						//这说明函数调用的时候传入的是数组
						queue.add(parameter1);
					}else{
						//其它变量的话都是可以获得值的
						
						//consoleTextArea.append("开始往queue里面添加数据\n");
						double value = getValueofTerm(parameter1);
						//consoleTextArea.append("value的值为：" +value);
						
						queue.add(value+"");
						
						
						//consoleTextArea.append("成功添加进队列"+queue.removeFirst()+"\n");
					}
								
				}else if(address.equals("$return")){
					double value = getValueofTerm(parameter1);
					returnValue = value;
						
					//consoleTextArea.append("returnValue被赋值为："+value);
				}else if(parameter1.equals("$return")){
					
					//将函数的返回结果给变量
					double value = returnValue;
					
					if (address.contains("[")) {
						//consoleTextArea.append("赋值对象成功识别为数组");
				
						//String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
						String array_name = address.substring(0, address.indexOf("["));
						SymbolElement element = symbolTable.getPossibleElement(array_name, level);
				
						//consoleTextArea.append("赋值数组的类型"+element.getType()+"\n");
				
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
					  * 纯粹的赋值语句
					  */
						double value = getValueofTerm(parameter1);
					
						if (address.contains("[")) {
							//consoleTextArea.append("赋值对象成功识别为数组");
					
							//String indexstr = parameter2.substring(parameter2.indexOf("[") + 1, parameter2.length() - 1);
							String array_name = address.substring(0, address.indexOf("["));
							SymbolElement element = symbolTable.getPossibleElement(array_name, level);
					
							//consoleTextArea.append("赋值数组的类型"+element.getType()+"\n");
					
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
				 * 如果是函数语句最后的out语句的话，地址上会有$stack
				 * 这时就要从栈中取出存放在栈里的函数返回地址
				 */
				if (address.equals("$stack")) {
					i = stack.pop();
				}
				
			}else if (command.equals("write")) {
				
				//consoleTextArea.append("到了write命令,address为： "+address+"level : "+level);
				
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
					
					//consoleTextArea.append("开始取变量的值：,当前的level为： "+level);
					SymbolElement element  = symbolTable.getPossibleElement(address, level);
					
					//consoleTextArea.append("获取到了变量"+element.getName());
					
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
				 *函数调用的操作，首先的话就是存储返回地址
				 *然后设置跳转 
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
	 * 从全局变量获取用户输入
	 * @return
	 * 
	 * 需要考虑进程同步，然后在这里需要暂停解释进程
	 */
	
	public synchronized String readInput() {
		String value = null;
		try {
			while (input == null) {
				wait(); //进程进入阻塞状态
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		value = input;
		input = null;
		return value;
	}
	
	
	
	/**
	 * 这个方法从外部获取输入，并唤醒阻塞的进程
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
			//因为写到发现这里的复用性比较高，所以重构了一下，写了一个获取数组值的函数
			double real_value = getRealArrayValue(parameter2);
					
			SymbolElement element  = new SymbolElement(parameter1, "real", level);
			element.setValue(real_value);
			symbolTable.add(element);
			
		}else{
			/*
			 * 就只剩下变量赋值了
			 */
			
			SymbolElement value_element = symbolTable.getPossibleElement(parameter2, level);
			SymbolElement element = new SymbolElement(parameter1, "real", level);
			element.setValue(value_element.getValue());
			symbolTable.add(element);
			
		}
	}

	
	/**
	 * 获取real数组值的函数
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
		 * 考虑到声明时赋值的情况，我需要考虑以下几种情况：
		 * 		1. 纯int数值 2.变量赋值 3.数组赋值 （在数组赋值的情况下，我需要考虑数组下标的类型，也就是说是int值，还是变量）
		 */
		
		//consoleTextArea.append("开始对其进行分析"+parameter1+"   "+parameter2);
		
		if(parameter2.equals("")){
			
			symbolTable.add(new SymbolElement(parameter1, "int", level));
			
		}else if (isInt(parameter2)) {
			
			//consoleTextArea.append("赋值已判断成int");
			
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(Integer.parseInt(parameter2));
			symbolTable.add(element);		
			
			
		//	consoleTextArea.append("成功添加进符号表");
			
			
		}else if(parameter2.contains("[")){
			//利用数组值进行赋值，首先要获得数组名以及下标
			int value = getIntArrayValue(parameter2);
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(value);
			symbolTable.add(element);
		}else{
			/*
			 * 就只剩下变量赋值了
			 */
			SymbolElement value_element = symbolTable.getPossibleElement(parameter2, level);
			SymbolElement element = new SymbolElement(parameter1, "int", level);
			element.setValue(value_element.getValue());
			symbolTable.add(element);
		}
	}
	
}
