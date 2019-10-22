import java.io.*;
    public class Serializator {
        public static ByteArrayOutputStream serialization(Messages messages) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(b)) {
                oos.writeObject(messages);
            } catch (IOException e) {
                System.out.println("Произошла ошибка сериализации обьекта!");
            }finally {
                try {
                    b.close();
                } catch (IOException e) {
                    System.out.println("Произошла ошибка закрытия потока записи байтов! ");
                    ;
                }
            }
            return b;
        }

        public static Messages deserialization(byte[] buf) {
            ByteArrayInputStream b = new ByteArrayInputStream(buf);
            try (ObjectInputStream ois = new ObjectInputStream(b)) {
                try {
                    Messages messages = (Messages) ois.readObject();
                    return messages;

                } catch (ClassNotFoundException e) {
                    System.out.println("Данного класса не существует!");
                }

            } catch (IOException e) {
                System.out.println("Произошла ошибка десериализации обьекта!");
            }finally {
                try {
                    b.close();
                }catch(IOException e){
                    System.out.println("Произошла ошибка закрытия потока чтения байтов!");
                }
            }
            return null;
        }
    }
