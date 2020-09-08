package com.techelevator.view;


import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println("\n*** " + userInput + " is not valid ***\n");
			}
		} while(result == null);
		return result;
	}

	public void printTransferDetails(Transfer transfer, String fromName, String toName) {
		System.out.println("Id: " + transfer.getTransferId());
		System.out.println("From: " + fromName);
		System.out.println("To: " + toName);
		System.out.println("Type: " + (transfer.getTransferTypeId() == 1 ? "Request" : "Send"));
		System.out.println("Status: " + (transfer.getTransferStatusId() == 1 ? "Pending" : "Approved"));
		System.out.println(String.format("%s%.2f", "Amount: $" , transfer.getAmount()));
	}

	public void printUsersList(List<User> users, int id) {
		for (User user : users) {
			if (user.getId() == id) {
				continue;
			}
			System.out.println(user);
		}
	}

	public void printList(List<String> strings) {
		for (String str : strings) {
			System.out.println(str);
		}
	}

	public void printPendingTransfer(Transfer t, String name, boolean currentUserSentThisTransfer) {
		if (currentUserSentThisTransfer) {
			System.out.println(String.format("%s%23s%13s%.2f", t.getTransferId(), "To: " + name, "$", t.getAmount()));
		} else {
			System.out.println(String.format("%s%23s%13s%.2f", t.getTransferId(), "From: " + name, "$", t.getAmount()));
		}
	}

	public void printHeading(String headingText, String optionalText) {
		System.out.println("-------------------------------------------");
		System.out.println(headingText);
		if (!optionalText.isEmpty()) {
			System.out.println(optionalText);
		}
		System.out.println("-------------------------------------------");
	}

	public void printError(String errorMessage) {
		System.err.println(errorMessage);
	}

	public void printAcceptMenu() {
		System.out.println("1: Approve");
		System.out.println("2: Reject");
		System.out.println("0: Don't approve or reject");
		System.out.println("---------");

	}


}
