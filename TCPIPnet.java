import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;


public class TCPIPnet {
    public static final int PORT = 9999;
    TManager tMan = new TManager();

    public TCPIPnet() { }


    public void startServer(){
            ServerSocket serverSocket = null;

        try {
            //ServerSocket 시작
            serverSocket = new ServerSocket();

            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
            consoleLog("Waiting for Connection at " + hostAddress + ":" + PORT);

            while(true){
                // Client에서 연결할 때까지 기다리다가 연결요청이 들어오면 accept
                Socket socket = serverSocket.accept();
                //연결 후
                GameThread gt = new GameThread(socket); // 클라이언트 소켓 추가
                gt.start();  //Thread start
                tMan.add(gt); //Vector에 접속한 Client Thread 추가 

                consoleLog("["+socket.getInetAddress()+"] client connected");
                tMan.showConnectedNum();
            }
        } catch (Exception e) {
            System.out.println("Server exception: "+e.getMessage());
            e.printStackTrace();
        } finally {
            try{
                if(serverSocket != null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
                consoleLog("Server Closed.....");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)  {
        TCPIPnet server = new TCPIPnet();
        server.startServer();
    }


    private static void consoleLog(String log) {
        System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
    }


    class GameThread extends Thread {

        Socket socket;
        BufferedReader reader;

        PrintWriter writer;

        GameThread(Socket socket){
            this.socket = socket;
        }

        Socket getSocket() {
            return socket;
        }

        public void run() {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer =
                        new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                while(true){

                    String msg = reader.readLine();
                    System.out.println("input : "+msg);
                    
                    //들어오는 값이 없을 때 연결 끊기
                    if(msg == null){
                        consoleLog("Connection Closed....");
                        reader.close();
                        socket.close();
                        tMan.remove(this);
                        tMan.showConnectedNum();
                        break;
                    }

                    if (tMan.isFull()) //접속자가 2명 초과일때 
                    {
                        tMan.remove(this); //Vector에 들어간 클라이언트 삭제
                        if (reader != null)
                        {
                            reader.close(); //Inputstream close
                        }
                        if (writer != null)
                        {
                            writer.close(); //OutputStream close
                        }
                        if (socket != null)
                        {
                            socket.close(); //Socket close
                        }
                        reader = null;
                        writer = null;
                        socket = null;
                    } else {
                        String protocol = msg.split(":")[0]; //Protocol
                        String value = msg.split(":")[1]; //값
                        
                        tMan.sendTo(protocol, value);
                    }
                }

            } catch(IOException e){
                e.printStackTrace();
            }catch (Exception e) {
                try{
                    tMan.remove(this);
                    if(reader != null){
                        reader.close();
                    }
                    if(writer != null){
                        writer.close();
                    }
                    if(socket != null){
                        socket.close();
                    }
                    reader = null;
                    writer = null;
                    socket = null;
                    System.out.println("Connection closed...");
                    tMan.showConnectedNum();
                }catch (Exception e1){
                    System.out.println(e1.getMessage());
                }
            }
        }

    }

    //다중스레드 관리 위한 Vector 클래스
    class TManager extends Vector{
        synchronized boolean isFull()        // 서버가 다 찼는지 확인
        {
            if (size() > 2)
            {
                return true;
            }
            return false;
        }
        public TManager() { }

        GameThread getGt(int i){
            return (GameThread)elementAt(i);
        }

        Socket getSocket(int i) {
            return getGt(i).getSocket();
        }

        void showConnectedNum(){ //현재 접속된 Client수 출력
            System.out.println("접속자 수: "+size());
        }
        
        //Client에게 메시지 보내기
        void sendTo(String protocol, String value){
            String msg="";
            try{
                switch(protocol){
                    case "ENTER":
                        msg += "ORDER:";
                        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getSocket(size()-1).getOutputStream())));
                        writer.println(msg+(size()-1));
                        writer.flush();
                        break;
                    case "VALUE":
                        break;
                }
                System.out.println("sent message to Client");
                
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

    }

}