package modelo.auxiliar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import modelo.artigo.Artigo;

/**
 * Classe que gera um conjunto aleatorio de artigos. Ela existe para propositos
 * de teste.
 *
 */
public class GeradorArtigos {
	/**
	 * Gerador de numeros aleatorios da classe.
	 */
	private Random rng;
	
	/**
	 * Constroi uma instancia da classe.
	 */
	public GeradorArtigos() {
		this.rng = new Random();
	}
	
	/**
	 * Gera um conjunto de artigos de acordo com os parametros especificados.
	 * 
	 * @param qtdArtigos quantidade de artigos que sera gerada.
	 * @param maxPalavrasChave maximo numero de palavras chave que um artigo pode ter.
	 * @param maxAutores maximo numero de autores que um artigo pode ter.
	 * 
	 * @return {@code ArrayList} de artigos gerados aleatoriamente.
	 */
	public ArrayList<Artigo> gera(int qtdArtigos, int maxPalavrasChave, int maxAutores) {	
		ArrayList<Artigo> artigos = new ArrayList<>();
		
		for(int i = 0; i < qtdArtigos; i++) {
			final String titulo = this.geraTitulo();
			final ArrayList<String> autores = this.geraAutores(maxAutores);
			final ArrayList<String> palavrasChave = this.geraPalavrasChave(maxPalavrasChave);
			final String link = this.geraLink(titulo);
			final LocalDate data = this.geraData();
			
			artigos.add(new Artigo(titulo, autores, palavrasChave, data, link));
		}
		
		return artigos;
	}
	
	/**
	 * Gera uma data aleatoria.
	 * 
	 * @return um objeto {@code LocalDate} com uma data aleatoria.
	 */
	private LocalDate geraData() {
		final int anoRandom = pegaAleatorioEntre(1900, 2019);
		final int mesRandom = pegaAleatorioEntre(1, 12);
		final int diaRandom = pegaAleatorioEntre(1, 28);
		
		return LocalDate.of(anoRandom, mesRandom, diaRandom);
	}
	
	/**
	 * Pega um numero aleatorio no intervalo [left, right]
	 * 
	 * @param left comeco do intervalo
	 * @param right fim do intervalo
	 * 
	 * @return um valor aleatorio dentro do intervalo
	 */
	private int pegaAleatorioEntre(int left, int right) {
		return left + (int) Math.round(Math.random() * (right - left));
	}
	
	/**
	 * Gera um link com base no nome do artigo.
	 * 
	 * @param nomeArtigo nome do artigo referente ao link
	 * 
	 * @return String com o link gerado.
	 */
	private String geraLink(String nomeArtigo) {
		return "www.link-" + nomeArtigo + ".edu.br";
	}

	/**
	 * Gera um titulo aleatorio para o artigo.
	 * 
	 * @return titulo gerado.
	 */
	private String geraTitulo() {
		return "Titulo" + this.rng.nextInt();
	}
	
	/**
	 * Gera um conjunto com de palavras chave. As palavras chave geradas por essa
	 * funcao sao no formato "Palavra[numero]", onde [numero] e um numero aleatorio.
	 * A funcao garante que nao serao geradas duas palavras iguais na mesma lista.
	 * 
	 * @param maxPalavras tamanho maximo desse conjunto.
	 * 
	 * @return {@code ArrayList} com ate {@code maxPalavras} palavras chave.
	 */
	private ArrayList<String> geraPalavrasChave(int maxPalavras) {
		ArrayList<Integer> listaAleatoria = this.sequenciaEmbaralhada(maxPalavras*100000);
		ArrayList<String> palavrasAleatorias = new ArrayList<>();
		int qtdPalavras = this.rng.nextInt(maxPalavras) + 1;
		
		for(int i = 0; i < qtdPalavras; i++) {
			palavrasAleatorias.add("Palavra" + listaAleatoria.get(i));
		}
		
		return palavrasAleatorias;
	}

	/**
	 * Gera um conjunto com de autores. Os autores gerados por essa funcao sao
	 * no formato "Autor[numero]", onde [numero] e um numero aleatorio. A funcao
	 * garante que nao serao gerados dois autores iguais na mesma lista.
	 * 
	 * @param maxAutores tamanho maximo desse conjunto.
	 * 
	 * @return {@code ArrayList} com ate {@code maxAutores} autores.
	 */
	private ArrayList<String> geraAutores(int maxAutores) {
		ArrayList<Integer> listaAleatoria = this.sequenciaEmbaralhada(maxAutores*100000);
		ArrayList<String> autoresAleatorios = new ArrayList<>();
		int qtdAutores = rng.nextInt(maxAutores) + 1;
		
		for(int i = 0; i < qtdAutores; i++) {
			autoresAleatorios.add("Autor" + listaAleatoria.get(i));
		}
		
		return autoresAleatorios;
	}
	
	/**
	 * Gera uma sequencia com os numeros de 0 até n-1 em uma ordem aleatoria.
	 * 
	 * @param n quantidade de itens na lista.
	 * 
	 * @return {@code ArrayList} embaralhada com os numeros de 0 a n-1.
	 */
	private ArrayList<Integer> sequenciaEmbaralhada(int n) {
		ArrayList<Integer> lista = new ArrayList<>();
		
		for(int i = 0; i < n; i++) {
			lista.add(i);
		}
		
		Collections.shuffle(lista);
		
		return lista;
	}
}
