import com.google.gson.Gson;

import java.util.Scanner;

public class TEST {
    static String login;
    static  String password;
    public static void main(String[] args) throws InterruptedException {
       Scanner x = new Scanner(System.in);
       login = x.nextLine();
       password=x.nextLine();
       while(true){
           System.out.println(login);
           System.out.println(password);
           Thread.sleep(3000);
       }
    }
}
