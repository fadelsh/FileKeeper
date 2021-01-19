import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The remote interface for the File Keeper application 
 * @Fadel Alshammasi
 * @Lily Krohn
 * Date: Nov 14, 2020
 */


public interface Remoteinterface extends Remote {
    
    public boolean createAccount (String username, String password) throws RemoteException;
    public void createFolder (String username) throws RemoteException;
    public boolean login (String username, String password) throws RemoteException; 
    public void upload(byte[] mybyte, String serverpath, int length, String owner, String filename, String extention) throws RemoteException;
    public byte[] download(String servername) throws RemoteException;
    public void uploadNoteToServer (Note note, String username) throws RemoteException;
    public ArrayList<Note> getMyListOfNotesFromServer (String username) throws RemoteException;
}