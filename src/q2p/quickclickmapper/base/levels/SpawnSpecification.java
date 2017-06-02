package q2p.quickclickmapper.base.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.quickclickmapper.base.info.SignData;

public class SpawnSpecification implements LevelSpecification {
	short[] spawnPosition = null;
	public void writeInfo(DataOutputStream dos) throws IOException {
		for(byte i = 0; i < 3; i++) dos.writeShort(spawnPosition[i]);
	}
	public void readInfo(DataInputStream dis, String fileName) throws IOException {
		spawnPosition = new short[]{dis.readShort(), dis.readShort(), dis.readShort()};
	}
	public boolean compare(String string) {
		return string.equals("spawn");
	}
	public String getFileName() {
		return "spawn";
	}
	public void parseSign(SignData data) {
		if(data.lines[0].equals("spawn")) spawnPosition = new short[] {data.position[0], data.position[1], data.position[2]};
	}
	public ArrayList<SignData> createSigns() {
		ArrayList<SignData> datas = new ArrayList<SignData>();
		datas.add(new SignData(spawnPosition, new String[]{"spawn","","",""}));
		return datas;
	}
	public boolean parseMeta(String line1, String line2, String line3) {
		return true;
	}
	public String[] sendMeta() {
		return new String[]{"spawn","","",""};
	}
	public void cleanUp() {
		spawnPosition = null;
	}
	public void postSign() {}
}