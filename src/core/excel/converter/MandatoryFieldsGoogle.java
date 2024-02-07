package core.excel.converter;

import java.util.ArrayList;
import java.util.Arrays;

public class MandatoryFieldsGoogle
{
    public static final String CARIMBO = "Carimbo de data/hora";
    public static final String NOME_PROMOTOR = "NOME DO PROMOTOR";
    public static final String DATA = "DATA";
    public static final String LOJA = "SELECIONE A LOJA";
    public static final String PEDIDO = "N° do Pedido";
    public static final String OBSERVACOES = "Observações";
    public static final String FOTO = "Insira uma foto do pedido";

    public static final ArrayList<String> MANDATORY_FIELDS_GOOGLE = new ArrayList<>(Arrays.asList(
            CARIMBO, NOME_PROMOTOR, DATA, LOJA, PEDIDO, OBSERVACOES, FOTO
    ));
}
