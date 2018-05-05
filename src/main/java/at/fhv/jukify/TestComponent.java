package at.fhv.jukify;

import at.fhv.jukify.controller.AuthController;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestComponent {

    @PostConstruct
    public void init(){
        AuthController.getInstance().requestUserAuthentification();
    }
}
