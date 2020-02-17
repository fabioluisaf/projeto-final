package modelo.btree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe da arvore B que traduz uma chave de um tipo qualquer
 * para um indice. A arvore nao adiciona chaves, nem indices
 * duplicados.
 *
 */
public class BTree<K extends Comparable<K>> implements Serializable {
	private static final long serialVersionUID = -7560067459913604026L;
	private Node<K> raiz;
	private int grauMaximo;
	
	/**
	 * Constroi uma arvore binaria.
	 */
	public BTree() {
		this(0);
	}
	
	/**
	 * Constroi uma arvore B n-aria. Caso {@code n} passado seja menor que 2,
	 * o grau maximo da arvore sera 2.
	 * 
	 * @param n	grau maximo da arvore, i.e., a quantidade maxima de filhos 
	 * que cada node pode ter.
	 */
	public BTree(int n) {
		// n = 1 nao e um valor permitido, o minimo e dois, para uma arvore 
		// binaria
		if(n <= 1) {
			this.grauMaximo = 2;
		} else {
			this.grauMaximo = n;
		}
		
		this.raiz = null;
	}
	
	/**
	 * Getter do grau maximo da arvore.
	 * 
	 * @return grau maximo da arvore.
	 */
	public int getGrauMaximo() {
		return this.grauMaximo;
	}
	
	/**
	 * Busca uma determinada chave recursivamente na arvore.
	 * 
	 * @param chave chave buscada.
	 * @return o indice dessa chave, ou -1, caso a mesma nao esteja na arvore.
	 */
	public int busca(K chave) {
		if(this.raiz != null) {
			return raiz.busca(chave);
		} else {
			return -1;
		}
	}
	
	/**
	 * Insere um novo par (chave, indice) na arvore chamando a funcao do node.
	 * 
	 * @param chave chave a ser inserida.
	 * @param indice indice referente a chave inserida.
	 * 
	 * @see Node#insere(Comparable, int)
	 */
	public void insere(K chave, int indice) {
		// caso a raiz seja nula, atribua a ela um node novo
		if(this.raiz == null) {
			this.raiz = new Node<K>(this.grauMaximo);
		}
		
		// so insere se a arvore nao tiver o indice
		if(!raiz.temIndice(indice)) {
			// insere o par recursivamente na arvore.
			raiz.insere(chave, indice);

			// como quem balanceia um node e seu pai, esse balanceamento precisa ser
			// feito aqui.
			//
			// essa checagem e redundante, ja que a funcao corrigeOverflowEm() checara
			// isso tambem, mas como o tratamento para overflow na raiz e especial, 
			// ela precisa ser feita
			if(raiz.overflow()) {
				// criando a lista de filhos da nova raiz com a raiz antiga nela
				ArrayList<Node<K>> filhosNovaRaiz = new ArrayList<>();
				filhosNovaRaiz.add(this.raiz);
				
				// criando o node da nova raiz e corrigindo o overflow da raiz antiga
				Node<K> novaRaiz = new Node<>(this.grauMaximo, null, null, filhosNovaRaiz);
				novaRaiz.corrigeOverflowEm(0);
				
				this.raiz = novaRaiz;
			}
		}
	}
	
	/**
	 * Remove recursivamente a {@code chave} da arvore.
	 * 
	 * @param chave chave a ser removida.
	 */
	public void remove(K chave) {
		if(this.raiz != null) {
			raiz.remove(chave);
		}
	}
	
	@Override
	public String toString() {
		if(this.raiz == null) {
			return "{ }";
		} else {
			return raiz.toString();
		}
	}
}
