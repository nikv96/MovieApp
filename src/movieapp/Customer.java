package movieapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Team Fork
 */
public class Customer extends Person {
    
    static CustomerDB custObj[] = new CustomerDB[10];
    
    static JSONObject custJSON[] = new JSONObject[10];
  
    static int total_id = 0;
    
    protected int id;
    
    protected int booked[];
    
    private Customer cObject[] = new Customer[10];
    
    public Customer(){
    }

    public Customer(String name, int age, String emailid, long phno, long postal, String gender, String user, String pass, int[] book) {
        this.name = name;
        this.age = age;
        this.emailID = emailid;
        this.phone = phno;
        this.postalCode = postal;
        this.gender = gender;
        this.username = user;
        this.password = pass;
        this.booked = book;
    }
    
    @Override
    public void login (){
        try{
        int ch;
        boolean flag;
        do{
            super.login();
            flag = CustomerDB.checkUser(username, password);
            if ( flag == false ){
                System.out.println("Either username or password is wrong.");
                System.out.println("1. Sign up 2. Login");
                ch = sc.nextInt();
                if(ch == 1)
                {
                    signup();
                    login();
                    flag = true;
                }
            
            }
        } while (flag !=true);
        displayMenu();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
        
    
    @Override
    public void signup (){
        super.signup();
        Customer c = new Customer();
        c.id = total_id++;
        c.name = name;
        c.username = username;
        c.password = password;
        c.age = age;
        c.gender = gender;
        c.emailID = emailID;
        c.phone = phone;
        c.postalCode = postalCode;
        c.booked = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
        
        try{
            cObject[total_id] = c;
            custJSON[total_id] = CustomerDB.addCustomer(c);
        } catch(IOException | ParseException e)
        {
            e.getMessage();
        }
                
    }
    
    @Override
    public void displayMenu() throws IOException, FileNotFoundException
    {
        try {
            CustomerDB.getCustomersArray(cObject);
        } catch (ParseException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        MovieMenu menu = new MovieMenu();
        MovieDB menuObj = new MovieDB();
        MovieDB movies[] = new MovieDB[menuObj.getTotalId()];
        try{
            movies = menuObj.getMovieArray(movies);
            menu.customerMenu(movies, cObject[0]);
        } catch(IOException | ParseException e)
        {
            e.getMessage();
        }
        
    }

    
    public int[] getBookingHistory(Customer cObject)
    {
        return cObject.booked;
    }
}
