package modelo.artigo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa um artigo.
 *
 */
public class Artigo implements Serializable {
	// serial do objeto. caso sejam feitas alteracoes importantes no objeto, que fariam
	// ele ficar incompativel com versoes anteriores, esse numero deve ser alterado para
	// refletir isso.
	private static final long serialVersionUID = 5144118450258209477L;
	private String titulo;
	private List<String> palavrasChave;
	private List<String> autores;
	private LocalDate dataPublicacao;
	private String link;
	
	/**
	 * Constroi um artigo a partir das informacoes passadas.
	 * 
	 * @param titulo titulo do artigo.
	 * @param autores autores do artigo.
	 * @param palavrasChave palavras chave do artigo.
	 * @param dataDePublicacao data de publicacao do artigo.
	 * @param link link para encontrar o artigo online.
	 */
	public Artigo(String titulo, List<String> autores, List<String> palavrasChave, LocalDate dataDePublicacao, String link) {
		if(palavrasChave == null || palavrasChave.size() < 1) {
			throw new RuntimeException("Artigo tem que ter pelo menos uma palavra chave!!");
		}
		
		this.autores = autores == null ? new ArrayList<>() : new ArrayList<>(autores);
		this.palavrasChave = new ArrayList<>(palavrasChave);
		this.titulo = titulo;
		this.dataPublicacao = dataDePublicacao;
		this.link = link;
	}
	
	/**
	 * Getter das palavras chave do artigo.
	 * 
	 * @return uma lista nao modificavel das palavras chave do artigo.
	 */
	public List<String> getPalavrasChave() {
		return Collections.unmodifiableList(this.palavrasChave);
	}
	
	/**
	 * Getter do titulo do artigo.
	 * 
	 * @return titulo do artigo.
	 */
	public String getTitulo() {
		return titulo;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("{ Titulo: " + this.titulo + ", publicacao: " + this.dataPublicacao);
		res.append(", autores: " + this.autores + ", palavras chave: " + this.palavrasChave);
		res.append(", link: " + this.link + " }");
		
		return res.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Artigo)) {
			return false;
		} else {;
			return this.titulo.equals(((Artigo) obj).titulo);
		}
	}
}
