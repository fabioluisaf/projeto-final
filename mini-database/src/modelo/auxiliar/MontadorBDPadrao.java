package modelo.auxiliar;

import java.util.ArrayList;

import banco.MiniBD;
import modelo.artigo.Artigo;
import modelo.artigo.PalavraChave;

/**
 * Classe que monta um banco de dados padrao, i.e., com artigos
 * gerados aleatoriamente pelo {@code GeradorArtigos}.
 *
 * @see modelo.auxiliar.GeradorArtigos
 */
public class MontadorBDPadrao {
	private MiniBD<PalavraChave, String> banco;
	private String nomeBanco;
	
	/**
	 * Constroi uma instancia da classe.
	 * 
	 * @param nomeBanco nome do banco que sera criado.
	 */
	public MontadorBDPadrao(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	/**
	 * Getter do banco criado por essa classe
	 * 
	 * @return uma instancia de {@code}
	 */
	public MiniBD<PalavraChave, String> getBanco() {
		// caso um banco nao tenha sido criado, crie um
		if(this.banco == null) {
			this.monta();
		}
		
		return banco;
	}
	
	/**
	 * Funcao que cria o banco aleatorio.
	 */
	public void monta() {
		final GeradorArtigos geradorArtigos = new GeradorArtigos();
		final ArrayList<Artigo> artigos = geradorArtigos.gera(100000, 5, 5);
		final ArrayList<PalavraChave> palavrasChave = ProcessadorPalavrasChave.processa(artigos);
		
		this.banco = new MiniBD<>(palavrasChave, (PalavraChave pc) -> pc.getPalavra(), this.nomeBanco);
	}
}
