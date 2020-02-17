package principal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import banco.MiniBD;
import modelo.artigo.Artigo;
import modelo.artigo.PalavraChave;

/**
 * Classe principal que permite com que o usuario interaja com o banco.
 * Ela prove as interfaces necessarias para que o usuario adicione artigos,
 * remova artigos ou palavras chave e busque por artigos ou por palavras chave.
 */
public class Sistema {
	/**
	 * O banco de dados do sistema.
	 */
	private static MiniBD<PalavraChave, String> banco;
	private static String nomeBanco = "banco";
	
	/**
	 * Programa principal com o qual o usuario interage.
	 */
	public static void main(String[] args) {
		// pega a escolha do usuario
		int escolha = menuPrincipal();
		
		// abre o banco a partir do arquivo. caso nenhum exista, um novo eh criado.
		// caso ocorram erros no processo, o programa e finalizado
		try {
			banco = MiniBD.carregaBanco(nomeBanco, nomeBanco + "-indices", nomeBanco + "-posicoes-vazias", (PalavraChave pc) -> pc.getPalavra());
		} catch (FileNotFoundException e) {
			banco = new MiniBD<PalavraChave, String>((PalavraChave pc) -> pc.getPalavra(), nomeBanco);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Erro ao carregar os modulos do programa.", "Erro", JOptionPane.ERROR_MESSAGE);
			escolha = 6;
			banco = null;
		}  catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro ao abrir o banco.", "Erro", JOptionPane.ERROR_MESSAGE);
			escolha = 6;
			banco = null;
		}
		
		// loop do programa principal, enquanto o usuario nao escolher a opcao 6
		// esse loop e executado
		while(escolha >= 1 && escolha < 6) {
			// chama o menu adequado, de acordo com a escolha do usuario.
			switch(escolha) {
			case 1:
				menuInserirArtigo();
				escolha = menuPrincipal();
				break;
			case 2:
				menuBuscarArtigo();
				escolha = menuPrincipal();
				break;
			case 3:
				menuBuscarPalavraChave();
				escolha = menuPrincipal();
				break;
			case 4:
				menuRemoverArtigo();
				escolha = menuPrincipal();
				break;
			case 5:
				menuRemoverPalavraChave();
				escolha = menuPrincipal();
				break;
			default:
				break;
			}
		}
		
		// caso o banco nao seja null, salve ele
		if(banco != null) {
			try {
				banco.close();
			} catch (IOException e) {
				// caso algum erro ocorra, mostre uma mensagem de erro
				JOptionPane.showMessageDialog(null, "Erro ao fechar o banco.", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Menu principal. Apresenta as opcoes para o usuario e devolve a escolhida.
	 * A funcao so devolve opcoes adequadas, i.e., enquanto o usuario nao digitar
	 * uma opcao valida, a funcao nao termina.
	 * 
	 * @return a escolha do usuario.
	 */
	public static int menuPrincipal() {
		// montando o texto do menu principal
		StringBuilder textoMenu = new StringBuilder();
		
		textoMenu.append("Digite uma opcao: \n");
		textoMenu.append("1. Inserir um novo artigo no banco.\n");
		textoMenu.append("2. Buscar um artigo no banco.\n");
		textoMenu.append("3. Buscar uma palavra chave no banco.\n");
		textoMenu.append("4. Remover um artigo do banco.\n");
		textoMenu.append("5. Remover uma palavra chave do banco.\n");
		textoMenu.append("6. Sair\n");
		
		int escolha = -1;

		// enquanto a escolha do usuario nao for valida, pede a escolha do usuario
		while(escolha < 1 || escolha > 6) {
			try {
				// pede para o usuario escolher uma das opcoes
				escolha = Integer.parseInt(JOptionPane.showInputDialog(null, textoMenu.toString(), "Menu principal", JOptionPane.QUESTION_MESSAGE));
			} catch(NumberFormatException e) {
				// esse erro ocorre quando o usuario digita algo que nao e um numero
				if(e.getMessage().equals("null")) {
					// caso ele tenha clicado em cancelar, ou em fechar, sai do programa
					escolha = 6;
				} else {
					// caso contrario, i.e., ele digitou algo que nao e um numero, volta pro
					// comeco do loop
					escolha = -1;
				}
			}
		}
		
		return escolha;
	}
	
	/**
	 * Menu de inserir um artigo na base de dados.
	 */
	public static void menuInserirArtigo() {
		// monta o objeto referente ao artigo que o usuario quer inserir
		Artigo artigoUsuario = menuMontadorArtigo();
		
		// caso o usuario nao tenha cancelado a operacao, ou o artigo nao esteja
		// presente na base de dados, insere ele
		if(artigoUsuario != null && !buscaArtigo(artigoUsuario)) {
			// para cada uma das palavras digitadas pelo usuario
			for(String palavra : artigoUsuario.getPalavrasChave()) {
				PalavraChave pc = banco.busca(palavra);
								
				if(pc != null) {
					// se a palavra ja esta no banco, adiciona o artido na lista de
					// artigos da palavra
					pc.addArtigo(artigoUsuario);
				} else {
					// caso contrario, insere a palavra no banco e adiciona o artigo na
					// lista de artigos dessa palavra
					PalavraChave novaPalavra = new PalavraChave(palavra);
					banco.adiciona(novaPalavra);
					novaPalavra.addArtigo(artigoUsuario);
				}
			}
			
			// mostra a mensagem de sucesso da insercao
			JOptionPane.showMessageDialog(null, "Artigo inserido com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Menu para buscar um artigo na base de dados.
	 */
	public static void menuBuscarArtigo() {
		// monta um objeto de artigo simplificado, i.e., um objeto que
		// so tem titulo e palavras chave
		Artigo artigoUsuario = menuMontadorArtigoSimplificado();
		
		// busca o artigo na base de dados
		boolean artigoExiste = buscaArtigo(artigoUsuario);
		
		// apresenta a mensagem adequada para o usuario
		if(artigoExiste) {
			JOptionPane.showMessageDialog(null, "O artigo " + artigoUsuario.getTitulo() + " esta presente na base de dados.", 
					"Encontrado", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "O artigo " + artigoUsuario.getTitulo() + " nao esta presente na base de dados.", 
					"Nao encontrado", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Busca um artigo na base de dados.
	 * 
	 * @param artigo o artigo que sera buscado.
	 * @return {@code true} se o artigo esteja presente, {@code false}
	 * caso contrario.
	 */
	public static boolean buscaArtigo(Artigo artigo) {
		// pra cada palavra chave do artigo, busca ela no banco e verifica
		// se o artigo esta na lista de artigos dessa palavra
		for(String palavra : artigo.getPalavrasChave()) {
			PalavraChave pc = banco.busca(palavra);
			
			if(pc != null && pc.getArtigos().contains(artigo)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Menu para buscar uma palavra chave na base de dados.
	 */
	public static void menuBuscarPalavraChave() {	
		// pergunta qual palavra o usuario quer buscar
		String palavraUsuario = JOptionPane.showInputDialog(null, "Digite a palavra chave que deseja buscar: ", 
				"Menu buscar palavra chave", JOptionPane.QUESTION_MESSAGE);
		// busca essa palavra na base de dados
		PalavraChave resultado = banco.busca(palavraUsuario);
		
		// mostra a mensagem adequada, i.e., caso a palavra foi ou nao encontrada
		if(resultado == null) {
			JOptionPane.showMessageDialog(null, "O termo " + palavraUsuario + " nao retornou nenhum resultado.", 
					"Resultado", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, resultado, 
					"Artigos com a palavra: " + palavraUsuario, JOptionPane.INFORMATION_MESSAGE);
		}	
	}
	
	/**
	 * Menu para remover um artigo da base de dados.
	 */
	public static void menuRemoverArtigo() {
		// monta um artigo simplificado, i.e., so com nome e palavras chave
		Artigo artigoUsuario = menuMontadorArtigoSimplificado();
	
		// caso o usuario nao tenha cancelado a operacao
		if(artigoUsuario != null) {
			PalavraChave objPalavraChave = null;
			int i = 0;
			
			// procura, dentre as palavras chave do artigo, alguma que esteja no
			// banco
			while (objPalavraChave == null) {
				String palavraChave = artigoUsuario.getPalavrasChave().get(i);
				objPalavraChave = banco.busca(palavraChave);
				i++;
			}
			
			// pega o artigo original, para ter acesso a todas as palavras chave
			// originais daquele artigo
			int posicaoArtigoOriginal = objPalavraChave.getArtigos().indexOf(artigoUsuario);
			Artigo artigoOriginal = objPalavraChave.getArtigos().get(posicaoArtigoOriginal);
			
			// pra cada uma das palavras do artigo original, remova o artigo da lista de
			// artigos da palavra chave
			for(String pc : artigoOriginal.getPalavrasChave()) {
				PalavraChave palavra = banco.busca(pc);
				
				if(palavra != null) {
					palavra.removeArtigo(artigoUsuario);
				}
				
				// caso a palavra chave nao tenha nenhum outro artigo na sua lista, remova
				// ela do banco
				if(palavra.getArtigos().size() == 0) {
					banco.remove(palavra.getPalavra());
				}
			}
		} else {
			// caso o usuario tenha cancelado a operacao, retorne
			return;
		}
		
		// mostre a mensagem de sucesso na remocao
		JOptionPane.showMessageDialog(null, "Remocao realizada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Menu para remover uma palavra chave da base de dados
	 */
	public static void menuRemoverPalavraChave() {
		// prde para o usuario digitar a palavra chave
		String palavraUsuario = JOptionPane.showInputDialog(null, "Digite a palavra chave que deseja remover: ", 
				"Menu remover palavra chave", JOptionPane.QUESTION_MESSAGE);
		
		// caso ela nao esteja no banco, retorna
		if(palavraUsuario == null) {
			return;
		}
		
		// remove a palavra do banco e mostra a mensagem de sucesso
		banco.remove(palavraUsuario);
		JOptionPane.showMessageDialog(null, "Remocao realizada com sucesso!", "Remocao", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Monta um objeto {@code Artigo} com os parametros passados pelo usuario.
	 * Esse objeto so tera titulo e palavras chave.
	 * 
	 * @return objeto artigo gerado, ou null, caso a operacao seja cancelada.
	 * 
	 * @see modelo.artigo.Artigo
	 */
	public static Artigo menuMontadorArtigoSimplificado() {
		// pergunte o titulo do artigo
		String titulo = JOptionPane.showInputDialog(null, "Digite o titulo do artigo: ", "Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		
		// caso o usuario cancele a operacao, saia da funcao
		if(titulo == null) {
			return null;
		}
		
		// pede as palavras chave do artigo
		String palavras = JOptionPane.showInputDialog(null, "Digite todas as palavras chave do artigo, separadas por virgula: ", 
				"Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		
		// caso o usuario tenha cancelado, saia da funcao
		if(palavras == null) {
			return null;
		}
		
		// quebre a string de palavras chave em varias palavras, removendo os espacos
		List<String> palavrasChave = Arrays.asList(palavras.replaceAll("\\s+","").split(","));
		
		return new Artigo(titulo, null, palavrasChave, null, null);
	}
	
	/**
	 * Monta um objeto {@code Artigo} com todos os parametros.
	 * 
	 * @return objeto Artigo, ou null, caso a operacao seja cancelada.
	 * 
	 * @see modelo.artigo.Artigo
	 */
	public static Artigo menuMontadorArtigo() {
		// pede o titulo do para o usuario, caso ele cancele a operacao, sai da funcao
		String titulo = JOptionPane.showInputDialog(null, "Digite o titulo do artigo: ", "Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		if(titulo == null) {
			return null;
		}
		
		// pede a lista de palavras chave para o usuario, caso ele cancele a operacao, sai da funcao
		String palavras = JOptionPane.showInputDialog(null, "Digite todas as palavras chave do artigo, separadas por virgula: ", 
				"Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		if(palavras == null) {
			return null;
		}
		
		// pede a lista de autores para o usuario, caso ele cancele a operacao, sai da funcao
		String autores = JOptionPane.showInputDialog(null, "Digite todos os autores do artigo, separados por virgula: ", 
				"Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		if(autores == null) {
			return null;
		}
		
		// pede o link do artigo para o usuario, caso ele cancele a operacao, sai da funcao
		String link = JOptionPane.showInputDialog(null, "Digite o link do artigo: ", "Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
		if(link == null) {
			return null;
		}
		
		// pede a data de publicacao, enquanto o usuario nao digitar uma data valida ou cancelar
		// ou cancelar a operacao, continua pedindo a data. Caso o usuario cancele a operacao, sai
		// da funcao.
		LocalDate data = null;
		while(data == null) {
			// pede a data para o usuario
			String dataUsuario = JOptionPane.showInputDialog(null, "Digite a data de publicacao do artigo, no formato dd/mm/aaaa: ", 
					"Menu montar artigo", JOptionPane.QUESTION_MESSAGE);
			int dia, mes, ano;
			
			// tenta quebrar a string de data em dia, mes e ano, caso nao consiga, pede para o usuario
			// digitar uma nova data
			List<String> partesData = Arrays.asList(dataUsuario.trim().split("/"));
			
			try {
				dia = Integer.parseInt(partesData.get(0));
				mes = Integer.parseInt(partesData.get(1));
				ano = Integer.parseInt(partesData.get(2));
				
				data = LocalDate.of(ano, mes, dia);
			} catch (Exception e) {
				if(e.getMessage().equals("null")) {
					// caso o usuario cancele a operacao, sai da funcao
					return null;
				}
			}
		}
		
		// quebra a lista de palavras e remove os espaces em branco
		List<String> palavrasChave = Arrays.asList(palavras.replaceAll("\\s+","").split(","));
		List<String> autoresLista = Arrays.asList(autores.replaceAll("\\s+","").split(","));
		
		return new Artigo(titulo, autoresLista, palavrasChave, data, link);
	}
}
