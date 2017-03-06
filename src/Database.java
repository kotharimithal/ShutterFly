import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Database {
	//  Database credentials
	static final String username = "root";
	static final String password = null;
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost?useSSL=false";	
	static final String DBName = "ShutterFly";
	
	Connection conn = null;
	Statement stmt = null;
	
	java.util.Date dt = new java.util.Date();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String CUSTOMER_TABLE = "CREATE TABLE CUSTOMER ( " +
							            " `key` VARCHAR(255) not NULL, " +
							            " eventTime DATETIME not NULL, " + 
							            " lastName VARCHAR(255), " +
							            " adrCity VARCHAR(255), " +
							            " adrState VARCHAR(255), " +
							            " createdAt DATETIME, " +
							            " updatedAt DATETIME, " +
							            " PRIMARY KEY (`key`))"; 
	
	private static final String SITE_VISIT_TABLE = "CREATE TABLE SITE_VISIT ( " +
										            "`key` VARCHAR(255) not NULL, " +
										            " eventTime DATETIME not NULL, " + 
										            " customerId VARCHAR(255) not NULL, " +
										            " tags VARCHAR(255), " +
										            " createdAt DATETIME, " +
										            " updatedAt DATETIME, " +
										            " PRIMARY KEY (`key`), " +
										            " FOREIGN KEY (customerId) REFERENCES CUSTOMER (`key`))"; 
	
	private static final String IMAGE_TABLE = "CREATE TABLE IMAGE ( " +
										            "`key` VARCHAR(255) not NULL, " +
										            " eventTime DATETIME not NULL, " + 
										            " customerId VARCHAR(255) not NULL, " +
										            " cameraMake VARCHAR(255), " +
										            " cameraModel VARCHAR(255), " +
										            " createdAt DATETIME, " +
										            " updatedAt DATETIME, " +
										            " PRIMARY KEY (`key`), " +
										            " FOREIGN KEY (customerId) REFERENCES CUSTOMER (`key`)) ";
	
	private static final String ORDER_TABLE = "CREATE TABLE `ORDER` ( " +
										            "`key` VARCHAR(255) not NULL, " +
										            " eventTime DATETIME not NULL, " + 
										            " customerId VARCHAR(255) not NULL, " +
										            " totalAmount VARCHAR(255) not NULL, " +
										            " createdAt DATETIME, " +
										            " updatedAt DATETIME, " +
										            " PRIMARY KEY (`key`)," +
										            " FOREIGN KEY (customerId) REFERENCES CUSTOMER (`key`))"; 
	
	private static final List<String> listOfTables = 
			new ArrayList<String>(Arrays.asList(CUSTOMER_TABLE,SITE_VISIT_TABLE,IMAGE_TABLE,ORDER_TABLE));
	
	public Database() 
	{
	   try{
		   boolean dbCreated = false;
		   conn = getConnection();	
		   stmt = conn.createStatement();
		   dbCreated = createDB(conn,DBName);
		   if (dbCreated){
			   stmt.executeUpdate("use "+ DBName+" ;");
			   createTables(conn);
		   }
		   }catch(Exception e){
		      e.printStackTrace();
		   }
	}

	private void createTables(Connection connection) 
	{
		int tableIndex = 0;
		try {
			stmt = connection.createStatement();
			for (tableIndex = 0; tableIndex < listOfTables.size(); tableIndex++){
				stmt.executeUpdate(listOfTables.get(tableIndex));
			}
		} catch (SQLException e) {
			System.out.println("Error while creating table "+ listOfTables.get(tableIndex));
		}
	}

	private boolean createDB(Connection connection, String databaseName) 
	{
		ResultSet resultSet = null;
		try {
			resultSet = connection.getMetaData().getCatalogs();
			while (resultSet.next()) {
		          String dbName = resultSet.getString(1).toLowerCase();
		            if(dbName.equals(databaseName.toLowerCase())){
		            	System.out.println("Database "+databaseName+" already exist");
		            	resultSet.close();
		            	return false;
		            	}
		            }			
			stmt = conn.createStatement();
		    String sql = "CREATE DATABASE "+databaseName;
		    stmt.executeUpdate(sql);
		    System.out.println("Database "+databaseName+" is created");
		} catch (SQLException e) {
			System.out.println("Error while creating DB "+ databaseName +" getting list of databases that exists");
			return false;
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException e) {
			}
		}
		return true;		
	}

	void closeConnection() 
	{
      try{
	       if(stmt!=null)
	    	   stmt.close();
	       if(conn!=null)
	    	   conn.close();
	       }catch(SQLException se2){
	       }
	}

	private Connection getConnection() 
	{
      try {
    	  Class.forName(JDBC_DRIVER).newInstance();
	      System.out.println("Connecting to database...");
	      conn = DriverManager.getConnection(DB_URL, username, password);
      	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
      	System.out.println("Error Creating connection");
      	return null;
      	} 
      return conn;
	}

	
	public <T> void processEvent(T event) {
		try {
			@SuppressWarnings("unchecked")
			Map<String,String> fields = (Map<String, String>) event.getClass().getDeclaredField("fields").get(event);
			String verb = fields.get("verb").toLowerCase();
			if (verb.equals("update")){
				this.updateExistingEntry(fields);
			} else {
				this.createNewEntry(fields);
			}
		} catch (IllegalArgumentException | IllegalAccessException |NoSuchFieldException | SecurityException e) {
			System.out.println("Fields not found for "+event.getClass());
		}
		
	}
	
	private void updateExistingEntry(Map<String, String> fields) 
	{
		Set<String> fieldsKeySet = fields.keySet();
		StringBuilder sqlUpdateStatement = new StringBuilder();
		StringBuilder query = new StringBuilder();
		String currentTime = '"'+sdf.format(dt)+'"';
		sqlUpdateStatement.append("UPDATE "+getTableName(fields.get("type").toLowerCase())+" SET ");
		for (String key: fieldsKeySet){
			if (key.equals("type") || key.equals("verb")){
				continue;
			} else if (key.equals("key")){
				query.append("`key` = '"+fields.get("key").toString().toLowerCase()+"'");
			} else {
				query.append(key+" = '"+fields.get(key).toString().toLowerCase()+"'");
			}
			query.append(",");
		}
		query.append("UpdatedAt = "+currentTime);
		sqlUpdateStatement.append(query+" WHERE `KEY` = '"+fields.get("key")+"';");
		try {
			this.stmt = this.conn.createStatement();
			stmt.executeUpdate(sqlUpdateStatement.toString());
		} catch (SQLException e) {
			System.out.println("Error occurred while update event : "+sqlUpdateStatement.toString());
		}
	}

	private void createNewEntry(Map<String, String> fields) 
	{
		Set<String> fieldsKeySet = fields.keySet();
		StringBuilder sqlInsertStatement = new StringBuilder();
		StringBuilder keys = new StringBuilder();
		StringBuilder values = new StringBuilder();
		keys.append("(");
		values.append(" VALUES (");
		for (String key: fieldsKeySet){
			if (key.equals("type") || key.equals("verb")){
				continue;
			} else if (key.equals("key")){
				keys.append("`key`");
			} else {
				keys.append(key);
			}
			values.append("'"+fields.get(key)+"', ");
			keys.append(", ");
		}
		keys.append("createdAt, UpdatedAt)");
		String currentTime = '"'+sdf.format(dt)+'"';
		values.append(currentTime+", "+currentTime+")");
		
		sqlInsertStatement.append("INSERT INTO "+getTableName(fields.get("type").toLowerCase())+" "+keys.toString()+" "+values.toString()+";");
		System.out.println(sqlInsertStatement.toString());
		try {
			this.stmt = this.conn.createStatement();
			stmt.executeUpdate(sqlInsertStatement.toString());
		} catch (SQLException e) {
			System.out.println("Error occurred while inserting statement : "+sqlInsertStatement.toString());
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private Object getTableName(String tableName) 
	{
		if (tableName.equals("order"))
			return "`order`";
		return tableName;
	}

}
