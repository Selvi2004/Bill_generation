import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;



public class App {

    public static void main(String[] args) {
        
        try {

            //Class.forName("com.mysql.cj.jdbc.Driver");

            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery", "root",
                    //"Selvi@2004");

            Statement stmt = conn.createStatement();

            String selectSql = "SELECT ID, Items, Amount,stock FROM items";
            ResultSet rs = stmt.executeQuery(selectSql);

            System.out.println("------------------------------------------------------------");
            System.out.printf("\t\t\tAvailable Items:\n");
            System.out.println("------------------------------------------------------------");
            while (rs.next()) {
                int ID = rs.getInt("ID");
                String itemName = rs.getString("Items");
                int amount = rs.getInt("Amount");
                int stock = rs.getInt("stock");
                System.out.println(ID + "|" + itemName + " | " + amount + "|" + stock);
            }

            rs.close();
            stmt.close();
            conn.close();

            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            double totalAmount = 0.0;
            

            while (choice != 3) {
                System.out.println("\nSelect an option:");
                System.out.println("1. Enter items");
                System.out.println("2. Calculate total amount");
                System.out.println("3.Exit");
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid option.");
                    scanner.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1:
                        int numItems;
                        while (true) {
                            System.out.println("Enter the number of items to select: ");
                            try {
                                numItems = scanner.nextInt();
                                break;
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input. Please enter a valid number of items.");
                                scanner.nextLine();
                            }
                        }
                        totalAmount = 0.0;
                        for (int i = 0; i < numItems; i++) {
                            System.out.println("Enter item name " + (i + 1) + ": ");
                            scanner.nextLine(); // Consume the newline character
                            String itemName = scanner.nextLine();
                            int quantity;
                            while (true) {
                                System.out.println("Enter the quantity for item number " + itemName + ": ");
                                try {
                                    quantity = scanner.nextInt();
                                    break;
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input. Please enter a valid quantity.");
                                    scanner.nextLine();
                                }
                            }
                          // conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery", "root",
                                 //   "Selvi@2004");
                            stmt = conn.createStatement();
                            String selectItemSql = "SELECT * FROM items WHERE Items =  '" + itemName + "'";
                            rs = stmt.executeQuery(selectItemSql);
                            if (rs.next()) {
                                double price = rs.getDouble("Amount");
                                int currentStock = rs.getInt("stock");
                                if (quantity <= currentStock) {
                                    double itemTotalAmount = price * quantity;
                                    totalAmount += itemTotalAmount;
                                    System.out.println("Selected item price: " + price);
                                    System.out.println("Quantity: " + quantity);
                                    System.out
                                            .println("Total amount for item number " + itemName + ": "
                                                    + itemTotalAmount);
                                    int updatedStock = currentStock - quantity;
                                    String updateStockSql = "UPDATE items SET stock = " + updatedStock
                                            + " WHERE Items = '" + itemName + "'";

                                    stmt.executeUpdate(updateStockSql);
                                    String insertBillSql = "INSERT INTO bill (Items,quantity,Amount) VALUES ('"
                                            + itemName + "', " + quantity + ", " + itemTotalAmount + ")";

                                    stmt.executeUpdate(insertBillSql);
                                    
                                } else {
                                    System.out.println("Insufficient stock for item number " + itemName);
                                }
                            } else {
                                System.out.println("Invalid item number");
                            }
                            rs.close();
                            stmt.close();
                            conn.close();
                        }

                       // System.out.println("Total amount for " + numItems + " items: " + totalAmount);
                       // conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery", "root", "Selvi@2004");
                        //stmt = conn.createStatement();
                        String selectMaxCustIdSql = "SELECT MAX(cust_id) AS max_cust_id FROM grocery_details";
                        rs = stmt.executeQuery(selectMaxCustIdSql);
                        int customerID = 1;
                        if (rs.next()) {
                            customerID = rs.getInt("max_cust_id") + 1;
                        }
                        String insertDetailsSql = "INSERT INTO grocery_details (cust_id, purchased_amt) VALUES (" + customerID + ", " + totalAmount + ")";
                        stmt.executeUpdate(insertDetailsSql);
                        rs.close();
                        stmt.close();
                        conn.close();
                        break;
                    case 2:
                        System.out.println("Total amount: " + totalAmount);
                        break;
                    case 3:
                        System.out.println("Thank you");
                        break;
                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                        break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
