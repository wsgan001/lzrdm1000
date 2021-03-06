package gui.editor.commands.sequenceeditor;

import sequencer.EventPointsSequence;

import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.core.QPointF;

import control.types.BaseType;
import control.types.DoubleType;

import edu.uci.ics.jung.graph.util.Pair;
import gui.editor.BaseEditorCommand;
import gui.editor.Editor;
import gui.item.editor.sequencedataeditor.TouchableDoubleTypeSequenceDataItem;

public class CreateDoublePointSequenceDataItem<T extends BaseType> extends SequenceEditorCommand {

	private EventPointsSequence<T> sequence;
	private QPointF pos;
	private QGraphicsScene scene;
	private Editor editor;
	private Object object;
	private String slot;
	
	public CreateDoublePointSequenceDataItem(EventPointsSequence<T> sequence, QPointF pos, Editor editor, QGraphicsScene scene, Object object, String slot) {
		super();
		this.sequence = sequence;
		this.pos = pos;
		this.scene = scene;
		this.editor = editor;
		this.object = object;
		this.slot = slot;
	}
	@Override
	public boolean execute() {
		TouchableDoubleTypeSequenceDataItem item = new TouchableDoubleTypeSequenceDataItem(this.editor);
		this.scene.addItem(item);
		item.setPosition(pos);
		item.updateTickAndValueFromPosition(pos);
		Pair<Object> p = item.getTickValuePairFromPosition();
		
		
		(this.sequence).insert((T)p.getSecond(), (Long)p.getFirst());
		item.dragged.connect(this.object, this.slot);

		
		return true;
	}

}
