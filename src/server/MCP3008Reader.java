
/*
 * 以下でimportするのは、Raspberry Piを除くコンピュータで開発するためである。
 * 実際に動作させるには、Raspberry Piにおいて別途Pi4Jのインストールが必要な点に留意すること。
 * インストールに必要なコマンドは以下のURLを参照する。
 * https://pi4j.com/1.2/install.html
 */
import com.pi4j.gpio.extension.base.AdcGpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.spi.SpiChannel;

public class MCP3008Reader {
	// センサ読み取りの無限ループを抜けるため、読み取られた値と比較するしきい値
	private static final double SENSOR_THRESHOLD = 200;

	public static void main(String[] args) throws Exception {
		System.out.println("<--Pi4J--> MCP3008 Monitoring Started.");

		final GpioController gpio = GpioFactory.getInstance();
		// MCP3008には8chあり、CH0からCH7までが利用できる
		final AdcGpioProvider provider = new MCP3008GpioProvider(SpiChannel.CS0);
		final GpioPinAnalogInput analogInput = gpio.provisionAnalogInputPin(provider, MCP3008Pin.CH0,
				"CH0-Photoresistor");

		while (true) {
			double value = analogInput.getValue();
			System.out.println("[" + analogInput.getName() + "] : RAW VALUE = " + value);
			if (value < SENSOR_THRESHOLD) {
				break;
			}
			Thread.sleep(1000); // 単位はミリ秒
		}

		gpio.shutdown();
		System.exit(0);
	}

}