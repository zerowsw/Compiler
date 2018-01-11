package semantic_analyzer;

import java.util.ArrayList;

public class SymbolTable {
	// ��һ��ArrayList���洢�͹�����ű��¼
	private ArrayList<SymbolElement> symbolTable = new ArrayList<SymbolElement>();

	/**
	 * ���ݷ������ƺ������򣬶Ե�ǰ������ķ�Χ���в���
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
	 * ���ݺ������Ͳ����б��жϺ����Ƿ����
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
		 * ��¼���ܲ��ҵ���element��Ȼ���ȡlevel����element�����ʵ���˾ͽ�ԭ��
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
	 * �˺���ÿ�����˳�һ���ֲ�������ʱ�������ǰlevelֵ�ı���������һ��ʹ���ű�ʵʱ���µĺ���
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
	 * ��ӷ��ŵ����ű�
	 * 
	 * @param element
	 */
	public void add(SymbolElement element) {
		symbolTable.add(element);
	}

}
