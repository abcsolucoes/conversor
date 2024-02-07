package core.excel.converter;

import core.excel.converter.conversion_types.AbstractExcelConverter;
import core.excel.converter.conversion_types.SendToClientConverter;

import java.util.HashMap;

public class ConversionManager
{
    public static final String SEND_TO = "ENVIO PARA CLIENTE";

    public static AbstractExcelConverter sendToClientConverter = new SendToClientConverter();

    public static final HashMap<ConversionMethod, String> FILE_TYPES = new HashMap<>() {{
        put(sendToClientConverter, SEND_TO);
    }};
}
