package lunes.wallet.desktop.app.swing;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import org.apache.http.util.TextUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.SWTResourceManager;

import lunes.wallet.desktop.app.controller.WalletController;
import lunes.wallet.desktop.app.interfaces.WalletInterfaceServices;

import org.eclipse.swt.widgets.Text;

public class SendBalance extends Dialog {
	private Text inputAdress;
	private Text inputAmount;
	private Text tx_balance_send;
	private WalletInterfaceServices services = new WalletController();
	private String seed;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	/*
	 * public SendBalance(Shell parentShell) { super(parentShell); }
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public SendBalance(Shell parentShell, String seed) {
		super(parentShell);
		this.seed = seed;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblAdressWalletTo = new Label(container, SWT.NONE);
		lblAdressWalletTo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		lblAdressWalletTo.setAlignment(SWT.CENTER);
		lblAdressWalletTo.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.BOLD));
		lblAdressWalletTo.setLayoutData(new RowData(441, 30));
		lblAdressWalletTo.setText("Address wallet recipient");
		
		inputAdress = new Text(container, SWT.BORDER | SWT.CENTER);
		inputAdress.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		inputAdress.setLayoutData(new RowData(441, 28));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setLayoutData(new RowData(441, 23));
		lblNewLabel.setText("Amunt LUNES");
		
		inputAmount = new Text(container, SWT.BORDER | SWT.CENTER);
		inputAmount.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		inputAmount.setLayoutData(new RowData(439, 28));
		inputAmount.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				calculeteValue();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				calculeteValue();
			}
		});
		
		Label text_fee = new Label(container, SWT.CENTER);
		text_fee.setLayoutData(new RowData(440, 16));
		text_fee.setText("Fee : 0.00100000 LUNES");
		
		tx_balance_send = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		tx_balance_send.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.ITALIC));
		tx_balance_send.setText("1000.001000");
		tx_balance_send.setLayoutData(new RowData(440, 29));

		return container;
	}
	private void calculeteValue() {
		try {
			if(TextUtils.isEmpty(inputAmount.getText())){
				tx_balance_send.setText("0.00000000 LUNES to send");
			}else {
				Double value = Double.parseDouble(inputAmount.getText().toString());
				Double values = value + 0.001;		
				NumberFormat format = new DecimalFormat("0.00000000");
				format.setMaximumFractionDigits(8);
				format.setMaximumIntegerDigits(10);
				format.setRoundingMode(RoundingMode.HALF_UP);
				tx_balance_send.setText(""+format.format(values).replaceAll(",", ".")+" LUNES to send");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Errors: value invalid", "Attencion", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		setReturnCode(OK);
	}
	@Override
	protected void okPressed() {
		if(TextUtils.isEmpty(inputAdress.getText()) || TextUtils.isEmpty(inputAmount.getText())) {
			JOptionPane.showMessageDialog(null, "Errors Empty fields", "Attencion", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if(!services.validarAdress(inputAdress.getText())) {
				JOptionPane.showMessageDialog(null, "Errors Adress invalid", "Attencion", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Errors Empty fields", "Attencion", JOptionPane.ERROR_MESSAGE);			
			return;
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(null, "Errors Empty fields", "Attencion", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Double value = Double.parseDouble(inputAmount.getText().toString());
		try {
			String tx = services.sendLunes(seed, value, inputAdress.getText());
			JOptionPane.showMessageDialog(null, "<html><body>Sent with success...<br/>Transaction:<a href='https://blockexplorer.lunes.io/tx/"+tx+"'>https://blockexplorer.lunes.io/tx/"+tx+"</a>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Errors \n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Errors \n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
