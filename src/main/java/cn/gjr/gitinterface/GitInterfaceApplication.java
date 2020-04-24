package cn.gjr.gitinterface;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

/**
 * springboot启动类
 *
 * @author GaoJunru
 */
@SpringBootApplication
public class GitInterfaceApplication {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        // For thread safety, this method should be invoked from the event-dispatching thread.
        SwingUtilities.invokeLater(() -> new Base(args));
    }
}
