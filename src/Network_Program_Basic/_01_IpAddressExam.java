package Network_Program_Basic;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class _01_IpAddressExam {
    public static void main(String[] args) throws UnknownHostException {
        // 127.0.0.1 == 자신의 ip
        // localhost == 자신의 도메인
        try{
            InetAddress ia =  InetAddress.getLocalHost(); // 내 컴퓨터의 ip 정보를 구한다. == cmd -> ipconfig 사용
            System.out.println(ia.getHostAddress());
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }

        try{
            InetAddress[] iaArray =  InetAddress.getAllByName("www.google.com"); // 해당 사이트의 ip 정보를 배열로 반환
            for(InetAddress ia : iaArray) {
                System.out.println(ia.getHostAddress());
            }
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }

    }
}
