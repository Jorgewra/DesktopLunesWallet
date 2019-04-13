package lunes.wallet.desktop.app.controller;

import org.springframework.stereotype.Service;

import io.lunes.lunesJava.Account;
import io.lunes.lunesJava.Node;
import io.lunes.lunesJava.PrivateKeyAccount;
import lunes.wallet.desktop.app.interfaces.WalletInterfaceServices;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@Service
public class WalletController implements WalletInterfaceServices{
	
	private final long FEE = 100000;
	private final Double CONVERT_VALUE = 100000000.0;
	@Override
	public String generationSeed() throws IOException, URISyntaxException{
		String seed = PrivateKeyAccount.generateSeed();
		String directory = System.getProperty("user.dir");
		FileWriter archive = new FileWriter(directory+"\\SEEDWORD.txt");
	    PrintWriter archiveSave = new PrintWriter(archive);
	    archiveSave.print(seed);
	    archiveSave.close();
		return seed;
	}
	@Override
	public String[] infoAccount(String seed) throws IOException, URISyntaxException{
		PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(seed, Account.MAINNET);
		Node node = new Node();
		NumberFormat format = new DecimalFormat("0.00000000");
		format.setMaximumFractionDigits(8);
		format.setMaximumIntegerDigits(10);
		format.setRoundingMode(RoundingMode.HALF_UP);
		Double valueAmount = Double.valueOf(node.getBalance(alice.getAddress()));
		
		Double values = (valueAmount / CONVERT_VALUE);
		
		String[] account = {
				alice.getAddress(),//Adress
				format.format(values).replaceAll(",", ".")//Balance
		};
		return account;
	}
	@Override
	public String sendLunes(String seed, Double amont, String recipient) throws IOException, URISyntaxException{
		PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(seed, Account.MAINNET);		
		Node node = new Node();
		Double mont = amont * CONVERT_VALUE;
		String txId = node.transfer(alice, recipient, mont.intValue(), FEE);		
		return txId;
	}
	@Override
	public boolean validarAdress(String recipient) throws IOException, URISyntaxException{
		Node node = new Node();
		return node.validateAddresses(recipient);
	}
}
