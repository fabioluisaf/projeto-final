package testes;

import java.io.IOException;
import java.util.ArrayList;

import modelo.auxiliar.GuardadorObjeto;

public class TesteExcreverArquivos {
	
	public static void main(String[] args) {
		ArrayList<Integer> lista = new ArrayList<>();
		GuardadorObjeto<ArrayList<Integer>> ga = new GuardadorObjeto<>("teste");
		
		lista.add(2);
		lista.add(3);
		lista.add(1);
		lista.add(5);
		lista.add(55);
		lista.add(42);
		
		try {
			ga.salvaObjeto(lista);
		} catch (IOException e) {
			System.out.println("Erro ao tentar abrir o arquivo " + ga.getNomeArquivo());
		}
	}
}
