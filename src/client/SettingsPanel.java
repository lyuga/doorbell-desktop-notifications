import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class SettingsPanel extends JPanel implements ActionListener {
	private Map cardButtonMap;
	private Settings settings;
	private JButton monitoringButton;
	private JTextField addressField;
	private JTextField portField;

	public SettingsPanel(Map cardButtonMap, Settings settings) {
		this.cardButtonMap = cardButtonMap;
		this.settings = settings;

		JPanel fields = new JPanel();
		fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
		addressField = new JTextField("192.168.0.0");
		addressField.setFont(new Font("Arial", Font.PLAIN, 25));
		addressField.setBorder(new TitledBorder("Address"));
		fields.add(addressField);
		portField = new JTextField("9888");
		portField.setFont(new Font("Arial", Font.PLAIN, 25));
		portField.setBorder(new TitledBorder("Port"));
		fields.add(portField);

		monitoringButton = (JButton) cardButtonMap.get("monitoring");
		monitoringButton.addActionListener(this);

		setLayout(new BorderLayout());
		add(fields, BorderLayout.CENTER);
		add(monitoringButton, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		settings.setAddress(addressField.getText());
		settings.setPort(Integer.parseInt(portField.getText()));
	}
}
