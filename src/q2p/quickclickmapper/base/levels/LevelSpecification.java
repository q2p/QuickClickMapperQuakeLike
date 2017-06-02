package q2p.quickclickmapper.base.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.quickclickmapper.base.info.SignData;

public interface LevelSpecification {
	public void writeInfo(DataOutputStream dos) throws IOException;
	public void readInfo(DataInputStream dis, String fileName) throws IOException;
	public boolean compare(String string);
	public String getFileName();
	public void parseSign(SignData data);
	public ArrayList<SignData> createSigns();
	public boolean parseMeta(String line1, String line2, String line3);
	public String[] sendMeta();
	public void cleanUp();
	public void postSign();
}
