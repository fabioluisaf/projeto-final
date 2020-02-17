package banco;

/**
 * Essa interface nos permite criar expressoes lambda, assim
 * conseguimos passar para o banco de dados um lambda para definir
 * a chave primaria de um objeto.
 * 
 *
 * @param <T> o tipo do objeto guardado no banco.
 * @param <K> o tipo da chave primaria do objeto guardado no banco.
 */
@FunctionalInterface
public interface FuncaoChavePrimaria<T, K> {
	K get(T obj);
}
