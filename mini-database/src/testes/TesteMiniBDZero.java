package testes;

import java.io.IOException;

import banco.MiniBD;

public class TesteMiniBDZero {

	public static void main(String[] args) {
		MiniBD<String, String> banquinhoDeDados = new MiniBD<String, String>((String s) -> s, "banquinho");
		
		banquinhoDeDados.adiciona("Batata");
		banquinhoDeDados.adiciona("batatinha");
		banquinhoDeDados.adiciona("bolacha");
		banquinhoDeDados.adiciona("gif");
		banquinhoDeDados.adiciona("copo");
		banquinhoDeDados.adiciona("batatao");
		banquinhoDeDados.adiciona("hermeserenato");
		banquinhoDeDados.adiciona("bingo");
		banquinhoDeDados.adiciona("trave");
		banquinhoDeDados.adiciona("ufabc");
		banquinhoDeDados.adiciona("mouse");
		
		System.out.println(banquinhoDeDados.busca("Batata"));
		System.out.println(banquinhoDeDados.busca("hermeserenato"));
		System.out.println(banquinhoDeDados.busca("fabaolaf"));
		
		try {
			banquinhoDeDados.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
