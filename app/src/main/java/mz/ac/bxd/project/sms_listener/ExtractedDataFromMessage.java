package mz.ac.bxd.project.sms_listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractedDataFromMessage {

    private static String idRecarga;
    private static String dataRecarga;
    static String valorRecarga;

    public ExtractedDataFromMessage() {
    }

    public static Recarga extractDataFromMessageEmola(String mensagem) {

        String padraoIdTransacao = "ID da (transacao|transaccao): ((PP|CI)\\d{6}\\.\\d{4}\\.[A-Za-z]\\d+)";


        Pattern patternIdTransacao = Pattern.compile(padraoIdTransacao);
        Matcher matcherIdTransacao = patternIdTransacao.matcher(mensagem);

        if (matcherIdTransacao.find())
            idRecarga = matcherIdTransacao.group(2);
        else
            idRecarga = "DiversosEmola";


        System.out.println(mensagem);
        System.out.println(idRecarga);

        String padraoValor = "\\b(\\d+(?:\\.\\d{1,2})?)\\s*MT\\b";
        Pattern patternValor = Pattern.compile(padraoValor);
        Matcher matcherValor = patternValor.matcher(mensagem);

        if (matcherValor.find())
             valorRecarga = matcherValor.group(1);


        String padraoHoraData = "as (\\d{1,2}:\\d{2}:\\d{2} \\d{1,2}/\\d{1,2}/\\d{2,4})";
        Pattern patternHoraData = Pattern.compile(padraoHoraData);
        Matcher matcherHoraData = patternHoraData.matcher(mensagem);

        if (matcherHoraData.find())
            dataRecarga = matcherHoraData.group(1);

        return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", mensagem, false);

    }


    static Recarga extractDataFromMessageMpesa(String mensagem) {
        int posicaoConfirmed = mensagem.indexOf("Confirmed");

        if (posicaoConfirmed != -1)
            idRecarga = mensagem.substring(0, posicaoConfirmed).trim();

        String padraoValor = "\\b(\\d*([\\d\\,]*)\\.?\\d+)MT\\b";
        Pattern patternValor = Pattern.compile(padraoValor);
        Matcher matcherValor = patternValor.matcher(mensagem);

        String padraoData = "(\\d{1,2}/\\d{1,2}/\\d{2,4} at \\d{1,2}:\\d{2} [APMapm]{2})";
        Pattern patternData = Pattern.compile(padraoData);
        Matcher matcherData = patternData.matcher(mensagem);

        if (matcherData.find())
            dataRecarga = matcherData.group(1);

        if (matcherValor.find()) {
            valorRecarga = matcherValor.group(1);
        }else {
            idRecarga = "DiversosMpesa";
            dataRecarga = "data";
            valorRecarga = "0";
        }

        return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", mensagem, false);
    }

}

