package semantic_analyzer;

import java.util.ArrayList;

public class SymbolTable {
	// 用一个ArrayList来存储和管理符号表记录
	private ArrayList<SymbolElement> symbolTable = new ArrayList<SymbolElement>();

	/**
	 * 根据符号名称和作用域，对当前作用域的范围进行查找
	 * 
	 * @param name
	 * @param level
	 * @return
	 */
	public SymbolElement getElement(String name, int level) {
		for (SymbolElement element : symbolTable) {
			if (element.getName().equals(name) && element.getLevel() == level) {
				return element;
			}
		}
		return null;
	}

	/**
	 * 根据函数名和参数列表判断函数是否存在
	 * 
	 * @param name
	 * @param parameterListType
	 * @return
	 */

	public SymbolElement getFunction(String name, String parameterListType) {
		// boolean result = false;
		for (SymbolElement element : symbolTable) {
			if (element.getName().equals(name)
					&& element.getParameterListTypeString().equals(
							parameterListType)) {
				return element;
			}
		}

		return null;
	}

	public SymbolElement getPossibleElement(String name, int level) {

		SymbolElement return_element = null;
		/*
		 * 记录可能查找到的element，然后获取level最大的element，这就实现了就近原则
		 */
		int resultlevel = 0;

		for (SymbolElement element : symbolTable) {
			if (element.getName().equals(name) && element.getLevel() <= level) {
				if (element.getLevel() >= resultlevel) {
					resultlevel = element.getLevel();
					return_element = element;
				}
			}
		}
		return return_element;
	}

	/**
	 * 此函数每次在退出一个局部作用域时，清楚当前level值的变量，这是一个使符号表实时更新的函数
	 * 
	 * @param level
	 */
	public void remove(int level) {
		for (int i = 0; i < symbolTable.size(); i++) {
			if (symbolTable.get(i).getLevel() > level) {
				symbolTable.remove(i);
				i--;
			}
		}

	}

	/**
	 * 添加符号到符号表
	 * 
	 * @param element
	 */
	public void add(SymbolElement element) {
		symbolTable.add(element);
	}

}
