package drive;

import com.fazecast.jSerialComm.SerialPort;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

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

            Component[] component = controller.getComponents();
            for(int i=0;i<component.length;i++) {

                if (component[i].getName().equals("x")) {
                    aButton = component[i].getPollData() == 1.0;
                }

                if (component[i].getName().equals("B")) {
                    bButton = component[i].getPollData() == 1.0;
                }

                if (component[i].getName().equals("X")) {
                    xButton = component[i].getPollData() == 1.0;
                }

                if (component[i].getName().equals("Y")) {
                    yButton = component[i].getPollData() == 1.0;
                }
                if (component[i].isAnalog()){
                    //System.out.println(component.getIdentifier().getName());
                    if(component[i].getIdentifier().getName().equals("x")){
                        System.out.println(component[i].getPollData());
                        //xAxis = (byte)Math.floor(component.getPollData() * 127.99);
                    }
                    if (component[i].getIdentifier().getName().equals("y")){
                        //yAxis = (byte)Math.floor(component.getPollData() * 127.99);
                        //System.out.println("y axis found");
                    }
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