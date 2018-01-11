package semantic_analyzer;

//��Ԫʽ����
public class FourElementFormula {

	// ��Ԫʽ�е�������
	private String command;
	// ��һ������
	private String parameter1;
	// �ڶ�������
	private String parameter2;
	// �������洢��λ��
	private String result;
	

	public FourElementFormula(String command, String parameter1,
			String parameter2, String result) {
		this.command = command;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.result = result;
	}

	public String toString(){
		
		return command+"\t"+parameter1+"\t"+parameter2+"\t"+result;
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
