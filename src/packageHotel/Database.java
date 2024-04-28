/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packageHotel;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import java.util.LinkedList;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.DeleteResult;

/**
 *
 * @author github.com/Reza1290
 */
public class Database {

    private final String uri;
    private MongoClient client;
    private String database;

    /**
     * uri -> string connection mongoDB, Databasename
     *
     * @param uri
     * @param databaseName
     */
    public Database(String uri, String databaseName) {
        this.uri = uri;
        this.database = databaseName;
    }

    /**
     * Set Uri to Default Localhost Default Port
     */
    public Database(String databaseName) {
        this.uri = "localhost:27017";
        this.database = databaseName;
    }

    public String getUri() {
        return uri;
    }

    public MongoDatabase connectDatabase() {
        
        try{            
            this.client = MongoClients.create(this.uri);
            MongoDatabase db = this.client.getDatabase(this.database);
            return db;

        }catch(MongoException e){
            System.err.println(e.getMessage());
        }
        return null;

    }

    /**
     * Return allData in Collection!
     *
     * @param collectionName
     * @return
     */
    public LinkedList getAllData(String collectionName) {
        LinkedList data = new LinkedList();

        MongoCollection<Document> collection = this.connectDatabase().getCollection(collectionName);
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()) {
            Document document = cursor.tryNext();
            data.add(document);
        }

        return data;
    }
    
    /**
     * get data by query equal
     * @param collectionName
     * @param identifierKey
     * @param identifierValue
     * @param isString
     * @return 
     */
    public Document getData(String collectionName, String identifierKey, String identifierValue, boolean isString) {

        MongoCollection<Document> collection = this.connectDatabase().getCollection(collectionName);
        
        Bson query = isString ? eq(identifierKey, identifierValue) : eq(identifierKey, Integer.parseInt(identifierValue));
        
        try {
            Document document = collection.find(query).first();

            if (document == null) {
                throw new MongoException("NotFound");
            }
            
            return document;
        } catch (MongoException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * post Data ( Document ) to Collections
     *
     * @param collectionName
     * @param data
     * @return
     */
    public boolean postData(String collectionName, Document data) {

        MongoCollection<Document> collection = this.connectDatabase().getCollection(collectionName);

        try {
            InsertOneResult res = collection.insertOne(data);

            if (res.getInsertedId() == null) {
                throw new MongoException("Cannot Put Documents :( ~~");
            }
            return true;
        } catch (MongoException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }
    
    /**
     * edit data by query Equal
     * 
     * @param collectionName
     * @param data
     * @param identifierKey
     * @param identifierValue
     * @param isString
     * @return 
     */
    public boolean editData(String collectionName, Document data, String identifierKey, String identifierValue, boolean isString) {

        MongoCollection<Document> collection = this.connectDatabase().getCollection(collectionName);
        Document query = new Document(identifierKey, isString ? identifierValue : Integer.parseInt(identifierValue));

        Bson updates = Updates.combine(data.entrySet().stream()
                .map(entry -> Updates.set(entry.getKey(), entry.getValue()))
                .toArray(Bson[]::new));

        try {
            UpdateResult res = collection.updateOne(query, updates);
            //for now use this :>
            if (res.getModifiedCount() == 0) {
                throw new MongoException("Update Not Successfully");
            }
            return true;

        } catch (MongoException e) {
            
            System.err.println(e.getMessage());
            
        }
        
        return false;
    }
    
    /**
     * delete data by query equal
     * 
     * @param collectionName
     * @param identifierKey
     * @param identifierValue
     * @param isString
     * @return 
     */
    public boolean deleteData(String collectionName, String identifierKey, String identifierValue, boolean isString) {
        MongoCollection<Document> collection = this.connectDatabase().getCollection(collectionName);

        Bson query = isString ? eq(identifierKey, identifierValue) : eq(identifierKey, Integer.parseInt(identifierValue));

        try {
            DeleteResult res = collection.deleteOne(query);

            if (res.getDeletedCount() == 0) {
                throw new MongoException("None Deleted");
            }

            return true;
        } catch (MongoException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }
}
