package q2p.quickclickmapper.base.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.quickclickmapper.Mapper;

public class PortalDestination extends SpawnPoint {
	public byte[] velocity;
	public byte id;
	
	public PortalDestination(short[] position, float[] offset, int rotation, float[] velocity, byte id) {
		super(position, offset, rotation);
		this.velocity = new byte[]{(byte)Math.round(velocity[0]*2), (byte)Math.round(velocity[1]*2), (byte)Math.round(velocity[2]*2)};
		this.id = id;
	}
	
	public PortalDestination(DataInputStream dis) throws IOException {
		super(dis);
		velocity = new byte[]{dis.readByte(), dis.readByte(), dis.readByte()};
	}

	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		for(byte i = 0; i != 3; i++) dos.writeByte(velocity[i]);
	}
	
	public static PortalDestination getBySign(SignData data) {
		if(!data.lines[0].startsWith("dest ")) return null;
		byte id;
		try { id = Byte.parseByte(data.lines[0].substring("dest ".length())); }
		catch(Exception e) { id = 1; }
		int rotation;
		try { rotation = Integer.parseInt(data.lines[1]); }
		catch(Exception e) { rotation = 0; }
		return new PortalDestination(data.position, Mapper.threeFloatArgumentsFromLine(data.lines[2]), rotation, Mapper.threeFloatArgumentsFromLine(data.lines[3]), id);
	}
	
	public SignData toSign(byte id) {
		return new SignData(position, new String[] {"dest "+(id+1), ""+getRotation(), Mapper.byteArrayToFloatString(offset), Mapper.byteArrayToFloatString(velocity)});
	}
}