/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mainProjectHotel;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import org.bson.Document;
import org.bson.types.Decimal128;
import packageHotel.Database;
import packageHotel.Hotel;
import packageHotel.Room;
import packageHotel.Tamu;

/**
 *
 * @author Nitro
 */
public class ProjectHotel {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//        Hotel Purnama = new Hotel("Purnama","JL. kenangan");
//        
//        
//        Room r1 = new Room(101,"King",400);
//        Room r2 = new Room(102,"Twin",600);
//        Room r3 = new Room(103,"Deluxe",500);
//        Room r4 = new Room(104,"Single",300);
//        
//        System.out.println(Purnama.tambahKamar(r1));
//        System.out.println(Purnama.tambahKamar(r2));
//        System.out.println(Purnama.tambahKamar(r3));
//        System.out.println(Purnama.tambahKamar(r4));
//        
//        
//        Tamu user1 = new Tamu("Budi","Jl. Karang", "6282400003", "ayam@g.com");
//        Tamu user2 = new Tamu("Ari","Jl. Karang", "6282400003", "ayam@g.com");
//        Tamu user3 = new Tamu("Ulki","Jl. Karang", "6282400003", "ayam@g.com");
//        
//        Purnama.pesanKamar(user1, r1, LocalDate.of(2023, Month.SEPTEMBER, 29),LocalDate.of(2023, Month.OCTOBER, 1));
//        Purnama.pesanKamar(user2, r2, LocalDate.of(2023, Month.SEPTEMBER, 29), LocalDate.of(2023, Month.OCTOBER, 1));
//        System.out.println(Purnama.pesanKamar(user3, r3, LocalDate.of(2023, Month.SEPTEMBER, 29), LocalDate.of(2023, Month.MAY, 1)));
//        
////      System.out.println(Purnama.pesanKamar(user2, r3, LocalDate.of(2023, Month.SEPTEMBER, 29), LocalDate.of(2023, Month.OCTOBER, 1)));
//        
//        List daftarKamars = Purnama.getDaftarKamar();
////        
//        System.out.println(daftarKamars);
//        
////        System.out.println(Purnama);
//        
//        
//    }

//    public static void main(String[] args) {
//        Database db = new Database("mongodb+srv://reza:reza1290@cluster0.snqx5vy.mongodb.net/","db_hotel_java");
//        LinkedList<Document> l = new LinkedList<Document>();
////        System.out.println(db.getAllData("kamar"));
//        l = db.getAllData("kamar");
//        // hehe not bad
//        
//        Document doc = new Document();
//        doc.put("nomorKamar", 3);
//        doc.put("tipeKamar", "King");
////        doc.put("status", true);
////        doc.put("harga",new Decimal128(12000));
//        
//        
////        l.get(0).forEach((key,value)->{System.out.println(key + value);});
//        
////        if(db.postData("kamar", doc)){
////            System.out.println("berhasil");
////        }
////        
////        if(db.editData("kamar", doc, "tipeKamar","Queen",true)){
////            System.out.println("gg");
////        }
////        if(db.editData("kamar", doc, "nomorKamar","1",false)){
////            System.out.println("gg");
////        }
////        if(db.deleteData("kamar", "nomorKamar", "3", false)){
////            System.out.println("wu");
////        }
//        
////        Document d = db.getData("kamar", "nomorKamar", "105", false);
////        Document d = db.getData("pemesanan" , "kamar.nomorKamar", "1", false);
//
////        System.out.println(d);    
//        
//    }
//    
}
