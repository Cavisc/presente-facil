import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controller.MainController;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MainController mainController = new MainController();
        mainController.start();
    }
}
