package testes;

import java.io.IOException;
import java.util.ArrayList;

import modelo.auxiliar.GuardadorObjeto;

public class TesteLerArquivos {

	public static void main(String[] args) {
		ArrayList<Integer> obj = null;
		GuardadorObjeto<ArrayList<Integer>> ga = new GuardadorObjeto<>("teste");	
		//String obj = null;
		//GuardadorObjeto<String> ga = new GuardadorObjeto<>("teste", obj);		
		
		try {
			obj = ga.carregaObjeto();
		} catch (ClassNotFoundException e) {
			System.out.println("Classes faltando no programa");
		} catch (IOException e) {
			System.out.println("Erro ao tentar abrir o arquivo " + ga.getNomeArquivo());
		} catch (ClassCastException e) {
			System.out.println("Classes erradas");
		}

		System.out.println(obj);
	}
}
