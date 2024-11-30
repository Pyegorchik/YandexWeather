package pyegor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class App {

    public static void main(String[] args) {
        try {

            // лучше бы ключ положить в .env, но для простоты задания я залил в гитхаб коммит не с моим ключом
            String apiKey = "c27afa6f-4f52-449f-8c1c-98ac4c481b62";
            

            double lat = 42.75;
            double lon = 33.62;
            
            int limitDays = 11; // Количество дней для прогноза

            if (limitDays <= 0 || limitDays > 11) {
                System.out.println("\nНевалидное количество дней " + limitDays);
                return;
            }

            String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&limit=" + limitDays;

            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", apiKey);
            

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            

            System.out.println("Ответ от сервера:");
            System.out.println(jsonResponse.toString(2)); 


            // Текущая температура
            JSONObject fact = jsonResponse.getJSONObject("fact");
            int currentTemp = fact.getInt("temp");
            System.out.println("\nТекущая температура: " + currentTemp + "°C");





            // Вычисление средней температуры за период
            JSONArray forecasts = jsonResponse.getJSONArray("forecasts");
            int sumTemp = 0;
            int count = 0;

            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject dayForecast = forecasts.getJSONObject(i);
                JSONArray hours = dayForecast.getJSONArray("hours");

                for (int j = 0; j < hours.length(); j++) {
                    JSONObject hourData = hours.getJSONObject(j);
                    if (hourData.has("temp")) {
                        sumTemp += hourData.getInt("temp");
                        count++;
                    }
                }
            }

            if (count > 0) {
                double averageTemp = (double) sumTemp / count;
                System.out.println("\nСредняя температура за " + limitDays + " день(ей): " + averageTemp + "°C");
            } else {
                System.out.println("\nНе удалось вычислить среднюю температуру: данных нет.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
