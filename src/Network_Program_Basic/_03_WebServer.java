package Network_Program_Basic;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class _03_WebServer {
    public static void main(String[] args) throws Exception {

        // 클라이언트가 접속할 때까지 대기할 때 필용한 객체 ServerSocket
        ServerSocket serverSocket = new ServerSocket(10000); // 10000 포트 사용

        System.out.println("1. 대기한다.");

        // 서버를 죽지않게 무한 반복
        try {
            while (true) {
                // 대기하다가 클라이언트가 접속하면 클라이언트와 통신할 수 있는 소켓을 반환한다.
                Socket clientSocket = serverSocket.accept(); // 하나씩 요청한 순서대로 처리한다. (동시에 처리하기 위해 쓰레드 필요!)
                System.out.println("2. 접속 완료.");

                CilentThread ct = new CilentThread(clientSocket);
                ct.start();
            }
        } finally {
            serverSocket.close();
            System.out.println("서버가 종료됩니다.");
        }
    }
}

class CilentThread extends Thread {

    private Socket clientSocket;

    public CilentThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // 한줄씩 정보를 읽어들이기 위한 코드
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)); // 한줄씩 받기

            // 한줄씩 출력해주기 위한 코드
            OutputStream outputStream = clientSocket.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));

            ////////////////////////////////////////////////////////////////////////////////

            // http://localhost:10000 (localhost == 127.0.0.1)
            // GET / HTTP/1.1
            // http://localhost:10000/hello
            // GET /hello HTTP/1.1

            String firstLine = br.readLine(); // 첫번째 줄(정보)
            // 첫번째로 GET /____ HTTP/1.1를 반환하기에 이를 이용해서 다양한 응답메세지를 보낼 수 있다.

            String msg = "";
            if (firstLine.indexOf("/hello") >= 0)
                msg = "hello";
            else if (firstLine.indexOf("/hi") >= 0)
                msg = "hi";

            System.out.println();

            // Request Header 정보 읽어들이기
            String line = null;
            List<String> headers = new ArrayList<>();
            while (!(line = br.readLine()).equals("")) {
                System.out.println(line);
            }
            // 빈줄까지 읽어들이면 끝

            ////////////////////////////////////////////////////////////////////////////////

            // 서버에 응답 메세지 보내기
            System.out.println("3. 응답한다.");

            // HTTP/1.1 200 OK <-- 상태 메세지
            // 헤더 1
            // 헤더 2
            // 빈줄
            // 내용

            pw.println("HTTP/1.1 200 OK"); // 상태 메세지
            pw.println("name : Jo"); // 헤더1
            pw.println("email : hyuntae9912@naver.com"); // 헤더2
            pw.println(); // 빈줄
            pw.flush();
            // 여기서 flush하는 이유?
            // GET /hello HTTP/1.1의 요청이 왔을 때, pf.png, txt 등에 대한 파일을 읽어서 출력한다.
            // 각 파일에 대한 사전정보가 필요한데 이를 헤더1, 헤더2에서 미리 반환해줘야한다.

            // /hello를 어디서 읽어들이나? -> 서버입장으로 봤을 때 어디 경로에 있는 파일을 읽어들이지??

            // 내용 (body)
            pw.println("<html>");
            pw.println(firstLine + "!!!!");
            pw.println("</html>");

            pw.flush(); // 정보를 클라이언트에게 전송

            // 종료
            br.close();
            pw.close();
            clientSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


