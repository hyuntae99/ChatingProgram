package Network_Program_Basic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class _02_VerySimpleWebServer {
    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(9090); // 9090 포트로 대기한다.

        // 클라이언트 대기하다 -> 클라이언트가 접속하는 순간, 클라이언트와 통신할 수 있는 소켓을 반환한다.
        System.out.println("클라이언트 접속을 기다립니다.");
        // 소켓 = 브라우저(클라이언트)와 통신할 수 있는 객체
        // 접속할 때까지 대기
        // 1. http://127.0.0.1:9090/으로 접속
        // 2. http://127.0.0.1:9090/board/hello.html
        Socket socket = ss.accept();

        // 클라이언트와 읽고 쓸 수 있는 InputStream, OutputStream을 반환
        OutputStream out = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out)); // out을 통해서 pw를 사용가능
        InputStream in = socket.getInputStream();

        // HTTP 프로토콜은 클라이언트가 정보를 서버에게 보내준다 (요청 정보)
        // 정보가 한번에 나오고 빈줄이 포함되어있음. -> 한줄씩 읽어야한다.
        BufferedReader br = new BufferedReader(new InputStreamReader(in)); // 한줄씩 받기
        String firstLine = br.readLine(); // 한줄씩 받기
        List<String> headers = new ArrayList<>();
        String line = null; // 빈줄
        // 빈줄이 나올때까지 실행
        while(!(line = br.readLine()).equals("")) {
            headers.add(line);
        }
        // 요청정보 읽기 끝
        System.out.println(firstLine);
        for (int i = 0; i < headers.size(); i++) {
            System.out.println(headers.get(i));
        }

        // 서버에 응답 메세지 보내기
        // HTTP/1.1 200 OK <-- 상태 메세지
        // 헤더 1
        // 헤더 2
        // 빈줄
        // 내용
        pw.println("HTTP/1.1 200 OK"); // 상태 메세지
        pw.println("name : Jo"); // 헤더1
        pw.println("email : hyuntae9912@naver.com"); // 헤더2
        pw.println(); // 빈줄
        // 내용
        pw.println("<html>");
        pw.println("<h1>Hello!!</h1>");
        pw.println("</html>");
        pw.close();

        ss.close();
        System.out.println("서버가 종료됩니다.");

        /////////////////////////////////////////////////////////////
        //////////    한번 정보를 보내고 서버는 죽는 프로그램    //////////
        /////////////////////////////////////////////////////////////

        // 쓰레드를 사용해야 계속 정보를 보내는 제대로 된 서버를 만들 수 있따.
    }
}
