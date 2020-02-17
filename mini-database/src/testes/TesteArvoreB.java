package testes;

import modelo.btree.BTree;

public class TesteArvoreB {
	public static void main(String[] args) {
		BTree<String> arvore = new BTree<>(3);
		String[] lista = {"a", "b", "c", "d", "e", "f", "aa", "aaa", "g", "aaaa", "aaaaa"};
		
		arvore.insere("a", 45);
		arvore.insere("b", 2);
		arvore.insere("c", 64);
		arvore.insere("d", 4);
		arvore.insere("e", 54);
		arvore.insere("f", 52);
		arvore.insere("aa", 50);
		arvore.insere("aaa", 14);
		arvore.insere("g", 689);
		arvore.insere("aaaa", 1);
		arvore.insere("aaaaa", 12);
		System.out.println(arvore);
		
		arvore.remove("c");
		System.out.println(arvore);
		
		for(String str : lista) {
			System.out.println(arvore.busca(str));
		}
	}
}
