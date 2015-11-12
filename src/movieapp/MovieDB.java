package movieapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Team Fork
 */
public class MovieDB {
    
    private String movieName;
    
    private String movieType;
    
    private String[] showTimings;
    
    private boolean[][][] seats;
    
    private double price;
    
    private String[] reviews;
    
    private double[] rating;
            
    private static int total_id = 0;
    
    private int id;
    
    private MovieDB allMovies[];
    
    public MovieDB(){
    }
    
    public MovieDB(String movieName, String movieType, String[] showTimings, String[] review, double[] rating){
        this.movieName = movieName;
        this.movieType = movieType;
        this.showTimings = showTimings;
        this.price = 20;
        this.seats = new boolean[10][40][10];
        for(int i =0;i<10;i++)
            for(int j=0;j<40;j++)
                for(int k=0;k<10;k++)
                    this.seats[i][j][k]=false;
        this.reviews = review;
        this.rating = rating;
    }
    public MovieDB(String movieName, String movieType, String[] showTimings,
            double price, String[] reviews, double[] rating){
        this.movieName = movieName;
        this.movieType = movieType;
        this.price = price;
        this.showTimings = showTimings;
        this.seats = new boolean[10][40][10];
        for(int i =0;i<10;i++)
            for(int j=0;j<40;j++)
                for(int k=0;k<10;k++)
                    this.seats[i][j][k]=false;
        this.reviews = reviews;
        this.rating = rating;
    }
    
    public MovieDB(String movieName, String movieType, String[] showTimings,
            boolean[][][] seats, double price,String[] reviews, double[] rating){
        this.movieName = movieName;
        this.movieType = movieType;
        this.price = price;
        this.showTimings = showTimings;
        this.seats = seats;
        this.reviews = reviews;
        this.rating = rating;
    }
    
    public int getTotalId(){
        return total_id;
    }
    
    public String getMovieName(){
        return this.movieName;
    }
    
    public String getMovieType(){
        return this.movieType;
    }
    
    public String[] getShowTimings(){
        return this.showTimings;
    }
    
    public boolean[][][] getSeats(){
        return this.seats;
    }
    
    public double getPrice(){
        return this.price;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String[] getReviews(){
        return this.reviews;
    }
    
    public double[] getRating(){
        return this.rating;
    }
    
    public double getRatingAverage(){
        double sum =0;
        for(int i=0;i<rating.length;i++)
            sum += rating[i];
        return sum/rating.length;
    }
    
    public void setSeat(int row, int column, int showId){
        this.seats[row][column][showId] = true;
    }
    
    public void setReview(String review) {
        String[] temp_reviews = new String[reviews.length+1];
        for(int i=0;i<reviews.length;i++)
            temp_reviews[i]= reviews[i];
        temp_reviews[reviews.length] = review;
        reviews = temp_reviews;
    }
    
    public void setRating(double rating) {
        this.rating[this.rating.length] = rating;
    }
    
    public static void setTotalId() throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj1 = (JSONObject) parser.parse(new FileReader("movieData.txt"));
        total_id = obj1.size();
    }
    
    /*
    * This method adds a new movie to the JSON file
    */
    
    public void createMovie (MovieDB movie) throws IOException, ParseException{
      JSONParser parser = new JSONParser();
      JSONObject obj = new JSONObject();
      File f = new File("movieData.txt");
      if(f.exists()) { 
          obj = (JSONObject) parser.parse(new FileReader("movieData.txt"));
      }
      JSONArray jsonArray = new JSONArray();
      jsonArray.add(movie.movieName);
      jsonArray.add(movie.movieType);
      JSONArray parentJsonArray = new JSONArray();
      parentJsonArray.addAll(Arrays.asList(movie.showTimings));
      jsonArray.add(parentJsonArray);
      parentJsonArray = new JSONArray();
      for (boolean[][] seatsCheck : movie.seats) {
            JSONArray childJsonArray1 = new JSONArray();
            for (boolean[] seat : seatsCheck) {
                JSONArray childJsonArray2 = new JSONArray();
                for(int k=0; k<seat.length ; k++)
                    childJsonArray2.add(seat[k]);
                childJsonArray1.add(childJsonArray2);
            }
            parentJsonArray.add(childJsonArray1);
        }
      jsonArray.add(parentJsonArray);
      jsonArray.add(movie.price);
      if(movie.reviews != null)
      {
      parentJsonArray = new JSONArray();
      parentJsonArray.addAll(Arrays.asList(movie.reviews));
      jsonArray.add(parentJsonArray);
      }
      else
      {
          movie.setReview("");
      }
      parentJsonArray = new JSONArray();
      for(int i=0; i<rating.length; i++)
          parentJsonArray.add(rating[i]);
      jsonArray.add(parentJsonArray);
      obj.put(total_id++, jsonArray);
      try (FileWriter file = new FileWriter("movieData.txt")) 
      {
               file.write(obj.toJSONString());
	}
    }
    
    /*
    * This method deletes a particular movie from the JSON file
    */
    
    public void deleteMovie (String movieName) throws ParseException, IOException{
        int i = 0;
      JSONParser parser = new JSONParser();
      JSONObject obj1 = (JSONObject) parser.parse(new FileReader("movieData.txt"));
      while (obj1.get(Integer.toString(i))!=null){
          if(movieName.equals( ((JSONArray)obj1.get(Integer.toString(i))).get(0).toString() ))
          {
              obj1.remove(Integer.toString(i));
              total_id--;
              break;
          }
          i++;
      }
      
      try (FileWriter file = new FileWriter("movieData.txt")) {
	file.write(obj1.toJSONString());
      }
      
      MovieDB[] movies = new MovieDB[total_id];
      movies = this.getMovieArray(movies,i);
      this.updateMovieListings(movies);
    }
    
    /*
    * This method updates a particular movie in the database
    */
    
    public void updateMovie (int id, int edit) throws IOException, ParseException {
      JSONParser parser = new JSONParser();
      Scanner sc = new Scanner (System.in);
      JSONObject obj = (JSONObject) parser.parse(new FileReader("movieData.txt"));
      JSONArray arr = (JSONArray) obj.get(Integer.toString(id));
      arr.remove(edit - 1);
      switch (edit){
          /* 1. Movie Name
           * 2. Movie Type
           * 3. Show timings
           * 4. Price
           */
          case 1:
              System.out.print("\nPlease enter the new Movie Name: ");
              arr.add(0, sc.nextLine()); 
              break;
          case 2:
              System.out.print("Please enter the new Movie Type: ");
              arr.add(1, sc.next()); 
              break; 
          case 3:
              JSONArray childJsonArray = new JSONArray();
              System.out.print("Please enter the new Show Timings: ");
                char choice = 'y';
                do {
                    childJsonArray.add(sc.nextLine());
                    System.out.println("More shows? ");
                    choice = sc.next().charAt(0);
                } while (choice == 'y'|| choice == 'Y');
                arr.add(2,childJsonArray);
              break;
          default:
              break;
      }
      obj.put(id,arr);
      try (FileWriter file = new FileWriter("movieData.txt")) {
		file.write(obj.toJSONString());
	}
    }
    
    /*
    * This method gets the entire set of movies from the database and puts them in objects
    */
    
    public MovieDB[] getMovieArray (MovieDB movies []) throws FileNotFoundException, IOException, ParseException{
        JSONParser parser = new JSONParser();
        JSONObject obj1 = (JSONObject) parser.parse(new FileReader("movieData.txt"));
        JSONArray arr,arr2,arr3,arr4;
        int id=0;
        String[] s,s1;
        double[] s2;
        int i,j,k;
        boolean[][][] seats=null;
        while (obj1.get(Integer.toString(id))!=null){
            arr = (JSONArray)obj1.get(Integer.toString(id));
            
            //Showtimings
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(2);
            s = new String[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s[i] = (String) arr2.get(i);
            
            //Seats Available
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(3);
            seats = new boolean[10][40][10];
            for(i=0;i<arr2.size();i++){
                arr3 = new JSONArray();
                arr3 = (JSONArray)arr2.get(i);
                for(j=0;j<arr3.size();j++){
                    arr4 = new JSONArray();
                    arr4 = (JSONArray)arr3.get(j);
                    for(k=0;k<arr4.size();k++)
                        seats[i][j][k]=(boolean) arr4.get(k);
                }
            }
            
            //Reviews
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(5);
            s1 = new String[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s1[i] = (String) arr2.get(i);
            
            //Ratings
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(6);
            s2 = new double[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s2[i] = (double) arr2.get(i);
            
            movies[id] = new MovieDB((arr.get(0)).toString(),
                    (arr.get(1)).toString(),
                    s,
                    seats,
                    (double)arr.get(4),
                    s1,
                    s2);
            id++;
        }
        return movies;
    }
    
    public MovieDB[] getMovieArray (MovieDB movies [], int check_id) throws FileNotFoundException, IOException, ParseException{
        JSONParser parser = new JSONParser();
        JSONObject obj1 = (JSONObject) parser.parse(new FileReader("movieData.txt"));
        JSONArray arr,arr2,arr3,arr4;
        int id=0,id1=0;
        String[] s,s1;
        double[] s2;
        int i,j,k;
        boolean[][][] seats=null;
        if(id==check_id)
            id++;
        while (obj1.get(Integer.toString(id))!=null){
            arr = (JSONArray)obj1.get(Integer.toString(id));
            
            //Showtimings
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(2);
            s = new String[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s[i] = (String) arr2.get(i);
            
            //Seats Available
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(3);
            seats = new boolean[10][40][10];
            for(i=0;i<arr2.size();i++){
                arr3 = new JSONArray();
                arr3 = (JSONArray)arr2.get(i);
                for(j=0;j<arr3.size();j++){
                    arr4 = new JSONArray();
                    arr4 = (JSONArray)arr3.get(j);
                    for(k=0;k<arr4.size();k++)
                        seats[i][j][k]=(boolean) arr4.get(k);
                }
            }
            
            //Reviews
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(5);
            s1 = new String[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s1[i] = (String) arr2.get(i);
            
            //Ratings
            arr2 = new JSONArray();
            arr2 = (JSONArray)arr.get(6);
            s2 = new double[arr2.size()];
            for(i=0;i<arr2.size();i++)
                s2[i] = (double) arr2.get(i);
            
            movies[id1++] = new MovieDB((arr.get(0)).toString(),
                    (arr.get(1)).toString(),
                    s,
                    seats,
                    (double)arr.get(4),
                    s1,
                    s2);
            id++;
            if(id==check_id)
                id++;
        }
        return movies;
    }
    
    /*
    * This method updates the entire movie listings
    */
    
    public void updateMovieListings (MovieDB movies[]) throws IOException {
        //write this code
        int id=0;
        JSONObject obj = new JSONObject();
        JSONArray jsonArray;
        for (MovieDB movie : movies) {
            jsonArray = new JSONArray();
            jsonArray.add(movie.getMovieName());
            jsonArray.add(movie.getMovieType());
            JSONArray parentJsonArray = new JSONArray();
            parentJsonArray.addAll(Arrays.asList(movie.getShowTimings()));
            jsonArray.add(parentJsonArray);
            parentJsonArray = new JSONArray();
            for (boolean[][] seatsCheck : movie.seats) {
            JSONArray childJsonArray1 = new JSONArray();
            for (boolean[] seat : seatsCheck) {
                JSONArray childJsonArray2 = new JSONArray();
                for(int k=0; k<seat.length ; k++)
                    childJsonArray2.add(seat[k]);
                childJsonArray1.add(childJsonArray2);
            }
            parentJsonArray.add(childJsonArray1);
            }
            jsonArray.add(parentJsonArray);
            parentJsonArray = new JSONArray();
            parentJsonArray.addAll(Arrays.asList(movie.reviews));
            jsonArray.add(parentJsonArray);
            parentJsonArray = new JSONArray();
            for(int i=0; i<movie.rating.length; i++)
                parentJsonArray.add(movie.rating[i]);
            jsonArray.add(parentJsonArray);
            jsonArray.add(movie.getPrice());
            obj.put(id++, jsonArray);
        }
        try (FileWriter file = new FileWriter("movieData.txt")) {
               file.write(obj.toJSONString());
	}
    }
    
    public double calcPrice(String cineType, Customer cObj){
        double finalPrice = 0.0;
        finalPrice = this.getPrice();
        
        if(cObj.getAge() >= 60){
            finalPrice = finalPrice * 0.94;
        }
        if(this.getMovieType().equals("3D")){
            finalPrice = finalPrice * 1.05;
        }
        if(cineType.equals("Platinum"))
            return finalPrice*1.10;
        return finalPrice;
    }
    
}