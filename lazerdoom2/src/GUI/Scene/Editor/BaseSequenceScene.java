package GUI.Scene.Editor;

import lazerdoom.Core;
import sparshui.common.Event;
import sparshui.common.messages.events.DragEvent;
import sparshui.common.messages.events.EventType;
import sparshui.common.messages.events.TouchEvent;
import GUI.Multitouch.TouchableGraphicsItem;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSizeF;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QGraphicsItem.GraphicsItemFlag;

import edu.uci.ics.jung.graph.util.Pair;

public class BaseSequenceScene extends EditorScene {
	public static final double valueMultiplyer = 1000;
	public static final double tickMultiplyer = 1.0;
	
	public Signal2<Long, Boolean> startMoved = new Signal2<Long, Boolean>();
	public Signal2<Long, Boolean> lengthMoved = new Signal2<Long, Boolean>();
	
	private int fontSize = 40;
	private QFont captionFont = new QFont("Helvetica [Cronyx]", fontSize);
	
	private QGraphicsLineItem cursor = new QGraphicsLineItem();
	QPen cursorPen = new QPen(QColor.red);
	
	public class StartCursor extends TouchableGraphicsItem {

		public Signal2<Long, Boolean> moved = new Signal2<Long, Boolean>();
		
		QRectF boundingRect = new QRectF(0,-1000, 50, 2000);
		QColor color = new QColor(QColor.green);
		QPen pen = new QPen(color);
		QBrush brush = new QBrush(color);
		QPainterPath path;
		
		public StartCursor() {
			this.setPath();
			this.setZValue(1000.0);
			this.setFlag(GraphicsItemFlag.ItemIgnoresTransformations, true);
			
		}
		
		public boolean processEvent(Event e) {
			if(e instanceof DragEvent) {
				this.moved.emit((long) (e.getSceneLocation().x()/tickMultiplyer), ((DragEvent) e).isSuccessful());
			}
			return true;
		}
		
		private void setPath() {
			path = new QPainterPath();
			path.moveTo(0,-1000);
			path.lineTo(0,1000);
			path.moveTo(0,-15);
			path.lineTo(30,0);
			path.lineTo(0,15);
			path.lineTo(0,-15);
		}
		
		@Override
		public QRectF boundingRect() {
			return this.boundingRect;
		}

		@Override
		public void paint(QPainter painter, QStyleOptionGraphicsItem option,
				QWidget widget) {
			
			painter.setPen(this.pen);
			painter.setBrush(this.brush);
			painter.drawPath(this.path);
		}
		
		@Override
		public QSizeF getMaximumSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public QSizeF getPreferedSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setGeometry(QRectF size) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class LengthCursor extends TouchableGraphicsItem {

		QRectF boundingRect = new QRectF(0,-1000, 50, 2000);
		QColor color = new QColor(QColor.blue);
		QPen pen = new QPen(color);
		QBrush brush = new QBrush(color);
		QPainterPath path;
		
		Signal2<Long, Boolean> moved = new Signal2<Long, Boolean>();
		
		public LengthCursor() {
			this.setPath();
			this.setZValue(1000.0);
			this.setFlag(GraphicsItemFlag.ItemIgnoresTransformations, true);
		}
		
		private void setPath() {
			path = new QPainterPath();
			path.moveTo(26,-1000);
			path.lineTo(26,1000);
			path.moveTo(26,-15);
			path.lineTo(-4,0);
			path.lineTo(26,15);
			path.lineTo(26,-15);
		}
		
		public boolean processEvent(Event e) {
			if(e instanceof DragEvent) {
				this.moved.emit((long)(e.getSceneLocation().x()/ tickMultiplyer), ((DragEvent) e).isSuccessful());
			}
			return true;
		}
		
		@Override
		public QRectF boundingRect() {
			return this.boundingRect;
		}

		@Override
		public void paint(QPainter painter, QStyleOptionGraphicsItem option,
				QWidget widget) {
			
			painter.setPen(this.pen);
			painter.setBrush(this.brush);
			painter.drawPath(this.path);
		}
		
		@Override
		public QSizeF getMaximumSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public QSizeF getPreferedSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setGeometry(QRectF size) {
			// TODO Auto-generated method stub
			
		}
		
	} 

	private StartCursor startCursor = new StartCursor();
	private LengthCursor lengthCursor = new LengthCursor();
	
	private QPen vGridPen = new QPen(QColor.darkGray);
	private QPen vGridPenEmph = new QPen(QColor.darkGray);
	private QPen hGridPen = new QPen(QColor.darkGray);
	private QPen hGridPenEmph = new QPen(QColor.darkGray);
	
	public BaseSequenceScene() {
		this.cursor.setLine(new QLineF(0,-1000,0,1000));
		this.cursor.setPos(0, 0);
		this.cursorPen.setWidth(5);
		this.cursor.setPen(this.cursorPen);
		this.addItem(cursor);
		this.setSceneRect(new QRectF(0,-1000,1000,2000));
		
		this.addItem(startCursor);
		this.addItem(lengthCursor);
		
		this.startCursor.moved.connect(this.startMoved);
		this.lengthCursor.moved.connect(this.lengthMoved);
		
		vGridPen.setWidth(2);
		vGridPenEmph.setWidth(4);
		hGridPen.setWidth(2);
		hGridPenEmph.setWidth(4);
				
	}
	
	public void setPlayCursor(long tick) {
		this.cursor.setPos(tickMultiplyer*tick, 0);
	}
	
	public void setStartOffsetCursor(long tick) {
		System.out.println(tick+" ss");
		this.startCursor.setPos(tickMultiplyer*tick, 0);
	}
	
	public void setLengthCursor(long tick) {
		this.lengthCursor.setPos(tickMultiplyer*tick-lengthCursor.boundingRect.width(), 0);
	}
	
	private int verticalGridSize(double verticalScale) {
		// 1/8 grid @ scale 1.0;
		int measure = lazerdoom.Util.nearestPowerOfTwo((int)(8.0*(verticalScale)));
		
		return (int) Core.getInstance().beatMeasureToPPQ(1, measure);
	}
	
	@Override
	public void drawVerticalGrid(QPainter painter, QRectF rect, double verticalScale) {
		int gridSize = verticalGridSize(verticalScale);
		int leftSide = (int)rect.left();
		int rightSide = (int)rect.right();
		int offset = (int)leftSide% (int)gridSize;
		
		int gridStart = leftSide/gridSize;
		
		int top = (int)rect.top();
		int bottom = (int)rect.bottom();
		
		painter.setClipRect(rect);
		
		for(int pos = leftSide-offset; pos <= rightSide; pos = pos+gridSize) {
			if(gridStart % 4 == 0) {
				painter.setPen(vGridPenEmph);
			} else {
				painter.setPen(vGridPen);
			}
			painter.drawLine(pos, top, pos, bottom);
			gridStart++;
		}
	}
	
	@Override
	public void drawHorizontalGrid(QPainter painter, QRectF rect, double horizontalScale) {
		painter.setPen(hGridPen);
		painter.setClipRect(rect);
		painter.drawLine((int)rect.left(), 0, (int)rect.right(), 0);
	}
	
	@Override
	public void drawVerticalGridCaption(QPainter painter, QRectF rect, QPointF point, double verticalScale) {
		painter.setClipRect(rect);
		painter.setPen(QColor.darkGray);
		this.captionFont.setPointSize((int)(this.fontSize/(verticalScale*2)));
		painter.setFont(this.captionFont);
		painter.drawText(point, "1/"+lazerdoom.Util.nearestPowerOfTwo((int)(8.0*(verticalScale))));
	}
	
	double oldVerticalScale = -1337.0;
	int oldVerticalSnapGridResolution = 1000; 
	
	@Override
	public int verticalSnapToGridResolution(double verticalScale) {
			return this.verticalGridSize(verticalScale);
	}
}
