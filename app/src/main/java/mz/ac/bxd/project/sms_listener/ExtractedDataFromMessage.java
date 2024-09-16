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
                idRecarga = "Emola";


            System.out.println(mensagem);
            System.out.println(idRecarga);

            String padraoValor = "\\b(\\d*([\\d\\,]*)\\.?\\d+)MT\\b";
            Pattern patternValor = Pattern.compile(padraoValor);
            Matcher matcherValor = patternValor.matcher(mensagem);

            if (matcherValor.find())
                 valorRecarga = matcherValor.group(1).replace(",", "");


            String padraoHoraData = "as (\\\\d{2}:\\\\d{2}:\\\\d{2})(?: de)? (\\\\d{2}/\\\\d{2}/\\\\d{4})";
            Pattern patternHoraData = Pattern.compile(padraoHoraData);
            Matcher matcherHoraData = patternHoraData.matcher(mensagem);

            if (matcherHoraData.find()) {
                String hora = matcherHoraData.group(1); // Captura a hora
                String data = matcherHoraData.group(2); // Captura a data
                dataRecarga = hora + " " + data; // Formata a data e hora
            }

            String opt = mensagem.substring(mensagem.indexOf("Seu OTP: ") + 9, mensagem.indexOf("Seu OTP: ") + 15);
            if(idRecarga.equals("Emola"))
                return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", opt, false);

            return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", "", false);

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
            valorRecarga = matcherValor.group(1).replace(",", "");
        }else {
            idRecarga = "Mpesa";
            dataRecarga = "data";
            valorRecarga = "0";
        }

        if(idRecarga.equals("Mpesa"))
            return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", mensagem, false);

        return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", "", false);
    }

}

