package modelo.auxiliar;

import java.util.ArrayList;

import modelo.artigo.Artigo;
import modelo.artigo.PalavraChave;

/**
 * Classe que processa um conjunto de artigos e cria uma lista de objetos
 * {@code PalavraChave} onde, cada palavra chave contem uma lista dos artigos
 * onde ela aparece.
 *
 */
public class ProcessadorPalavrasChave {
	
	/**
	 * Funcao que processa a lista de artigos. Formalmente falando, uma palavra
	 * chave esta na lista retornada por essa funcao se e somente se ela aparece
	 * em ao menos um artigo.
	 * 
	 * 
	 * @param artigos a lista de artigos que sera processada.
	 * 
	 * @return {@code ArrayList} de palavras chave, onde cada uma possui uma
	 * lista dos artigos nos quais ela aparece.
	 */
	public static ArrayList<PalavraChave> processa(ArrayList<Artigo> artigos) {
		ArrayList<PalavraChave> palavras = new ArrayList<>();
		
		// repete para cada artigo
		for(Artigo a : artigos) {
			// repete para cada palavra chave do artigo
			for(String pc : a.getPalavrasChave()) {
				// cria um objeto PalavraChave referente a palavra atual
				final PalavraChave pcDummy = new PalavraChave(pc);
				
				if(palavras.contains(pcDummy)) {
					// caso essa palavra ja esteja na lista, adicione o artigo atual
					// na lista de artigos dessa palavra
					final int posicao = palavras.indexOf(pcDummy);
					PalavraChave pcReal = palavras.get(posicao);
					
					pcReal.addArtigo(a);
				} else {
					// caso contrario, adicione o artigo na lista de artigos da palavra
					// chave e adicione a palavra chave na lista de palavras.
					pcDummy.addArtigo(a);
					palavras.add(pcDummy);
				}
			}
		}
		
		return palavras;
	}
}
