package Chat_ver1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

// java 패키지명.ChatThread 닉네임 [enter]
// java Chat.ChatThread Jo; -> Jo가 이름
public class ChatClient {
    public static void main(String[] args) throws  Exception{
        String name = args[0]; // args = 외부로부터 받아들인 것


        Socket socket = new Socket("127.0.0.1", 9912);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        String line = null;
        InputThread inputThread = new InputThread(in);
        inputThread.start();

        // 한줄씩 읽어서 서버로 전송
        while((line = keyboard.readLine()) != null) {
            out.println(name + " : " + line);
            out.flush();
        }

    }
}

class InputThread extends Thread {
    BufferedReader in = null;

    public InputThread (BufferedReader in) {
        this.in = in;
    }

    @Override
    // 화면에 출력
    public void run() {
        try {
            String line = null;
            while((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}