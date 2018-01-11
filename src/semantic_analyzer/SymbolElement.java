package semantic_analyzer;

import interpreter.ExecuteError;

import java.util.ArrayList;


public class SymbolElement {
	
	//���ŵ�����
	private String name;
	/*
	 * ���ŵ�����
	 * ����Ǻ����Ļ�����ô���Ǻ����ķ�������
	 */
	private String type;
	//���ŵ�������ȼ�
	private int level;
	//�������ԣ�ר���������
	private int length;
	//value���ԣ�������ִ���м����Ĺ����в���Ҫ������
	private double value;
	/*
	 * Ϊ��ʵ�ֺ������������������أ����ﶨ��һ���������������б�
	 * Ĭ�ϳɿ�
	 */
	private String parameterListTypeString = "";
	
	//�����������¼��������ʱ��俪ʼ��λ�õ�
	private int startPosition;
	
	/*
	 * ��̬����Ԫ��������ִ��ʱ����Ĵ洢
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
	

	
	//������Ĺ��캯��
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
	 * �����±��ȡ�����ֵ
	 */
	public int getIntArrayValue(int index) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("�����±����");
		}
				
		return int_array.get(index);
	}
	
	/*
	 * ��ȡreal�����ֵ
	 */
	public double getRealArrayValue(int index) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("�����±����");
		}
		
		return real_array.get(index);
	}
	
	/*
	 * ���������ֵ,
	 */
	public void setIntArrayValue(int index,int value) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("�����±����");
		}
		
		int_array.set(index, value);
	}
	
	public void setRealArrayValue(int index,double real) throws ExecuteError{
		if (index>length-1 || index< 0) {
			throw new ExecuteError("�����±����");
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
