package gui.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lazerdoom.LazerdoomConfiguration;

import sceneitems.TouchPointCursor;
import sequencer.EventPointsSequence;
import sequencer.SequenceEvalListenerInterface;
import sequencer.SequenceEvent;
import sequencer.SequenceEventListenerInterface;
import sequencer.SequencerInterface;
import sequencer.TestingSequencer;
import sparshui.client.Client;
import sparshui.common.Event;
import sparshui.common.Location;
import sparshui.common.TouchState;
import sparshui.common.messages.events.DeleteEvent;
import sparshui.common.messages.events.DragEvent;
import sparshui.common.messages.events.EventType;
import sparshui.common.messages.events.ExtendedGestureEvent;
import sparshui.common.messages.events.GroupEvent;
import sparshui.common.messages.events.RotateEvent;
import sparshui.common.messages.events.TouchEvent;
import synth.event.SynthEventServer;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.core.Qt.FillRule;
import com.trolltech.qt.core.Qt.KeyboardModifier;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.MouseButtons;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsLinearLayout;
import com.trolltech.qt.gui.QGraphicsPathItem;
import com.trolltech.qt.gui.QGraphicsProxyWidget;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QGraphicsWidget;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QRadialGradient;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QGraphicsScene.ItemIndexMethod;
import com.trolltech.qt.gui.QSizePolicy.ControlType;
import com.trolltech.qt.gui.QSizePolicy.Policy;
import com.trolltech.qt.opengl.QGLWidget;

import control.types.DoubleType;
import lazerdoom.lazerdoom;

import edu.uci.ics.jung.graph.util.Pair;
import gui.editor.*;
import gui.item.*;
import gui.item.SequencerMenuButton.ActionType;
import gui.item.editor.TouchableEditor;
import gui.item.editor.TouchableEditorItem;
import gui.item.editor.TouchableGraphicsView;
import gui.multitouch.*;
import gui.scene.editor.SequenceDataEditorScene;


import java.util.Map.Entry;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lazerdoom.lazerdoom;
import message.Intercom;
import message.ProcessorInterface;
import message.Scheduler;
import message.ThreadComSlotted;
import message.ThreadXBarSlotted;
import message.Intercom.IntercomSystem.GestureInput.ProcessEvent;
import message.Intercom.IntercomSystem.SequenceEventDispatcher.SequenceEventContainer;
import message.Intercom.IntercomSystem.SynthEventDispatcher.SynthEventContainer;

public class SequencerView extends QGraphicsView implements TouchItemInterface {	

	private BaseSequencerItemEditorController sequenceEditorController = new BaseSequencerItemEditorController();
	public BaseSequencerItemEditorController getItemEditorController() {
		return this.sequenceEditorController;
	}
	
	private QWidget parentWidget;
	private lazerdoom lzrdm;
	
	private static SequencerView instance;
	
	public static SequencerView getInstance() {
		return SequencerView.instance;
	}
	
	private void setupSynthEventDispatch() {
		Intercom.getInstance().system.synthEventDispatch.synthEventCom.executeSignal.connect(this, "propagateSynthEvent(Object)");
	}
	
	ConcurrentLinkedQueue<SynthEventContainer> propagateSynthEvent = new ConcurrentLinkedQueue<SynthEventContainer>(); 
	public void propagateSynthEvent(Object container) {
		SynthEventContainer sec = (SynthEventContainer) container;
		
		//propagateSynthEvent.add(sec);
		sec.syneli.dispatchSynthEvent(sec.event);
		//System.err.println("GUI--eve "+sec.event.getEvent());
	}
	
	private void setupSequenceEventDispatch() {
		Intercom.getInstance().system.sequenceEventDispatch.sequenceEvalCom.executeSignal.connect(this, "propagateSequenceEval(Object)");
		Intercom.getInstance().system.sequenceEventDispatch.sequenceEventCom.executeSignal.connect(this, "propagateSequenceEvent(Object)");
	}
	
	public void propagateSequenceEvent(Object container) {
		SequenceEventContainer sc = (SequenceEventContainer) container;
		sc.seli.dispatchSequenceEvent(sc.se);
	}
	
	private void updateGfx() {
		for(SynthEventContainer cnt: propagateSynthEvent) {
			cnt.syneli.dispatchSynthEvent(cnt.event);
		}
		propagateSynthEvent.clear();
		
		this.viewport().update();
	}
	
	public void propagateSequenceEval(Object container) {
		SequenceEventContainer sc = (SequenceEventContainer) container;
		sc.svali.dispatchEvalEvent(sc.tick);
	}
	
	private void setupIntercomGestureIntput() {
		Intercom.getInstance().system.gestureInput.groupIDXBar.executeSignal.connect(this, "getGroupIDSlot(Object, Object)");
		Intercom.getInstance().system.gestureInput.allowedGesturesXBar.executeSignal.connect(this, "getAllowedGesturesSlot(Object, Object)");
		Intercom.getInstance().system.gestureInput.processEventCom.executeSignal.connect(this, "processEventSlot(Object)");
	}
	
	private void processEventSlot(Object processEvent) {
		ProcessEvent pE = (ProcessEvent) processEvent;
		this.processEventLocalThread(pE.id, pE.event);
	}
	
	private void getGroupIDSlot(Object container, Object loc) {
		System.err.println(container+" "+loc);
		int retVal = this.getGroupIDLocalThread((Location)loc);
		
		Intercom.getInstance().system.gestureInput.groupIDXBar.post(container, retVal);
	}
	
	private void getAllowedGesturesSlot(Object container, Object id) {
		List<Integer> ret = this.getAllowedGesturesLocalThread((Integer) (id));
		
		Intercom.getInstance().system.gestureInput.allowedGesturesXBar.post(container, ret);
	}
	
	public static QGLWidget sharedGlWidget;
	private LinkedList<TouchableEditor> editors = new LinkedList<TouchableEditor>();
	
	public void registerEditor(TouchableEditor editor) {
		editors.add(editor);
	}
	
	public void unregisterEditor(TouchableEditor editor) {
		editors.remove(editor);
	}
	
	private HashMap<Integer, TouchItemInterface> touchItemGroupIDMap = new HashMap<Integer, TouchItemInterface>();
	
	
	/**
	 * 
	 * for sparshui interface
	 *
	 */
	
	private int viewGroupID = 1;
	private LinkedList<Integer> viewGestures = new LinkedList<Integer>();
	
	private LinkedList<SequencerMenuButton> menuItems = new LinkedList<SequencerMenuButton>();
	
	private void createMenuItems() {
		double margin = 10;
		
		QGraphicsScene scene = this.scene();
		
		//basic size
		QRectF sceneRect = new QRectF(0,0,2000,2000);//scene.sceneRect();
		
		SequencerMenuButton button1 = new SequencerMenuButton(ActionType.addSequence);
		SequencerMenuButton button2 = new SequencerMenuButton(ActionType.addSynth);
		SequencerMenuButton button3 = new SequencerMenuButton(ActionType.addSequencePlayer);
		SequencerMenuButton button4 = new SequencerMenuButton(ActionType.addEditor);
				
		QRectF buttonRect = button1.boundingRect();
		double verticalOffset = (sceneRect.height() - (buttonRect.height()*4))/2;
		
		button1.setPos(buttonRect.width(),verticalOffset);
		button2.setPos(buttonRect.width(),verticalOffset+margin+buttonRect.height());
		button3.setPos(buttonRect.width(),verticalOffset+2*(margin+buttonRect.height()));
		button4.setPos(buttonRect.width(),verticalOffset+3*(margin+buttonRect.height()));
		
		
		button1.setParent(this);
		button2.setParent(this);
		button3.setParent(this);
		button4.setParent(this);
		
		scene.addItem(button1);
		scene.addItem(button2);
		scene.addItem(button3);
		scene.addItem(button4);
		
		menuItems.add(button1);
		menuItems.add(button2);
		menuItems.add(button3);
		menuItems.add(button4);

		button4 = new SequencerMenuButton(ActionType.addSequence);
		button3 = new SequencerMenuButton(ActionType.addSynth);
		button2 = new SequencerMenuButton(ActionType.addSequencePlayer);
		button1 = new SequencerMenuButton(ActionType.addEditor);
		
		button1.setPos(sceneRect.width()-buttonRect.width(),verticalOffset);
		button2.setPos(sceneRect.width()-buttonRect.width(),verticalOffset+margin+buttonRect.height());
		button3.setPos(sceneRect.width()-buttonRect.width(),verticalOffset+2*(margin+buttonRect.height()));
		button4.setPos(sceneRect.width()-buttonRect.width(),verticalOffset+3*(margin+buttonRect.height()));
		
		button1.rotate(180.0);
		button2.rotate(180.0);
		button3.rotate(180.0);
		button4.rotate(180.0);

		button1.setParent(this);
		button2.setParent(this);
		button3.setParent(this);
		button4.setParent(this);

		
		scene.addItem(button1);
		scene.addItem(button2);
		scene.addItem(button3);
		scene.addItem(button4);
		
		menuItems.add(button1);
		menuItems.add(button2);
		menuItems.add(button3);
		menuItems.add(button4);
	}
	
	private SequencerEditor sequencerEditor;
	
	public SequencerView(SequencerEditor editor, lazerdoom lzrdm) {
		System.err.println("CPUs: "+Runtime.getRuntime().availableProcessors());
		
		
		this.sequencerEditor = editor;
		this.lzrdm = lzrdm;
		
		/*
		 * Gestures supported by the view
		 */
		
		viewGestures.add(sparshui.gestures.GestureType.TOUCH_GESTURE.ordinal());
		viewGestures.add(sparshui.gestures.GestureType.GROUP_GESTURE.ordinal());
		viewGestures.add(sparshui.gestures.GestureType.DRAG_GESTURE.ordinal());
		viewGestures.add(sparshui.gestures.GestureType.DELETE_GESTURE.ordinal());
		
		//viewGestures.add(sparshui.gestures.GestureType.ZOOM_GESTURE2D.ordinal());
		
		if(LazerdoomConfiguration.getInstance().enableOpenGl) {
			this.setCacheMode(QGraphicsView.CacheModeFlag.CacheNone);
			SequencerView.sharedGlWidget = new QGLWidget();
			this.setViewport(SequencerView.sharedGlWidget);
		}
		this.setViewportUpdateMode(ViewportUpdateMode.NoViewportUpdate);
		
		
		SequencerView.instance = this;
		
		this.setRenderHint(LazerdoomConfiguration.getInstance().viewRenderHint);
		
		this.registerTouchItem(this);
	
		/*SequenceEventThread st = new SequenceEventThread();
		QThread sequenceEventThread = new QThread(st);
		sequenceEventThread.start();
		st.moveToThread(sequenceEventThread);*/

		this.setupIntercomGestureIntput();
		this.setupSequenceEventDispatch();
		this.setupSynthEventDispatch();
		
		// Fixed framerate
		//updateGuiTimer.timeout.connect(this, "updateGfx()");
		updateGuiTimer.timeout.connect(this.viewport(), "update()");
		updateGuiTimer.start(1000/(LazerdoomConfiguration.getInstance().frameRate));
	}
	
	private QTimer updateGuiTimer = new QTimer();
	
	public void setUp(double scale) {
		QRectF sceneRect = this.scene().sceneRect();
		createMenuItems();
		this.centerOn(new QPointF(sceneRect.height()/2, sceneRect.width()/2));
		this.scale(scale, scale);
		this.viewport().setSizePolicy(Policy.Maximum, Policy.Maximum);
		this.setSceneRect(this.scene().sceneRect());
		
		/*ControllerItem cc = new ControllerItem();
		this.scene().addItem(cc);
		cc.setPos(600,600);*/
		
		//this.scene().setItemIndexMethod(ItemIndexMethod.NoIndex);
		/*
		
		
		SequenceItem si1 = new SequenceItem(true);
		si1.setPos(500,500);
		this.scene().addItem(si1);
		
		SequenceItem si2;
		si2 = new SequenceItem(false);
		si2.setPos(700,500);
		this.scene().addItem(si2);
		
		SequenceItem si3;
		si3 = new SequenceItem(false);
		si3.setPos(300,500);
		this.scene().addItem(si3);
		
		SequenceItem si4;
		si4 = new SequenceItem(false);
		si4.setPos(300,300);
		this.scene().addItem(si4);
		
		SequenceItem si5;
		si5 = new SequenceItem(false);
		si5.setPos(300,700);
		this.scene().addItem(si5);
		
		SequenceItem si6;
		si6 = new SequenceItem(false);
		si6.setPos(200,200);
		this.scene().addItem(si6);
		
		SequencePlayerItem player;
		player = new SequencePlayerItem();
		player.setPos(800,800);
		this.scene().addItem(player);
		
		SynthesizerItem synth;
		synth = new SynthesizerItem(new String[]{"A", "B", "C"});
		
		synth.setPos(500,800);
		this.scene().addItem(synth);
	
		EditorCursor ec = new EditorCursor();
		ec.setPos(1000,1000);
		this.scene().addItem(ec);
		
		this.scene().addItem(new SequenceConnection( player.getSequenceOutConnector(), si2.getSequenceInConnector()));
		this.scene().addItem(new SequenceConnection( si1.getSequenceOutConnector(), si6.getSequenceInConnector()));
		this.scene().addItem(new SequenceConnection( si3.getSequenceOutConnector(), si5.getSequenceInConnector()));
		this.scene().addItem(new SequenceConnection( si3.getSequenceOutConnector(), si1.getSequenceInConnector()));
		this.scene().addItem(new SequenceConnection( si2.getSequenceOutConnector(), si3.getSequenceInConnector()));
		this.scene().addItem(new SequenceConnection( si2.getSequenceOutConnector(), si4.getSequenceInConnector()));
		this.scene().addItem(new SynthConnection(synth.getSynthInConnectors().get(1), si4.getSynthOutConnectors().get(0)));
		this.scene().addItem(new SynthConnection(synth.getSynthInConnectors().get(0), si2.getSynthOutConnectors().get(0)));
		this.scene().addItem(new SynthConnection(synth.getSynthInConnectors().get(2), si3.getSynthOutConnectors().get(0)));*/
		//this.scene().addItem(new SynthConnection(synth.getSynthInConnectors().get(3), si6.getSynthOutConnectors().get(0)));
		//this.scene().addItem(new MulticontrolItem());
	
		
		
		/*QGraphicsLinearLayout baseLayout = new QGraphicsLinearLayout() {
			public String toString() { return "BNGGGFFGGF"; }
		};
		baseLayout.setOrientation(Orientation.Vertical);
		QGraphicsLinearLayout layout = new QGraphicsLinearLayout();
		layout.setOrientation(com.trolltech.qt.core.Qt.Orientation.Horizontal);
		PushButton button = new PushButton("<");
		TextLabel button1 = new TextLabel("SynthesizerOne");
		PushButton button2 = new PushButton(">");
		TouchableGraphicsItemContainer container = new TouchableGraphicsItemContainer();
		container.setItem(button);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(button1);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(button2);
		layout.addItem(container);
		baseLayout.addItem(layout);
		
		layout = new QGraphicsLinearLayout();
		
		Slider slider1 = new Slider();
		Slider slider2 = new Slider();
		Slider slider3 = new Slider();
		Slider slider4 = new Slider();
		Slider slider5 = new Slider();
		Slider slider6 = new Slider();
		Slider slider7 = new Slider();
		Slider slider8 = new Slider();
		
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider1);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider2);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider3);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider4);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider5);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider6);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider7);
		layout.addItem(container);
		container = new TouchableGraphicsItemContainer();
		container.setItem(slider8);
		layout.addItem(container);

		baseLayout.addItem(layout);*/
		//QGraphicsProxyWidget x = new QGraphicsProxyWidget();
		//QGraphicsScene sc = new QGraphicsScene();
		//TouchableGraphicsView gv = new TouchableGraphicsView();
		//gv.setScene(sc);
		//gv.setSizeIncrement(500, 800);
		//x.setWidget(new QGraphicsView());

		/*EventPointsSequence<DoubleType> s = new EventPointsSequence<DoubleType>(new TestingSequencer());
		s.insert(new DoubleType(0.5), 100);
		s.insert(new DoubleType(-0.5), 200);
		s.insert(new DoubleType(1.0), 300);
		s.insert(new DoubleType(-1.0), 400);
		s.insert(new DoubleType(0.3), 500);
		s.insert(new DoubleType(0.2), 600);
		s.insert(new DoubleType(-0.1), 700);
		s.insert(new DoubleType(0.3), 800);
		
		SequenceDataEditorScene<DoubleType> sc = new SequenceDataEditorScene<DoubleType>();
		SequenceDataEditor<DoubleType> e = new SequenceDataEditor<DoubleType>(sc, s);
		TouchableEditor editor = new TouchableEditor();
		editor.setScene(sc);
		QFrame frame = new QFrame();
		QGraphicsView fview = new QGraphicsView(frame);
		fview.setScene(sc);
		this.scene().addItem(editor);
		editor.setPos(800, 800);
		this.registerEditor(editor);*/
		//SynthesizerEditor synthEd = new SynthesizerEditor();
		//TouchableEditor editor = new TouchableEditor();
		//editor.setCurrentEditor(synthEd);
		//this.scene().addItem(editor);
		//editor.setPos(500,500);
		//this.registerEditor(editor);
		//this.update();
	}
		
	public void registerTouchItem(TouchItemInterface it) {
		touchItemGroupIDMap.put(it.getGroupID(), it);
	}
	
	public void unregisterTouchItem(TouchItemInterface it) {
		touchItemGroupIDMap.remove(it.getGroupID());
	}
	
	public List<Integer> getAllowedGesturesLocalThread(int id) {
		TouchItemInterface it = null;
		List<Integer> ret = null;
		
		System.out.println(id +" + "+viewGroupID);
		
		if(id == viewGroupID) {
			ret = viewGestures;
		} else if((it = touchItemGroupIDMap.get(id)) != null) {
			System.out.println("WHAT?");
			ret = it.getAllowedGestures();
		}
		
		return ret;
	}
	
	public int getGroupIDLocalThread(Location pos) {
		double x = pos.getX();
		double y = pos.getY();
		int getGroupIDReturnValue = 0;
		
		int retVal = -1;
		
		QGraphicsItemInterface it = this.scene().itemAt(convertScreenPos(x,y));

		if(it != null && it instanceof TouchItemInterface) {
			getGroupIDReturnValue = ((TouchItemInterface)it).getGroupID();
			
			if(!touchItemGroupIDMap.containsKey(getGroupIDReturnValue)) {
				this.registerTouchItem(((TouchItemInterface)it));
			}
			
		} else {
			
			for(TouchableEditor editor: editors) {
				System.out.println("size "+editors);
				QPolygonF poly = editor.mapToScene(editor.boundingRect());
				QPointF point = convertScreenPos(x,y);
				//System.out.println(poly.containsPoint(possy, FillRule.OddEvenFill));
				//System.out.println(possy+" LOLOLOL " +poly.contains(convertScreenPos(x,y))+" "+poly+" kldkdk "+editor.boundingRect()+" üps "+editor.pos());
				System.out.println("editor");
				if(poly.containsPoint(point, FillRule.OddEvenFill)) {
					System.out.println(editor);
					Pair<Object> pair = editor.getGroupIDViewCoordinates(convertScreenPos(x,y));
					
					if(pair != null && pair.getSecond() != null) {
						retVal = (Integer) pair.getFirst();
						TouchItemInterface editorTouchItem = (TouchItemInterface) pair.getSecond(); 
						System.out.println("YES EDITOR "+retVal);
						if(!touchItemGroupIDMap.containsKey(retVal)) {
							System.out.println("REGISTERED EDITOR");
							this.touchItemGroupIDMap.put(retVal, editorTouchItem);
							//						this.registerTouchItem(((TouchItemInterface)editor));
						}
					}
				}
			} 
			
			if(retVal == -1) {
				getGroupIDReturnValue = this.viewGroupID;
			} else {
				getGroupIDReturnValue = retVal;
			}
		}
		//tect.setGroupID(tc, getGroupIDReturnValue);
		
		return getGroupIDReturnValue;
	}
	
	public QPointF convertScreenPos(double x, double y) {
		x = x*this.viewport().width()*(LazerdoomConfiguration.getInstance().hStretch);
		y = y*this.viewport().height()*(LazerdoomConfiguration.getInstance().vStretch);
		return this.mapToScene((int)x, (int)y);
	}
	
	
	HashMap<TouchItemInterface, Pair<Integer>> filteredEvents = new HashMap<TouchItemInterface, Pair<Integer>>();
	public void focusCurrentEvent(TouchItemInterface item, Event event) {
		if(event.isOngoing()) {
			filteredEvents.put(item, new Pair(new Integer(event.getEventType()), new Integer(event.getTouchID())));
		}
	}
	
	private boolean filterEvent(TouchItemInterface item, Event event) {
		Pair<Integer> p = null;
		
		if((p = filteredEvents.get(item)) != null) {
			Integer type =  p.getFirst();
			Integer id = p.getSecond();

			if(event.getTouchID() == id) {
				if(event.getEventType() != type) {
					// filter event
					return true;
				} else {
					event.setFocused();
				}
			} else {
				// new id -> new event -> remove from map
				filteredEvents.remove(item);
			}
		} 
		
		return false;
	}
	
	private void processEventLocalThread(Integer id, Event event) {
		TouchItemInterface it = null;
		if((it  = touchItemGroupIDMap.get(id)) != null) {
			
			if(!filterEvent(it, event)) {
				//System.out.println(event.isFocused()+"isFocused");
				//System.out.print("EVENt tYPE "+event.getEventType()+" eee "+event.getTouchID()+" ongoing "+event.isOngoing());

				if(event instanceof TouchEvent) {

					TouchEvent e = (TouchEvent) event;
					e.setSceneLocation(convertScreenPos(e.getX(), e.getY()));
				} else if(event instanceof DragEvent) {
					DragEvent e = (DragEvent) event;
					e.setSceneLocation(convertScreenPos(e.getRelX(), e.getRelY()));
				} else if(event instanceof RotateEvent) {
					RotateEvent e = (RotateEvent) event;
					e.setSceneLocation(convertScreenPos(e.getCenter().getX(), e.getCenter().getY()));
				} else if(event instanceof DeleteEvent) {
					DeleteEvent e = (DeleteEvent) event;
					e.setSceneLocation(convertScreenPos(e.getRelX(), e.getRelY()));
					
					if(e.isSuccessful()) {
						e.setSceneCrossPoint(convertScreenPos(e.getCrossPoint().x(), e.getCrossPoint().y()));
					}
				}

			/*	if(event instanceof ExtendedGestureEvent) {
					ExtendedGestureEvent e = (ExtendedGestureEvent) event;
					e.setSceneLocation(convertScreenPos(e.getRelX(), e.getRelY()));
				}*/

				//System.out.println(it);
				it.processEvent(event);
			}
		}
	}
	
	
	
	private QColor bgColor1 = new QColor(255,255,255);//new QColor(38,50,62);
	private QColor bgColor2 = new QColor(255,255,255);//new QColor(bgColor1.lighter(155));
	
	
	private QBrush currentBG = new QBrush(new QColor(38,50,62));
	
    protected void drawBackground(QPainter painter, QRectF rect) {
        // Fill
        
    	//QRadialGradient gradient = new QRadialGradient(0,0,rect.width());
        //gradient.setColorAt(0, bgColor2);
        //gradient.setColorAt(1, bgColor1);
        
    	painter.fillRect(rect, currentBG);// new QBrush(gradient));
    }

	@Override
	public List<Integer> getAllowedGestures() {
		// dummy
		return this.viewGestures;
	}

	@Override
	public int getGroupID() {
		return this.viewGroupID;
	} 

	@Override
	public void mousePressEvent(QMouseEvent event) {
		this.lzrdm.handleMousePressEvent(event);
	    super.mousePressEvent(event);
	}
	
	@Override
	public void mouseMoveEvent(QMouseEvent event) {
		this.lzrdm.handleMouseMoveEvent(event);
		super.mouseMoveEvent(event);
	}
	
	@Override
	public void mouseReleaseEvent(QMouseEvent event) {
		this.lzrdm.handleMouseReleaseEvent(event);
		super.mouseReleaseEvent(event);
	}

	
	private void sendMouseEventFromTouchEvent(TouchEvent touchEvent) {
		QPoint mousePoint =  this.mapFromScene(convertScreenPos(touchEvent.getX(), touchEvent.getY()));
		QEvent event;
		
		if(touchEvent.getState() == TouchState.BIRTH) {
			event = new QMouseEvent(Type.MouseButtonPress, new QPoint((int)mousePoint.x(), (int)mousePoint.y()), MouseButton.LeftButton, new Qt.MouseButtons() , KeyboardModifier.NoModifier);
		} else if(touchEvent.getState() == TouchState.MOVE) {
			event = new QMouseEvent(Type.MouseMove, new QPoint((int)mousePoint.x(), (int)mousePoint.y()), MouseButton.LeftButton, new Qt.MouseButtons() , KeyboardModifier.NoModifier);
		} else {
			event = new QMouseEvent(Type.MouseButtonRelease, new QPoint((int)mousePoint.x(), (int)mousePoint.y()), MouseButton.LeftButton, new Qt.MouseButtons() , KeyboardModifier.NoModifier);
		}
		
		QApplication.sendEvent(this.parentWidget, event);
	}
	
	public SequencerEditor getSequencerEditor() {
		return this.sequencerEditor;
	}
	
	QGraphicsEllipseItem ellipse = null;
	@Override
	public boolean processEvent(Event event) {
		if(event instanceof ExtendedGestureEvent) {
			ExtendedGestureEvent e = (ExtendedGestureEvent) event;
			e.setSceneLocation(convertScreenPos(e.getRelX(), e.getRelY()));
			if(e instanceof DeleteEvent) {
				if(((DeleteEvent) e).getCrossPoint() != null) {
					((DeleteEvent) e).setSceneCrossPoint(convertScreenPos(((DeleteEvent) e).getCrossPoint().x(),((DeleteEvent) e).getCrossPoint().y()));
				}
			}
			
			this.sequencerEditor.handleExtendedGestureEvent(e);
			this.update();

		} else if(event instanceof TouchEvent) {
			//System.out.println("tp ");
			TouchEvent e = (TouchEvent) event;
			e.setSceneLocation(convertScreenPos(e.getX(), e.getY()));
			this.sequencerEditor.handleTouchEvent(e);
			this.update();
			//System.out.println("WHA?");
		} 
		/*if(event instanceof DragEvent) {
			DragEvent de = (DragEvent) event;
			
			TouchableGraphicsItem tii;
			
			if((tii = (TouchableGraphicsItem) de.getSource()) != null && tii.getParent() == this) {
				System.out.println(">***");
				System.out.println(de);
				System.out.println(de.getTouchID());
				System.out.println(de.isDrop());
				System.out.println("***>");
			}
		}*/
		return false;
	}
}
