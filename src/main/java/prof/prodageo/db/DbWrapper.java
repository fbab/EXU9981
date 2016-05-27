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

public class DbWrapper {

        private static final Logger log = LoggerFactory.getLogger(DbWrapper.class);
        String DBNAME = "testinmem" ;
        Server server ;
        Connection conn ;
        boolean not_started = true ;


        // throws ClassNotFoundException, SQLException, IOException
        public void init() 
        {
            // open the in-memory database within a VM
            try {   

                if ( not_started )
                {
                    Class.forName("org.h2.Driver"); // (1)
                    conn 
                        = DriverManager.getConnection("jdbc:h2:mem:" + DBNAME, "sa", ""); // (2)
                    // username:password are very important and must be used for connecting via H2 Console

                    Statement stat = conn.createStatement(); // (3)
                    stat.executeUpdate("create table mytbl(id int primary key, name varchar(255))");
                    stat.executeUpdate("insert into mytbl values(1, 'Hello')");
                    stat.executeUpdate("insert into mytbl values(2, 'World')");
                }

                Statement stat1 = conn.createStatement(); // (3)
                // Verify that sample data was really inserted
                ResultSet rs = stat1.executeQuery("select * from mytbl");
                log.info("ResultSet output:");
                while (rs.next()) {
                    log.info(rs.getString("name"));
                } 
            // finally { log.info("DB mem initiated !");
            } catch (final ClassNotFoundException e) {
                log.info("ClassNotFoundException !");
                return;
            } catch (final SQLException e) {
                log.info("SQLException sur H2mem !");
                return;
            }

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
                }
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