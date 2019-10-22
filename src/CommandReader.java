import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandReader {
    private CollectionsOlders collectionsOlders=null;
    private Olders arg;
    public String[] readAndParse() {
        Scanner consoleScanner = new Scanner(System.in);
        String command;
        String[] fullcomand;
        int count = 0;
        try {
            System.out.println("Введите команду");
            command = consoleScanner.nextLine();
            fullcomand = command.trim().split(" ", 2);
            if (!(fullcomand[0].equals("import") || fullcomand[0].equals("load") || fullcomand[0].equals("save") || fullcomand[0].equals("add")
                    || fullcomand[0].equals("add_if_max") || fullcomand[0].equals("remove") || fullcomand[0].equals("remove_lower")
                    || fullcomand[0].equals("show") || fullcomand[0].equals("help") || fullcomand[0].equals("info")
                    || fullcomand[0].equals("clear") || fullcomand[0].equals("exit"))){
                System.out.println(fullcomand[0] + " не является командой");
                readAndParse();
            }
                if (fullcomand.length == 1) return fullcomand;
                else if (fullcomand[0].equals("add_if_min") || fullcomand[0].equals("add") || fullcomand[0].equals("remove")) {
                    fullcomand[1] = fullcomand[1].trim();
                    command = fullcomand[1];
                    fullcomand[1] = "";
                    while (!command.contains("{")) {
                        fullcomand[1] += command;
                        command = consoleScanner.nextLine().trim();
                    }
                    count += command.replace("{", "").length() - command.replace("}", "").length();
                    fullcomand[1] += command;
                    while (count != 0) {
                        command = consoleScanner.nextLine();
                        fullcomand[1] += command;
                        count += command.replace("{", "").length() - command.replace("}", "").length();
                    }
                } else return fullcomand;
            } catch(NoSuchElementException ex){
                fullcomand = new String[1];
                fullcomand[0] = "exit";
            }
            return fullcomand;
        }
    public Messages read() {

            boolean checkRead = true;
            while (checkRead == true) {
                String[] arrCommand = readAndParse();
                Gson gson;
                gson = new Gson();
                try {
                    if (arrCommand[0].equals("add") || arrCommand[0].equals("add_if_max") || arrCommand[0].equals("remove") || arrCommand[0].equals("remove_lower")) {
                        this.arg = gson.fromJson(arrCommand[1], Olders.class);
                        if(this.arg.getUserID()!=0){
                            this.arg.setUserID(0);
                        }
                        Messages messages = new Messages(arrCommand[0], arg);
                        return messages;
                    } else if(arrCommand[0].equals("import")) {
                        Messages messages = new Messages(arrCommand[0]);
                        CollectionFileScanner collectionFileScanner = new CollectionFileScanner();
                        Scanner x = new Scanner(System.in);
                        System.out.println("Введите путь к файлу:");
                        String filePath=x.nextLine();
                        this.collectionsOlders=collectionFileScanner.readFile(filePath);
                        messages.setFile(collectionsOlders.getCollectionsOlders());
                        return messages;
                    }else if(arrCommand[0].equals("exit")) {
                        return new Messages(arrCommand[0]);
                    }else{
                        Messages messages = new Messages(arrCommand[0]);
                        return messages;
                    }
                } catch (JsonSyntaxException | IllegalStateException e) {
                    System.out.println("Формат задания данного обьекта не соотвествует условию!");
                    read();
                }

            }

        return null;
    }
    }

