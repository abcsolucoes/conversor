package core.excel.converter;

import java.util.ArrayList;
import java.util.Arrays;

public class MandatoryFieldsDysrup
{
    public static final String CLIENTE = "Cliente";
    public static final String REDE = "Rede";
    public static final String BANDEIRA = "Bandeira";
    public static final String PDV = "Loja";
    public static final String REGIONAL = "Regional";
    public static final String REPOSITOR = "Funcionário";
    public static final String DATA_E_HORA_PESQUISA = "Data";
    public static final String PEDIDO = "Nº do pedido";
    public static final String CODIGO_PRODUTO_REDE = "Código do produto na rede";
    public static final String CODIGO_PRODUTO_INDUSTRIA = "Código referência da indústria";
    public static final String NOME_PRODUTO = "Produto";
    public static final String QUANTIDADE = "Quantidade";
    public static final String OBSERVACOES = "Observação";

    public static final ArrayList<String> MANDATORY_FIELDS_DYSRUP = new ArrayList<>(Arrays.asList(
            CLIENTE, REDE, BANDEIRA, PDV, REGIONAL, REPOSITOR,
            DATA_E_HORA_PESQUISA, PEDIDO, CODIGO_PRODUTO_REDE,
            CODIGO_PRODUTO_INDUSTRIA, NOME_PRODUTO, QUANTIDADE, OBSERVACOES
    ));
}
