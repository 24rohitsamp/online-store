package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Store {

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products(in).csv", inventory);

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }


    public static void loadInventory(String fileName, ArrayList<Product> inventory) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                String id = parts[0].trim();
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());

                inventory.add(new Product(id, name, price));
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }


    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {
        System.out.println("\n--- Products ---");
        System.out.printf("%s %s %s%n", "ID", "Name", "Price");
        System.out.println("-".repeat(55));

        // Show every product in the inventory
        for (Product product : inventory) {
            System.out.printf("%s %s $%.2f%n",
                    product.getId(), product.getName(), product.getPrice());
        }

        System.out.println("-".repeat(55));
        System.out.print("Enter a product ID to add it to your cart (or X to go back): ");
        String input = scanner.nextLine().trim();

        // X returns to the main menu
        if (input.equalsIgnoreCase("X")) {
            return;
        }

        // Look up the product and add it to the cart if found
        Product found = findProductById(input, inventory);
        if (found != null) {
            cart.add(found);
            System.out.println("\"" + found.getName() + "\" added to your cart.");
        } else {
            System.out.println("Product ID \"" + input + "\" not found.");
        }
    }


    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {
        System.out.println("\n--- Your Cart ---");

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        // List each product in the cart and accumulate the total
        double totalAmount = 0;
        for (Product product : cart) {
            System.out.printf("%s %s $%.2f%n",
                    product.getId(), product.getName(), product.getPrice());
            totalAmount += product.getPrice();
        }

        System.out.println("-".repeat(55));
        System.out.printf("Total: $%.2f%n", totalAmount);
        System.out.print("Enter C to check out, or X to go back: ");
        String input = scanner.nextLine().trim();

        // C proceeds to checkout; anything else returns to the main menu
        if (input.equalsIgnoreCase("C")) {
            checkOut(cart, totalAmount, scanner);
        }
    }


    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        System.out.printf("%nYour total is $%.2f.%n", totalAmount);
        System.out.print("Confirm purchase? (Y / N): ");
        String confirm = scanner.nextLine().trim();
// if you click N the purchase is canceled which returns the user to the main menu.
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Purchase cancelled. Returning to main menu.");
            return;
        }

        // Accept payment — keep asking until an amount >= total is entered
        double amountPaid = 0;
        while (amountPaid < totalAmount) {
            System.out.print("Enter payment amount: $");
            try {
                amountPaid = Double.parseDouble(scanner.nextLine().trim());
                if (amountPaid < totalAmount) {
                    System.out.printf("Insufficient payment. Amount due: $%.2f%n", totalAmount);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid dollar amount.");
            }
        }

        double change = amountPaid - totalAmount;

        // This prints the receipt with totalAmount, amountPaid,and change included along with a thank you for your purchase note.
        System.out.println("\n========== RECEIPT ==========");
        for (Product product : cart) {
            System.out.printf("%s $%.2f%n", product.getName(), product.getPrice());
        }
        System.out.println("-----------------------------");
        System.out.printf("%s $%.2f%n", "Total:", totalAmount);
        System.out.printf("%s $%.2f%n", "Paid:", amountPaid);
        System.out.printf("%s $%.2f%n", "Change:", change);
        System.out.println("==============================");
        System.out.println("Thank you for your purchase!");


        cart.clear(); // clears the cart so new purchases can be made/new transaction

    }


    // this function finds the product by ID, if the product ID is a match it wll return the product, if it is not a match it will return null
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product product : inventory) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }
}