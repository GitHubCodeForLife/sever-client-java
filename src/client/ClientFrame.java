package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientFrame extends JFrame {

	ClientHelper clientHelper;
	private JPanel contentPane;
	private JTextField txtHello;
	JTextArea textArea;
	JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ClientFrame frame = new ClientFrame();
					frame.setVisible(true);
					frame.clientHelper = new ClientHelper(frame);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 546, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtHello = new JTextField();
		txtHello.setText("Hello\r\n");
		txtHello.setBounds(61, 52, 186, 28);
		contentPane.add(txtHello);
		txtHello.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientHelper.sendString(txtHello.getText());
				txtHello.setText("");
			}
		});
		btnNewButton.setBounds(325, 55, 85, 21);
		contentPane.add(btnNewButton);

		textArea = new JTextArea();
		textArea.setBounds(61, 136, 447, 171);
		contentPane.add(textArea);

		progressBar = new JProgressBar();
		progressBar.setValue(10);
		progressBar.setBounds(134, 95, 146, 11);
		contentPane.add(progressBar);
	}

	public void updateArea(String str) {
		textArea.append(str + "\n");
	}

	public void setProgressBar(int n) {
		progressBar.setValue(n);
	}

	public void showError(String str) {
		JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

}
