import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * The Server side code of our File Keeper application 
 * @Fadel Alshammasi
 * @Lily Krohn
 * Date: Nov 14, 2020
 */


public class Server implements Remoteinterface {

   /**
    * A hashmap to keep track of user credentials
    */
   private HashMap <String, String> userInfo= new HashMap <String, String>(); 

   /**
    * A hashmap to keep track of notes for each user 
    */
   private HashMap<String, ArrayList<Note>> noteBrowser= new HashMap <String, ArrayList<Note>>(); 

   public static String DRIVE_LOCATION_IN_YOUR_COMPUTER= "/Users/fadelalshammasi/Desktop/Drive"; // please change it to yours 


        
    public Server() {}

    
    /**
     * A method to add the user's credentials in the hashmap when they sign up
     * @return false if username already exists, otherwise return true
     */
    public boolean createAccount (String username, String password) throws RemoteException{
        if(userInfo.containsKey(username)){
            return false; 
        }
        else{
            userInfo.put(username, password);
            return true; 
        }

    }

    /**
     * A method to check if the password entered for that username is correct
     * @return true if correct, otherwise return false
     */
    public boolean login (String username, String password) throws RemoteException {
        String verify= userInfo.get(username);
        if (password.equals(verify)){
            return true; 
        }
        return false; 
    }

    /**
     * A method to create a folder for each user in the "Drive"
     */
    public void createFolder (String username) throws RemoteException{
        String toDriveFolder= DRIVE_LOCATION_IN_YOUR_COMPUTER;
        String concatUsername= toDriveFolder +"/"+username;
        File file = new File(concatUsername);
        boolean bool = file.mkdir();

        if(bool){
            System.out.println("Directory created successfully");
         }else{
            System.out.println("Sorry couldn’t create specified folder");
         }

    }

    /**
     * A method to upload the file to the user's folder in the drive 
     */
    public void upload (byte[] mydata, String serverpath, int length, String username, String filename, String extention) throws RemoteException{
         
        try {
        
            FileOutputStream out=new FileOutputStream(DRIVE_LOCATION_IN_YOUR_COMPUTER +"/"+ username +"/"+ filename+"."+extention);
            byte [] data=mydata;
            out.write(data);
            out.flush();
            out.close();
           

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    /**
     * A method to retrieve the file from the drive 
     */
    public byte[] download(String serverpath) throws RemoteException {
					
		   byte [] mydata;	
		
			File serverpathfile = new File(serverpath);			
			mydata=new byte[(int) serverpathfile.length()];
			FileInputStream in;
			try {
				in = new FileInputStream(serverpathfile);
				try {
					in.read(mydata, 0, mydata.length);
				} catch (IOException e) {
					
					e.printStackTrace();
				}						
				try {
					in.close();
				} catch (IOException e) {
				
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}		
			
			return mydata;
				 
    }
    

    /**
     * A method to store the uploaded note to the server's hashmap 
     */
    public void uploadNoteToServer (Note note, String username) throws RemoteException{

        if(!noteBrowser.containsKey(username)){
            ArrayList <Note> n= new ArrayList <Note>();
            n.add(note);
            noteBrowser.put(username,n);
        }

        else{
            ArrayList<Note> notes= noteBrowser.get(username);
            notes.add(note);
            noteBrowser.put(username,notes);
        }
        

    }

    /**
    * A method that returns the list of Notes that belong to the requesting user
    */
    public ArrayList<Note> getMyListOfNotesFromServer (String username) throws RemoteException{
        return noteBrowser.get(username);
    }

    public static void main(String args[]) {
        
        try {
            Server obj = new Server();
            Remoteinterface stub = (Remoteinterface) UnicastRemoteObject.exportObject(obj, 0);

            
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Remoteinterface", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}