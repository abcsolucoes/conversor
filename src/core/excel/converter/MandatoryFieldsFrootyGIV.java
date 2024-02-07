package core.excel.converter;

import java.util.ArrayList;
import java.util.Arrays;

public class MandatoryFieldsFrootyGIV {

    public static final String REDE = "Rede";
    public static final String BANDEIRA = "Bandeira";
    public static final String DATA = "Data";
    public static final String PROMOTOR = "Promotor";
    public static final String LOJA = "Loja";
    public static final String CODIGO_LOJA = "Código da Loja";
    public static final String HAVERA_PEDIDO = "Haverá pedido para a loja?";
    public static final String MOTIVO = "Por qual motivo o gerente não quis retirar o pedido?";
    public static final String OUTROS_MOTIVOS = "Outros Motivos";
    public static final String NUMERO_PEDIDO = "Informe o número do pedido";
    public static final String CODIGO_PRODUTO = "Código do Produto";
    public static final String PRODUTO = "Produto (SKU)";
    public static final String OBSERVACOES = "Observações";
    public static final String FOTO_DO_PEDIDO = "Foto do Pedido";
    public static final String QUANTIDADE = "Quantidade";

    public static final ArrayList<String> MANDATORY_FIELDS_GIV = new ArrayList<>(Arrays.asList(
            REDE, BANDEIRA, DATA, PROMOTOR, LOJA, CODIGO_LOJA, HAVERA_PEDIDO, MOTIVO, OUTROS_MOTIVOS,
            NUMERO_PEDIDO, CODIGO_PRODUTO, PRODUTO, OBSERVACOES, FOTO_DO_PEDIDO, QUANTIDADE
    ));
}
