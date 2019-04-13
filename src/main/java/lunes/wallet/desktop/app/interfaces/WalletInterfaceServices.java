package lunes.wallet.desktop.app.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;

public interface WalletInterfaceServices {

	String generationSeed() throws IOException, URISyntaxException;

	String[] infoAccount(String seed) throws IOException, URISyntaxException;

	String sendLunes(String seed, Double amont, String recipient) throws IOException, URISyntaxException;

	boolean validarAdress(String recipient) throws IOException, URISyntaxException;

}
