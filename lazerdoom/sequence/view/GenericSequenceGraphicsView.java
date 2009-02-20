package sequence.view;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QResizeEvent;

public class GenericSequenceGraphicsView extends QGraphicsView {
	private GenericSequenceViewWidget parentWidget;
	
	public Signal0 viewChanged = new Signal0();
	
	public Signal1<QPointF> createItemAtScenePos = new Signal1<QPointF>();
	public Signal1<QPointF> mouseAtScenePos = new Signal1<QPointF>();
	
	GenericSequenceGraphicsView(GenericSequenceViewWidget parent) {
		super(parent);
		
		parentWidget = parent;
		
		this.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOn);
		this.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOn);
		this.horizontalScrollBar().valueChanged.connect(viewChanged);
		this.verticalScrollBar().valueChanged.connect(viewChanged);
		
		//this.setViewportUpdateMode(QGraphicsView.ViewportUpdateMode.SmartViewportUpdate);
	}
	public QRectF visibleRect() {
		return new QRectF(this.mapToScene(0,0), this.mapToScene(width(),height()));
	}
	
	protected void resizeEvent(QResizeEvent event) {
		super.resizeEvent(event);
		viewChanged.emit();
	}

	protected void mouseMoveEvent(QMouseEvent e) {
		mouseAtScenePos.emit(this.mapToScene(e.pos()));
		super.mouseMoveEvent(e);
	}
	
	protected void mouseDoubleClickEvent(QMouseEvent e) {
		createItemAtScenePos.emit(this.mapToScene(e.pos()));
		System.out.println("moo");
		super.mouseDoubleClickEvent(e);
	}
	
	protected void mousePressEvent(QMouseEvent e) {
		super.mousePressEvent(e);
	}
	
	protected void mouseReleaseEvent(QMouseEvent e) {
		super.mouseReleaseEvent(e);
	}

	
	protected void keyPressEvent(QKeyEvent arg__1) {
		
	}
}
