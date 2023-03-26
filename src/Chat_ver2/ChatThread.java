package Chat_ver2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatThread extends Thread{
    private String name;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    List<ChatThread> list;

    public ChatThread(Socket socket, List<ChatThread> list) throws Exception {
        this.socket = socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = br;
        this.pw = pw;
        this.name = br.readLine(); // 처음 오는 값 = 이름
        this.list = list;
        this.list.add(this); // 자기자신(ChatThread)를 리스트에 넣어준다.
    }

    public void sendMessage (String msg) {
        pw.println(msg);
        pw.flush();
    }


    @Override
    public void run() {
        // broadcast
        // : ChatThread는 사용자 보낸 메세지를 읽어들여서 접속된 모든 클라이언트들에게 메세지를 보낸다.

        // 나를 제외한 모든 사용자에게 "--님이 연결되었습니다."
        // 현재 ChatThread를 제외하고 보낸다.
        try {
            broadcast(name + "님이 연결되었습니다.", false);

            String line = null;
            while ((line = br.readLine()) != null) {
                if ("/quit".equals(line)) {
                    throw new RuntimeException("접속 종료"); // 예외 발생
                }
                // 나를 포함한 ChatThread에게 메세지를 보낸다.
                broadcast(name + " : " + line, true);
            }
        } catch (Exception ex) { // ChatThread의 연결 끊어졌을 때
            ex.printStackTrace();
        } finally {
            broadcast(name + "님의 연결이 끊어졌습니다... :(", false);
            this.list.remove(this); // 자기자신을 제거
            // 종료
            try {
                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                pw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void broadcast (String msg, boolean includeMe) {
        List<ChatThread> chatThreads = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            chatThreads.add(list.get(i));
        }
        // 연결이 끊어지면 list가 제거되는데 중간에 list가 제거되어도 버그X

        try{
            for (int i = 0; i < chatThreads.size(); i++) {
                ChatThread ct = chatThreads.get(i);
                if (!includeMe) { // 나를 포함하고 않다면
                    if (ct == this) {
                        continue; // 무시하고 다시 시작
                    }
                }
                ct.sendMessage(msg);
            }
        } catch (Exception ex) {
            System.out.println("//");
        }

    }

}
