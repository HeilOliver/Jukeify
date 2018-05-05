package at.fhv.jukify;

import at.fhv.jukify.controller.AuthController;
import at.fhv.jukify.controller.ConfigController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

@SpringBootApplication
public class SpringBootApp  {   //extends Application

//    @Override
//    public void init() throws Exception {
//        ConfigurableApplicationContext context = SpringApplication.run(SpringBootApp.class, args);
//        FXMLLoader loader = new FXMLLoader();
//        loader.setControllerFactory(context::getBean);
//    }

    @Value("${local.server.port}")
    private static String port;

    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootApp.class, args);

        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ConfigController.getInstance().set(port, hostname);
    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//
//    }
}
