package modelo.artigo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa uma palavra chave que sera guardada no banco.
 *
 */
public class PalavraChave implements Serializable {
	// serial da palavra chave. Deve mudar caso haja alguma alteracao que faca com que
	// a nova versao seja incompativel com as anteriores.
	private static final long serialVersionUID = -2780337079761920801L;
	private String palavra;
	private List<Artigo> artigos;
	
	/**
	 * Constroi um objeto {@code palavraChave} a partir da palavra passada, com uma lista
	 * vazia de artigos.
	 * 
	 * @param palavra palavra referente a palavra chave.
	 */
	public PalavraChave(String palavra) {
		this(palavra, null);
	}
	
	/**
	 * Constroi um objeto {@code palavraChave} a partir da palavra e da lista passada.
	 * 
	 * @param palavra palavra referente a palavra chave.
	 * @param artigos lista de artigos que possuem a palavra chave.
	 */
	public PalavraChave(String palavra, List<Artigo> artigos) {
		this.palavra = palavra;
		this.artigos = new ArrayList<>();
		
		for(Artigo a : artigos) {
			this.addArtigo(a);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append(this.palavra + ": ");
		
		for(Artigo a : this.artigos) {
			res.append("\n" + a);
		}
		
		return res.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof PalavraChave)) {
			return false;
		}
		
		PalavraChave outra = (PalavraChave) obj;
		return outra.palavra.equals(this.palavra);
	}
	
	/**
	 * Getter de da lista de artigos.
	 * 
	 * @return uma lista nao modificavel com os artigos da palavra chave.
	 */
	public List<Artigo> getArtigos() {
		return Collections.unmodifiableList(this.artigos);
	}
	
	/**
	 * Getter da palavra chave.
	 * 
	 * @return a palavra referente a palavra chave.
	 */
	public String getPalavra() {
		return this.palavra;
	}
	
	/**
	 * Adiciona um artigo na lista de artigos atual.
	 * 
	 * @param artigo o artigo que sera adicionado.
	 */
	public void addArtigo(Artigo artigo) {
		List<String> palavrasChaveArtigo = artigo.getPalavrasChave();
		
		// so adiciona um artigo se ele contem a palavra chave
		if(palavrasChaveArtigo.contains(this.palavra)) {
			this.artigos.add(artigo);
		} else {
			System.out.println("O artigo " + artigo + "nao contem a palavra " + this.palavra);
		}
	}
	
	/**
	 * Remove um artigo da lista de artigos.
	 * 
	 * @param artigo artigo que sera removido
	 */
	public void removeArtigo(Artigo artigo) {
		this.artigos.remove(artigo);
	}
	
	/**
	 * Verifica se um artigo esta na lista de artigos.
	 * 
	 * @param artigo arigo que sera verificado.
	 * 
	 * @return {@code true} caso o artigo esteja na lista de artigos,
	 * {@code false} caso contrario
	 */
	public boolean contemArtigo(Artigo artigo) {
		return this.artigos.contains(artigo);
	}
}
