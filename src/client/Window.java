import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame implements ActionListener {
	private JPanel cards;
	private CardLayout layout;
	/*
	 * アプリケーションのContent Paneには、レイアウトマネージャがCardLayoutであるcardsを配置している。
	 * cardsはSettingsPanelとMonitoringPanelを格納し、常にどちらかの一方がContent Paneに表示される。
	 * buttonMapはそれらのPanelを切り替えるためのボタンを格納する。
	 */
	private Map<String, JButton> buttonMap;

	public Window() {
		super("インターホン通知システム");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);

		layout = new CardLayout();
		cards = new JPanel();
		cards.setLayout(layout);
		getContentPane().add(cards, BorderLayout.CENTER);

		buttonMap = new HashMap<String, JButton>();
		buttonMap.put("monitoring", new JButton("OK"));
		buttonMap.put("settings", new JButton("設定"));

		for (Map.Entry button : buttonMap.entrySet()) {
			((AbstractButton) button.getValue()).addActionListener(this);
			((AbstractButton) button.getValue()).setActionCommand((String) button.getKey());
			((JComponent) button.getValue()).setFont(new Font("メイリオ", Font.PLAIN, 40));
		}

		// Panels間において共通のSettingsインスタンスを参照するため、引数として渡す
		Settings settings = new Settings();
		SettingsPanel sp = new SettingsPanel(buttonMap, settings);
		MonitoringPanel mp = new MonitoringPanel(buttonMap, settings);
		addCard(sp, "settings");
		addCard(mp, "monitoring");
	}

	public void actionPerformed(ActionEvent e) {
		String constraints = e.getActionCommand();
		layout.show(cards, constraints);
	}

	public JButton getNavigationButton(String constraints) {
		if (buttonMap.containsKey(constraints)) {
			return buttonMap.get(constraints);
		} else {
			throw new IllegalArgumentException("No button matched.");
		}
	}

	private void addCard(JPanel card, String constraints) {
		cards.add(card, constraints);
	}
}
