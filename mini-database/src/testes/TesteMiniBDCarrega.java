package testes;

import java.io.IOException;

import banco.MiniBD;

public class TesteMiniBDCarrega {

	public static void main(String[] args) {
		MiniBD<String, String> banquinhoDeDados = null;
		
		try {
			banquinhoDeDados = MiniBD.carregaBanco("banquinho", "banquinho-indices", "banquinho-posicoes-vazias", (String s) -> s);
			
			System.out.println("Buscando Batata no banquinho: " + banquinhoDeDados.busca("Batata"));
			System.out.println("Buscando hermeserenato no banquinho: " + banquinhoDeDados.busca("hermeserenato"));
			System.out.println("Buscando fabaolaf no banquinho: " + banquinhoDeDados.busca("fabaolaf"));
			
			System.out.println("Buscando trave no banquinho: " + banquinhoDeDados.busca("trave"));
			System.out.println("Removendo trave do banquinho: " + banquinhoDeDados.remove("trave"));
			System.out.println("Buscando trave no banquinho: " + banquinhoDeDados.busca("trave"));
			
			banquinhoDeDados.close();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
