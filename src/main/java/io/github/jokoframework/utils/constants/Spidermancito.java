package io.github.jokoframework.utils.constants;

public class Spidermancito extends Spiderman {

	public static void main(String[] args) {
		//Una instancia de tipo Spiderman
		Spidermancito peter = new Spidermancito();
		System.out.println("Hola soy: " + peter.toString());
	}
	
	@Override
	public String toString() {		
		String miCadena = super.toString() + " Petercito"; 
		return miCadena;
	}
	
}
