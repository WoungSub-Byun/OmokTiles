import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;


public class TCPIPnet {

    public int countTiles = 0;
    public int[][] pan = new int[13][13];
    public boolean isBlackWin = false;
    public boolean isWhiteWin = false;
    public int blackTile = 2;
    public int whiteTile = 1;
    int nowPlayer;
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
    public boolean isChecked(int x, int y) {
        //true => 돌이 이미 놓아져 있는 상태
        //pan으로 확인
        if(pan[x][y] == 0){
            return false; //돌 없을때
        }
        return true; //돌있는상태
    }
    
    
    //검은돌 세로
    public boolean black_heightCheck(int a, int b){
        int count=0;
        int max_count=0;
        if(pan[a][b] == 2){
            int j=b;
    
            for(int i=(a-5);i<=(a+5);i++){
                if(i>=0&&i<13){
                    if(pan[i][j]==2){
                        count++;
                        if(max_count<count){
                            max_count=count;
                        }
                    }
                    else{
                        count=0;
                    }
                }
    
            }
            //System.out.println("max : " + max_count);
            if(max_count==5) //5목 확인
                return true;
            else if(max_count>=6){ //6목 방지
                return false;
            }
        }
        return false;
    }
    
    //검은돌 가로
    public boolean black_widthcheck(int a,int b){ //검정돌 세로확인.
        int count=0;
        int max_count=0;
        if(pan[a][b] == 2){
            int i=a;
    
            for(int j=(b-5);j<=(b+5);j++){
                if(j>=0&&j<13){
                    if(pan[i][j]==2){
                        count++;
                        if(max_count<count){
                            max_count=count;
                        }
                    }
                    else{
                        count=0;
                    }
                }
    
            }
            //System.out.println("max : " + max_count);
            if(max_count==5) //5목 확인
                return true;
            else if(max_count>=6){ //6목 방지
                return false;
            }
        }
        return false;
    }
    
    //검은돌 좌상우하
    public boolean black_leftdiagcheck(int a,int b){//검정돌 좌상우하 대각선
        int count=0;
        int max_count=0;
        if(pan[a][b]==2){
            for(int i=-5;i<=5;i++){
                if(a+i>=0&&a+i<13){
                    if(b+i>=0&&b+i<13){
                        if(pan[a+i][b+i]==2){
                            count++;
                            if(max_count<count){
                                max_count=count;
                            }
                        }
                        else{
                            count=0;
                        }
                    }
                }
            }
            if(max_count==5){
                return true;
            }
            else if(max_count>=6){
                return false;
            }
        }
        return false;
    }
    
    //검은돌 우상좌하
    public boolean black_rightdiagcheck(int a,int b){//검정돌 우상좌하 대각선
        int count=0;
        int max_count=0;
        if(pan[a][b]==2){
            for(int i=-5;i<=5;i++){
                if(a-i>=0&&a-i<13){
                    if(b+i>=0&&b+i<13){
                        if(pan[a-i][b+i]==2){
                            count++;
                            if(max_count<count){
                                max_count=count;
                            }
                        }
                        else{
                            count=0;
                        }
                    }
                }
            }
            if(max_count==5){
                return true;
            }
            else if(max_count>=6){
                return false;
            }
        }
        return false;
    }
    
    //흰돌 가로
    public boolean white_widthCheck(int a, int b){
        int count=0;
        int max_count=0;
        if(pan[a][b] == 1){
            int j=b;
    
            for(int i=(a-5);i<=(a+5);i++){
                if(i>=0&&i<13){
                    if(pan[i][j]==1){
                        count++;
                        if(max_count<count){
                            max_count=count;
                        }
                    }
                    else{
                        count=0;
                    }
                }
    
            }
            //System.out.println("max : " + max_count);
            if(max_count==5) //5목 확인
                return true;
            else if(max_count>=6){ //6목 방지
                return false;
            }
        }
        return false;
    }
    
    //흰돌 세로
    public boolean white_heightcheck(int a,int b){ //검정돌 세로확인.
        int count=0;
        int max_count=0;
        if(pan[a][b] == 1){
            int i=a;
    
            for(int j=(b-5);j<=(b+5);j++){
                if(j>=0&&j<13){
                    if(pan[i][j]==1){
                        count++;
                        if(max_count<count){
                            max_count=count;
                        }
                    }
                    else{
                        count=0;
                    }
                }
    
            }
            //System.out.println("max : " + max_count);
            if(max_count==5) //5목 확인
                return true;
            else if(max_count>=6){ //6목 방지
                return false;
            }
        }
        return false;
    }
    
    //흑돌 좌상우하
    public boolean white_leftdiagcheck(int a,int b){//검정돌 좌상우하 대각선
        int count=0;
        int max_count=0;
        if(pan[a][b]==1){
            for(int i=-5;i<=5;i++){
                if(a+i>=0&&a+i<13){
                    if(b+i>=0&&b+i<13){
                        if(pan[a+i][b+i]==1){
                            count++;
                            if(max_count<count){
                                max_count=count;
                            }
                        }
                        else{
                            count=0;
                        }
                    }
                }
            }
            if(max_count==5){
                return true;
            }
            else if(max_count>=6){
                return false;
            }
        }
        return false;
    }
    
    //흰돌 우상좌하
    public boolean white_rightdiagcheck(int a,int b){//검정돌 우상좌하 대각선
        int count=0;
        int max_count=0;
        if(pan[a][b]==1){
            for(int i=-5;i<=5;i++){
                if(a-i>=0&&a-i<13){
                    if(b+i>=0&&b+i<13){
                        if(pan[a-i][b+i]==1){
                            count++;
                            if(max_count<count){
                                max_count=count;
                            }
                        }
                        else{
                            count=0;
                        }
                    }
                }
            }
            if(max_count==5){
                return true;
            }
            else if(max_count>=6){
                return false;
            }
        }
        return false;
    }
    
    public boolean checkOrder(int myTurn){
        if (myTurn == nowPlayer){
            return true;
        } else {return false;
        }
    }

    public void nextTurn() {
        nowPlayer++;
        if(nowPlayer == 2){
            nowPlayer = 0;
        }
    }

    // 결과 확인
    public String checkResult(int a, int b){
        if(black_heightCheck(a, b) || black_widthcheck(a, b) || black_leftdiagcheck(a, b) || black_rightdiagcheck(a, b) ){
            return "blackWin";
        }else if(white_widthCheck(a, b) || white_heightcheck(a, b)||white_leftdiagcheck(a, b) || white_rightdiagcheck(a, b))  { //검은돌 세로 오목 체크
            return "whiteWin";
        } else if(countTiles > 169) {
            return "draw";
        } return "continue";
    }

    //판 초기화
    public void panInit(){
        for(int i=0;i<13;i++){
            for(int j =0;j<13;j++){
                pan[i][j] = 0;
            }
        }
    }

    //클라이언트 입력 관리
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
                    System.out.println("input : "+msg+" from ["+getSocket().getLocalAddress()+"]");
                    
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
                        String protocol = msg.split(":")[0];
                        String message = ""; //client에 보낼 메시지 변수

                        switch(protocol) {
                            case "ENTER": //client가 처음 입장했을때 
                                System.out.println(tMan.size());
                                tMan.sendTo(tMan.size()-1, "ORDER:"+String.valueOf(tMan.size()-1)); //들어온 순서대로 돌 색지정
                                if(tMan.size()==2){
                                    message = "START";  //두명 들어왔을때 게임시작-> click 잠금 풀기
                                    tMan.sendToAll(message);
                                    nowPlayer = 0;
                                }
                                break;
                            case "VALUE": //좌표값
                                String value = msg.split(":")[1];
                                int myTurn = Integer.parseInt(msg.split(":")[2]); // 0:black 1:white
                                int x = Integer.parseInt(value.split(",")[0]);
                                int y = Integer.parseInt(value.split(",")[1]);
                                int win, lose;
                                if(checkOrder(myTurn)){ //보낸 클라이언트의 순서가 맞을때
                                    if (isChecked(x, y)){
                                        tMan.sendTo(myTurn, "돌이 이미 놓여져 있습니다.");
                                    }else{
                                        countTiles++;
                                        if(myTurn == 0){
                                            pan[x][y] = blackTile;
                                            tMan.sendToAll("VALUE:"+x+","+y+":"+nowPlayer);
                                        } else {
                                            pan[x][y] = whiteTile;
                                            tMan.sendToAll("VALUE:"+x+","+y+":"+nowPlayer);
                                        }
                                        if(checkResult(x,y) == "blackWin"){ // 검은돌이 승리했을시
                                            win = nowPlayer;
                                            lose = ++nowPlayer;
                                            tMan.sendTo(win,"RESULT:Win!!!!!!");
                                            tMan.sendTo(lose,"RESULT:LOSE");
                                            panInit();
                                        } else if(checkResult(x,y) == "whiteWin"){  // 흰돌이 승리했을시
                                            win = nowPlayer;
                                            lose = --nowPlayer;
                                            tMan.sendTo(win,"RESULT:Win!!!!!!");
                                            tMan.sendTo(lose,"RESULT:LOSE");
                                            panInit();
                                        }else if(checkResult(x,y)=="draw"){ //무승부일때
                                            tMan.sendToAll("RESULT:DRAW");
                                            panInit();
                                        }else{
                                            nextTurn(); //nowplayer++
                                        }
                                    }
                                } else{ //현재 클라이언트의 순서가 아닐때
                                    tMan.sendTo(myTurn, "당신의 차례가 아닙니다.");
                                }
                                break;
                            case "EDIT": //나갈때
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

                                break;
                        }
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
            return (GameThread)get(i);
        }

        Socket getSocket(int i) {
            return getGt(i).getSocket();
        }

        void showConnectedNum(){ //현재 접속된 Client수 출력
            System.out.println("접속자 수: "+size());
            for(int i = 0; i<size(); i++){
                System.out.println(i+"번 : "+getSocket(i));
            }
        }
        
        //클라이언트에게 메시지 보내기
        void sendTo(int i, String msg){
            try{
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getSocket(i).getOutputStream())));
                writer.println(msg);
                writer.flush();
                System.out.println("["+msg+"] was sent");
                
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

        //접속한 모든 클라이언트에게
        void sendToAll(String msg){
            for(int i=0; i < size(); i++) {
                sendTo(i, msg);
            }
        }

    }
}