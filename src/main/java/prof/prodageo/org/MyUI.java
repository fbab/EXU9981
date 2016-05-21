package prof.prodageo.org;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* import for explicit declaration of callback */
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import org.h2.tools.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("prof.prodageo.org.MyAppWidgetset")
public class MyUI extends UI {

        private static final Logger log = LoggerFactory.getLogger(MyUIServlet.class);

    /* explicit declaration as attributes of graphical components for GenMyModel */
        final VerticalLayout layout = new VerticalLayout();
        final TextField name = new TextField();
        final TextField surname = new TextField();
        Button button = new Button("Click Me") ;
        DbStuff1 the_db = new DbStuff1() ;
        DbStuff2 the_db2 = new DbStuff2() ;
        String DBNAME = "testinmem" ;
        Server server ;
        Connection conn ;
        boolean not_started = true ;

public class DbStuff2 {

        // throws ClassNotFoundException, SQLException, IOException
        public void init() 
        {
            // open the in-memory database within a VM
            try {   

                if ( not_started )
                {
                    Class.forName("org.h2.Driver"); // (1)
                    conn 
                        = DriverManager.getConnection("jdbc:h2:mem:" + DBNAME, "sa", "sa"); // (2)
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

                /*
                final Statement stmt = con.createStatement();
                final ResultSet rs = stmt.executeQuery(
                        "SELECT USERNAME_TXT,PASSWORD_TXT, NAME_TXT "
                                + "FROM USERS "
                                + "ORDER BY USERNAME_TXT");
                */

                /*
                try {
                    while (rs.next()) {
                        final String username = rs.getString("USERNAME_TXT");
                        final String password = rs.getString("PASSWORD_TXT");
                        final String name = rs.getString("NAME_TXT");
                        // final User user = new User(username, password, name);
                        // users.add(user);
                    }
                } finally {
                    rs.close();
                }
                */

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
 



    /* explicit callback */
    /* https://vaadin.com/docs/-/part/framework/application/application-events.html */
    public class ClickMeClass implements Button.ClickListener
    {
        public void buttonClick(ClickEvent event) 
        {
            layout.addComponent(new Label("Thanks " + name.getValue() + ", it works!"));
            the_db2.init() ;
            log.info("Button clicked with value : " + name.getValue());
        }
    }
        


    @Override
    protected void init(VaadinRequest vaadinRequest)
    {


        // final VerticalLayout layout = new VerticalLayout();
        
        // final TextField name = new TextField();
        name.setCaption("Type your name here:");

        /*
        Button button = new Button("Click Me");
        button.addClickListener( e -> {
            layout.addComponent(new Label("Thanks " + name.getValue() 
                    + ", it works!"));
            log.info("Button clicked with value : " + name.getValue());
        });
        */
        ClickMeClass callback = new ClickMeClass() ;
        button.addClickListener( callback ) ;

        layout.addComponents(name, button);
        layout.setMargin(true);
        layout.setSpacing(true);
        
        setContent(layout);
    }

    // @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
