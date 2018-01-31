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

    public static void main(String [] args){
        port(7676);


        path("/data", () ->{
            before("/*",(request, response) -> {
                String session = getSession(request);
                if(!sessions.contains(session)){
                    halt(401,"You are not authentificated");
                }
            });
            get("/getAllCars",(((request, response) -> new Gson().toJson(cars))));
            after("/*",((request, response) -> {
                //Can you make something.
            }));

            post("/add", ((request, response) -> {
                String body = request.body();
                if(body != null){
                    String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                    message = message.replace("=","");
                    Car car = new Gson().fromJson(message,Car.class);
                    cars.add(car);
                }
                response.status(200);
                return "success";
            }));
        });



        post("/login", ((request, response) -> {
            String body = request.body();
            if(body != null){
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name()).replace("=","");
                JsonParser jsonParser = new JsonParser();
                JsonObject json = (JsonObject) jsonParser.parse(message);
                String userName = json.get("userName").getAsString();
                String password = json.get("password").getAsString();
                if("demo".equals(userName) && "demo".equals(password)){
                    sessions.add(getSession(request));
                    response.status(200);
                    return "success";
                }else{
                    halt(401,"Invalid creditentials");
                }
            }
            return "Login failed";
        }));



        post("/logout",((request, response) -> {
            String session = getSession(request);
            if(sessions.contains(session)){
                sessions.remove(session);
            }
            response.status(200);
            return "Success";
        }));

        get("/test",((request, response) -> "Test success"));
    }

    private static String getSession(Request request) throws UnsupportedEncodingException {

        Map<String, String> cookies = request.cookies();
        for(String key : cookies.keySet()){
            String str = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            if(str.contains("JSESSIONID")){
                String value = str.split("=")[1];
                return value;
            }
        }
        return "";
    }
}