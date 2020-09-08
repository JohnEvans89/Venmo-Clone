package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.util.ArrayList;
import java.util.List;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

	private int currentUserId;

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private AccountService accountService;
	private UserService userService;
	private TransferService transferService;


	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL),
				new UserService(API_BASE_URL), new TransferService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService,
			   UserService userService, TransferService transferService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.userService = userService;
		this.transferService = transferService;

	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {

		try {
			Account userAccount = accountService.getOne(currentUserId);
			System.out.println("Your current account balance is: $" + userAccount.getBalance());
		} catch (AccountServiceException ex) {
			console.printError(ex.getMessage());
		}
	}

	private void viewTransferHistory() {

		try {
			// collectTransferId() calls console method to print currentUser's transfer history
			int transferId = collectTransferId(true);
			if (transferId == 0) {
				return;
			}

			Transfer specifiedTransfer = transferService.getTransferByTransferId(transferId);
			String transferSenderName = userService.getNameById(specifiedTransfer.getAccountFrom());
			String transferReceiverName = userService.getNameById(specifiedTransfer.getAccountTo());

			console.printHeading("Transfer Details", "");
			console.printTransferDetails(specifiedTransfer, transferSenderName, transferReceiverName);

		} catch (UserServiceException | TransferServiceException e) {
			console.printError(e.getMessage());
		}
	}

	private void viewPendingRequests() {

		List<Transfer> pending = createPendingTransfersList();
		boolean userHasTransfersToApprove = false;

		console.printHeading("Pending Transfers", String.format("%-1s%21s%19s", "ID", "From/To", "Amount"));

		for (Transfer transfer : pending) {
			try {
				String toUser = userService.getNameById(transfer.getAccountTo());
				String fromUser = userService.getNameById(transfer.getAccountFrom());

				if (transfer.getAccountFrom() == currentUserId) {
					console.printPendingTransfer(transfer, toUser, true);
				} else if (transfer.getAccountTo() == currentUserId) {
					console.printPendingTransfer(transfer, fromUser, false);
					userHasTransfersToApprove = true;
				}

			} catch (UserServiceException ex) {
				console.printError(ex.getMessage());
			}
		}

		// current user may have pending transfer requests sent out to other users
		// but if there are no requests waiting there approval this boolean check prevents them from having to
		// manually back out to the main menu by hitting 0
		if (!userHasTransfersToApprove) {
			System.out.println("\nNo transfers currently requiring your approval. Returning to previous menu.");
			return;
		}

		int pendingId = collectTransferId(false);
		if (pendingId == 0) {
			return;
		}

		console.printAcceptMenu();

		try {
			int answer;
			while (true) {
				String approveRejectNeither = console.getUserInput("Please choose an option: ");
				if (isNumber(approveRejectNeither)) {
					answer = Integer.parseInt(approveRejectNeither);
					if (answer == 1 || answer == 2 || answer == 0) {
						break;
					}
				}
				System.out.println("\nInvalid input.\n");
			}

			Transfer pendingTransfer = transferService.getTransferByTransferId(pendingId);
			double currentUserBalance = accountService.getOne(currentUserId).getBalance();
			boolean sufficientFunds = currentUserBalance >= pendingTransfer.getAmount();

			// if approved
			if (answer == 1) {
				// if current users account has enough money
				if (sufficientFunds) {
					pendingTransfer.setTransferStatusId(2);
					transferService.update(pendingTransfer);
					updateAccountBalancesAfterTransfer(pendingTransfer.getAccountFrom(), pendingTransfer.getAmount());
				} else {
					System.out.println("\nInsufficient funds to complete request.");
				} // if rejected
			} else if (answer == 2) {
				pendingTransfer.setTransferStatusId(3);
				transferService.update(pendingTransfer);
			} else {
				return;
			}
		} catch (TransferServiceException | AccountServiceException ex) {
			console.printError(ex.getMessage());
		}
	}

	private void sendBucks() {

		// collectId() calls a method from console that prints the list of users
		int userToId = collectId(true);
		if (userToId == 0) {
			return;
		}

		double amount = collectTransferAmount(true);
		if (amount == 0) {
			return;
		}

		try {

			if (updateAccountBalancesAfterTransfer(userToId, amount)) {
				Transfer transfer = createTransfer(userToId, amount, true);
				transferService.addNewTransfer(transfer);
			}
		} catch (TransferServiceException e) {
			console.printError(e.getMessage());
		}
	}

	private void requestBucks() {

		// collectId() calls a method from console that prints the list of users
		int userToId = collectId(false);
		if (userToId == 0) {
			return;
		}

		double amount = collectTransferAmount(false);
		if (amount == 0) {
			return;
		}

		try {
			Transfer transfer = createTransfer(userToId, amount, false);
			transferService.addNewTransfer(transfer);
		} catch (TransferServiceException ex) {
			console.printError(ex.getMessage());
		}
	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) //will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				AccountService.AUTH_TOKEN = currentUser.getToken();
				UserService.AUTH_TOKEN = currentUser.getToken();
				TransferService.AUTH_TOKEN = currentUser.getToken();
				currentUserId = currentUser.getUser().getId();
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private int collectId(boolean isForSendTypeTransfer) {

		console.printHeading("Users", String.format("%-1s%17s", "ID", "Name"));

		try {

			List<User> allUsers = userService.allUsers();
			console.printUsersList(allUsers, currentUserId);
			// used to validate user input
			int maxIdFromUserList = allUsers.get(allUsers.size() - 1).getId();

			String input;
			int validIdOrZero;

			while (true) {

				if (isForSendTypeTransfer) {
					input = console.getUserInput("\nEnter ID of user you are sending to (0 to cancel)");
				} else {
					input = console.getUserInput("\nEnter ID of user you are requesting from (0 to cancel)");
				}

				if (isNumber(input)) {
					validIdOrZero = Integer.parseInt(input);
					if (validIdOrZero == 0) {
						System.out.println("\nTransaction canceled. Returning to previous menu.");
						return 0;
					} else if (validIdOrZero > 0 && validIdOrZero != currentUserId && validIdOrZero <= maxIdFromUserList) {
						return validIdOrZero;
					} else {
						System.out.println("\nInvalid ID.");
					}
				} else {
					System.out.println("\nNot a number.");
				}
			}
		} catch (UserServiceException ex) {
			console.printError(ex.getMessage());
		}
		return 0;
	}

	private int collectTransferId(boolean isAllTransfers) {

		if (isAllTransfers) {
			console.printHeading("Transfers", String.format("%-1s%21s%19s", "ID", "From/To", "Amount"));
			console.printList(createFormattedTransferHistory());
		}

		// used to validate user input
		List<Integer> transferIdList = createListOfTransferIds();
		List<Integer> pendingIdList = createListOfPendingTransferIdsCurrentUserReceived();

		String input;
		int validIdOrZero;

		while (true) {

			if (isAllTransfers) {
				input = console.getUserInput("\nPlease enter transfer ID to view details (0 to cancel)");
			} else {
				input = console.getUserInput("\nPlease enter transfer ID to approve/reject (0 to cancel)");
			}

			if (isNumber(input)) {
				validIdOrZero = Integer.parseInt(input);
				if (validIdOrZero == 0) {
					System.out.println("\nTransaction canceled. Returning to previous menu.");
					return 0;
				} else if (!isAllTransfers && pendingIdList.contains(validIdOrZero) && transferIdList.contains(validIdOrZero)) {
					return validIdOrZero;
				} else if (transferIdList.contains(validIdOrZero) && isAllTransfers) {
					return validIdOrZero;
				} else {
					System.out.println("\nInvalid ID.");
				}
			} else {
				System.out.println("\nNot a number.");
			}
		}
	}

	private double collectTransferAmount(boolean isForSendTypeTransfer) {

		String input;
		double amount;

		try {

			double amountAvailable = accountService.getOne(currentUserId).getBalance();

			while (true) {
				input = console.getUserInput("Enter amount (0 to cancel)");
				if (isDouble(input)) {
					amount = Double.parseDouble(input);
					if (amount == 0) {
						System.out.println("\nTransaction canceled. Returning to previous menu.");
						return 0; // if it is for a send we need to make sure user has sufficient funds in their account
					} else if (amount > 0 && amount <= amountAvailable && isForSendTypeTransfer) {
						return amount; // if it isn't for a send any positive number can be requested
					} else if (amount > 0 && (!(isForSendTypeTransfer))) {
						return amount;
					} else {
						System.out.println("\nInsufficient funds or invalid input.");
					}
				} else {
					System.out.println("\nNot a number.");
				}
			}

		} catch (AccountServiceException e) {
			console.printError(e.getMessage());
		}
		return 0;
	}

	private boolean updateAccountBalancesAfterTransfer(int recipientId, double amount) {

		try {

			Account currentUserAccount = accountService.getOne(currentUserId);
			Account recipientsAccount = accountService.getOne(recipientId);

			currentUserAccount.setBalance(currentUserAccount.getBalance() - amount);
			recipientsAccount.setBalance(recipientsAccount.getBalance() + amount);

			accountService.update(currentUserAccount);
			accountService.update(recipientsAccount);

		} catch (AccountServiceException ex) {
			console.printError(ex.getMessage());
			return false;
		}
		return true;
	}

	private Transfer createTransfer(int recipientId, double amount, boolean isSend) {
		Transfer transfer = new Transfer();

		transfer.setAccountFrom(currentUserId);
		transfer.setAccountTo(recipientId);
		transfer.setAmount(amount);
		// there are only two transfer type ids, 2 (send) and 1 (request)
		if (isSend) {
			transfer.setTransferTypeId(2);
		} else {
			transfer.setTransferTypeId(1);
		}
		// if transfer type id is 2 we know it is a send, if not it is a request since a transfer
		// would never be created with status rejected (rejected is transfer_status_id 3 in database)
		int transferStatus = transfer.getTransferTypeId() == 2 ? 2 : 1;
		transfer.setTransferStatusId(transferStatus);

		return transfer;
	}

	private List<String> createFormattedTransferHistory() {
		List<String> formattedTransfers = new ArrayList<>();

		try {
			List<Transfer> usersTransfers = transferService.allUsersTransfers(currentUserId);
			for (Transfer t : usersTransfers) {
				String receiver = userService.getNameById(t.getAccountTo());
				String giver = userService.getNameById(t.getAccountFrom());

				if (t.getAccountFrom() == currentUserId) {
					if (t.getTransferId() < 10) {
						formattedTransfers.add(String.format("%s%23s%13s%.2f", t.getTransferId(), "To: " + receiver, "$", t.getAmount()));
					} else {
						formattedTransfers.add(String.format("%s%22s%13s%.2f", t.getTransferId(), "To: " + receiver, "$", t.getAmount()));
					}
				} else {
					if (t.getTransferId() < 10) {
						formattedTransfers.add(String.format("%s%23s%13s%.2f", t.getTransferId(), "From: " + giver, "$", t.getAmount()));
					} else {
						formattedTransfers.add(String.format("%s%22s%13s%.2f", t.getTransferId(), "From: " + giver, "$", t.getAmount()));
					}
				}
			}
		} catch (TransferServiceException | UserServiceException ex) {
			console.printError(ex.getMessage());
		}
		return formattedTransfers;
	}

	private List<Transfer> createPendingTransfersList() {

		List<Transfer> pendingTransfers = new ArrayList<>();

		try {
			// pending transfers have transferStatusId of 1
			pendingTransfers = transferService.allUsersPendingTransfers(currentUserId, 1);
		} catch (TransferServiceException ex) {
			console.printError(ex.getMessage());
		}
		return pendingTransfers;
	}

	// this list is used to validate user input in collectTransferId method
	private List<Integer> createListOfTransferIds() {
		List<Integer> transferIds = new ArrayList<>();
		try {
			List<Transfer> usersTransfers = transferService.allUsersTransfers(currentUserId);
			for (Transfer t : usersTransfers) {
				transferIds.add(t.getTransferId());
			}
		} catch (TransferServiceException ex) {
			console.printError(ex.getMessage());
		}
		return transferIds;
	}

	// this list is used to validate user input in collectTransferId method
	private List<Integer> createListOfPendingTransferIdsCurrentUserReceived() {
		List<Integer> pendingIds = new ArrayList<>();
		List<Transfer> allPending = createPendingTransfersList();

		for (Transfer transfer : allPending) {
			if (transfer.getAccountTo() == currentUserId) {
				pendingIds.add(transfer.getTransferId());
			}
		}
		return pendingIds;
	}

	private boolean isNumber(String input) {
		try {
			Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	// we tried using isNumber to verify doubles entered as strings i.e. "12.34" but it id not recognize
	// them as numbers, hence the necessity of this method as well, despite both serving the same purpose
	private boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}