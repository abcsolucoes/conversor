package core.excel.converter;

import java.util.ArrayList;
import java.util.Arrays;

public class MandatoryFieldsGIV
{
    public static final String MARCA = "Marca";
    public static final String NOME_PRODUTO = "Produto (SKU)";
    public static final String CODIGO_PRODUTO = "Código do Produto";
    public static final String REDE = "Rede";
    public static final String BANDEIRA = "Bandeira";
    public static final String PDV = "Loja";
    public static final String CODIGO_PDV = "Código da Loja";
    public static final String DATA_E_HORA_PESQUISA = "Data";
    public static final String NOTIFICANTE = "Promotor";
    public static final String PEDIDO = "N° do Pedido";
    public static final String QUANTIDADE = "Quantidade";
    public static final String OBSERVACOES = "Observações";

    public static final ArrayList<String> MANDATORY_FIELDS_GIV = new ArrayList<>(Arrays.asList(
            MARCA, NOME_PRODUTO, CODIGO_PRODUTO, REDE,
            BANDEIRA, PDV, CODIGO_PDV, DATA_E_HORA_PESQUISA,
            NOTIFICANTE, QUANTIDADE,
            PEDIDO, OBSERVACOES
    ));
}
