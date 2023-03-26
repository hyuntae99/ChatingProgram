package Chat_ver1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws Exception {

        // cmd -> telnet localhost 9912 (접속)
        // "Ctrl+ ]" -> q입력 (종료)
        ServerSocket serverSocket = new ServerSocket(9912); // 9912번 포트에서 대기

        // 공유객체에서 쓰레드에 안전한 리스트를 만든다.
        List<PrintWriter> outList = Collections.synchronizedList(new ArrayList<>());
        // 서버를 계속 유지하기 위해서 무한 반복문
        while (true) {
            Socket socket = serverSocket.accept(); // 클라이언트와 통신하기 위한 소켓
            System.out.println("접속 : " + socket);

            ChatThread chatThread = new ChatThread(socket, outList); // ChatThread 에게 소켓을 넘김
            chatThread.start();
        }
    }
}

// 동시에 처리하기 위해서 Thread
class ChatThread extends Thread {

    private Socket socket;
    private List<PrintWriter> outList; // 공유 리스트
    private PrintWriter out;
    private BufferedReader in;

    // 생성자
    public ChatThread(Socket socket, List<PrintWriter> outList) {
        this.socket = socket; // 현재 연결된 클라이언트와만 통신 가능
        this.outList = outList;

        // 1. 소켓을 쓰기 위한 객체를 얻는다. (현재 연결된 클라이언트에게 쓰는 객체)
        // 2. 소켓으로부터 읽어들일 수 있는 객체를 얻는다.
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outList.add(out); // 외부의 리스트에 ChatThread 의 out을 넣어준다.
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void run() {

        // 3. 클라이언트가 보낸 메세지를 읽는다.
        // 4. 접속된 모든 클라이언트에게 메세지를 보낸다.
        // (현재 접속된 모든 클라이언트에게 쓸 수 있는 객체 필요)
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                for (int i = 0; i < outList.size(); i++) { // 접속한 모든 클라이언트에게 메세지를 전송한다.
                    PrintWriter o = outList.get(i);
                    o.println(line); // 입력받은 값을 전송
                    o.flush(); // 전송
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally { // 접속이 끊어질 때
            try {
                outList.remove(out);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < outList.size(); i++) {
                PrintWriter o = outList.get(i);
                o.println("어떤 클라이언트와의 접속이 끊어졌습니다. :(");
                o.flush();
            }
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
