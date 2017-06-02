package q2p.quickclickmapper.base.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.quickclickmapper.Mapper;

public class PortalArea extends MapArea {
	public byte destinationId;
	
	public PortalArea(short[] position, byte[] size, byte destinationId) {
		super(position, size);
		this.destinationId = destinationId;
	}
	
	public PortalArea(DataInputStream dis) throws IOException {
		super(dis);
		destinationId = dis.readByte();
	}

	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeByte(destinationId);
	}
	
	public static PortalArea getBySign(SignData data) {
		if(data.lines[0].startsWith("portal ")) {
			byte dest;
			try { dest = Byte.parseByte(data.lines[0].substring("portal ".length())); }
			catch(Exception e) { dest = 1; }
			int[] size = Mapper.threeIntegerArgumentsFromLine(data.lines[1]);
			return new PortalArea(data.position, new byte[]{(byte)size[0],(byte)size[1],(byte)size[2]}, dest);
		}
		return null;
	}

	public SignData toSign() {
		return new SignData(position, new String[] {"portal "+(destinationId+1), Mapper.byteArrayToIntegerString(size), "", ""});
	}
}
