import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientServer {

    private static String host = "localhost";


    private static int port = 8888;
    private static  String password;
    private static String login;

    public static void main(String... args) throws InterruptedException{
        System.out.println("Приветствую тебя путник в моем клиент-серверном приложении. Здесь вы можете назначить старцев(руководaителей или бригадиров, как хотите называйте) и строителей для постройки новой столицы!");
        System.out.println("По команде help, вы можете познакомится с функционалом данной программы.В путь!");
        try {
            //Создаем Сокет
            DatagramSocket ds = new DatagramSocket();
            //Получаем адрес для передачи информации
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Для начала работы программы вы должны пройти авторизацию.");
            while(!checkUser(ds,address));
            senderAllMessages(ds, address);
        }catch(SocketException e){
            System.out.println("Возникла ошибка при работе сокета!Проверьте совпадние портов у клиента и сервера");
        }catch (UnknownHostException e){
            System.out.println("Неизвесное имя хоста!");
        }
    }

    public static void senderAllMessages(DatagramSocket ds,InetAddress address){
            while (true) {
                CommandReader commandReader = new CommandReader();
                Messages messages = commandReader.read();
//                System.out.println(login);
//                System.out.println(password);
                messages.setLogin(login);
                messages.setPassword(password);
                try{
                checkAnswerFromServer(sendMessage(ds, address, messages));
                }catch (NullPointerException e){
                    System.out.println("Произошла ошибка получения данных с сервера! Попробуйте еще раз!");
                    continue;
                }
            }
    }
    public static boolean checkUser(DatagramSocket ds,InetAddress address){
            String commandForAuthorization,checkLogin,checkPassword,answerFromServer;
            Messages messages;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Вы уже есть в системе?(YES/NO)");
            String answer = scanner.nextLine();
            while (true) {
                if (answer.equals("NO")) {
                    commandForAuthorization = "reg";
                    System.out.println("---------------РЕГИСТРАЦИЯ------------------");
                    System.out.println("Введите почтовый ящик:");
                    checkLogin = scanner.nextLine();
                    System.out.println("Происходит регистрация");
                    messages = new Messages(commandForAuthorization, checkLogin);
                    answerFromServer=sendMessage(ds, address, messages);
                    answer=checkAnswerFromServer(answerFromServer);
                    if(answer==null){
                        System.out.println("Произошла ошибка подключения к базе данных! Попробуйте еще раз!");
                        answer="NO";
                        continue;
                    }
                }
                if(answer.equals("YES")){
                    commandForAuthorization="Authorization";
                    System.out.println("---------------АВТОРИЗАЦИЯ------------------");
                    System.out.println("Введите почтовый ящик:");
                    checkLogin=scanner.nextLine();
                    System.out.println("Введите пароль:");
                    checkPassword=scanner.nextLine();
                    System.out.println("Происходит авторизация");
                    messages = new Messages(commandForAuthorization,checkLogin,checkPassword);
                    answerFromServer=sendMessage(ds,address, messages);
                    try {
                        if (checkAnswerFromServer(answerFromServer).equals("NO")) {
                            return false;
                        }
                    }catch (NullPointerException e){
                    }
                    login=checkLogin;
                    password=checkPassword;
                    break;
                }
                else{
                    System.out.println("Командами ответа являются YES / NO!Попробуйте еще раз!");
                    System.out.println("Введите команду:");
                    answer = scanner.nextLine();
                    continue;
                }
            }
            return true;
    }
    public static String sendMessage(DatagramSocket ds, InetAddress address, Messages messages) {
        try {
            ByteArrayOutputStream ob = Serializator.serialization(messages);
            byte[] in = ob.toByteArray();
            DatagramPacket dpInput = new DatagramPacket(in, in.length, address, port);
            ds.send(dpInput);
            ds.setSoTimeout(10000);
            if (messages.getCommand().equals("exit")) {
                System.out.println("Программа завершила свою работу!");
                System.exit(0);
            }
            ByteBuffer buffer = ByteBuffer.allocate(65507);
            DatagramPacket dpOutput = new DatagramPacket(buffer.array(), buffer.array().length);
            ds.receive(dpOutput);
            Messages m = Serializator.deserialization(dpOutput.getData());
            return m.getAnswer();
            ///Users/whatislove118/desktop/программирование/labs/lab6.Client/lab5.xml
            // {"name":"Innokenti","id":5,"userid":42,dateOfInitialization":{"dateTime":{"date":{"year":2019,"month":9,"day":18},"time":{"hour":20,"minute":28,"second":58,"nano":764000000}},"offset":{"totalSeconds":10800}}}
            // {"name":"Innokenti","id":5,"userid":30,dateOfInitialization":{"dateTime":{"date":{"year":2019,"month":9,"day":18},"time":{"hour":20,"minute":28,"second":58,"nano":764000000}},"offset":{"totalSeconds":10800}}}
        } catch (IOException e) {
            System.out.println("Превышено время ожидания ответа от сервера!(Возможно происходят какие-либо технические работы или произошла ошибка, которая от вас не зависит)) Попробуйте подключится позднее! Спасибо");
            System.exit(0);
        }
        return null;
    }
    public static String checkAnswerFromServer(String answerFromServer){
        switch (answerFromServer){
            case "Illegal mailaddress":
                                        System.out.println("Ошибка отправки сообщения! Возможно, данной почты не существует! Попробуйте еще раз!");
                                        return "";
            case "Identification failed":
                                        System.out.println("Идентификация провалена! Попробуйте еще раз");
                                        return "";
            case "Registration success":
                                        System.out.println("Вы успешно зарегистрированы!");
                                        return "YES";
            case "Registration failed":
                                        System.out.println("Возникла ошибка при регистрации! Данная почта уже зарегистрирована  в системе или вы ввели некорректные данные! Попробуйте еще раз!");
                                        return "";
            case "Authorization success":
                                        System.out.println("Вы успешно авторизовались!");
                                        System.out.println("Добро пожаловать!");
                                        return "";
            case "Authorization failed":
                                        System.out.println("Возникла ошибка авторизации!Проверьте правильность введенных данных и попробуйте еще раз!");
                                        return "NO";
        }
        return null;
    }

}



