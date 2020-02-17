package modelo.btree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe do node de uma arvore. Ela armazena dois {@code ArrayList}, um para
 * as chaves e outro para os indices associados e essas chaves. 
 *
 * @param <K> tipo de chave armazenada na arvore. Deve implementar a interface
 * {@code Comparable}, para que as chaves possam ser comparadas.
 * 
 * @see Comparable
 */
public class Node<K extends Comparable<K>> implements Serializable {
	private static final long serialVersionUID = -8402442685097892671L;
	private ArrayList<K> chaves;
	private ArrayList<Integer> indices;
	
	/**
	 * {@code List} de filhos de um dado no. Essa lista possui um tamanho maximo que e
	 * controlado pelo proprio node. Cada filho representa um intervalo de nos, por exemplo
	 * o filho 0 representa todas as chaves menores que a primeira chave, o filho 1 representa
	 * as chaves que sao maiores que a primeira e menores que a segunda, e assim por diante.
	 */
	private List<Node<K>> filhos;
	
	/**
	 * Um {@code int} que representa a quantidade maxima de filhos que esse node pode ter.
	 * Alem disso, vale lembrar que o no deve ter entre {@code grauMaximo - 1}/2 e {@code grauMaximo - 1} elementos.
	 */
	private int grauMaximo;
	
	/**
	 * Construtor de um no com capacidade para {@code grauMaximo - 1} elementos.
	 * 
	 * @param grauMaximo	grauMaximo do node.
	 * 
	 */
	public Node(int grauMaximo) {
		this(grauMaximo, null, null, null);
	}
	
	/**
	 * Constroi um Node com capacidade para {@code grauMaximo} elementos, com as 
	 * chaves, indices e filhos passados como parametro.
	 * 
	 * @param grauMaximo a quantidade maxima de filhos que o node pode ter.
	 * @param chaves as chaves que o node tera.
	 * @param indices os indices que o node tera.
	 * @param filhos os filhos de um node.
	 */
	public Node(int grauMaximo, ArrayList<K> chaves, ArrayList<Integer> indices, List<Node<K>> filhos) {
		this.grauMaximo = grauMaximo;
		
		// caso as chaves e os indices nao sejam ambos null, use eles, caso
		// contrario, um par novo e usado
		if(chaves != null && indices != null && chaves.size() == indices.size()) {
			this.chaves = new ArrayList<>(chaves);
			this.indices = new ArrayList<>(indices);
		} else {
			this.chaves = new ArrayList<>();
			this.indices = new ArrayList<>();
		}
		
		
		this.filhos = filhos == null ? new ArrayList<>() : new ArrayList<>(filhos);
	}
	
	/**
	 * Getter da lista de filhos do node.
	 * 
	 * @return {@code List} nao modificavel com os filhos do node.
	 */
	public List<Node<K>> getFilhos() {
		return Collections.unmodifiableList(this.filhos);
	}
	
	/**
	 * Getter de grauMaximo do node.
	 * 
	 * @return grauMaximo do node.
	 */
	public int getgrauMaximo() {
		return grauMaximo;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("{ " + super.toString() + " - { ");
		
		for(int i = 0; i < this.chaves.size(); i++) {
			res.append(this.chaves.get(i) + "=" + this.indices.get(i) + " ");
		}
		
		res.append("} - [ ");
		
		for(Node<K> filho : this.filhos) {
			res.append(filho.refId() + " ");
		}
		
		res.append("] }\n");
		
		for(Node<K> filho : this.filhos) {
			res.append(filho.toString());
		}
		
		return res.toString();
	}
	
	/**
	 * O toString() da classe object.
	 * 
	 * @return Uma string contendo o endereco do objeto.
	 */
	private String refId() {
		return super.toString();
	}
	
	/**
	 * Procura o indice associado com a {@code chave}.
	 * 
	 * @param chave chave buscada.
	 * @return o indice associado com a chave, ou -1, caso
	 * ela nao esteja na arvore.
	 */
	public int busca(K chave) {
		// caso a chave esteja no node atual, essa variavel contem 
		// a posicao da chave, caso contrario, ela contem a posicao
		// do filho referente ao intervalo onde essa chave pode estar
		int intervalo = this.getIntervalo(chave);
		
		if(intervalo < this.chaves.size() && this.chaves.get(intervalo).compareTo(chave) == 0) {
			// se estiver no node atual, retorne o indice referente a chave
			return this.indices.get(intervalo);
		} else if(this.filhos.size() != 0) {
			// caso contrario, se o node atual possuir filhos, procure no
			// filho correto
			return this.filhos.get(intervalo).busca(chave);
		} else {
			// caso contrario, a chave nao esta na arvore
			return -1;
		}
	}
	
	/**
	 * Insere um par (chave, indice) na arvore, fazendo as alteracoes necessarias 
	 * para mante-la balanceada. Por padra, chaves repetidas nao sao adicionadas,
	 * mas indices repetidos podem ser adicionados, mas essa checagem deve ser feita
	 * antes de chamar a funcao.
	 * 
	 * @param chave a chave a ser inserida.
	 * @param indice o indice referente aquela chave.
	 */
	public void insere(K chave, int indice) {
		// caso a chave esteja no node atual, essa variavel contem 
		// a posicao da chave, caso contrario, ela contem a posicao
		// do filho referente ao intervalo onde essa chave pode estar
		final int intervalo = this.getIntervalo(chave);
		
		// so insira caso a chave nao esteja na arvore
		if(!(intervalo < this.chaves.size() && this.chaves.get(intervalo).compareTo(chave) == 0)) {
			if(this.filhos.size() == 0) {
				// caso o node atual seja uma folha, adiciona a chave na posicao correta
				if(intervalo == this.chaves.size()) {
					this.add(chave, indice);
				} else {
					this.add(intervalo, chave, indice);
				}
			} else {
				// caso contrario, adiciona recursivamente no filho correto, fazendo os
				// balanceamentos necessarios
				final Node<K> filhoAlterado = this.filhos.get(intervalo);
				filhoAlterado.insere(chave, indice);
				
				// verifique se ha overflow e corrija caso necessario
				this.corrigeOverflowEm(intervalo);
			}
		}	
	}
	
	/**
	 * Remove {@codo chave} da arvore, fazendo os balanceamentos necessarios.
	 * 
	 * @param chave chave a ser removida.
	 */
	public void remove(K chave) {
		// caso a chave esteja no node atual, essa variavel contem 
		// a posicao da chave, caso contrario, ela contem a posicao
		// do filho referente ao intervalo onde essa chave pode estar
		final int intervalo = this.getIntervalo(chave);
		
		if(intervalo < this.chaves.size() && this.chaves.get(intervalo).compareTo(chave) == 0 && this.filhos.size() == 0) {
			// estamos em um node folha e encontramos o elemento no node atual
			this.chaves.remove(intervalo);
			this.indices.remove(intervalo);
		} else if(intervalo < this.chaves.size() && this.chaves.get(intervalo).compareTo(chave) == 0) {
			// estamos em um node interno e encontramos o elemento
			// removendo o elemento do node atual
			final int valorChave = this.indices.remove(intervalo);
			this.chaves.remove(chave);
			
			// pega o node mais a direita da subarvore esquerda
			final Node<K> filhoEsq = this.filhos.get(intervalo);
			final Node<K> maiorEsq = filhoEsq.biggest();
			
			// pega a maior chave desse node, i.e., a maior chave da subarvore
			// da esquerda
			int qtdChavesEsq = maiorEsq.chaves.size();
			
			K maiorChaveEsq = maiorEsq.chaves.remove(qtdChavesEsq - 1);
			int valorMaiorChave = maiorEsq.indices.remove(qtdChavesEsq - 1);
			
			// troca a chave atual com a maior da esquerda, colocando a chave que sera
			// removida em um node folha
			maiorEsq.add(chave, valorChave);
			this.add(intervalo, maiorChaveEsq, valorMaiorChave);
			
			// pede para a subarvore esquerda remover a chave realocada e corrige
			// possiveis underflows
			filhoEsq.remove(chave);
			this.corrigeUnderflowEm(intervalo);
		} else if (this.filhos.size() != 0){
			// a chave nao esta no node atual, entao procuramos ela no node
			// filho apropriado e corrigimos possiveis underflows
			Node<K> filhoAlterado = this.filhos.get(intervalo);
			filhoAlterado.remove(chave);
			this.corrigeUnderflowEm(intervalo);
		}
	}
	
	/**
	 * Funcao para adicionar um par chave-indice na ultima
	 * posicao do node atual.
	 * 
	 * @param chave chave a ser adicionada.
	 * @param indice indice associado a essa chave.
	 */
	private void add(K chave, int indice) {
		this.chaves.add(chave);
		this.indices.add(indice);
	}
	
	/**
	 * Adiciona uma chave em {@code posicao}, deslocando as chaves
	 * seguintes para direita.
	 * 
	 * @param posicao posicao onde a chave sera adicionada.
	 * @param chave chave a ser adicionada.
	 * @param indice indice associado a essa chave.
	 */
	private void add(int posicao, K chave, int indice) {
		this.chaves.add(posicao, chave);
		this.indices.add(posicao, indice);
	}
	
	/**
	 * Muda o valor da chave que esta em {@code posicao}.
	 * 
	 * @param posicao posicao da chave que sera mudada.
	 * @param chave nova chave que ficara na posicao indicada.
	 * @param indice indice associado a essa nova chave.
	 */
	private void set(int posicao, K chave, int indice) {
		this.chaves.set(posicao, chave);
		this.indices.set(posicao, indice);
	}

	/**
	 * Procura se um indice ja esta na arvore.
	 * 
	 * @param indice o indice buscado.
	 * @return {@code true} se {@code indice} esta na arvore, ou {@code false}
	 * caso contrario.
	 */
	public boolean temIndice(int indice) {
		// procura no node atual
		if(this.indices.contains(indice)) {
			return true;
		}
		
		// procura recursivamente em todos os filhos
		for(Node<K> filho : this.getFilhos()) {
			if(filho.temIndice(indice)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Procura uma chave no node atual. Caso ela esteja, o 
	 * retorno da funcao representa a posicao dessa chave.
	 * Caso contrario, o retorno dessa funcao representa o
	 * filho onde essa chave deve estar. Em qualquer um dos
	 * casos, o retorno 'r' dessa funcao referencia as chaves 
	 * armazenadas que sejam menores ou iguais a chave que esta
	 * na posicao 'r' do node atual.
	 * 
	 * </p>A busca efetuada e uma busca binaria.
	 * 
	 * @param chave chave buscada.
	 * @return a posicao da chave no node, caso ela esteja nele
	 * ou a posicao do filho que deve conter essa chave.
	 */
	private int getIntervalo(K chave) {
		int left = 0;
		int right = this.chaves.size() - 1;
		
		while(left <= right) {
			int middle = (int) Math.floor((left + right)/2);
			K elemMeio = this.chaves.get(middle);
			
			if(elemMeio.compareTo(chave) == 0) {
				return middle;
			} else if(elemMeio.compareTo(chave) < 0) {
				left = middle + 1;
			} else {
				right = middle - 1;
			}
		}
		
		return left;
	}
	
	/**
	 * Divide um node em 2 metades, uma com as chaves menores que a mediana e outra com
	 * as maiores que a mediana.
	 * 
	 * @return Uma {@code ArrayList} contendo dois nodes, cada um com as chaves correspondentes
	 * a uma das metades descritas acima.
	 */
	public ArrayList<Node<K>> splitNode() {
		final int mediana = (int) Math.ceil(grauMaximo/2);
		
		// salvando os dados de cada uma das metades, excluindo a mediana
		final ArrayList<K> chavesEsq = new ArrayList<>(this.chaves.subList(0, mediana));
		final ArrayList<Integer> valoresEsq = new ArrayList<>(this.indices.subList(0, mediana));
		final ArrayList<K> chavesDir = new ArrayList<>(this.chaves.subList(mediana + 1,	this.chaves.size()));
		final ArrayList<Integer> valoresDir = new ArrayList<>(this.indices.subList(mediana + 1, this.indices.size()));
		
		// criando a lista de filhos de um node. Essa lista sera vazia caso o no que esta
		// sendo dividido nao tiver filhos
		ArrayList<Node<K>> filhosEsq = null;
		ArrayList<Node<K>> filhosDir = null;
		
		// caso o node dividito tenha filhos, salve metade deles em cada uma das listas
		if(this.filhos.size() > 0) {
			final ArrayList<Node<K>> filhos = new ArrayList<>(this.getFilhos());
			
			filhosEsq = new ArrayList<>(filhos.subList(0, mediana + 1));
			filhosDir = new ArrayList<>(filhos.subList(mediana + 1, filhos.size()));
		}
		
		// criando cada um dos nos esquerdo(metade menor) e direito(metade maior)
		final Node<K> filhoEsq = new Node<>(this.grauMaximo, chavesEsq, valoresEsq, filhosEsq);
		final Node<K> filhoDir = new Node<>(this.grauMaximo, chavesDir, valoresDir, filhosDir);

		ArrayList<Node<K>> resultado = new ArrayList<>();
		
		// salvando os filhos para retornar
		resultado.add(filhoEsq);
		resultado.add(filhoDir);
		
		return resultado;
	}
	
	/**
	 * Corrige o filho na posicao {@code intervalo} do node atual, i.e., quebra o no em
	 * 3 partes, a mediana, os menores que a mediana, a mediana e os maiores que a mediana.
	 * Caso o filho correspondente a posicao nao esteja com overflow, a funcao nao faz nada.
	 * 
	 * @param intervalo a posicao do filho que vai ser checado.
	 */
	public void corrigeOverflowEm(int intervalo) {
		// acessando o filho em questao
		final Node<K> filhoAnalisado = this.filhos.get(intervalo);
		
		// se tiver overflow, corrija
		if(filhoAnalisado.overflow()) {
			// variaveis para separar as partes desse filho com overflow, elas servem
			// para facilitar o acesso aos dados e filhos desse filho com overflow
			final int mediana = (int) Math.ceil(filhoAnalisado.getgrauMaximo()/2);
			
			// quebrando o node em 3:
			// - Chave mediana
			// - Chaves menores que a mediana
			// - Chaves maiores que a mediana
			final K chaveMediana = filhoAnalisado.chaves.get(mediana);
			final int valorChaveMediana = filhoAnalisado.indices.get(mediana);
			final ArrayList<Node<K>> novosNodes = filhoAnalisado.splitNode();
			
			// colocando a chave mediana no node atual
			this.add(intervalo, chaveMediana, valorChaveMediana);
			
			// arrumando o intervalo entre a nova chave e a chave anterior
			this.filhos.set(intervalo, novosNodes.get(0));
			
			// arrumando o intervalo entre a nova chave e a chave seguinte
			if(intervalo == this.filhos.size() - 1) {
				// caso a nova chave seja a ultima, o intervalo apos ela e
				// o ultimo da lista
				this.filhos.add(novosNodes.get(1));
			} else {
				// caso contrario, coloque o novo intervalo no meio dos que
				// existiam e desloque os que estavam la para a direita
				this.filhos.add(intervalo + 1, novosNodes.get(1));
			}
		}
	}
	
	/**
	 * Corrige o filho da posicao {@code intervalo}, caso haja necessidade,
	 * caso contrario, nao faz nada.
	 * 
	 * @param intervalo a posicao do filho que sera checado.
	 */
	public void corrigeUnderflowEm(int intervalo) {
		// acessando o filho em questao
		final Node<K> filhoAnalisado = this.getFilhos().get(intervalo);
		
		// se tiver underflow, corrija
		if(filhoAnalisado.underflow()) {
			if(intervalo - 1 >= 0 && !this.getFilhos().get(intervalo - 1).temMinElementos()) {
				// se existe algum node na esquerda do analisado e ele nao esta
				// com a quantidade minima de elementos
				this.rotacionaComEsq(intervalo);
			} else if (intervalo + 1 < this.getFilhos().size() && !this.getFilhos().get(intervalo + 1).temMinElementos()) {
				// se existe algum node na direita do analisado e ele nao esta
				// com a quantidade minima de elementos
				this.rotacionaComDir(intervalo);
			} else if (intervalo - 1 >= 0) {
				// caso contrario, merge com o node da esquerda, se existir
				this.mergeComEsq(intervalo);
			} else {
				// caso contrario, merge com o node da direita
				this.mergeComDir(intervalo);
			}
		}
	}
	
	/**
	 * Faz a rotacao com o filho a esquerda de {@code posicao}, i.e.,
	 * passa um elemento da esquerda para o pai e um do pai para {@code
	 * posicao}. O filho que fica sobrando na esquerda vai para {@code 
	 * posicao}.
	 * 
	 * @param posicao o node com underflow.
	 */
	private void rotacionaComEsq(int posicao) {
		// variaveis para facilitar o acesso ao filho com underflow e
		// o seu irmao esquerdo
		Node<K> filhoUnderflow = this.getFilhos().get(posicao);
		Node<K> irmaoEsq = this.getFilhos().get(posicao - 1);

		int qtdFilhosEsq = irmaoEsq.getFilhos().size();
		Node<K> dirFilhoEsq;
		
		// pegando o filho a direita do irmao esquerdo, caso ele exista
		if(qtdFilhosEsq > 0) {
			dirFilhoEsq = irmaoEsq.getFilhos().get(qtdFilhosEsq - 1);
			irmaoEsq.filhos.remove(qtdFilhosEsq - 1);
		} else {
			dirFilhoEsq = null;
		}
		
		// pegando a chave separadora do filho com underflow e o seu irmao
		K chave = this.chaves.get(posicao - 1);
		int valorChave = this.indices.get(posicao - 1);
		
		// pegando a chave do irmao esquerdo
		K chaveEsq = irmaoEsq.chaves.remove(irmaoEsq.chaves.size() - 1);
		int valorChaveEsq = irmaoEsq.indices.remove(irmaoEsq.indices.size() - 1);
		
		// alterando as chaves do node atual e do node com underflow
		this.set(posicao - 1, chaveEsq, valorChaveEsq);
		filhoUnderflow.add(0, chave, valorChave);
		
		// caso tenha um filho a direita do irmao esquerdo, coloque ele no filho
		// com underflow
		if(dirFilhoEsq != null) {
			filhoUnderflow.filhos.add(0, dirFilhoEsq);
		}
	}

	/**
	 * Faz a rotacao com o filho a direita de {@code posicao}, i.e.,
	 * passa um elemento da direita para o pai e um do pai para {@code
	 * posicao}. O filho que fica sobrando na direita vai para {@code 
	 * posicao}.
	 * 
	 * @param posicao o node com underflow.
	 */
	private void rotacionaComDir(int posicao) {
		// variaveis para facilitar o acesso ao filho com underflow
		// e seu irmao direito
		Node<K> filhoUnderflow = this.getFilhos().get(posicao);
		Node<K> irmaoDir = this.getFilhos().get(posicao + 1);
		
		Node<K> esqFilhoDir;
		
		// caso o irmao direito tenha filhos, pegue o filho menor
		if(irmaoDir.filhos.size() > 0) {
			esqFilhoDir = irmaoDir.getFilhos().get(0);
			irmaoDir.filhos.remove(0);
		} else {
			esqFilhoDir = null;
		}
		
		// salvando a chave separadora entre o filho com underflow
		// e seru irmao direito
		K chave = this.chaves.get(posicao);
		int valorChave = this.indices.get(posicao);
		
		// salvando a menor chave do irmao direito
		K chaveDir = irmaoDir.chaves.remove(0);
		int valorChaveDir = irmaoDir.indices.remove(0);
		
		// alterando a chave no node atual e no node com underflow
		this.set(posicao, chaveDir, valorChaveDir);
		filhoUnderflow.add(chave, valorChave);
		
		// caso existisse algum filho no irmao direito, adicione ele
		// no node com overflow
		if(esqFilhoDir != null) {
			filhoUnderflow.filhos.add(esqFilhoDir);
		}
	}

	/**
	 * Merge o node com underflow com o seu irmao esquerdo.
	 * 
	 * @param posicao a posicao do node com underflow
	 */
	private void mergeComEsq(int posicao) {
		// variaveis para facilitar o accesso ao node com underflow
		// e seu irmao esquerdo
		final Node<K> filhoUnderflow = this.filhos.get(posicao);
		final Node<K> irmaoEsq = this.filhos.get(posicao - 1);
		
		// pegando a chave separadora entre o filho com underflow
		// e o seu irmao esquerdo
		final K chaveSeparadora = this.chaves.remove(posicao - 1);
		final int valorChaveSeparadora = this.indices.remove(posicao - 1);
		
		// componentes do novo node
		ArrayList<K> chavesNovoFilho = new ArrayList<>();
		ArrayList<Integer> valoresNovoFilho = new ArrayList<>();
		ArrayList<Node<K>> filhosNovoFilho = new ArrayList<>();
		
		// adicionando todas as chaves do irmao esquerdo no novo node
		for(int i = 0; i < irmaoEsq.chaves.size(); i++) {
			chavesNovoFilho.add(irmaoEsq.chaves.get(i));
			valoresNovoFilho.add(irmaoEsq.indices.get(i));
		}
		
		// adicionando a chave separadora no novo nodes
		chavesNovoFilho.add(chaveSeparadora);
		valoresNovoFilho.add(valorChaveSeparadora);
		
		// adicionando as chaves do filho com underflow no novo node
		for(int i = 0; i < filhoUnderflow.chaves.size(); i++) {
			chavesNovoFilho.add(filhoUnderflow.chaves.get(i));
			valoresNovoFilho.add(filhoUnderflow.indices.get(i));
		}
		
		// adicionando todos os filhos do filho com underflow e do irmao
		// esquerdo no novo node
		filhosNovoFilho.addAll(irmaoEsq.filhos);
		filhosNovoFilho.addAll(filhoUnderflow.filhos);
		
		final Node<K> novoFilho = new Node<>(this.grauMaximo, chavesNovoFilho, valoresNovoFilho, filhosNovoFilho);
		
		// adicionando o novo node como filho no node atual e removendo o irmao esquerdo
		// da lista de filhos
		this.filhos.set(posicao, novoFilho);
		this.filhos.remove(posicao - 1);
	}

	private void mergeComDir(int posicao) {
		// variaveis para facilitar o accesso ao node com underflow
		// e seu irmao esquerdo
		Node<K> filhoUnderflow = this.filhos.get(posicao);
		Node<K> irmaoDir = this.filhos.get(posicao + 1);
		
		// pegando a chave separadora entre o filho com underflow
		// e o seu irmao dreito
		K chaveSeparadora = this.chaves.remove(posicao);
		int valorChaveSeparadora = this.indices.remove(posicao);
		
		// componentes do novo filho
		ArrayList<K> chavesNovoFilho = new ArrayList<>();
		ArrayList<Integer> valoresNovoFilho = new ArrayList<>();
		ArrayList<Node<K>> filhosNovoFilho = new ArrayList<>();
		
		// adicionando todas as chaves do filho com underflow no node novo
		for(int i = 0; i < filhoUnderflow.chaves.size(); i++) {
			chavesNovoFilho.add(filhoUnderflow.chaves.get(i));
			valoresNovoFilho.add(filhoUnderflow.indices.get(i));
		}
		
		// adicionando a chave separadora no node novo
		chavesNovoFilho.add(chaveSeparadora);
		valoresNovoFilho.add(valorChaveSeparadora);
		
		// adicionando todas as chaves do irmao direito no novo node
		for(int i = 0; i < irmaoDir.chaves.size(); i++) {
			chavesNovoFilho.add(irmaoDir.chaves.get(i));
			valoresNovoFilho.add(irmaoDir.indices.get(i));
		}
		
		// adicionando os filhos do filho com underflow e do irmao direito
		// no novo node
		filhosNovoFilho.addAll(filhoUnderflow.filhos);
		filhosNovoFilho.addAll(irmaoDir.filhos);
		
		Node<K> novoFilho = new Node<>(this.grauMaximo, chavesNovoFilho, valoresNovoFilho, filhosNovoFilho);
		
		// adicionando o novo node e removendo o filho direito
		this.filhos.set(posicao, novoFilho);
		this.filhos.remove(posicao + 1);
	}
	
	/**
	 * Pega o maior node da subarvore atual.
	 * 
	 * @return maior node da subarvore atual.
	 */
	private Node<K> biggest() {
		if(this.filhos.size() == 0) {
			return this;
		} else {
			int last = this.filhos.size() - 1;
			return this.filhos.get(last).biggest();
		}
	}

	/**
	 * Verifica se um node esta com overflow.
	 * 
	 * @return {@code true} caso o node esteja com overflow,
	 * {@code false} caso contrario.
	 */
	public boolean underflow() {
		return this.chaves.size() < Math.floor((this.getgrauMaximo() - 1)/2);
	}
	
	/**
	 * Verifica se um node esta com overflow.
	 * 
	 * @return {@code true} caso o node esteja com overflow,
	 * {@code false} caso contrario.
	 */
	public boolean overflow() {
		return this.chaves.size() >= this.getgrauMaximo();
	}
	
	/**
	 * Verifica se um node esta com a quantidade minima de elementos.
	 * 
	 * @return {@code true} caso o node esteja com a quantidade minima
	 * de elementos, {@code false} caso contrario.
	 */
	public boolean temMinElementos() {
		return this.chaves.size() == Math.floor((this.getgrauMaximo() - 1)/2);
	}
}
