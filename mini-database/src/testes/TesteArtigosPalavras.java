package testes;

import java.util.ArrayList;
import java.util.List;

import modelo.artigo.Artigo;
import modelo.artigo.PalavraChave;
import modelo.auxiliar.GeradorArtigos;
import modelo.auxiliar.ProcessadorPalavrasChave;

// batata banana antes lapis livro folha

public class TesteArtigosPalavras {

	public static void main(String[] args) {
		GeradorArtigos ga = new GeradorArtigos();
		ArrayList<Artigo> listaArtigos = ga.gera(100, 5, 5);
		
		for(Artigo a : listaArtigos) {
			System.out.println(a);
		}
		
		List<PalavraChave> palavrasChave = ProcessadorPalavrasChave.processa(listaArtigos);
		
		System.out.println("\n\n");
		
		for(PalavraChave pc : palavrasChave) {
			System.out.println(pc);
		}
	}
}
