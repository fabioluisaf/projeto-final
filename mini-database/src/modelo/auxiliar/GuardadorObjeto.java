package modelo.auxiliar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Classe que le/escreve objetos em um arquivo especifico.
 *
 * @param <T> tipo do objeto guardado por essa classe
 */
public class GuardadorObjeto<T extends Serializable> {
	/**
	 * Nome do arquivo onde o objeto sera guardado.
	 */
	private String nomeArquivo;

	/**
	 * Constroi uma instancia da classe, que guardara o objeto no arquivo
	 * passado por parametro.
	 * 
	 * @param nomeArquivo nome do arquivo onde o objeto sera guardado.
	 */
	public GuardadorObjeto(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	/**
	 * Getter do nome do arquivo.
	 * 
	 * @return o nome do arquivo.
	 */
	public String getNomeArquivo() {
		return nomeArquivo + ".bin";
	}

	/**
	 * Salva o objeto passado no arquivo da classe.
	 * 
	 * @param objeto objeto que sera salvol
	 * 
	 * @throws IOException Caso ocorram erros na abertura do arquivo.
	 */
	public void salvaObjeto(T objeto) throws IOException {		
		if(objeto != null) {
			try {
				// salva o objeto em um novo arquivo, para preservar o anterior
				// caso ocorram erros
				ObjectOutputStream escritorObjs = new ObjectOutputStream(new FileOutputStream(this.nomeArquivo + "-new.bin"));
				escritorObjs.writeObject(objeto);
				escritorObjs.close();
				
				// caso nenhum erro ocorra, sobrescreve o anterior
				sobrescreveAnterior();
			} catch (IOException e) {
				throw new IOException("Erro ao tentar abrir o arquivo " + this.getNomeArquivo() + "-new.bin.");
			}
		}
	}

	/**
	 * Le um objeto do arquivo e retorna ele.
	 * 
	 * @return O objeto contido no arquivo.
	 * 
	 * @throws IOException Caso ocorram erros na abertura do arquivo.
	 * 
	 * @throws ClassNotFoundException Casso estejam faltando modulos no programa.
	 */
	@SuppressWarnings("unchecked")
	public T carregaObjeto() throws IOException, ClassNotFoundException {
		try {
			// le o conteudo do arquivo e salva na variavel objeto
			ObjectInputStream leitorObjs = new ObjectInputStream(new FileInputStream(this.nomeArquivo + ".bin"));
			Object objetoLido = leitorObjs.readObject();
			leitorObjs.close();
			
			return (T) objetoLido;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Erro ao tentar abrir o arquivo " + this.getNomeArquivo() + ".");
		} catch (IOException e) {
			throw new IOException("Erro ao tentar abrir o arquivo " + this.getNomeArquivo() + ".");
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("Algum dos modulos do programa esta ausente. Erro " + e.getMessage());
		}
	}
	
	/**
	 * Sobrescreve o arquivo anterior apos salvar o novo objeto.
	 */
	private void sobrescreveAnterior() {
		File old = new File(this.nomeArquivo + ".bin");
		File curr = new File(this.nomeArquivo + "-new.bin");
		
		if(old.exists()) {
			old.delete();
		} 
		
		curr.renameTo(old);
	}
}