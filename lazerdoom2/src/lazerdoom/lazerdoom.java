package lazerdoom;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QGraphicsView.ViewportUpdateMode;
import com.trolltech.qt.gui.QPainter.RenderHint;
import com.trolltech.qt.opengl.QGLWidget;

import sparshui.server.GestureServer;
import sparshui.client.Client;
import sparshui.client.ServerConnection;
import sparshui.common.Event;
import sparshui.common.Location;
import sparshui.gestures.GestureType;


public class lazerdoom extends QWidget {
	
	private SparshTUIOAdapter sparshTuioAdapter;
	private GestureServer sparshGestureServer;
	private Thread sparshGestureServerThread;
	private ServerConnection sparshServerConnection;
	
	private Scene scene = new Scene();
	private View view = new View();
	private QHBoxLayout layout = new QHBoxLayout();

    public static void main(String[] args) {
        QApplication.initialize(args);

        lazerdoom lazerdoomApp = new lazerdoom(null);
        lazerdoomApp.show();

        QApplication.exec();
    }

    public void stopCommunication() {
    	sparshGestureServerThread.interrupt();
    }
    
    
    public lazerdoom(QWidget parent){
        super(parent);
        
        /*System.out.println("Starting Sparsh-GestureServer");
        sparshGestureServer = new GestureServer();
        sparshGestureServerThread = new QThread (sparshGestureServer);
        sparshGestureServerThread.start();

        System.out.println("Starting Sparsh-TUIOAdapter");
        sparshTuioAdapter = new SparshTUIOAdapter(3333,5945);

        
        System.out.println("Starting Sparsh-ServerConnection");
        try {
			sparshServerConnection = new ServerConnection("localhost", view);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
        TUIOTouchHandler handler = new TUIOTouchHandler(view);
        
		this.setLayout(layout);
		layout.addWidget(view);
		//QGLWidget w = new QGLWidget();
		//view.setViewport(w);
		view.setScene(scene);
		//view.setRenderHint(RenderHint.Antialiasing);
		view.setViewportUpdateMode(ViewportUpdateMode.MinimalViewportUpdate);
		//view.scale(0.5, 0.5);
		
		

		this.setWindowFlags(Qt.WindowType.Window);
    }
}