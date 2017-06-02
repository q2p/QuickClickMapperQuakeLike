package q2p.quickclickmapper.base.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.quickclickmapper.Mapper;

public class LaunchPad extends MapArea {
	public byte[] velocity;
	
	public LaunchPad(short[] position, byte[] size, float[] velocity) {
		super(position, size);
		this.velocity = new byte[]{(byte)Math.round(velocity[0]*2), (byte)Math.round(velocity[1]*2), (byte)Math.round(velocity[2]*2)};
	}
	
	public LaunchPad(DataInputStream dis) throws IOException {
		super(dis);
		velocity = new byte[]{dis.readByte(), dis.readByte(), dis.readByte()};
	}

	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		for(byte i = 0; i != 3; i++) dos.writeByte(velocity[i]);
	}

	public static LaunchPad getBySign(SignData data) {
		if(!data.lines[0].equals("launch")) return null;
		return new LaunchPad(data.position, Mapper.threeByteArgumentsFromLine(data.lines[1]), Mapper.threeFloatArgumentsFromLine(data.lines[2]));
	}

	public SignData toSign() {
		return new SignData(position, new String[] {"launch", size[0] + " " + size[2], Mapper.byteArrayToFloatString(velocity),""});
	}
}
