package com.my.regex;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class ParamTransTest {

	public static void main(String[] args) {
		String formula1 = "=CostCalc_YJ(\"\",\"tg02\",\"\",\"0101,0201\",\"001-002\",\"02002,03001\",\"\",\"\",\"999\",\"entry.entry_outputqty\",\"\")";
		String formula = "\"\",\"tg02\",\"\",\"0101,0201\",\"001-002\",\"02002,03001\",\"\",\"\",\"999\",\"entry.entry_outputqty\",\"\"";
		
		List<String> params = new ArrayList<>(10);
		boolean isQuotationStart = false;
		StringBuilder param = new StringBuilder(10);
		for(char c : formula.toCharArray()){
			if(c == '"'){
				isQuotationStart = !isQuotationStart;
			}else if(c == ','){
				if(isQuotationStart){
					param.append(c);
				}else{
					params.add(param.toString());
					param.setLength(0);
				}
			}else{
				param.append(c);
			}
		}
		params.add(param.toString());
		System.out.println(Joiner.on("|").join(params).toString());
	}

}
