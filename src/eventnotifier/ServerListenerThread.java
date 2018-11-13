package eventnotifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

public class ServerListenerThread implements Runnable {
    private Thread thread = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public ServerListenerThread( ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream ) {
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;

        thread = new Thread( this );
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message messageIn = (Message) objectInputStream.readObject();
                if ( messageIn instanceof Ping ) {
                    Ping ping = (Ping) messageIn;
                    objectOutputStream.writeObject( new Ping() );
                } else {
                    System.out.println("[" + messageIn.getTime().toString() + "]" + messageIn.getLogin() + ":" + messageIn.getMessage());
                }
            }
        }
        catch ( SocketException e ) { e.getMessage(); }
        catch ( ClassNotFoundException e ) { e.getMessage(); }
        catch ( IOException e ) { e.getMessage(); }
    }
}