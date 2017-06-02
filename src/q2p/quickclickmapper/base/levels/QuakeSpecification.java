package q2p.quickclickmapper.base.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import q2p.quickclickmapper.Mapper;
import q2p.quickclickmapper.base.info.LaunchPad;
import q2p.quickclickmapper.base.info.PickUp;
import q2p.quickclickmapper.base.info.PortalArea;
import q2p.quickclickmapper.base.info.PortalDestination;
import q2p.quickclickmapper.base.info.SignData;
import q2p.quickclickmapper.base.info.SpawnPoint;

public class QuakeSpecification implements LevelSpecification {
	String displayName = "";
	String fileName = "";
	ArrayList<SpawnPoint> spawns = new ArrayList<SpawnPoint>();
	ArrayList<PickUp> pickUps = new ArrayList<PickUp>();
	ArrayList<PortalArea> portals = new ArrayList<PortalArea>();
	ArrayList<PortalDestination> destinations = new ArrayList<PortalDestination>();
	ArrayList<LaunchPad> launchPads = new ArrayList<LaunchPad>();
	
	public void writeInfo(DataOutputStream dos) throws IOException {
		byte[] buff = displayName.getBytes(StandardCharsets.UTF_8);
		dos.writeByte(buff.length);
		dos.write(buff);

		dos.writeByte(spawns.size());
		while(!spawns.isEmpty()) spawns.remove(0).write(dos);
		dos.writeByte(pickUps.size());
		while(!pickUps.isEmpty()) pickUps.remove(0).write(dos);
		dos.writeByte(portals.size());
		while(!portals.isEmpty()) portals.remove(0).write(dos);
		dos.writeByte(destinations.size());
		while(!destinations.isEmpty()) destinations.remove(0).write(dos);
		dos.writeByte(launchPads.size());
		while(!launchPads.isEmpty()) launchPads.remove(0).write(dos);
	}
	public void readInfo(DataInputStream dis, String fileName) throws IOException {
		this.fileName = fileName;
		byte[] buff = new byte[dis.readByte()];
		dis.read(buff);
		displayName = new String(buff, StandardCharsets.UTF_8);
		
		for(int i = dis.readByte(); i != 0; i--) spawns.add(new SpawnPoint(dis));
		for(int i = dis.readByte(); i != 0; i--) pickUps.add(new PickUp(dis));
		for(int i = dis.readByte(); i != 0; i--) portals.add(new PortalArea(dis));
		for(int i = dis.readByte(); i != 0; i--) destinations.add(new PortalDestination(dis));
		for(int i = dis.readByte(); i != 0; i--) launchPads.add(new LaunchPad(dis));
	}
	public boolean compare(String string) {
		return string.equals("quake");
	}
	public String getFileName() {
		return fileName;
	}
	public void parseSign(SignData data) {
		Object info;
		if((info = SpawnPoint.getBySign(data))!=null) spawns.add((SpawnPoint)info);
		else if((info = PickUp.getBySign(data))!=null) pickUps.add((PickUp)info);
		else if((info = PortalArea.getBySign(data))!=null) portals.add((PortalArea)info);
		else if((info = PortalDestination.getBySign(data))!=null) destinations.add((PortalDestination)info);
		else if((info = LaunchPad.getBySign(data))!=null) launchPads.add((LaunchPad)info);
	}
	public ArrayList<SignData> createSigns() {
		ArrayList<SignData> datas = new ArrayList<SignData>();
		while(!spawns.isEmpty()) datas.add(spawns.remove(0).toSign());
		while(!pickUps.isEmpty()) datas.add(pickUps.remove(0).toSign());
		while(!portals.isEmpty()) datas.add(portals.remove(0).toSign());
		byte i = 0;
		while(!destinations.isEmpty()) {
			datas.add(destinations.remove(0).toSign(i));
			i++;
		}
		while(!launchPads.isEmpty()) datas.add(launchPads.remove(0).toSign());
		return datas;
	}
	public boolean parseMeta(String line1, String line2, String line3) {
		// TODO: lowercase only
		fileName = line1;
		if(!Mapper.isValid(fileName) || fileName.contains(" ")) {
			Bukkit.broadcastMessage("File name can only contains english lowercase letters.");
			return false;
		}
		displayName = line2;
		if(!Mapper.isValid(displayName)) {
			Bukkit.broadcastMessage("File name can only contains english letters and spaces.");
			return false;
		}
		return true;
	}
	public String[] sendMeta() {
		return new String[]{"quake",fileName,displayName,""};
	}
	public void cleanUp() {
		displayName = "";
		fileName = "";
		spawns.clear();
		pickUps.clear();
		portals.clear();
		destinations.clear();
		launchPads.clear();
	}
	public void postSign() {
		for(byte i = 0; i < portals.size(); i++) {
			for(byte j = 0; j < destinations.size(); j++) {
				if(portals.get(i).destinationId == destinations.get(j).id) {
					portals.get(i).destinationId = j;
					break;
				}
			}
		}
	}
}
