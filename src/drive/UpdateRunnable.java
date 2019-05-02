package drive;

import com.fazecast.jSerialComm.SerialPort;
import net.java.games.input.Component;
import net.java.games.input.Controller;

import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateRunnable implements Runnable {

    private Controller controller;
    private SerialPort serialPort;

    UpdateRunnable(Controller controller, SerialPort serialPort) {
        this.controller = controller;
        this.serialPort = serialPort;
    }

    private AtomicBoolean running = new AtomicBoolean(false);

    void start() {
        serialPort.openPort();
        Thread worker = new Thread(this);
        worker.start();
        System.out.println("\u001b[32;1mEnabled!\u001b[0m");
    }

    void stop() {
        running.set(false);
        serialPort.closePort();
        System.out.println("\u001b[31;1mDisabled!\u001b[0m");
    }

    @Override
    public void run() {
        running.set(true);

        while(running.get()) {
//            System.out.println("SENDING");
            controller.poll();
            boolean aButton = false;
            boolean bButton = false;
            boolean xButton = false;
            boolean yButton = false;
            byte xAxis = 0;
            byte yAxis = 0;
            for (Component component : controller.getComponents()) {
//            System.out.println(component.getName());



                if (component.getName().equals("A")) {
                    aButton = component.getPollData() == 1.0;
                }

                if (component.getName().equals("B")) {
                    bButton = component.getPollData() == 1.0;
                }

                if (component.getName().equals("X")) {
                    xButton = component.getPollData() == 1.0;
                }

                if (component.getName().equals("Y")) {
                    yButton = component.getPollData() == 1.0;
                }

                if (component.getName().equals("x")) {
                    xAxis = (byte)Math.floor(component.getPollData() * 127.99);
                }

                if (component.getName().equals("y")) {
                    yAxis = (byte)Math.floor(component.getPollData() * 127.99);
                }

            }

            byte buttons = (byte) ((aButton ? 1 : 0) + (bButton ? 2 : 0) + (xButton ? 4 : 0) + (yButton ? 8 : 0));
            byte[] packet = new byte[]{xAxis, yAxis, buttons, (byte)255};

//            System.out.println(Arrays.toString(packet));

            serialPort.writeBytes(packet, 4);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}