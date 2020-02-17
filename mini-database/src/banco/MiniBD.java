package banco;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.auxiliar.GuardadorObjeto;
import modelo.btree.BTree;

/**
 * Classe que representa um banco de dados simplificado, capaz de armazenar um grupo de objetos
 * e fazer pesquisas de forma eficiente de um ponto de vista assintotico e de um ponto de vista
 * real, levando em conta que o SO precisara tirar e colocar coisas na memoria principal. 
 * 
 * </p>Os registros sao armazenados em um Array e o banco mantem uma arvore B para guardar a
 * informacao sobre qual o indice dos registros nesse array. Como o array não é ordenado, todas
 * as chaves precisam estar na arvore B, assim, sacrificamos um pouco de memoria, mas economizamos
 * em operacoes de reordenacao do array de registros. 
 * 
 * </p>Quando um registro e removido sua posicao nao e preenchida, mas e guardada em outro array para que, 
 * no futuro, um novo registro seja inserido nessa posicao. Como um espaco vazio no meio do array ocupa
 * bem menos espaco que um objeto normal, nao ha um consumo significativo de memoria. O ganho que temos e
 * de performance, ja que se reordenassemos o array a cada remocao, precisariamos reordenar a arvore de
 * indices tambem.
 * 
 *
 * @param <T> O tipo do objeto guardado. Precisa implementar a interface {@code Serializable} para que ele
 * possa ser salvo num arquivo.
 * 
 * @param <K> O tipo da chave primaria dos objetos. Implementa a interface {@code Comparable<K>} para que
 * duas chaves possam ser comparadas.
 * 
 * @see java.io.Serializable
 * @see java.lang.Comparable
 */
public class MiniBD<T extends Serializable, K extends Comparable<K>> implements AutoCloseable{
	/**
	 * Lista de indices vazios na tabela
	 */
	private ArrayList<Integer> posicoesVazias;
	/**
	 * Objeto que toma conta de ler/escrever o array acima em um arquivo
	 */
	private GuardadorObjeto<ArrayList<Integer>> guardadorPosicoesVazias;
	
	/**
	 *  Tabela de registros do banco
	 */
	private ArrayList<T> tabela;
	/**
	 *  Objeto que toma conta de ler/escrever o array acima em um arquivo 
	 */
	private GuardadorObjeto<ArrayList<T>> guardadorTabela;
	
	/**
	 *  Arvore que guarda o arquivo de indices
	 */
	private BTree<K> indices;
	/**
	 *  Objeto que toma conta de ler/escrever o array acima em um arquivo
	 */
	private GuardadorObjeto<BTree<K>> guardadorIndices;
	
	/**
	 *  Funcao que diz qual a chave primaria dos objetos guardados
	 */
	private FuncaoChavePrimaria<T, K> funcaoChavePrimaria;
	
	/**
	 * Construtor padrao. Ele e {@code private} pois so e usado dentro da propria classe
	 */
	private MiniBD() {}
	
	/**
	 * Constroi um banco de dados vazio. Caso a String {@code nomeBanco} se refira a um
	 * arquivo que ja esta sendo usado, esse sera reescrevido.
	 * 
	 * 
	 * @param funcaoChavePrimaria funcao que pega a chave primaria de um objeto armazenado no banco.
	 * 
	 * @param nomeBanco nome do arquivo onde sera guardado o banco. os arquivos auxiliares usarao esse
	 * nome como base.
	 */
	public MiniBD(FuncaoChavePrimaria<T, K> funcaoChavePrimaria, String nomeBanco) {
		this(null, funcaoChavePrimaria, nomeBanco);
	}
	
	/**
	 * Construtor de um banco de dados a partir de uma tabela de dados ja existente.
	 * 
	 * 
	 * @param tabela a tabela de dados que sera guardada no banco.
	 * 
	 * @param funcaoChavePrimaria funcao que pega a chave primaria de um objeto armazenado no banco.
	 * 
	 * @param nome do arquivo onde sera guardado o banco. os arquivos auxiliares usarao esse
	 * nome como base. 
	 */
	public MiniBD(List<T> tabela, FuncaoChavePrimaria<T, K> funcaoChavePrimaria, String nomeBanco) {
		// impede que o banco nao tenha uma chave primaria
		if(funcaoChavePrimaria == null) {
			throw new NullPointerException("O objeto guardado deve ter uma chave primaria!!");
		}
		
		// criando a tabela com base na tabela passada
		this.tabela = tabela == null ? new ArrayList<>() : new ArrayList<>(tabela);
		// cria a arvore do arquivo de indices. o grau maximo e 20 para manter um equilibrio entre
		// a altura da arvore e a quantidade de chaves armazenada em um node da arvore.
		this.indices = new BTree<>(20);
		this.posicoesVazias = new ArrayList<>();
		this.funcaoChavePrimaria = funcaoChavePrimaria;
		
		// criando os objetos que salvam as componentes do banco nos arquivos adequados
		this.guardadorTabela = new GuardadorObjeto<ArrayList<T>>(nomeBanco);
		this.guardadorIndices = new GuardadorObjeto<BTree<K>>(nomeBanco + "-indices");
		this.guardadorPosicoesVazias = new GuardadorObjeto<ArrayList<Integer>>(nomeBanco + "-posicoes-vazias");
		
		// adicionando as chaves no arquivo de indices
		for(int i = 0; i < this.tabela.size(); i++) {
			K chave = funcaoChavePrimaria.get(this.tabela.get(i));
			this.indices.insere(chave, i);
		}
	}
	
	/**
	 * Cria um banco de dados com base nos arquivos passados.
	 * 
	 * 
	 * @param <T> tipo do objeto guardado no banco.
	 * 
	 * @param <K> tipo da chave primaria dos objetos.
	 * 
	 * @param arquivoTabela nome do arquivo onde se encontra a tabela de dados.
	 * 
	 * @param arquivoIndices nome do arquivo onde se encontra a arvore de indices.
	 * 
	 * @param arquivoPosicoesVazias nome do arquivo onde se encontra o array de posicoes 
	 * vazias da tabela.
	 * 
	 * @param funcaoChavePrimaria funcao que pega a chave primaria dos objetos armazenados.
	 * 
	 * 
	 * @return um objeto do tipo MiniBD, com componentes criadas a partir dos arquivos passados
	 * 
	 * 
	 * @throws ClassNotFoundException Caso existam classes faltando no programa.
	 * @throws IOException Caso ocorra algum erro na leitura dos arquivos.
	 * 
	 * @see java.lang.ClassNotFoundException
	 * @see java.io.IOException
	 */
	public static<T extends Serializable, K extends Comparable<K>> MiniBD<T, K> carregaBanco(String arquivoTabela, 
			String arquivoIndices, String arquivoPosicoesVazias, FuncaoChavePrimaria<T, K> funcaoChavePrimaria) 
			throws ClassNotFoundException, IOException {
		MiniBD<T, K> banco = new MiniBD<>();
		
		// criando os objetos que leem os arquivos
		banco.guardadorTabela = new GuardadorObjeto<ArrayList<T>>(arquivoTabela);
		banco.guardadorIndices = new GuardadorObjeto<BTree<K>>(arquivoIndices);
		banco.guardadorPosicoesVazias = new GuardadorObjeto<ArrayList<Integer>>(arquivoPosicoesVazias);
		
		// lendo os arquivos
		banco.posicoesVazias = banco.guardadorPosicoesVazias.carregaObjeto();
		banco.tabela = banco.guardadorTabela.carregaObjeto();
		banco.indices = banco.guardadorIndices.carregaObjeto();
		banco.funcaoChavePrimaria = funcaoChavePrimaria;
		
		return banco;
	}
	
	/**
	 * Getter da tabela de dados.
	 * 
	 * @return a tabela de dados.
	 */
	public List<T> getTabela() {
		return Collections.unmodifiableList(this.tabela);
	}

	@Override
	public void close() throws IOException {
		this.guardadorIndices.salvaObjeto(this.indices);
		this.guardadorTabela.salvaObjeto(this.tabela);
		this.guardadorPosicoesVazias.salvaObjeto(this.posicoesVazias);
	}
	
	/**
	 * Adiciona um registro no banco.
	 * 
	 * @param registro registro que sera adicionado.
	 */
	public void adiciona(T registro) {
		// pega a chave primaria do registro
		K chave = this.funcaoChavePrimaria.get(registro);
		
		// so adiciona chaves que nao estejam no banco.
		if(this.indices.busca(chave) == -1) {
			if(this.posicoesVazias.size() != 0) {
				// caso existam posicoes vazias no meio da tebela, adicione la
				int posicao = this.posicoesVazias.remove(0);
				this.tabela.set(posicao, registro);
				this.indices.insere(chave, posicao);
			} else {
				// caso contrario, adicione no final
				int posicao = this.tabela.size();
				this.tabela.add(registro);
				this.indices.insere(chave, posicao);
			}
		}
	}
	
	/**
	 * Remove um registro do banco.
	 * 
	 * @param chave chave primaria do registro que sera removido.
	 * 
	 * @return o registro removido, ou {@code null}, caso ele nao esteja
	 * no banco.
	 */
	public T remove(K chave) {
		// procura a posicao do registro
		int posicao = this.indices.busca(chave);
		
		if(posicao != -1) {
			// caso ele esteja na arvore, remove e retorna ele
			T registro = this.tabela.get(posicao);
			
			// removendo da tabela, do arquivo de indices, e colocando
			// a posicao no array de posicoes vazias
			this.tabela.set(posicao, null);
			this.indices.remove(chave);
			this.posicoesVazias.add(posicao);
			
			return registro;
		} else {
			// caso ele nao esteja na arvore, retorne null
			return null;
		}
	}
	
	/**
	 * Busca uma chave na arvore.
	 * 
	 * @param chave chave buscada.
	 * 
	 * @return registro associado a essa chave, ou null, caso
	 * nenhum registro esteja associado a essa chave.
	 */
	public T busca(K chave) {
		int posicao = this.indices.busca(chave);
		
		if(posicao != -1) {		
			return this.tabela.get(posicao);
		} else {
			return null;
		}
	}
}
