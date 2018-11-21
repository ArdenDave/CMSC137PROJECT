/*
 * GreetingClient.java
 * CMSC137 Sample Code for TCP Socket Programming
 */

import java.net.*;
import java.io.*;
import java.util.*;
import proto.TcpPacketProtos;
import proto.PlayerProtos;
import proto.*;
import com.google.protobuf.*;


public class GreetingClient{
    public static void main(String [] args){
        try{
            // String serverName = args[0]; //get IP address of server from first param
            // int port = Integer.parseInt(args[1]); //get port from second param
            // String message = args[2]; //get message from the third param

            /* Open a ClientSocket and connect to ServerSocket */
            System.out.println("Connecting to " + "202.92.144.45" + " on port " + 80);
            
			//creating a new socket for client and binding it to a port
            Socket server = new Socket("202.92.144.45", 80);

            System.out.println("Just connected to " + server.getRemoteSocketAddress());
            Scanner sc = new Scanner(System.in);

            System.out.println("Enter Player Name: ");
            String playername = sc.nextLine();


            proto.PlayerProtos.Player player = proto.PlayerProtos.Player.newBuilder().setName(playername).build();
            proto.TcpPacketProtos.TcpPacket.CreateLobbyPacket createlobbypacket = proto.TcpPacketProtos.TcpPacket.CreateLobbyPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CREATE_LOBBY).setLobbyId("AB4L").setMaxPlayers(4).build();
            proto.TcpPacketProtos.TcpPacket.DisconnectPacket disconnectpacket = proto.TcpPacketProtos.TcpPacket.DisconnectPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.DISCONNECT).build();
            proto.TcpPacketProtos.TcpPacket.ConnectPacket connectpacket = proto.TcpPacketProtos.TcpPacket.ConnectPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT).setLobbyId("AB4L").setPlayer(player).build();
            proto.TcpPacketProtos.TcpPacket.ChatPacket chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CHAT).setMessage("sample").setLobbyId("AB4L").build();
            proto.TcpPacketProtos.TcpPacket protopacket3 =  proto.TcpPacketProtos.TcpPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CHAT).build();
            


             // proto.TcpPacketProtos.TcpPacket.ConnectPacket disconnectpacket = proto.TcpPacketProtos.TcpPacket.DisconnectPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT).build();

            InputStream inFromServer = server.getInputStream();
            // ByteArrayInputStream serverdata = new ByteArrayInputStream(inFromServer);
            DataInputStream serverdata = new DataInputStream(inFromServer);
            // ObjectInputStream object = new ObjectInputStream(serverdata);
            // chatpacket = (proto.TcpPacketProtos.TcpPacket.ChatPacket) object.readObject();

            byte[] receive = new byte[serverdata.available()];
            // serverdata.read(receive);
            // chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.parseFrom(receive);





            System.out.println("Enter Mode: ");
            int mode = sc.nextInt();

            byte[] messageData = connectpacket.toByteArray();
            ByteArrayInputStream data = new ByteArrayInputStream(messageData);
            OutputStream outToServer = server.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);


            byte[] chatData = chatpacket.toByteArray();
            String chatmessage = "";
            chatmessage = sc.nextLine();
            if(mode == 2){
                chatmessage = sc.next();
                sc.nextLine();
                connectpacket = proto.TcpPacketProtos.TcpPacket.ConnectPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT).setLobbyId(chatmessage).setPlayer(player).build();
                messageData = connectpacket.toByteArray();
                data = new ByteArrayInputStream(messageData);
                outToServer = server.getOutputStream();
                out = new DataOutputStream(outToServer);
                out.write(messageData);
                

            }
            else{

                messageData = createlobbypacket.toByteArray();
                data = new ByteArrayInputStream(messageData);
                outToServer = server.getOutputStream();
                out = new DataOutputStream(outToServer);
                out.write(messageData);

                chatmessage = sc.nextLine();
                // chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CHAT).setMessage("test").setLobbyId("AB4L").build();
                // messageData = chatpacket.toByteArray();
                // data = new ByteArrayInputStream(messageData);
                // outToServer = server.getOutputStream();
                // out = new DataOutputStream(outToServer);
                // out.write(messageData);

                inFromServer = server.getInputStream();
                serverdata = new DataInputStream(inFromServer);
                receive = new byte[serverdata.available()];
                serverdata.read(receive);
                chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.parseFrom(receive);
                String lobby_id = chatpacket.getMessage();
                System.out.println(lobby_id);

                connectpacket = proto.TcpPacketProtos.TcpPacket.ConnectPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT).setLobbyId(lobby_id).setPlayer(player).build();
                messageData = connectpacket.toByteArray();
                data = new ByteArrayInputStream(messageData);
                outToServer = server.getOutputStream();
                out = new DataOutputStream(outToServer);
                out.write(messageData);
                System.out.println(connectpacket.getType());
                System.out.println(connectpacket.getPlayer());
                System.out.println(connectpacket.getLobbyId());

                // chatmessage = sc.nextLine();

                inFromServer = server.getInputStream();
                serverdata = new DataInputStream(inFromServer);
                receive = new byte[serverdata.available()];
                serverdata.read(receive);
                proto.TcpPacketProtos.TcpPacket protopacket = proto.TcpPacketProtos.TcpPacket.parseFrom(receive);
                System.out.println(protopacket.getType());
                if(protopacket.getType() == proto.TcpPacketProtos.TcpPacket.PacketType.ERR){
                    proto.TcpPacketProtos.TcpPacket.ErrPacket errorpacket = proto.TcpPacketProtos.TcpPacket.ErrPacket.parseFrom(receive);
                    System.out.println(errorpacket.getErrMessage());
                }
            }
            Thread receiverthread = new Thread(new ReceiverThread(server, inFromServer, serverdata, receive, connectpacket, protopacket3, chatpacket));
            receiverthread.start();


            while(true){
                System.out.println("Enter Message: ");
                chatmessage = sc.nextLine();
                if(chatmessage.equals("exit")){
                    break;
                }
                chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.newBuilder().setType(proto.TcpPacketProtos.TcpPacket.PacketType.CHAT).setMessage(chatmessage).setLobbyId("AB4L").build();
                messageData = chatpacket.toByteArray();
                data = new ByteArrayInputStream(messageData);
                outToServer = server.getOutputStream();
                out = new DataOutputStream(outToServer);
                out.write(messageData);

                // proto.TcpPacketProtos.TcpPacket protopacket2 = proto.TcpPacketProtos.TcpPacket.parseFrom(receive);
                // System.out.println("type is" + protopacket2.getType());

                // try{
                      
                //     if(protopacket2.getType() == proto.TcpPacketProtos.TcpPacket.PacketType.ERR){
                //     proto.TcpPacketProtos.TcpPacket.ErrPacket errorpacket = proto.TcpPacketProtos.TcpPacket.ErrPacket.parseFrom(receive);
                //     System.out.println(errorpacket.getErrMessage());
                //     }
                //     else if(protopacket2.getType() == proto.TcpPacketProtos.TcpPacket.PacketType.CONNECT){
                //         inFromServer = server.getInputStream();
                //         serverdata = new DataInputStream(inFromServer);
                //         receive = new byte[serverdata.available()];
                //         serverdata.read(receive);
                //         connectpacket = proto.TcpPacketProtos.TcpPacket.ConnectPacket.parseFrom(receive);
                //         System.out.println(connectpacket.getType());
                //         System.out.println(connectpacket.getPlayer());
                //         System.out.println(connectpacket.getLobbyId());                    }
                //     else{
                //         inFromServer = server.getInputStream();
                //         serverdata = new DataInputStream(inFromServer);
                //         receive = new byte[serverdata.available()];
                //         serverdata.read(receive);
                //         chatpacket = proto.TcpPacketProtos.TcpPacket.ChatPacket.parseFrom(receive);
                //         System.out.println(chatpacket.getMessage());
                //     }
                // }
                // catch(Exception e){
                //     System.out.println(e);
                // }
               
                

                
            }



             messageData = disconnectpacket.toByteArray();
             data = new ByteArrayInputStream(messageData);
             outToServer = server.getOutputStream();
             out.write(messageData);
            System.out.println("Disconnected From " + server.getRemoteSocketAddress());



            /* Receive data from the ServerSocket */
            // InputStream inFromServer = server.getInputStream();
            // DataInputStream in = new DataInputStream(inFromServer);
            // System.out.println("Server says " + in.readUTF());
            
			//closing the socket of the client
            server.close();
            receiverthread.stop();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Cannot find (or disconnected from) Server");
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Usage: java GreetingClient <server ip> <port no.> '<your message to the server>'");
        }
    }
}
