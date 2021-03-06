package gui.item;

import gui.multitouch.*;
import gui.view.SequencerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import sequencer.BaseSequence;
import synth.SynthInstance;
import synth.event.SynthEvent;
import synth.event.SynthEventListenerInterface;
import synth.event.SynthEventServer;

import lazerdoom.LzrDmObjectInterface;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSizeF;
import com.trolltech.qt.core.Qt.TransformationMode;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QRadialGradient;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.svg.QSvgRenderer;

import control.ParameterControlBus;


public class SynthesizerItem extends BaseSynthesizerItem implements ConnectableSynthInterface, LzrDmObjectInterface, SynthEventListenerInterface {
	private QRectF contentsRect = new QRectF(39.5,39.5, 121, 121);
	private QRectF iconRect = contentsRect.adjusted(20, 20, -20, -20);
	private static QColor normalColor = new QColor(130,130,130); 
	private static QColor actionColor = new QColor(211,120,0);
	private static QColor controlValueColor = new QColor(255,2,104); 
	private static QBrush gradientBrush;
	private QColor customColor = new QColor(38,50,62);
	
	private LinkedList<SynthInConnector> inPorts;
	private LinkedList<SynthOutConnector> outPorts;
	
	private double currentControlValue = 0.0;

	private static QRectF boundingRect = new QRectF(0,0,200,200);
	
	private static QRectF meterBoundingRect = new QRectF(-100,-100,400,400);
	
	private static String svgFileName = System.getProperty("user.dir")+"/src/gui/item/SVG/speaker-icon.svg";
	private static QSvgRenderer sharedRenderer = new QSvgRenderer(svgFileName);
	

	private QImage mnemonic = createMnemonic(boundingRect, 10, 8, 40);

	private SynthInstance synth;
	
	public SynthesizerItem() {
		this.setBrushes();

		this.setParent(SequencerView.getInstance());
	}
	
	private void addPorts() {
		if(this.scene() != null) {
			if(inPorts != null) {
				for(SynthInConnector in: inPorts) {
					scene().removeItem(in);
				}	
			}
		}
		
		inPorts = new LinkedList<SynthInConnector>();
		outPorts = new LinkedList<SynthOutConnector>();
		
		ParameterControlBus[] busses = this.synth.getControlBusses();
		
		if(busses == null || busses.length == 0) {
			return;
		}
		
		int numberOfIns = busses.length;
		System.out.println("blen "+numberOfIns);

		SynthInConnector connector;

		QPainterPath path = new QPainterPath();
		QRectF adjustedRect = this.boundingRect().adjusted(10, 10, -10, -10).adjusted(-SynthConnector.boundingRect.width()/4, -SynthConnector.boundingRect.height()/4 , SynthConnector.boundingRect.width()*0.75, SynthConnector.boundingRect.height()*0.75);

		path.addEllipse(adjustedRect);

		double circumference = Math.PI*adjustedRect.width();

		double increment = ((circumference)/(double)numberOfIns)/(circumference);
		double alignment = (0.5-(increment*(numberOfIns-1)))/2;

		LinkedList<QPointF> ptList = new LinkedList<QPointF>();
		LinkedList<Double> rotList = new LinkedList<Double>();



		for(double i = alignment; i < 1.0; i+=increment) {
			if(i < 0.0) continue;
			QPointF p = path.pointAtPercent(1.0-i);
			ptList.add(p);
			rotList.add(path.angleAtPercent(1.0-i));
		}


		int i = 0;
		for(QPointF p: ptList) {
			System.out.println("new connector " + busses[i].getControlDesc().getName());
			connector = new SynthInConnector(busses[i].getControlDesc().getName(), busses[i], this.synth);
			//connector.scale(2.0, 2.0);
			connector.setParent(this);
			connector.rotateCentered(-rotList.get(i));
			connector.setPos(p);
			inPorts.add(connector);
			//this.moved.connect(connector,"parentMoved()");
			i++;
		}
	}
	
	
	
	public void setCurrentControlValue(float controlValue) {
		controlValue = Math.abs(controlValue);
		if(controlValue > 1) {controlValue = 1;}
		this.currentControlValue = controlValue;
		this.update();
	}
	
	private void setBrushes() {
		double rad = contentsRect.width();
		QRadialGradient gr = new QRadialGradient(rad, rad, rad, 3 * rad / 5, 3 * rad / 5);
		gr.setColorAt(0.0, new QColor(255, 255, 255, 120));
		gr.setColorAt(0.1, new QColor(155, 150, 150, 100));
		gr.setColorAt(0.9, new QColor(50, 50, 50, 30));
		gr.setColorAt(0.95, new QColor(0, 0, 0, 20));
		gr.setColorAt(1, new QColor(0, 0, 0, 0));

		gradientBrush = new QBrush(gr);
	}

	public static QImage createMnemonic(QRectF size, int numberOfStrips, int penWidth, int minimumCircumference) {
		//dirty hack since painting with aliasing didnt work

		int upsampling = 4;
		int rectSizeWidth = (int)size.width()*upsampling;
		penWidth = penWidth*upsampling;

		QImage pixmap = new QImage(rectSizeWidth, rectSizeWidth, QImage.Format.Format_ARGB32);
		pixmap.fill(0);

		QPainter painter = new QPainter(pixmap);
		painter.setRenderHint(QPainter.RenderHint.HighQualityAntialiasing);

		Random random = new Random();
		QColor color = new QColor();
		for(int i = 0; i < numberOfStrips; i++) {
			QPen pen = new QPen();
			color.setHsvF(random.nextDouble(), random.nextDouble(), random.nextDouble());
			pen.setColor(new QColor(color));
			pen.setWidth(penWidth);
			painter.setPen(pen);
			int sizeDeg = minimumCircumference+Math.abs((random.nextInt()) % (rectSizeWidth-penWidth-minimumCircumference));
			QRectF rect = new QRectF((rectSizeWidth-sizeDeg)/2,(rectSizeWidth-sizeDeg)/2, sizeDeg, sizeDeg);
			painter.drawArc(rect, random.nextInt()%(16*360), random.nextInt()%(16*360));
		}
		painter.end();
		return pixmap.scaledToHeight(rectSizeWidth/upsampling, TransformationMode.SmoothTransformation);
	}

	@Override
	public QRectF boundingRect() {
		// TODO Auto-generated method stub
		return boundingRect;
	}

	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem option,
			QWidget widget) {
		QColor frameColor;

		if(!this.isSelected()) { 
			frameColor = normalColor;
		} else {
			frameColor = actionColor;
		}

        if(this.currentMeterValue > 0.1){// && this.meterBoundingRect != null) {
        	painter.setPen(QPen.NoPen);
        	painter.setBrush(meterGradient);
        	painter.drawEllipse(currentMeterBoundingRect);
        }
        
        painter.setPen(QColor.white);
        painter.drawText(-100, -100, ""+10.0f*this.currentMeterValue);
		
		painter.drawImage(0,0, mnemonic);	
		
		// outer circle
		painter.setPen(frameColor);
		painter.setBrush(QBrush.NoBrush);
		painter.drawEllipse(boundingRect);

		// inner circle + text
		painter.setBrush(customColor);
		painter.setPen(frameColor);
		painter.drawRoundedRect(contentsRect, 25.0,25.0);
		sharedRenderer.render(painter, "speaker", iconRect);
		
		

		//painter.setBrush(actionColor);
		//painter.drawPie(contentsRect, 16*90, -5000);
		/*if(currentControlValue > 0.0) {
			painter.setBrush(controlValueColor);
			double width = contentsRect.width()*currentControlValue;
			QRectF rect = new QRectF((boundingRect.width()-width)/2, (boundingRect.width()-width)/2, width, width);
			painter.drawRoundedRect(rect, 25.0*currentControlValue,25.0*currentControlValue);
		} */

		painter.setBrush(gradientBrush);
		painter.setPen(frameColor);

		painter.drawRoundedRect(contentsRect, 25.0,25.0);

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

	@Override
	public List<SynthInConnector> getSynthInConnectors() {
		return inPorts;
	}

	@Override
	public List<SynthOutConnector> getSynthOutConnectors() {
		return outPorts;
	}

	@Override
	public QSizeF getMaximumSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SynthInstance getSynthesizer() {
		return this.synth;
	}

	@Override
	public void setSynthesizer(SynthInstance synth) {
		if(synth != null) {
			if(this.synth != null) {
				this.synth.removeSynthEventListener(this);
			}
			
			this.isInitialized = true;
			this.synth = synth;
			this.addPorts();		
			
			this.synth.addSynthEventListener(this);
		}
		
	}

	private boolean isInitialized = false;
	@Override
	public boolean isInitialized() {
		return this.isInitialized;
	}

	@Override
	public void setContentObject(LzrDmObjectInterface object) {
		if(object instanceof SynthInstance) {
			this.setSynthesizer((SynthInstance) object);			
		}
		
	}
	
	private Random rnd = new Random();
	private float currentMeterValue = 0.0f;
	private boolean hasMeterValue = false;
	private QRectF currentMeterBoundingRect = null;
	private QRadialGradient meterGradient = null;
	private QColor meterColor = new QColor(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255),80);
	
	private void setMeterValue(float meter) {
		hasMeterValue = false;
		meter = Math.abs(meter);
		if(meter > 0.0f) {
			//System.err.println("meter "+meter);
			meter = meter+0.01f;
			//meter = (float) (20.0*Math.log10((double)meter));
			if(meter > 1.0) {
				meter = 1.0f;
			}else if(meter < 0.1f) {
				meter = 0.1f;
			}
	

			this.hasMeterValue = true;
			this.currentMeterValue = meter;
			
			//System.err.println("meter "+meter);
			
			currentMeterBoundingRect = new QRectF(meterBoundingRect.left()*meter,
											meterBoundingRect.top()*meter,
											meterBoundingRect.width()*meter,
											meterBoundingRect.height()*meter);
			
			meterGradient = new QRadialGradient(100,
														 100,
														 meterBoundingRect.width()/2.0);
			
			currentMeterBoundingRect = meterBoundingRect;
			meterGradient.setColorAt(meter+0.2, QColor.transparent);
			meterGradient.setColorAt(0.65*meter+0.2, meterColor.lighter(170));
			meterGradient.setColorAt(0, meterColor);
		}
	}

	@Override
	public void dispatchSynthEvent(SynthEvent se) {
		if(se.getID() == 0) {
			this.setMeterValue((Float) se.getEvent());
		} 
	}
}
