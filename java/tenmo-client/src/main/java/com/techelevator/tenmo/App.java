package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;


public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
//	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
//	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS,
//			MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS,
			MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private RestTemplate restTemplate;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.restTemplate = new RestTemplate();
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
//			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
//			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {

		BigDecimal balance = new BigDecimal(0);
		try {
			balance = restTemplate.exchange(API_BASE_URL + "balance/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
			System.out.println("Your current account balance is: $" + balance);
		} catch (RestClientException e) {
			System.out.println("Error getting balance");
		}
	}

	private void viewTransferHistory() {
		Transfers[] transferList = null;
		transferList = restTemplate.exchange(API_BASE_URL + "accounts/transfers/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
		System.out.println("-------------------------------------------\r\n" +
				"Transfers\r\n" +
				"ID          From/To                 Amount\r\n" +
				"-------------------------------------------\r\n");
		String toOrFrom = "";
		String nameOfUser = "";


		for(Transfers i : transferList) {
			if (currentUser.getUser().getUsername().equals(i.getUserFrom())){
				toOrFrom = "To: ";
				nameOfUser = i.getUserTo();
			}else if (!currentUser.getUser().getUsername().equals(i.getUserFrom())) {
				toOrFrom = "From: ";
				nameOfUser = i.getUserFrom();
			}
			System.out.println(i.getTransferId() + "\t\t" + toOrFrom + nameOfUser + "\t\t$" + i.getAmount());
		}
		System.out.print("-------------------------------------------\r\n" +
				"Please enter transfer ID to view details (0 to go back to previous menu): ");
		Scanner scanner = new Scanner(System.in);
		String userInput = scanner.nextLine();
		int transferId = Integer.parseInt(userInput);
		if (transferId != 0){
			boolean foundId = false;
			for(Transfers i: transferList){
				if( transferId == i.getTransferId()){
					Transfers viewTransferByID = restTemplate.exchange(API_BASE_URL + "transfers/" + i.getTransferId(), HttpMethod.GET, makeAuthEntity(), Transfers.class).getBody();
					foundId = true;
					System.out.println("-------------------------------------------\r\n" +
							"Transfers details\r\n" +
							"-------------------------------------------\r\n" +
							" Id: " + viewTransferByID.getTransferId() + "\r\n" +
							" From: " + viewTransferByID.getUserFrom() + "\r\n" +
							" To: " + viewTransferByID.getUserTo() + "\r\n" +
							" Type: " + viewTransferByID.getTransferType() + "\r\n" +
							" Status: " + viewTransferByID.getTransferStatus() + "\r\n" +
							" Amount: $" + viewTransferByID.getAmount() + "\r\n" +
							"-------------------------------------------\r");

				}
			}if(!foundId){
				System.out.println("Transfer ID is not valid.");
				System.out.println("Please try again.");
			}
		}

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	public void sendBucks() {
		User[] users = null;
		Transfers transfer = new Transfers();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);

		try {

			Scanner scanner = new Scanner(System.in);
			users = restTemplate.exchange(API_BASE_URL + "listusers", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
			System.out.println("-------------------------------------------\r\n" +
					"Users\r\n" +
					"ID\t\tName\r\n" +
					"-------------------------------------------");
			for (User i : users) {
				if (!i.getId().equals(currentUser.getUser().getId())) {
					System.out.println(i.getId() + "\t\t" + i.getUsername());


				}
			}
			System.out.print("-------------------------------------------\r\n" +
					"Enter ID of user you are sending to (0 to cancel): ");
			transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));
			transfer.setAccountFrom(currentUser.getUser().getId());
			if (transfer.getAccountTo() != 0) {
				System.out.print("Please enter amount: ");
				try {
					transfer.setAmount(new BigDecimal(scanner.nextLine()));
				} catch (NumberFormatException e) {
					System.out.println("Error entering amount");
				}
				String output = restTemplate.exchange(API_BASE_URL + "transfer", HttpMethod.POST,
						makeTransferEntity(transfer), String.class).getBody();
				System.out.println(output);
			}
		} catch (HttpServerErrorException e) {
			System.out.println("Invalid user.");
		}

	}



	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
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
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
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
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	private HttpEntity<Transfers> makeTransferEntity(Transfers transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity<Transfers> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}

	private HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}
}
