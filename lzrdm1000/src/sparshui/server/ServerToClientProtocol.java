package sparshui.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import sparshui.common.ClientProtocol;
import sparshui.common.Event;
import sparshui.common.Location;
import sparshui.common.utils.Converter;

/**
 * Represents the server to client connection.
 * 
 * @author Tony Ross
 */
public class ServerToClientProtocol extends ClientProtocol {
	
	private DataOutputStream _bufferOut;
	private ByteArrayOutputStream _buffer;
	
	/**
	 * Constructs a new connection to the client.
	 * 
	 * @param socket
	 * 		The socket that has been opened to the client.
	 */
	public ServerToClientProtocol(Socket socket) throws IOException {
		super(socket);
		_buffer = new ByteArrayOutputStream();
		_bufferOut = new DataOutputStream(_buffer);
	}
	
	/**
	 * Retrieve a list of allowed gestures for the provided group id.
	 * The message sent is of the following format:
	 * 
	 * 1 byte - Event Type
	 * 4 bytes - Message Length
	 * 4 bytes - GroupID
	 * 
	 * @param groupID
	 *            The ID of the group to obtain allowed gestures for
	 * @return A list containing string identifiers for all allowed gestures
	 * @throws IOException
	 *             If an error occurs while communication with the client.
	 */
	public Vector<Integer> getGestures(int groupID) throws IOException {
		Vector<Integer> gestures = new Vector<Integer>();
		sendType(MessageType.GET_ALLOWED_EVENTS);
		
		_bufferOut.writeInt(groupID);
		sendBuffer();
		
		int length = _in.readInt();
		while(length > 0) {
			int gestureID = _in.readInt();
			gestures.add(gestureID);
			length -= 4;
		}
		return gestures;
	}
	
	
	/*
	public Vector<String> getGestures(int groupID) {
		GetGestures msg = new GetGestures(groupID);
		GesturesMessage response = msg.send(_in, _out);
		return new Vector<String>(Arrays.asList(response.getGestures()));
	}
	*/
	
	/**
	 * Retrieve a list of allowed gestures for the provided group id.
	 * The message sent is of the following format:
	 * 
	 * 1 byte - Event Type
	 * 4 bytes - Message Length
	 * 4 bytes - x coordinate
	 * 4 bytes - y coordinate
	 * 
	 * @param location
	 * 			The data point for the group to retrieve from
	 * @return
	 * 			The Group ID
	 * @throws IOException
	 *          If an error occurs during communication with the client.
	 */
	public int getGroupID(Location location) throws IOException {
		byte[] tempFloat = new byte[4];
		// Event Type
		sendType(MessageType.GET_GROUP_ID);
		
		// X coordinate
		float x = location.getX();
		int xbits = Float.floatToIntBits(x);
		tempFloat = Converter.intToByteArray(xbits);
		_bufferOut.write(tempFloat);
		
		// Y coordinate
		float y = location.getY();
		int ybits = Float.floatToIntBits(y);
		tempFloat = Converter.intToByteArray(ybits);
		_bufferOut.write(tempFloat);

		// Send the length, x and y coordinates
		sendBuffer();
		
		// Get the Group ID
		int ret = _in.readInt();
		return ret;
	}
	
	/*
	public int getGroupID(Location location) {
		GetGroupID msg = new GetGroupID(location.getX(), location.getY());
		GroupIDMessage response = msg.send(_in, _out);
		return response.getGroupID();
	}
	*/
	
	/**
	 * Instruct the client to process the events that have
	 * been generated by a group.
	 * @param groupID
	 * 		The group ID these events have been generated on.
	 * @throws IOException 
	 * 		If there is a communication error.
	 */
	public void processEvents(int groupID, Vector<Event> events) throws IOException {
		for(Event event : events) {
			sendType(MessageType.EVENT);
			_bufferOut.writeInt(groupID);
			_bufferOut.write(event.serialize());
			sendBuffer();
		}
	}
	
	
	/**
	 * 
	 */
	/*
	public void processEvents(int groupID, Vector<Event> events) {
		ProcessEvents msg = new ProcessEvents(groupID, events.toArray(new Event[0]));
		msg.send(_in, _out);
	}
	*/
	
	/**
	 * 
	 * @throws IOException
	 */
	
	private void sendBuffer() throws IOException {
		_out.writeInt(_buffer.size()); // Message length (excluding type)
		_out.write(_buffer.toByteArray()); // Message contents
		_buffer.reset();
	}
	
	
	/**
	 * 
	 * @param type
	 * @throws IOException 
	 */
	
	private void sendType(MessageType type) throws IOException {
		byte data = (byte)(type.ordinal());
		_out.writeByte(data);
	}
	

}
