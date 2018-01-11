package semantic_analyzer;

//四元式对象
public class FourElementFormula {

	// 四元式中的命令项
	private String command;
	// 第一个参数
	private String parameter1;
	// 第二个参数
	private String parameter2;
	// 运算结果存储的位置
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
