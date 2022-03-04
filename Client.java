import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket,String username){
        try{
            this.socket = socket;
            this.bufferedReader  = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            this.username = username;

        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+""+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    //Gelen diğer client threadlerin mesajlarını bekliyor
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while(socket.isConnected()){
                    try {
                        messageFromGroupChat = bufferedReader.readLine();

                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Kullanıcı Adını Gir:");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost",1234); // sunucu bğalantısı
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();

    }


}
