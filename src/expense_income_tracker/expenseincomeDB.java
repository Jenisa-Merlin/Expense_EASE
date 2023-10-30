package expense_income_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dell
 */
public class expenseincomeDB 
{
    private final String host;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;
    private Connection connection;

    public expenseincomeDB(String host, int port, String databaseName, String username, String password)
    {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    /**
     * Get a connection to the database.
     *
     * @return A connection to the database.
     * @throws SQLException If there is a problem connecting to the database.
     */
    public Connection getConnection() throws SQLException
    {
        try 
        {
            if (connection == null || connection.isClosed())
            {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, username, password);
            }
        } 
        catch (Exception ex)
        {
            Logger.getLogger(expenseincomeDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    /**
     * Close the connection to the database.
     *
     * @throws SQLException If there is a problem closing the connection to the database.
     */
    public void closeConnection() throws SQLException 
    {
        try
        {
            if (connection != null && !connection.isClosed()) 
            {
                connection.close();
            }
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(expenseincomeDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Insert a new entry into the database.
     *
     * @param entry The expense or income entry to insert.
     * @return True if the entry was inserted successfully, false otherwise.
     * @throws SQLException If there is a problem inserting the entry into the database.
     */
    public static boolean insertExpenseIncomeEntry(expenseincomeDB databaseConnection, ExpenseIncomeEntry entry) throws SQLException
    {
        try 
        {
            PreparedStatement statement = databaseConnection.getConnection().prepareStatement("INSERT INTO expense (date, description, amount, expense) VALUES (?, ?, ?, ?)");
            statement.setString(1, entry.getDate());
            statement.setString(2, entry.getDescription());
            statement.setDouble(3, entry.getAmount());
            statement.setString(4, entry.getType());
            int rowsAffected = statement.executeUpdate();
            statement.close();
            return rowsAffected == 1;
        } 
        catch (SQLException e) 
        {
            // Handle the SQLException here
            System.out.println(e);
        }
        catch (Exception v)
        {
            System.out.println(v);
        }
        return false;
    }
    public boolean deleteExpenseIncomeEntry(ExpenseIncomeEntry entry) throws SQLException {
    try {
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM expenseincome WHERE date = ? AND description = ? AND amount = ? AND type = ?");
        statement.setString(1, entry.getDate());
        statement.setString(2, entry.getDescription());
        statement.setDouble(3, entry.getAmount());
        statement.setString(4, entry.getType());
        int rowsAffected = statement.executeUpdate();
        statement.close();
        return rowsAffected == 1;
    } 
    catch (Exception e) 
    {
        // Handle the SQLException here
        return false;
    }
}


    /*
     * A custom exception class to represent SQL errors.
     */
    private static class SQLException extends Exception 
    {
        public SQLException() 
        {
            super();
        }
    }
}
