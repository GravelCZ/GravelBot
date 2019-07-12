package cz.GravelCZLP.Bot.Utils;

import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {

	private HikariDataSource source;
	
	public void start(String host, int port, String username, String password, String database) {
		source = new HikariDataSource();
		source.setPoolName("Connection with MySQL");
		source.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=GMT&autoReconnect=true");
		source.addDataSourceProperty("user", username);
		source.addDataSourceProperty("password", password);
	}
	
	public void stop() {
		if (source != null) {
			source.close();
		}
	}
	
	public HikariDataSource getSource() {
		return source;
	}
}
