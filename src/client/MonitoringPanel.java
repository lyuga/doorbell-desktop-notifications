import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class MonitoringPanel extends JPanel {
	private Window window;
	private Settings settings;
	private JButton settingsButton;
	private JButton startButton;
	private JButton stopButton;
	private JButton confirmButton;
	private JButton[] buttons;
	private JLabel statusLabel;
	private Connection conn;
	private MonitoringTask monitoringTask;

	public MonitoringPanel(Window window, Settings settings) {
		this.window = window;
		this.settings = settings;

		settingsButton = window.getNavigationButton("settings");
		startButton = new JButton(new ConnectAction());
		startButton.setFont(new Font("メイリオ", Font.PLAIN, 40));
		stopButton = new JButton(new CancelAction());
		stopButton.setFont(new Font("メイリオ", Font.PLAIN, 40));
		confirmButton = new JButton(new ConfirmAction());
		confirmButton.setFont(new Font("メイリオ", Font.PLAIN, 40));
		statusLabel = new JLabel("ここに状況メッセージが表示されます");
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		statusLabel.setFont(new Font("メイリオ", Font.BOLD, 22));
		buttons = new JButton[] { settingsButton, startButton, stopButton, confirmButton };

		setLayout(new GridLayout(5, 1));
		add(settingsButton);
		add(startButton);
		add(stopButton);
		add(confirmButton);
		add(statusLabel);

		setSelectedButtonsEnabled(new JButton[] { settingsButton, startButton });

		window.addCard(this, "monitoring");
	}

	public void setSelectedButtonsEnabled(JButton[] selectedButtons) {
		for (JButton button : buttons) {
			if (Arrays.asList(selectedButtons).contains(button)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
	}

	public void updateWindow() {
		revalidate();
		repaint();
	}

	class MonitoringTask extends SwingWorker<Void, Void> {
		protected Void doInBackground() {
			conn.connect();
			conn.receive();
			return null;
		}

		@Override
		protected void done() {
			if (conn.hasSuccessfulConnection()) {
				if (conn.isDoorbellPressed()) {
					statusLabel.setText("インターホンが押されました");
				} else {
					statusLabel.setText("センサに異常があります");
				}
				setSelectedButtonsEnabled(new JButton[] { confirmButton });
			} else {
				statusLabel.setText(conn.getErrorStatus());
				setSelectedButtonsEnabled(new JButton[] { settingsButton, startButton });
			}
			window.bringWindowToFront();
		}
	}

	class ConnectAction extends AbstractAction {
		ConnectAction() {
			putValue(Action.NAME, "モニタ開始");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = new Connection(settings.getAddress(), settings.getPort());
			monitoringTask = new MonitoringTask();
			setSelectedButtonsEnabled(new JButton[] { stopButton });
			statusLabel.setText("インターホンのモニタ中...");
			statusLabel.paintImmediately(statusLabel.getVisibleRect());
			monitoringTask.execute();
		}
	}

	class CancelAction extends AbstractAction {
		CancelAction() {
			putValue(Action.NAME, "中止");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (monitoringTask != null && !monitoringTask.isDone()) {
				monitoringTask.cancel(true);
			}
			monitoringTask = null;

			conn.disconnect();

			setSelectedButtonsEnabled(new JButton[] { settingsButton, startButton });
			statusLabel.setText("モニタが中止されました");
		}
	}

	class ConfirmAction extends AbstractAction {
		ConfirmAction() {
			putValue(Action.NAME, "確認");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setSelectedButtonsEnabled(new JButton[] { settingsButton, startButton });
			statusLabel.setText("確認されました");
			conn.disconnect();
		}
	}
}
