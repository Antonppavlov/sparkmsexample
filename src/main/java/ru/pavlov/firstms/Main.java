package ru.pavlov.firstms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static spark.Spark.*;

public class Main {

    private static Set<String> sessions = new HashSet<>();
    private static List<Car> cars = new ArrayList<>();

    public static void main(String[] args) {
        port(7676);

        path("/data", () -> {
            before("/*", (request, response) -> {
                String session = getSession(request);
                if (!sessions.contains(session)) {
                    halt(401, "Not Authorization!!!");
                }
            });
            get("/getAllCars", (((request, response) -> new Gson().toJson(cars))));
            after("/*", ((request, response) -> {
                //can you make something.
            }));

            post("/add", ((request, response) -> {
                String body = request.body();
                if (body != null) {
                    String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                    Car car = new Gson().fromJson(message, Car.class);
                    cars.add(car);
                }
                response.status(200);
                return "success";
            }));
        });

        post("/login", ((request, response) -> {
            String body = request.body();
            if (body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "");
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(message);
                String userName = jsonObject.get("userName").getAsString();
                String password = jsonObject.get("password").getAsString();
                if ("demo".equals(userName) && "demo".equals(password)) {
                    sessions.add(getSession(request));
                    response.status(200);
                    return "success";
                }
                halt(404, "Invalid credentials");
            }
            return "Invalid failed";
        }));

        post("/logout", ((request, response) -> {
            String session = getSession(request);
            if (sessions.contains(session)) {
                sessions.remove(session);
            }
            response.status(200);
            return "success";
        }));

        get("/test",((request, response) -> "Test success"));
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
