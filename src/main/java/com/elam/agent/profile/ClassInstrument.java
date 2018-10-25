package com.elam.agent.profile;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;



public class ClassInstrument implements ClassFileTransformer {
	
	private static final StringBuilder WRAP_METHOD = new StringBuilder();
	
	static {
		
		WRAP_METHOD.append("{");
		WRAP_METHOD.append(" long startTimeAgent = System.currentTimeMillis(); ");
		WRAP_METHOD.append(" try { ");
		WRAP_METHOD.append(" {RETURN} {METHOD_NAME} ").append("({PARAMS});");
		WRAP_METHOD.append("}");
		WRAP_METHOD.append(" finally ");
		WRAP_METHOD.append(" { ");
		WRAP_METHOD.append(" long execTimeAgent = System.currentTimeMillis() - startTimeAgent; ");
		WRAP_METHOD.append(" System.out.println(").append("\"").append("[Elam Java Agent]");
		WRAP_METHOD.append(" {INSTRUCT_STMNT} ");
		WRAP_METHOD.append("\"").append("+");
		WRAP_METHOD.append("execTimeAgent").append("+");
		WRAP_METHOD.append("\"").append(" milliseconds.");
		WRAP_METHOD.append("\"").append("); ");
		WRAP_METHOD.append(" } }");
		
	}
	
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		
		String clazzName = className.replace("/", ".");
		StringBuilder statmntBuilder = new StringBuilder();	
		StringBuilder clazzNameBuilder = new StringBuilder();
		StringBuilder methodNameBuilder = new StringBuilder();
		
		
		
		
		ClassPool classPool = null;
		try {
			
			
			classPool = ClassPool.getDefault();
			
			
			if(clazzName.startsWith("com.elam.") ) {
			
			
				
				//if ("services.lookupTables.LookUpTable".equals(clazzName) ) {
				
				//System.out.println("CRS Dispute Class.:"+clazzName);
				
				classPool.appendClassPath(new LoaderClassPath(loader));
			
				
				CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer)); 
			
				clazzNameBuilder.append(clazzName).append(" ==> ");
		
				
				if (!ctClass.isInterface() && !ctClass.isFrozen()) {
				
				
			
				//StringBuilder methodBody = new StringBuilder();
				
			//	System.out.println("CRS Dispute Class.:"+ctClass.getName());
				
		for (CtMethod method : ctClass.getDeclaredMethods()) {
			
		//	methodBody.setLength(0);
			
			
			
			if(!isGetterOrSetterMethod(ctClass.getClassFile(),method.getName())) {
			
	
			
			if(!method.isEmpty()) {
				
				methodNameBuilder.append(method.getName()).append(" --> ");
				statmntBuilder.append(clazzNameBuilder).append(methodNameBuilder);
				
				
				CtMethod agentMethod = CtNewMethod.copy(method,	method.getName()+ "_AgentMethod", ctClass, null);
				
				makePrivateAccess(agentMethod);
				
				ctClass.addMethod(agentMethod);
				
				String methodBody = WRAP_METHOD.toString().replace("{METHOD_NAME}", agentMethod.getName());;
				methodBody = methodBody.replace("{INSTRUCT_STMNT}", statmntBuilder.toString());  
				
				
				if (null != method.getParameterTypes() && method.getParameterTypes().length > 0) {
					
					methodBody = methodBody.replace("{PARAMS}", "$$");
					
					/*if(method.getReturnType().toString().endsWith("[void]")) {
						
						methodBody = methodBody.replace("{RETURN}", "");*/
						
						
						//method.setBody(WRAP_METHOD.toString().replace("{RETURN}", "").replace("{METHOD_NAME}", copyMethod.getName()).replace("{PARAMS}", "$$"));
						//System.out.println(" Void return type");
					//method.setBody("{ long startTimeAgent = System.currentTimeMillis(); try {   "+ copyMethod.getName() +"($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+String.valueOf(execTimeAgent)+\" milli seconds.\"); } }", "this", method.getName());
					/*} else {
						methodBody = WRAP_METHOD.toString().replace("{RETURN}", "return");
						//method.setBody("{ long startTimeAgent = System.currentTimeMillis(); try {  return "+ copyMethod.getName() +"($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+String.valueOf(execTimeAgent)+\" milli seconds.\"); } }", "this", method.getName());
					}*/
			} else {
				
				methodBody = methodBody.replace("{PARAMS}", "");
				/*if(method.getReturnType().toString().endsWith("[void]")) {
					//System.out.println(" Void return type");
					//method.setBody("{ long startTimeAgent = System.currentTimeMillis(); try {  "+ copyMethod.getName() +"(); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+String.valueOf(execTimeAgent)+\" milli seconds.\"); } }", "this", method.getName());
				} else {
					//method.setBody("{ long startTimeAgent = System.currentTimeMillis(); try {  return "+ copyMethod.getName() +"(); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+String.valueOf(execTimeAgent)+\" milli seconds.\"); } }", "this", method.getName());
				}*/
			}
				
				
				if(method.getReturnType().toString().endsWith("[void]")) {
					methodBody = methodBody.replace("{RETURN}", "");
				} else {
					methodBody = methodBody.replace("{RETURN}", "return");
				}
				
				
				method.setBody(methodBody, "this", method.getName());
				
			//	method.setBody(	" { try   { "+method.getName() +"_copy($$); } finally { System.out.println(\"I am finally\"); } }");
						
				
				//System.out.println("CRS Dispute methos.:"+method.getName());
				
				
				
				//method.setBody("{ long startTimeAgent = System.currentTimeMillis(); try { System.out.println(\" try call\"); return $proceed($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }", "this", method.getName());
				
			/*	methodNameBuilder.append(method.getName()).append(" --> ");
				statmntBuilder.append(clazzNameBuilder).append(methodNameBuilder);*/
				
				
				//System.out.println("Return type..:"+ method.getReturnType());
				//CtMethod actualMethod = CtNewMethod.copy(method, ctClass, null);
				//CtMethod copyMethod = CtNewMethod.copy(method, ctClass, null);
			
				
				
				
				/*if (null != method.getParameterTypes() && method.getParameterTypes().length > 0) {
					actualMethod.setBody("{ long startTimeAgent = System.currentTimeMillis(); try { System.out.println(\" try call\"); return "+ method.getName() +"($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }", "this", method.getName());
				} else {
					actualMethod.setBody("{ long startTimeAgent = System.currentTimeMillis(); try { System.out.println(\" try call\"); return "+ method.getName() +"(); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }", "this", method.getName());
				}*/
				
				
				//System.out.println("Method Singnature..:"+method.getSignature());
			/*	
				try {
					actualMethod.getMethodInfo().rebuildStackMap(classPool);
					method.setName(method.getName() + "_original");
					method.setModifiers(Modifier.PRIVATE);
					method.getMethodInfo().rebuildStackMap(classPool);	
					
				 ctClass.addMethod(actualMethod);
				}catch(Exception exp) {
					System.out.println(" Recompile Exception..:"+clazzName+":..:"+method);
					return classfileBuffer;
				}*/
				
				
				//actualMethod.setBody("{ long startTimeAgent = System.currentTimeMillis(); try { System.out.println(\" try call\"); return "+ method.getName() +"($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }", "this", method.getName());
				//actualMethod.setBody("{ long startTimeAgent = System.currentTimeMillis(); try { System.out.println(\" try call\"); return $proceed($$); }finally {  long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }");
				
				
				
				//final String beforeMethod = "{ long startTimeAgent = System.currentTimeMillis(); System.out.println(\"Before method\"); ";  
				//final String afterMethod = "  finally { long execTimeAgent = System.currentTimeMillis() - startTimeAgent; System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } }";  
				  
				
			//	method.setBody(beforeMethod + " try {$_ = $proceed($$); } " + afterMethod);
				
				/*StringBuilder methodBuilder = new StringBuilder(beforeMethod);
				 AtomicInteger replaceCount = new AtomicInteger(0);*/
				 
				
			/*	method.instrument(  
				 new ExprEditor() {  
				 public void edit(MethodCall m)  
				 throws CannotCompileException  
				 {  
					 
					 //methodBuilder.append("$proceed($$);");
					 
					 System.out.println(" on ExprEditor..:"+m.getMethodName());
					 
					 if(replaceCount.get() == 0) {
						 m.replace(beforeMethod + " try {$_ = $proceed($$); } " + afterMethod);
						 replaceCount.set(1);
					 }
				 }  
				 });  */
				
			//	System.out.println(" Out ExprEditor..:"+clazzName+":..:"+method);
				
				//methodBuilder.append(afterMethod);
				
				//method.setBody(methodBuilder.toString());
				
				/*try {
				method.getMethodInfo().rebuildStackMap(classPool);
				}catch(Exception exp) {
					System.out.println(" Recompile Exception..:"+clazzName+":..:"+method);
				}*/
				
				
				
				
				//cc.writeFile();
				
				/*method.instrument(new ExprEditor() {

				        @Override
				        public void edit(MethodCall m) throws CannotCompileException {
				            System.out.println("edit class{}"+ m.getClassName());
				            try {
				            	 System.out.println("edit method:{}"+ m.getMethod().toString());
				            } catch (NotFoundException e) {
				            	e.printStackTrace();
				            }
				            System.out.println(m.getMethodName());
				            m.replace(" long startTimeAgent = 0;" + " try { startTimeAgent = System.currentTimeMillis(); $_ = $proceed($$); } finally  { long execTimeAgent = System.currentTimeMillis() - startTimeAgent;  System.out.println(\"finally instrumnted \"); System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } " );
				        }
				    });*/
				
				
				//method.setBody("try {  return $proceed($$); } finally { System.out.println(\"I am finally\"); }");		  
						  
				
			
			//method.addLocalVariable("startTimeAgent", CtClass.longType);
			
			//method.insertBefore(" try { ");
			//method.insertBefore("  { try { System.out.println(\"try { start\"); startTimeAgent = System.currentTimeMillis(); } ");
			
			
			
			/*methodNameBuilder.append(method.getName()).append(" --> ");
			statmntBuilder.append(clazzNameBuilder).append(methodNameBuilder);*/
			
			
			
			//method.insertAfter("   } finally { long execTimeAgent = System.currentTimeMillis() - startTimeAgent; System.out.println(\""+statmntBuilder.toString() +"\"+execTimeAgent+\" milli seconds.\"); } ");
				
				
				
			
			//InstructionPrinter.print(method, System.err);
			
			methodNameBuilder.setLength(0);
			statmntBuilder.setLength(0);
			
		/*	method.getMethodInfo2().doPreverify = true;
			method.getMethodInfo().doPreverify = true;*/
			
		}	
			
		}
			
		}
				}
				
		
	
		 //ctClass.writeFile("C:/Sun/Test.class");
				
		byte[] instrmntdByte = ctClass.toBytecode();
		
		//System.out.println("Class String..:"+ctClass.toBytecode(System.out));
				
				
				/*SunClassLoader sunClassLoader = new SunClassLoader();
				Class regeneratedClass = sunClassLoader.loadClassFromByte(clazzName,instrmntdByte,0, instrmntdByte.length);*/
		
		return instrmntdByte;
			
		//	}
			}
		
		} catch ( Exception    e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return classfileBuffer;
		
	}
	
	
	private boolean isGetterOrSetterMethod(ClassFile classFile,String methodName ) throws BadBytecode {
		
		MethodInfo minfo = classFile.getMethod(methodName);
		CodeAttribute codeAttr = minfo.getCodeAttribute();
		
		boolean isGetterOpCode = false;
		boolean isSetterOpCode = false;
		int opCodeCount = 0;
		
		if(!methodName.startsWith("get") &&  !methodName.startsWith("set")) {
			return false;
		}
		
		if( codeAttr == null) {
			return true;
		}
		
		
			
		//System.out.println("Code Length..:"+codeAttr.length());
		CodeIterator ci = codeAttr.iterator();
		
		
		while (ci.hasNext()) {
			opCodeCount++;
		    int index = ci.next();
		    int opByte = ci.byteAt(index);
		    		    
		    if(Mnemonic.OPCODE[opByte] != null ) {
		    if(Mnemonic.OPCODE[opByte].equals("getfield")) {
		    	isGetterOpCode = true;
		    } else if(Mnemonic.OPCODE[opByte].equals("putfield")) {
		    	isSetterOpCode = true;
		    } 
		    
		    }
		    
		}
		
		
		
		if((opCodeCount == 4 && isSetterOpCode) || (opCodeCount == 3 && isGetterOpCode)) {
			
			// System.out.println(" Getter or Setter..:") ;
			return true;
		} 
		
	return false;
	}
	
	private void makePrivateAccess(CtMethod copyMethod) {
		
		//int modifiers = copyMethod.getModifiers();
		
		
		copyMethod.setModifiers( (copyMethod.getModifiers() & ~(Modifier.PROTECTED | Modifier.PUBLIC))  | Modifier.PRIVATE );
		
		
		
		
		//Modifier modifier = new Modifier();
		
		
		//System.out.println("Method private modifier.:"+Modifier.isPrivate(copyMethod.getModifiers()));
		
		/*copyMethod.setModifiers();
		
		
		if(Modifier.isProtected(copyMethod.getModifiers())) {
			
			modifiers = modifiers - Modifier.PROTECTED;
			System.out.println("Method Protected.:");
		} else if(Modifier.isPublic(copyMethod.getModifiers())) {
			modifiers = modifiers - Modifier.PUBLIC;
			
			System.out.println("Method Public.:");
			
		} 
		
		modifiers = modifiers + Modifier.PRIVATE;
		
		copyMethod.setModifiers(modifiers);*/
		
	}
	
}

