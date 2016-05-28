package prof.prodageo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import org.h2.tools.*;

import java.lang.ClassLoader;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class DbWrapper {

        private static final Logger log = LoggerFactory.getLogger(DbWrapper.class);
        String DBNAME = "testinmem" ;
        Server server ;
        Connection conn ;
        Statement stat ;
        boolean not_started = true ;
	    String the_ddl_url = "/tmp/EXU9981/target/classes/ddl.sql";
        // the file is in /tmp/EXU9981/src/main/resources/ddl.sql
        String the_dml_url = "/tmp/EXU9981/target/classes/dml.sql";


        // throws ClassNotFoundException, SQLException, IOException
        public void init() 
        {
            // open the in-memory database within a VM
            try
            {   

                if ( not_started )
                {
                    try
                    {
                            URL the_resource_for_ddl_file = DbWrapper.class.getResource("/ddl.sql");
                            // URL resource = getClassLoader().getResource("ddl.sql"); // KO
                            if ( the_resource_for_ddl_file != null ) 
                            {
                                the_ddl_url = new File(the_resource_for_ddl_file.toURI()).getAbsolutePath();
                                log.info("Directory : " + the_ddl_url );
                            }
                            else
                            {
                                log.info("resource null"  );
                            }

                    } catch (final URISyntaxException e)
                    { 
                            log.info("URISyntaxException !");
                            return;
                    }


        		    // String runscript = "INIT = runscript from 'ddl.sql'\\;runscript from 'dml.sql'" ;
        		    String runscript_ddl = "runscript from '" + the_ddl_url + "'" ; // OK : absolute_path
                    // String script_ddl = "runscript from '~/ddl.sql'" ; // a verifier
                    // String runscript_ddl = "runscript from 'classpath:ddl.sql'" ; // KO (source : http://stackoverflow.com/questions/4490138/problem-with-init-runscript-and-relative-paths)
                    // String runscript_ddl = "runscript from 'classpath:/ddl.sql'" ; // a verifier (source : http://stackoverflow.com/questions/4490138/problem-with-init-runscript-and-relative-paths)
        		    // String runscript_dml = "runscript from '" + the_dml_url + "'" ; // OK : absolute_path


        		    // String runscript_all = "INIT=" + runscript_ddl + "\\;" + runscript_dml ; // KO
                    // String runscript_all = "INIT = " + runscript_ddl + ";" ; // KO - pas d'espace autour de INIT !!!
        		    String runscript_all = "INIT=" + runscript_ddl + ";" ; // OK
        		    // runscript = "INIT=runscript from '/tmp/EXU9981/target/classes/ddl.sql'" ; // OK
        		    // runscript = "INIT=runscript from 'ddl.sql'" ; // KO
        		    // runscript = "CREATE TABLE TEST(ID INT, NAME VARCHAR)" ; // a verifier

        		    String JDBC_URL = "jdbc:h2:mem:" + DBNAME + ";" + runscript_all  ;
                    log.info("JDBC_URL : " + JDBC_URL );
        		    // URL = "jdbc:h2:mem:" + DBNAME ;

                    Class.forName("org.h2.Driver"); // (1)
                    conn 
                        = DriverManager.getConnection( JDBC_URL , "sa", "sa"); // (2)
                    // username:password are very important and must be used for connecting via H2 Console
                    log.info("CONNECTION OK !");

                    stat = conn.createStatement();
                    log.info("createStatement OK !");

                } // end if started

            // finally { log.info("DB mem initiated !");
            } catch (final ClassNotFoundException e) {
                log.info("ClassNotFoundException !");
                return;
            } catch (final SQLException e) {
                log.info("SQLException sur H2mem !");
                return;
            }

            // load in memory a web server for H2 SQL console accessible at port 8082
            if ( not_started )
            {
                    // http://stackoverflow.com/questions/34238142/how-to-show-content-of-local-h2-databaseweb-console
                    not_started = false ;
                   try
                   {
                        server = Server.createWebServer("-web","-webAllowOthers","-webPort","8082");
                        server.start();
                       // server = Server.createTcpServer().start();
                       log.info(" URL " + server.getURL() ) ;
                   } catch (final SQLException e) {
                        log.info("SQLException sur Server !");
                        return;
                   }
            } // end if started
        } // end init()

        void test_create_table ()
        {
                   try
                   {
                    // Statement stat = conn.createStatement(); // (3)
                    stat.executeUpdate("DROP table INIT(id int primary key, name varchar(255))");
                    log.info("INIT dropped !");
                    stat.executeUpdate("create table INIT(id int primary key, name varchar(255))");
                   } catch (final SQLException e) {
                        log.info("SQLException sur create !");
                        return;
                   }
        }


        void test_populate_table ()
        {
                   try
                   {
                    // Statement stat = conn.createStatement(); // (3)
                    stat.executeUpdate("insert into mytbl values(1, 'Hello')");
                    stat.executeUpdate("insert into mytbl values(2, 'World')");
                   } catch (final SQLException e) {
                        log.info("SQLException sur populate !");
                        return;
                   }
            
        }

        String test_select ()
        {
           String the_result = "" ;
           try
           {
                // Statement stat1 = conn.createStatement(); // (3)
                // Verify that sample data was really inserted
                ResultSet rs = stat.executeQuery("select * from mytbl");
                log.info("ResultSet output:");
                while (rs.next())
                {
                    the_result = rs.getString("name") ;
                    log.info("test_select :" + the_result);
                }
                
            } catch (final SQLException e)
            {
                    log.info("SQLException sur select !");
            }

            return the_result ;
        }

}
/*
   public class DbStuff1 {
        // public void DbStuff();

        public void init()
        {
            // SOURCE :
            // https://github.com/bmatthews68/inmemdb-maven-plugin/blob/master/src/it/webapp/src/main/java/com/btmatthews/testapp/ListUsersServlet.java
            log.info("DB initiated !");




        try {
            final Context ctx = new InitialContext();
            final DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/myDb");
            final Connection con = ds.getConnection();
            try {


            } finally {
                con.close();
            }
        } catch (final SQLException e) {
            log.info("SQLException !");
            return;
        } catch (final NamingException e) {
            log.info("NamingException !");
            return;
        }

        }
    }
*/
