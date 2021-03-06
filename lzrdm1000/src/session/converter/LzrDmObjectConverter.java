package session.converter;

import session.SessionHandler;
import lazerdoom.LzrDmObjectInterface;


import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LzrDmObjectConverter implements Converter {

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		Long id = SessionHandler.getInstance().getRegisteredObjectID((LzrDmObjectInterface)obj);

		if(id != null) {
			writer.startNode("lzrdmobjID");
			System.out.println("ID: "+id+" obj "+obj);
			writer.setValue(id.toString());
			writer.endNode();
		} else {
			System.out.println("LzrDmObj-Converter: Unknown obj for SessionHandler! id: "+id+" obj "+obj);
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		
		reader.moveDown();
		Long id = new Long(reader.getValue());
		reader.moveUp();
		
		LzrDmObjectInterface obj = SessionHandler.getInstance().getRegisteredObject(id);
		
		if(obj == null) {
			System.out.println("LzrDmObj-Converter: Unknown id ( "+id+" ) for SessionHandler!");
		}
		
		return obj;
	}

	@Override
	public boolean canConvert(Class clazz) {
		boolean ret = false;
		for(Class interf: clazz.getInterfaces()) {
			if(interf.equals(LzrDmObjectInterface.class)) {
				ret = true;
			}
		}
		return ret;
	}

}
