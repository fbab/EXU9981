package prof.prodageo.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prof.prodageo.db.DbWrapper;

public class MyController
{
	private static final Logger log = LoggerFactory.getLogger(MyController.class);
    DbWrapper the_db = new DbWrapper() ;

 	public MyController ()
 	{ 	}   

 	public void init()
 	{
 	 		the_db.init();
 	}
	
}