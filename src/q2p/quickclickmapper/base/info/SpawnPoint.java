package q2p.quickclickmapper.base.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.quickclickmapper.Mapper;

public class SpawnPoint extends MapPoint {
	private byte rotation; /* -135 -90 -45 0 45 90 135 180 equals 0 1 2 3 4 5 6 7 */
	
	public SpawnPoint(short position[], float[] offset, int rotation) {
		super(position, offset);
		this.rotation = (byte)(rotation/45+3);
		if(this.rotation < 0 || this.rotation > 7) {
			this.rotation = 0;
		}
	}
	
	public SpawnPoint(DataInputStream dis) throws IOException {
		super(dis);
		rotation = dis.readByte();
	}
	
	public int getRotation() {
		return (rotation-3)*45;
	}

	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeByte(rotation);
	}
	
	public static SpawnPoint getBySign(SignData data) {
		if(!data.lines[0].equals("spawn")) return null;
		int rotation;
		try { rotation = Integer.parseInt(data.lines[1]); }
		catch(Exception e) { rotation = 0; }
		return new SpawnPoint(data.position, Mapper.threeFloatArgumentsFromLine(data.lines[2]), rotation);
	}

	public SignData toSign() {
		return new SignData(position, new String[]{"spawn", ""+getRotation(), Mapper.byteArrayToFloatString(offset), ""});
	}
}