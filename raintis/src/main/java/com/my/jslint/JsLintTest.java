package com.my.jslint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Files;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;

public class JsLintTest {

	public static void lint(final String filePath) throws FileNotFoundException, IOException {
	    String fileContent = Files.toString(new File(filePath),StandardCharsets.UTF_8);
	    JSLintBuilder builder = new JSLintBuilder();
	    JSLint jsLint = builder.fromDefault();
	    jsLint.addOption(Option.SLOPPY);  
        jsLint.addOption(Option.WHITE);  //忽略空格
        jsLint.addOption(Option.EQEQ);  
        jsLint.addOption(Option.NOMEN);  
        jsLint.addOption(Option.CONTINUE);  
        jsLint.addOption(Option.REGEXP);  
        jsLint.addOption(Option.VARS);  
        jsLint.addOption(Option.PLUSPLUS); 
       /* for(Option op :Option.values()){
        	jsLint.addOption(op);
        }*/

	    JSLintResult result = jsLint.lint("work.js", fileContent);
	    for (Issue issue : result.getIssues()) {
	        System.out.println(issue.toString()  + " content:" + issue.getEvidence());
	    }
	}
	
	public static void main(String[] args) throws Exception{
		lint("resource/js/work.js");
	}
}
