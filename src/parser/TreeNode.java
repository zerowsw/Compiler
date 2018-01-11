package parser;

import javax.swing.tree.DefaultMutableTreeNode;




/*
 * �﷨���Ľڵ����ݽṹ
 * Ϊ�˷�����ʾ��ʹ�ã��̳�ʹ�����е�DefaultMutableTreeNode
 */
public class TreeNode extends DefaultMutableTreeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7662933091727164755L;
	//�洢������
	private String content;
	//�к�
	private int linenumber;
	//����
	private String type;
	
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
	
	public String  getContent() {
		return content;
	}
	public void setContent(String content){
		this.content = content;
	}
	public int getLinenumber(){
		return linenumber;
	}
	public void setLinenumber(int linenumber){
		this.linenumber = linenumber;
	}
	

	public TreeNode(String content,int linenumber){
		super(content);
		this.content = content;
		this.linenumber = linenumber;
	}
	
	public TreeNode(String type,String content,int linenumber){
		super(content);
		this.type = type;
		this.content = content;
		this.linenumber = linenumber;
	}
	
	
	public String toString(){
		return content;
	}
	/*
	 * ��Ӻ��ӽڵ�
	 */
	public void addChild(TreeNode childNode){
		super.add(childNode);
	}
	
	/*
	 * ��ȡ���ӽڵ�
	 */
	public TreeNode getChild(int index){
		return (TreeNode)super.getChildAt(index);
	}
	
		
}
