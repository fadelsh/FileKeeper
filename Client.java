import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The client side code of our File Keeper application 
 * @Fadel Alshammasi
 * @Lily Krohn
 * Date: Nov 14, 2020
 */

public class Client {

    public static String DRIVE_LOCATION_IN_YOUR_COMPUTER= "/Users/fadelalshammasi/Desktop/Drive"; // please change it to yours 


    private Client() {}

    public static void main(String[] args) {

    
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            Remoteinterface stub = (Remoteinterface) registry.lookup("Remoteinterface");

              System.out.println("Welcome to File Keeper! ");
              System.out.println(); 
                Scanner sc= new Scanner (System.in);
                System.out.print("Do you have an account (y/n)? ");
                String res= sc.next();

            if (res.equals("n")){
                    String username="";
                    boolean answer=false; 

                System.out.println("Now is the time to create your username and password! Please note that you will have to try again with another username if your choice is taken"); 
                while (!answer){
                    System.out.print("Enter the username of the account you want to create: ");
                    username= sc.next(); 
                    System.out.print("Enter the password of the account you want to create: ");
                    String pass=sc.next(); 
                    answer= stub.createAccount(username, pass);
                    System.out.println(); 
                }
                stub.createFolder(username);

            }

                System.out.println ("Now is the time to login!");
                System.out.println(); 
                System.out.print("Enter your username: ");
                String user= sc.next(); 
                System.out.print("Enter your password: ");
                String passwordLogin= sc.next(); 

                while (!stub.login(user, passwordLogin)){
                  System.out.println("Sorry invalid credintials. Try again! "); 
                  System.out.println(); 

                  System.out.print("Enter your username: ");
                   user= sc.next(); 
                   System.out.print("Enter your password: ");
                    passwordLogin= sc.next(); 
                }
                System.out.println(); 
                System.out.println ("You have successfully logged in!");


                while (true){

                // Menu of options!
                System.out.println();
                System.out.println ("Menu");
                System.out.println ("1. Upload a file ");
                System.out.println ("2. Download a file");
                System.out.println ("3. Create a note");
                System.out.println ("4. Open an existing note");
                System.out.println(); 
                System.out.print("Enter your choice (select a number): ");

                String choice= sc.next();

                // Uploading 
                if (choice.equals("1")){
                    
                    System.out.print("Enter the full path of the file that you want to upload: ");
                    String p= sc.next(); 

                    String []strArray= p.split("/");
                    String last= strArray[strArray.length-1];
                    String [] further= last.split("\\.");
                    String fName= further[0];
                    String ext= further[1];
                    

                    File clientpathfile = new File(p);

                    if(!clientpathfile.exists()){
                        System.out.println(); 
                        System.out.println("You seem to have entered an incorrect path. Please double check and try uploading again with a correct path");
                    }

                    else{

                    byte [] mydata=new byte[(int) clientpathfile.length()];
                    FileInputStream in=new FileInputStream(clientpathfile);	
                    System.out.println("uploading to the file server...");
                    in.read(mydata, 0, mydata.length);
                    stub.upload(mydata, p, (int) clientpathfile.length(), user, fName, ext);
                    in.close();
                    }

                }

                //downloading 
                if (choice.equals("2")){

                           String[] pathnames;

                            String dir=DRIVE_LOCATION_IN_YOUR_COMPUTER+"/"+user;
                            File f= new File (dir);

                            pathnames=f.list();

                        if(pathnames.length==0){
                                System.out.println("You are trying to download BUT you haven't uploaded anything! Please upload first");

                            }

                    else{

                            System.out.println("These are the files that you have saved: ");

                            for(String pathname: pathnames){
                                System.out.println(pathname);
                            }

                            System.out.print("Enter the file's name that you want to download (including file extension): ");
                            String fileChosen= sc.next(); 
                            String [] fName= fileChosen.split("\\.");

                            String str= dir+"/"+fName[0]+"."+fName[1];
                            
                    
                    System.out.println(str);
                    byte [] mydata = stub.download(str); //location of download
		

                   System.out.print("Enter the location where you want to download the file to: ");
                   String loc=sc.next();


              File verificationExistance= new File (loc); 

              if(!verificationExistance.exists()){
                    System.out.println();
                    System.out.println("This directory/location that you want to save your file does not exist. Try all over again."); 
                
            }else{
                System.out.println("downloading...");
                String pOfDownloaded= loc+ "/"+fileChosen;
                File clientpathfile = new File(pOfDownloaded); //place where you want to save

				FileOutputStream out=new FileOutputStream(clientpathfile);				
	    		out.write(mydata);
				out.flush();
                out.close();

                System.out.println();
                System.out.println("Successfully downloaded in your desired location!");
            }
        }
    }
                
                // create a new note 
                if (choice.equals("3")){
                    System.out.print ("Enter the title of the note:");
                    String noteTitle=sc.next();
                    
                    LocalDate noteDate= java.time.LocalDate.now(); 

                    Scanner in = new Scanner(System.in);
                    
                    File outFile = new File(noteTitle+"."+"txt");
                    PrintWriter out = new PrintWriter(outFile);
                
                    System.out.println ("Enter the body of your note below then do ctrl-Z (Windows) or control-D (Mac) once you are done: ");
                    while (in.hasNextLine())
                    {
                      String line = in.nextLine();
                      out.println(line);
                    }
                    
                    System.out.println("Done");
                    out.close();
                    
                    Note note= new Note (outFile, noteTitle, noteDate); 

                    stub.uploadNoteToServer(note, user);


                }

            // opening an existing note
            if (choice.equals("4")){
                    ArrayList<Note> myNotes= stub.getMyListOfNotesFromServer(user);

                if (myNotes == null){
                    System.out.println("You have no notes! Please upload some notes first"); 
                }

                else{
                    System.out.println ("Below are your existing notes: ");
                    for (Note note: myNotes){
                        System.out.println(note.getTitle() + " created on: "+ note.getDate());

                    }
                    System.out.println();
                    System.out.print ("Which note do you want to open? Choose one by entering the title: ");
                    String noteChosen= sc.next();

                    System.out.println(); 

                    File f= new File (noteChosen+"."+"txt"); 

                    Scanner sc2= new Scanner (f); 

                    while (sc2.hasNextLine()){
                        String line= sc2.nextLine();
                        System.out.println(line);
                    }

                }

                
            }

    }
}

         catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}