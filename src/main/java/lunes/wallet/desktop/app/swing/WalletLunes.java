package lunes.wallet.desktop.app.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import lunes.wallet.desktop.app.controller.WalletController;
import lunes.wallet.desktop.app.interfaces.WalletInterfaceServices;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class WalletLunes extends ApplicationWindow {
	private static final String nomeQrCodeGerado = "qrcodeimage";
    private static final String formatoQrCodeGerado = "png";
	private Button btnSend;
	private Button btnTrasaction;
	private Text txAdress;
	private String seed;
	private Label txBalance;
	private Label imgQR;
	private String [] account;
	private WalletInterfaceServices services = new WalletController();
	/**
	 * Create the application window.
	 */
	public WalletLunes(String seed) {
		super(null);
		setShellStyle(SWT.MIN);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		Display.getCurrent().dispose();
		this.seed = seed;
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(null);
		
		Label lblBalance = new Label(container, SWT.NONE);
		lblBalance.setAlignment(SWT.CENTER);
		lblBalance.setFont(SWTResourceManager.getFont("Arial Black", 12, SWT.BOLD));
		lblBalance.setBounds(0, 0, 484, 35);
		lblBalance.setText("BALANCE");
		
		txBalance = new Label(container, SWT.BORDER);
		txBalance.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		txBalance.setAlignment(SWT.CENTER);
		txBalance.setBounds(0, 40, 487, 28);
		txBalance.setText("0.00000000 LUNES");
		
		imgQR = new Label(container, SWT.BORDER);
		imgQR.setImage(SWTResourceManager.getImage("!"));
		imgQR.setAlignment(SWT.CENTER);
		imgQR.setBounds(0, 70, 485, 319);
		
		btnSend = new Button(container, SWT.CENTER);
		btnSend.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		btnSend.setBounds(25, 430, 150, 40);
		btnSend.setText("SEND LUNES");
		btnSend.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SendBalance modal = new SendBalance(getParentShell(),seed);
				modal.open();
				if(modal.getReturnCode() == OK) {
					getWallet();
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		btnTrasaction = new Button(container, SWT.NONE);
		btnTrasaction.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.BOLD));
		btnTrasaction.setBounds(180, 430, 150, 40);
		btnTrasaction.setText("TRANSACTIONS");
		btnTrasaction.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				callTrasacions();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		txAdress = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		txAdress.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.NORMAL));
		txAdress.setText("errors...");
		txAdress.setBounds(0, 400, 495, 24);
		
		Button btnRefresh = new Button(container, SWT.NONE);
		btnRefresh.setFont(SWTResourceManager.getFont("Arial Black", 11, SWT.BOLD));
		btnRefresh.setBounds(350, 430, 120, 40);
		btnRefresh.setText("Refresh");
		btnRefresh.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWallet();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		getWallet();
		
		return container;
	}
	private void callTrasacions() {
		try {
			java.awt.Desktop.getDesktop().browse( new java.net.URI( "https://blockexplorer.lunes.io/address/" + account[0]  ) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Errors:\n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Errors:\n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void getWallet() {
		try {
			account = services.infoAccount(this.seed);
			txAdress.setText(account[0]);
			txBalance.setText(account[1]+" LUNES");
			gerarComZXing(account[0]);
			JOptionPane.showMessageDialog(null, "Wallet imported...");			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Errors:\n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(null, "Errors:\n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void gerarComZXing(String texto){
        try {
             
            File myFile = new File(System.getProperty("user.dir")+"/"+nomeQrCodeGerado+"."+formatoQrCodeGerado);
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(texto,BarcodeFormat.QR_CODE, 300, 300, hintMap);
            int CrunchifyWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
  
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
            graphics.setColor(Color.BLACK);
  
            for (int i = 0; i < CrunchifyWidth; i++) {
                for (int j = 0; j < CrunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, formatoQrCodeGerado, myFile);
            imgQR.setImage(SWTResourceManager.getImage(myFile.getAbsolutePath()));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                   }
  
    }
	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}


	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		String local  = System.getProperty("user.dir") + System.getProperty("file.separator")+"src\\main\\resources\\img\\lunes2.png";
		newShell.setImage(SWTResourceManager.getImage(local));
		newShell.setModified(true);
		newShell.setMinimumSize(new Point(500, 300));
		super.configureShell(newShell);
		newShell.setText("Wallet LUNES - (@JORGEWRA)");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 530);
	}
}
