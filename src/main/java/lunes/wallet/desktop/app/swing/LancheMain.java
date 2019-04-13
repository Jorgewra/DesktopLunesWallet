package lunes.wallet.desktop.app.swing;
import javax.swing.JOptionPane;

import org.apache.http.util.TextUtils;
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
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lunes.wallet.desktop.app.controller.WalletController;
import lunes.wallet.desktop.app.interfaces.WalletInterfaceServices;

import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

@SpringBootApplication(
		scanBasePackages = "lunes.wallet.desktop.app"
)
public class LancheMain extends ApplicationWindow {
	private Text txtSeed;
	private Composite container;
	private Button btnEnter;
	private Button btnGeneration;
	
	private WalletInterfaceServices services = new WalletController();
	/**
	 * Create the application window.
	 */
	public LancheMain() {
		super(null);
		setShellStyle(SWT.MIN);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		container.setFont(SWTResourceManager.getFont("Arial Black", 14, SWT.BOLD));
		container.setLayout(null);
		
		Label labelSeed = new Label(container, SWT.NONE);
		labelSeed.setFont(SWTResourceManager.getFont("Arial Black", 12, SWT.BOLD));
		labelSeed.setAlignment(SWT.CENTER);
		labelSeed.setBounds(10, 0, 473, 42);
		labelSeed.setText("Your Seed Word or Genarete now");
		
		txtSeed = new Text(container, SWT.BORDER | SWT.CENTER | SWT.MULTI);
		txtSeed.setFont(SWTResourceManager.getFont("Arial Black", 14, SWT.BOLD));
		txtSeed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtSeed.setBounds(72, 65, 348, 115);
		
		btnGeneration = new Button(container, SWT.CENTER);
		btnGeneration.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnGeneration.setFont(SWTResourceManager.getFont("Arial Black", 12, SWT.NORMAL));
		btnGeneration.setBounds(90, 198, 150, 35);
		btnGeneration.setText("New Seed");
		btnGeneration.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {				
				nwSeedWord();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnEnter = new Button(container, SWT.CENTER);
		btnEnter.setText("Access");
		btnEnter.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnEnter.setFont(SWTResourceManager.getFont("Arial Black", 12, SWT.NORMAL));
		btnEnter.setBounds(250, 198, 150, 35);
		btnEnter.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setWallet();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		return container;
	}
	
	public void setWallet() {	
		if(TextUtils.isEmpty(txtSeed.getText())) {
			JOptionPane.showMessageDialog(null, "Please report your seed", "Attencion", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String seed12[] =   txtSeed.getText().toString().split(" ");
		if(seed12.length <11) {
			JOptionPane.showMessageDialog(null, "Please report your seed coorect!", "Attencion", JOptionPane.ERROR_MESSAGE);
			return;
		}
		WalletLunes lunes = new WalletLunes(txtSeed.getText());
		lunes.setBlockOnOpen(true);
		lunes.open();	
	}
	public void nwSeedWord() {
		try {			
			txtSeed.setText(services.generationSeed());
			JOptionPane.showMessageDialog(null, "Save in : "+System.getProperty("user.dir")+"\\SEEDWORD.txt");			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Errors:\n"+e.getMessage(), "Attencion", JOptionPane.ERROR_MESSAGE);
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
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			LancheMain window = new LancheMain();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
//		String local  = getClass().getResource("/resources/img/lunes.png").getRef()
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
//		newShell.setImage(SWTResourceManager.getImage(local));
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
		return new Point(450, 300);
	}
}
