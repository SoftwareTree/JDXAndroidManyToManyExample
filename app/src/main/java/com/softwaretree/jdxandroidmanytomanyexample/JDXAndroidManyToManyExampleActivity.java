package com.softwaretree.jdxandroidmanytomanyexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.softwaretree.jdx.JDXHelper;
import com.softwaretree.jdx.JDXS;
import com.softwaretree.jdx.JDXSetup;
import com.softwaretree.jdxandroid.DatabaseAndJDX_Initializer;
import com.softwaretree.jdxandroid.Utils;
import com.softwaretree.jdxandroidmanytomanyexample.model.Group;
import com.softwaretree.jdxandroidmanytomanyexample.model.User;
import com.softwaretree.jdxandroidmanytomanyexample.model.UserGroup;
import com.softwaretree.jx.JXResource;
import com.softwaretree.jx.JXSession;
import com.softwaretree.jx.JXUtilities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This project exemplifies how JDXA ORM and associated utilities can be used to easily develop 
 * an Android application that exchanges data of domain model objects with an SQLite database.
 * In particular, this project demonstrates the following:
 * <p>
 * 1) How an ORM Specification (mapping) for domain model classes can be defined textually using
 * simple statements.  The mapping is specified in a text file \res\raw\manytomany_example.jdx identified 
 * by the resource id R.raw.manytomany_example.
 * <p>
 * 2) The object model used by this application has a many-to-many relationship.  
 * The many-to-many relationship among two classes employs an intermediate join class (table).
 * The collection of many objects may be an ArrayList, an array, or a Vector.
 * The mapping file shows how such relationships can easily be specified declaratively. 
 * <p>The mapping specification also illustrates the use of JDX_OBJECT_MODEL_PACKAGE 
 * specification that helps in further simplifying the mapping specification by using 
 * the dotted notation for class names that avoids the need for otherwise mentioning 
 * the fully qualified class names including the package names.
 * <p>
 * 3) Use of {@link AppSpecificJDXSetup} and {@link DatabaseAndJDX_Initializer} classes to easily: 
 *   <br>&nbsp;&nbsp;&nbsp;&nbsp;  a) create the underlying database, if not already present. 
 *   <br>&nbsp;&nbsp;&nbsp;&nbsp;  b) create the schema (tables and constraints) corresponding to the JDXA ORM specification 
 *      every time the application runs.  See setForceCreateSchema(true) in {@link AppSpecificJDXSetup#initialize()}.. 
 * <p>
 * 4) Example of JDX APIs to easily interact with relational data with just a few lines 
 * of object-oriented code that does not involve writing/processing any SQL statements.    
 * <p> 
 * 5) Examples of how details of an object or a list of objects can be added in JDX log and 
 * how that output can be collected in a file and then displayed in a scrollable TextBox.
 * <p> 
 * @author Damodar Periwal
 */
public class JDXAndroidManyToManyExampleActivity extends Activity {
    JDXSetup jdxSetup = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setTitle(getResources().getString(R.string.activity_title));
        
        TextView tvJDXLog = (TextView) findViewById(R.id.tvJDXLog);
        tvJDXLog.setMovementMethod(new ScrollingMovementMethod());

        try {    
            AppSpecificJDXSetup.initialize();  // must be done before calling getInstance()
            jdxSetup = AppSpecificJDXSetup.getInstance(this);
            
            // Use a JDXHelper object to easily configure capturing of JDX log output 
            JDXHelper jdxHelper = new JDXHelper(jdxSetup);
            String jdxLogFileName = getFilesDir() + System.getProperty("file.separator") + "jdx.log";
            jdxHelper.setJDXLogging(jdxLogFileName);
    		
            useJDXORM(jdxSetup);
            
            jdxHelper.resetJDXLogging();
            
            // Show the captured JDX log on the screen
            tvJDXLog.setText(Utils.getTextFileContents(jdxLogFileName));

        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Exception: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            cleanup();
            return;
        }
    }

    /**
     * Do the necessary cleanup.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanup();
    }
    
    private void cleanup() {
        AppSpecificJDXSetup.cleanup(); // Do this when the application is exiting.
        jdxSetup = null;
    }
    
    /**
     * Illustrates code using some JDXA ORM API calls.
     * 
     * @param jdxSetup
     */
    private void useJDXORM(JDXSetup jdxSetup) throws Exception {   
        if (null == jdxSetup) {
            return;
        }
        
        // Obtain ORM handles.
        JXResource jxResource = jdxSetup.checkoutJXResource();
        JXSession jxSessionHandle = jxResource.getJXSessionHandle();
        JDXS jdxHandle = jxResource.getJDXHandle();
        
        String userClassName = User.class.getName();
        String groupClassName = Group.class.getName();
        
        int gId = 1;
        int uId = 101;

        String gNameBase = "group";
        String uNameBase = "user";

        // Many-to-many relationship among two classes employs an intermediate join class (table).
        //
        // If an insert, update, and delete operation on the instances of such classes is done 
        // in a 'deep' fashion, the operation is limited to the top-level objects and the associated 
        // join class objects.  In other words, these operations do not change the related
        // objects in the database to avoid unintended recursion.
        //
        // A 'deep' query operation does extend upto the related objects but avoids recursion by
        // not fetching the related top-level original objects.
        
        try {                
            // ********** Start a transaction to delete all the existing objects.  ***********
        	JXUtilities.log("\n-- First deleting all the existing objects from the database --\n");
            jxSessionHandle.tx_begin();
            
            // First delete all the existing User objects from the database.  
            // This should also get rid of the associated join table entries 
            // because of the use of the parameter JDXS.FLAG_DEEP.
            jdxHandle.delete2(userClassName, null, JDXS.FLAG_DEEP);

            // Now delete all the existing Group objects from the database.  
            // There is no need of getting rid of the associated join table entries because
            // they would have already been deleted while deleting User object earlier.  
            // Hence the use of the parameter JDXS.FLAG_SHALLOW.
            jdxHandle.delete2(groupClassName, null, JDXS.FLAG_SHALLOW);
            
            // Commit the transaction. 
            jxSessionHandle.tx_commit();

            // **********  Now create a new Group with 2 new User objects.
            Group g1 = new Group(gId, gNameBase+gId);

            User u1 = new User(uId, uNameBase+uId);
            uId++;
            User u2 = new User(uId, uNameBase+uId);
            
            ArrayList<User> users = new ArrayList<User>();
            users.add(u1);
            users.add(u2);
            g1.setUsers(users);
            
            // **********  Now insert the new Group and its Users under a transaction *********
            JXUtilities.log("\n-- Now inserting a new Group (gId=" + g1.getgId() + ") with new Users (uId=" + u1.getuId() + " and uId=" + u2.getuId() + ")");
            jxSessionHandle.tx_begin();

            // Users have to be inserted separately because a many_to_many insert does not go beyond the join table. 
            // Users can be inserted after the join table is populated if there is no referential integrity specified 
            // at the database level.  Otherwise, user instances have to be inserted first.
            jdxHandle.insert(users, JDXS.FLAG_SHALLOW, null);  
  
            // Insert the Group object now.  This would also populate the join table for the associated User objects.
            jdxHandle.insert(g1, JDXS.FLAG_DEEP, null);
            
            // Commit the transaction. 
            jxSessionHandle.tx_commit();
            
            // Now do a shallow query for all the Group objects
            JXUtilities.log("\n-- Doing a shallow query for all the Group objects --\n");
            List queryResults = jdxHandle.query(groupClassName, null, -1, JDXS.FLAG_SHALLOW, null);
            JXUtilities.printQueryResults(queryResults);

            // Now do a deep query for all the Group objects along with the associated User objects
            JXUtilities.log("\n-- Doing a deep query for all the Group objects --\n");
            queryResults  = jdxHandle.query(groupClassName, null, -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);

            // Now do a shallow query for all the User objects
            JXUtilities.log("\n-- Doing a shallow query for all the User objects --\n");
            queryResults = jdxHandle.query(userClassName, null, -1, JDXS.FLAG_SHALLOW, null);
            JXUtilities.printQueryResults(queryResults);

            // Now do a deep query for all the User objects along with the associated Group objects
            JXUtilities.log("\n-- Doing a deep query for all the User objects --\n");
            queryResults  = jdxHandle.query(userClassName, null, -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
            // ********** Now create a new User u3 belonging to a new Group g2. 
            uId++;
            User u3 = new User(uId, uNameBase+uId);
            
            gId++;
            Group g2 = new Group(gId, gNameBase+gId);
            
            Vector<Group> groups = new Vector<Group>();
            groups.addElement(g2);
            u3.setGroups(groups);
            
            // **********  Now insert the new User u3 and its Group under a transaction *********
            JXUtilities.log("\n-- Now inserting a new Group (gId=" + g2.getgId() + ") with a new User (uId=" + u3.getuId() + ")");
            jxSessionHandle.tx_begin();
            
            // First insert the Group g2
            jdxHandle.insert(g2, JDXS.FLAG_SHALLOW, null);
            
            // Now insert the User u3.  This would also populate the join table for the associated Group object.
            jdxHandle.insert(u3, JDXS.FLAG_DEEP, null);
            
            // Make the new User u3 also associated with Group g1.
            // The following is an alternate way of showing how existing objects can be connected in
            // many-to-many relationships by just populating the join object UserGroup.
            // We could have added Group g1 in the Vector groups above before inserting User u3 to get the same effect.
            JXUtilities.log("\n-- Now associating the Group (gId=" + g1.getgId() + ") with the User (uId=" + u3.getuId() + ")");
            UserGroup ug = new UserGroup(u3.getuId(), g1.getgId());
            jdxHandle.insert(ug, JDXS.FLAG_SHALLOW, null);
            
            // Commit the transaction. 
            jxSessionHandle.tx_commit();
            
            // At this point, we have the following situation:
            //   Group g1 has User u1, u2, and u3.
            //   Group g2 has User u3.
            //   User u1 belongs to Group g1.
            //   User u2 belongs to Group g1.
            //   User u3 belongs to Group g1 and g2.
            
            JXUtilities.log("\n-- Doing a query for ListUsers for gId = 1 --\n");
            String listUsersClassName = "JDXVIEW_ListUsers_" + userClassName;
            queryResults  = jdxHandle.query(listUsersClassName, "gId = 1", -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
            // Now do a deep query for all the User objects along with the associated Group objects
            JXUtilities.log("\n-- Doing a deep query for all the User objects --\n");
            queryResults  = jdxHandle.query(userClassName, null, -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
            // Now do a deep query for the Group g1 along with the associated User objects
            JXUtilities.log("\n-- Doing a deep query for the Group with gId=" + g1.getgId() + " --\n");
            queryResults  = jdxHandle.query(groupClassName, "gId=" + g1.getgId(), -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
            // Now update the User object u3 in memory and then persist the changes in the database
            JXUtilities.log("\n-- Updating the user name for uId=" + u3.getuId() + " --\n");
            u3.setuName("new " + u3.getuName());
            jdxHandle.update(u3, JDXS.FLAG_SHALLOW, null);
            
            // Now do a deep query for the Group g2 along with the associated updated User objects u3
            JXUtilities.log("\n-- Doing a deep query for the Group with gId=" + g2.getgId() + " --\n");
            queryResults  = jdxHandle.query(groupClassName, "gId=" + g2.getgId(), -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
                                    
            // Now do a deep delete of a Group g1.  This will also delete the join table entries for Group g1
            // effectively removing all its relationships with any User objects.  The deep delete operation does 
            // not delete the related User objects though.
            JXUtilities.log("\n-- Deleting the Group with gId=" + g1.getgId() + " --\n");
            jdxHandle.delete(g1, JDXS.FLAG_DEEP, null);
            
            // At this point, we have the following situation:
            //   Group g1 does not exist.
            //   Group g2 has User u3.
            //   User u1 belongs to none of the groups.
            //   User u2 belongs to none of the groups.
            //   User u3 belongs to Group g2.
            
            // Do a deep query for all the User objects along with the associated Group objects
            JXUtilities.log("\n-- Doing a deep query for all the User objects --\n");
            queryResults  = jdxHandle.query(userClassName, null, -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
            // Now do a deep query for the Group objects along with the associated User objects
            JXUtilities.log("\n-- Doing a deep query for all the Group objects --\n");
            queryResults = jdxHandle.query(groupClassName, null, -1, JDXS.FLAG_DEEP, null);
            JXUtilities.printQueryResults(queryResults);
            
        } catch (Exception ex) {
            System.out.println("JDX Error " + ex.getMessage());
            Log.e("JDX", "Error", ex);
            throw ex;
        } finally {
            jdxSetup.checkinJXResource(jxResource);
        }
    }
}