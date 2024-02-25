package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			int unnamedStateCount= 0;
			if(content instanceof State) {
				State state = (State) content;
		        if(state.getName() == null || state.getName().isEmpty()) {
		            state.setName("UnnamedState" + unnamedStateCount++);
		        }
		        System.out.println(state.getName());
		        if(state.getOutgoingTransitions().isEmpty()) {
		            System.out.println(state.getName() + " is a trap state.");
		        }
			}
			if(content instanceof Transition) {
		        Transition transition = (Transition) content;
		        System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
		    }
			
		}
		System.out.println("public class RunStatechart {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"			\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"	    s.setTimer(new TimerService());\r\n" + 
				"	    RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"	    s.init();\r\n" + 
				"	    s.enter();\r\n" + 
				"	    s.runCycle();\r\n" + 
				"	    print(s);\r\n" + 
				"\r\n" + 
				"	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\r\n" + 
				"	    String line = \"\";"+"\r\n"
				+"while (!(line = reader.readLine()).equals(\"exit\")) {\r\n" + 
				"	        switch (line) {\r\n");
		
			
			TreeIterator<EObject> iterator3 = s.eAllContents();
			while (iterator3.hasNext()) {
				EObject content3 = iterator3.next();
				if(content3 instanceof EventDefinition) {
					EventDefinition eventdefinition = (EventDefinition) content3;
			        System.out.println("case \""+eventdefinition.getName()+"\":");
			        System.out.println("s.raise"+eventdefinition.getName().toUpperCase()+"();");	
			        System.out.println("break;\r\n");	
			}}
			System.out.println("default:");
			System.out.println("System.out.println(\\\"Unknown command: \\\" + line);");
			System.out.println("s.runCycle();");
			 System.out.println("print(s);");
		        System.out.println("}");
			System.out.println("}");
			System.out.println("System.exit(0);");
			System.out.println("}");
			
			System.out.println("public static void print(IExampleStatemachine s) {");
			TreeIterator<EObject> iterator2 = s.eAllContents();
			while (iterator2.hasNext()) {
				EObject content2 = iterator2.next();
				if(content2 instanceof VariableDefinition) {
					VariableDefinition variabledefinition = (VariableDefinition) content2;
			        System.out.println("System.out.println("+variabledefinition.getName().toUpperCase()+" = + s.getSCInterface().get"+variabledefinition.getName()+"());");
				
			}}
				System.out.println("}");
				System.out.println("}");
		
			
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	
	}}
