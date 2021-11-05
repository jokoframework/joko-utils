package io.github.jokoframework.utils.constants;

public class Spiderman extends java.lang.Object {

	String universe; 
	
	public static void main(String[] args) {
		//Una instancia de tipo Spiderman
		Spiderman peter = new Spiderman();
		Spidermancito petercito = new Spidermancito();

		System.out.println("Hola soy: " + peter.toString());
		System.out.println("Hola soy: " + petercito.toString());		

	}
	
	@Override
	public String toString() {		
		return "Peter Parker";
	}

}
