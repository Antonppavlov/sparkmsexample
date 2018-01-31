package ru.pavlov.firstms;

import com.google.gson.Gson;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static spark.Spark.*;

public class Main {

    private static Set<String> sessions = new HashSet<>();
    private static List<Car> carList = new ArrayList<>();

    public static void main(String[] args) {
        port(7676);
        patch("/data", () -> {
            before("/*", (request, response) -> {
                String session = getSession(request);
                if (!sessions.contains(session)) {
                    halt(401, "Not Authorization!!!");
                }
            });

            get("/getAllCars", (((request, response) -> new Gson().toJson(carList))));

            after("/*", ((request, response) -> {
                //can you make something.
            }));

            post("/add", ((request, response) -> {
                String body = request.body();
                if(body!=null){
                    String str = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                }
            }));
        });
    }

    private static String getSession(Request request) throws UnsupportedEncodingException {
        Map<String, String> cookies = request.cookies();
        String value = "";

        for (String key : cookies.keySet()) {
            String srt = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            if (srt.contains("JSESSIONID")) {
                value = srt.split("=")[1];
                break;
            }
        }

        return value;
    }
}
