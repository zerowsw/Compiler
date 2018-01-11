package parser;

import javax.swing.tree.DefaultMutableTreeNode;




/*
 * 语法树的节点数据结构
 * 为了方便显示和使用，继承使用现有的DefaultMutableTreeNode
 */
public class TreeNode extends DefaultMutableTreeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7662933091727164755L;
	//存储的内容
	private String content;
	//行号
	private int linenumber;
	//类型
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
	 * 添加孩子节点
	 */
	public void addChild(TreeNode childNode){
		super.add(childNode);
	}
	
	/*
	 * 获取孩子节点
	 */
	public TreeNode getChild(int index){
		return (TreeNode)super.getChildAt(index);
	}
	
		
}
