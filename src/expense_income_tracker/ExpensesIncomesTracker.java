/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expense_income_tracker;

import com.formdev.flatlaf.FlatDarkLaf;
import static expense_income_tracker.expenseincomeDB.insertExpenseIncomeEntry;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Dell
 */

public class ExpensesIncomesTracker extends JFrame{
    
    private final ExpenseIncomeTableModel tableModel;
    private final JTable table; 
    private final JTextField dateField;
    private final JTextField descriptionField;
    private final JTextField amountField;
    private final JComboBox<String> typeCombobox;
    private final JButton addButton;
    private final JLabel balanceLabel;
    private double balance;
    private JButton deleteButton;
    private JButton updateButton;
    
    //Constructor to initialize the application and set up the form
    public ExpensesIncomesTracker()
    {
        try
        {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        }
        catch(UnsupportedLookAndFeelException ex){
            System.err.println("Failed to Set FlatDarfLaf LookAndFeel");
        }
        
        //Custom color schemes for specific swing components
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.DARK_GRAY);
        UIManager.put("TextField.caretForeground", Color.RED);
        UIManager.put("ComboBox.foreground", Color.YELLOW);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.background", Color.ORANGE);
        UIManager.put("Label.foreground", Color.WHITE);
        
        //set the default font for the entire application
        Font customFont = new Font("Arial", Font.PLAIN,18);
        UIManager.put("Label.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("ComboBox.font", customFont);
        UIManager.put("Button.font", customFont);
        
        //Initialize the table model and balance variable 
        balance = 0.0;
        tableModel = new ExpenseIncomeTableModel();

        //Create a JTable and set up a scroll pane to display the data 
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        
        //Create input fields and components for adding new entries 
        dateField = new JTextField(10);
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);
        typeCombobox = new JComboBox<>(new String[]{"Expense","Income"});
        
        //Attach an actionlistener to the "add" button to handle new entry addition.
        addButton = new JButton("Add");
        addButton.addActionListener(e-> addEntry());
        balanceLabel = new JLabel("Balance: $" + balance);
        
        
        //Delete button for action listener
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteEntry());
        
        // Create the "Update" button and attach an ActionListener to handle updates
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateEntry());
    
        //create a input panel to arrange input components
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Date"));
        inputPanel.add(dateField);
      
        inputPanel.add(new JLabel("Description"));
        inputPanel.add(descriptionField);
        
        inputPanel.add(new JLabel("Amount"));
        inputPanel.add(amountField);
        
        inputPanel.add(new JLabel("Type"));
        inputPanel.add(typeCombobox);
        
        inputPanel.add(addButton);
        
        // Add the "Update" button to the input panel
        inputPanel.add(updateButton);
        
        //Add the "Delete" button to the input panel
        inputPanel.add(deleteButton);
        
        //create bottom panel to display the balance
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(balanceLabel);
        setLayout(new BorderLayout());
        
        //set the layout of the main frame and add components to appropriate positions
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        //set the title, default close operation, and visibility of the main frames 
        setTitle("Expenses and Incomes Tracker.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        
    }
    
    //method to handle adding new entries to the table
    private void addEntry()
    {
        //Getting input values from input fields
        String date = dateField.getText();
        String description = descriptionField.getText();
        String amountStr = amountField.getText();
        String type = (String) typeCombobox.getSelectedItem();
        double amount;
        
        int amt = Integer.parseInt(amountStr);
        //validate input fields
        if(amountStr.isEmpty() || amountStr.equals('0') || amt<=0)
        {
            JOptionPane.showMessageDialog(this,"Enter the Valid Amount","Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        try
        {
            amount = Double.parseDouble(amountStr);
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this,"Inavlid Amount Format","Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        //Converting expense to negative values
        if(type.equals("Expense"))
        {
            amount *= -1;
        }
        
        //create a new entry and add it to the table
        ExpenseIncomeEntry entry = new ExpenseIncomeEntry(date, description, amount, type);
        tableModel.addEntry(entry);
        
        //Update and display netbalance
        balance += amount;
        balanceLabel.setText("Balance: $"+balance);
        try {
            expenseincomeDB databaseConnection = new expenseincomeDB("localhost", 3306, "expenseincome", "root", "Hari@1314");
            boolean success = insertExpenseIncomeEntry(databaseConnection, entry);
            if (success) 
            {
                System.out.println("Entry saved");
            } else {
                System.out.println("Entry not saved");
            }
            databaseConnection.closeConnection();
        } 
        catch (Exception e) 
        {
        // Handle SQLException here.
            System.out.println("Exception caught while inserting: "+e.getMessage());
        }
        //clear input fields for next new entry
        clearInputFields();
    }
    
    //Method to clear input fields
    private void clearInputFields()
    {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        typeCombobox.setSelectedIndex(0);
    }
    
    private void deleteEntryFromDatabase(ExpenseIncomeEntry entry) {
    try 
    {
        expenseincomeDB databaseConnection = new expenseincomeDB("localhost", 3306, "expenseincome", "root", "Hari@1314");
        boolean success = databaseConnection.deleteExpenseIncomeEntry(entry);
        if (success) 
        {
            System.out.println("Entry deleted");
        } 
        else 
        {
            System.out.println("Entry not deleted");
        }
        databaseConnection.closeConnection();
    } 
    catch (Exception e)
    {
        System.err.println("Error deleting entry from the database: " + e.getMessage());
    }
}

    // Method to handle entry deletion
    private void deleteEntry() 
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) 
        {
            // Get the selected entry from the table model
            ExpenseIncomeEntry selectedEntry = tableModel.entries.get(selectedRow);

            // Update the balance based on the entry to be deleted
            balance -= selectedEntry.getAmount();
            balanceLabel.setText("Balance: $" + balance);

            // Remove the selected entry from the table model and notify the table
            tableModel.entries.remove(selectedRow);
            tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "Select an entry to delete", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    private void updateEntry()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) 
        {
            ExpenseIncomeEntry selectedEntry = tableModel.entries.get(selectedRow);

            String date = dateField.getText();
            String description = descriptionField.getText();
            String amountStr = amountField.getText();
            String type = (String) typeCombobox.getSelectedItem();
            double amount;

            if (amountStr.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Enter the Amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try
            {
                amount = Double.parseDouble(amountStr);
            } 
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (type.equals("Expense"))
            {
                amount *= -1;
            }

            // Update the selected entry with the new values
            selectedEntry.setDate(date);
            selectedEntry.setDescription(description);
            selectedEntry.setAmount(amount);
            selectedEntry.setType(type);

            // Update the table and balance
            tableModel.fireTableDataChanged();
            recalculateBalance();

            // Clear input fields for the next entry
            clearInputFields();
        } 
        else
        {
            JOptionPane.showMessageDialog(this, "Select an entry to update", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to recalculate the balance based on the table entries
    private void recalculateBalance() 
    {
        balance = 0.0;
        for (ExpenseIncomeEntry entry : tableModel.entries)
        {
            balance += entry.getAmount();
        }
        balanceLabel.setText("Balance: $" + balance);
    }

}
