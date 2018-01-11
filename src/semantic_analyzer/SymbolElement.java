package semantic_analyzer;

import interpreter.ExecuteError;

import java.util.ArrayList;


public class SymbolElement {
	
	//符号的名字
	private String name;
	/*
	 * 符号的种类
	 * 如果是函数的话，那么就是函数的返回类型
	 */
	private String type;
	//符号的作用域等级
	private int level;
	//长度属性，专门针对数组
	private int length;
	//value属性，这是在执行中间代码的过程中才需要的属性
	private double value;
	/*
	 * 为了实现函数，包括函数的重载，这里定义一个函数参数类型列表
	 * 默认成空
	 */
	private String parameterListTypeString = "";
	
	//这个是用来记录函数声明时语句开始的位置的
	private int startPosition;
	
	/*
	 * 动态数组元数，用于执行时数组的存储
	 */
	private ArrayList<Integer> int_array;
	
	private ArrayList<Double> real_array;
	


	
	public int getStartPosition() {
		return startPosition;
	}



	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}



	public String getParameterListTypeString() {
		return parameterListTypeString;
	}



	public void setParameterListTypeString(String parameterListTypeString) {
		this.parameterListTypeString = parameterListTypeString;
	}

	
	

	public ArrayList<Integer> getInt_array() {
		return int_array;
	}



	public void setInt_array(ArrayList<Integer> int_array) {
		this.int_array = int_array;
	}



	public ArrayList<Double> getReal_array() {
		return real_array;
	}



	public void setReal_array(ArrayList<Double> real_array) {
		this.real_array = real_array;
	}
	
	public SymbolElement(String name,String type,int level){
		this.name = name;
		this.type = type;
		this.level = level;
	}
	

	
	//数组项的构造函数
	public SymbolElement(String name,String type,int level,int length){
		this.name = name;
		this.type = type;
		this.level = level;
		this.length = length;
		
		if(type.equals("int_array")){
			int_array = new ArrayList<Integer>();
			for (int i = 0; i < length; i++) {
				int_array.add(0);
			}
		}else {
			real_array = new ArrayList<Double>();
			double i = 0.0;
			for (int j = 0; j<length; j++) {
				real_array.add(i);
			}
		}
		
	}
	
	
	/*
	 * 根据下标获取数组的值
	 */
	public int getIntArrayValue(int index) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("数组下标溢出");
		}
				
		return int_array.get(index);
	}
	
	/*
	 * 获取real数组的值
	 */
	public double getRealArrayValue(int index) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("数组下标溢出");
		}
		
		return real_array.get(index);
	}
	
	/*
	 * 设置数组的值,
	 */
	public void setIntArrayValue(int index,int value) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("数组下标溢出");
		}
		
		int_array.set(index, value);
	}
	
	public void setRealArrayValue(int index,double real) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("数组下标溢出");
		}
		
		real_array.set(index, real);
	}
	
	
	
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	
	
	
	
	
}
