import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConvertidorDeMoneda {

    private static final String API_KEY = "76467376e26f8197403e2dc1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/MXN";

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String monedaDesde = "MXN";

            // Solicitar la moneda a la que se desea convertir al usuario
            String monedaA = solicitarMoneda();

            // Salir del programa si el usuario introduce "salir"
            if (monedaA.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del programa...");
                break;
            }

            // Solicitar la cantidad a convertir al usuario
            double cantidad = solicitarCantidad();

            String exchangeRateURL = BASE_URL;
            URL url = new URL(exchangeRateURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Verificar si la respuesta contiene datos válidos
            if (jsonObject.has("conversion_rates")) {
                JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

                // Verificar si la moneda "monedaA" está presente en los tipos de cambio
                if (conversionRates.has(monedaA)) {
                    double tipoDeCambio = conversionRates.get(monedaA).getAsDouble();
                    double cantidadConvertida = cantidad * tipoDeCambio;

                    System.out.printf("%.2f %s es igual a %.2f %s\n", cantidad, monedaDesde, cantidadConvertida, monedaA);
                } else {
                    System.out.println("La moneda especificada no está disponible en los tipos de cambio proporcionados.");
                }
            } else {
                System.out.println("No se encontraron tipos de cambio en la respuesta.");
            }

            con.disconnect();
        }
    }

    private static String solicitarMoneda() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Introduce la moneda a la que deseas convertir\n(o escribe 'salir' para salir)\n" +
                "USD (Dólar estadounidense)\n" +
                "EUR (Euro)\n" +
                "GBP (Libra esterlina británica)\n" +
                "JPY (Yen japonés)\n" +
                "CAD (Dólar canadiense)\n" +
                "AUD (Dólar australiano)\n" +
                "CHF (Franco suizo)\n" +
                "CNY (Yuan chino)\n" +
                "INR (Rupia india)\n" +
                "MXN (Peso mexicano)\n" +
                "BRL (Real brasileño) ");
        return reader.readLine().toUpperCase(); // Convertir a mayúsculas para consistencia
    }

    private static double solicitarCantidad() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Introduce la cantidad a convertir: ");
        String input = reader.readLine();
        return Double.parseDouble(input);
    }
}
