import java.io.*;
import java.net.*;
import java.util.*;
import proto.TcpPacketProtos;
import proto.PlayerProtos;
import proto.*;
import com.google.protobuf.*;


public class ReceiverThread implements Runnable {

    public InputStream inFromServer;
    public DataInputStream serverdata;
    public byte[] receive;
    public proto.TcpPacketProtos.TcpPacket.ConnectPacket connectpacket;
    public  proto.TcpPacketProtos.TcpPacket protopacket2;
    public  proto.TcpPacketProtos.TcpPacket.ChatPacket chatpacket;
    public Socket server;

	//constructors
    
    public ReceiverThread(Socket server,InputStream inFromServer, DataInputStream serverdata, byte[] receive, proto.TcpPacketProtos.TcpPacket.ConnectPacket connectpacket, proto.TcpPacketProtos.TcpPacket protopacket2, proto.TcpPacketProtos.TcpPacket.ChatPacket chatpacket){
        this.inFromServer = inFromServer;
        this.serverdata = serverdata;
        this.receive = receive;
        this.connectpacket = connectpacket;
        this.protopacket2 = protopacket2;
        this.chatpacket = chatpacket;
        this.server = server;
    }

    public void run() {

        while (true) {
            try {
                if(protopacket2.getType() == proto.TcpPacketProtos.TcpPacket.PacketType.ERR){
                    proto.TcpPacketProtos.TcpPacket.ErrPacket errorpacket = proto.TcpPacketProtos.TcpPacket.ErrPacket.parseFrom(receive);
                    System.out.println(errorpacket.getErrMessage());
                    }
                    else if(protopacket2.getType() == proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT){
                        inFromServer = server.getInputStream();
                        serverdata = new DataInputStream(inFromServer);
                        receive = new byte[serverdata.available()];
                        serverdata.read(receive);
                        connectpacket = proto.TcpPacketProtos.TcpPacket.ConnectPacket.parseFrom(receive);
                        System.out.println(connectpacket.getType());
                        System.out.println(connectpacket.getPlayer());
                        System.out.println(connectpacket.getLobbyId());                    }
                    else{
                        inFromServer = server.getInputStream();
                        serverdata = new DataInputStream(inFromServer);
                        receive = new byte[serverdata.available()];
                        serverdata.read(receive);
                        chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.parseFrom(receive);
                        System.out.println(chatpacket.getMessage());
                    }

            } catch (IOException e) {

            }
        }
        
    }

    
}