package com.elam.agent.profile;

import java.lang.instrument.Instrumentation;

public class ElamJavaAgent {
	  public static void premain(String args, Instrumentation instrumentation){
		  
		  System.out.println("[Elam Java Agent] Elam Java Agent is instrumenting your application..");
		  
	    ClassInstrument transformer = new ClassInstrument();
	    instrumentation.addTransformer(transformer);
	  }
	}