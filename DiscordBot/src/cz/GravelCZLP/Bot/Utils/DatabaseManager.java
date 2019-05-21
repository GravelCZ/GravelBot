package cz.GravelCZLP.Bot.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

	private String host, database, username, password;
	private int port;
	
	private Connection connection;
	
	public void init(String host, int port, String username, String password, String database) {
		long start = System.currentTimeMillis();
		long end = 0;
		try {
			Logger.log("Attempting to establish a connection the MySQL server!");
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=GMT", username, password);
			end = System.currentTimeMillis();
			Logger.log("Connection to MySQL server established! (" + host + ":" + port + ")");
			Logger.log("Connection took " + (end - start) + "ms!");
		} catch (SQLException e) {
			Logger.error("Could not connect to MySQL server! because: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.error("JDBC Driver not found!");
		}
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	public synchronized boolean isConnected() {
		if (this.connection != null) {
			return true;
		}
		return false;
	}
	
	public synchronized void closeConnection() {
		if (this.connection == null) {
			try {
				this.connection.close();
				System.out.println("MySQL Connection closed");
			} catch (SQLException e) {
				System.out.println("Couldn't close connection");
			}
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public synchronized void refreshConnection() {
		PreparedStatement st = null;
		ResultSet valid = null;
		try {
			st = this.connection.prepareStatement("SELECT 1 FROM Dual");
			valid = st.executeQuery();
			if (valid.next()) {
				return;
			}
			closeQuietly(valid);
		} catch (SQLException e2) {
			System.out.println("Connection is idle or terminated. Reconnecting...");
		} finally {
			closeQuietly(valid);
		}
		long start = 0;
		long end = 0;
		try {
			start = System.currentTimeMillis();
			System.out.println("Attempting to establish a connection the MySQL server!");
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?serverTimezone=GMT", this.username, this.password);
			end = System.currentTimeMillis();
			System.out.println("Connection to MySQL server established! (" + this.host + ":" + this.port + ")");
			System.out.println("Connection took " + (end - start) + "ms!");
		} catch (SQLException e) {
			System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver not found!");
		}
	}
	
	public synchronized boolean execute(String sql) {
		refreshConnection();
		boolean st = false;
		try {
			Statement statement = this.connection.createStatement();
			st = statement.execute(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return st;
	}

	public synchronized PreparedStatement prepareStatement(String statement) {
		refreshConnection();
		try {
			return this.connection.prepareStatement(statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized ResultSet executeQuery(String sql) {
		refreshConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = this.connection.prepareStatement(sql);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public synchronized int executeUpdate(String sql) {
		refreshConnection();
		int st = 0;
		try {
			Statement statement = this.connection.createStatement();
			st = statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return st;
	}
	
	public static synchronized void closeQuietly(ResultSet rs) {
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) rs.getStatement();
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
		}
	}
}
