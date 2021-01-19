import java.rmi.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The Note class to be used in our File Keeper app
 * @Fadel Alshammasi
 * @Lily Krohn
 * Date: Nov 14, 2020
 */

public class Note implements Serializable{
    
    private static final long serialVersionUID = 20120731125400L;
    private String title; 
    private LocalDate date;
    private File body; 

    public Note(File b, String t, LocalDate d){
        this.body=b; 
        this.title= t;
        this.date=d;
    }

    public String getTitle(){
        return title;
    }

    public LocalDate getDate(){
        return date;
    }

    public File getTBody(){
        return body;
    }

    public void setTitle(String str){
        this.title=str;
    }

    public void setBody(File b){
        this.body=b; 
    }
    

}