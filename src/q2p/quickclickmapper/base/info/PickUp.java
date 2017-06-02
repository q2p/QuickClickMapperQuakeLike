package q2p.quickclickmapper.base.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.quickclickmapper.Mapper;

public class PickUp extends MapPoint {
	public static final byte TYPE_HEALTH = 0;
	public static final byte TYPE_ARMOR = 1;
	public static final byte TYPE_GUN = 2;
	public static final byte TYPE_AMMO = 3;

	public static final byte HEALTH_ARMOR_5 = 0;
	public static final byte HEALTH_ARMOR_25 = 1;
	public static final byte HEALTH_ARMOR_50 = 2;
	public static final byte HEALTH_ARMOR_100 = 3;
	
	public static final byte GUN_AMMO_MACHINE = 0;
	public static final byte GUN_AMMO_SHOTGUN = 1;
	public static final byte GUN_AMMO_ROCKET = 2;
	public static final byte GUN_AMMO_PLASMA = 3;
	public static final byte GUN_AMMO_THUNDER = 4;
	public static final byte GUN_AMMO_RAIL = 5;
	
	public byte type = 0;
	public byte subType = 0;
	
	public PickUp(short[] position, float[] offset, byte type, byte subType) {
		super(position, offset);
		this.type = type;
		this.subType = subType;
	}
	
	public PickUp(DataInputStream dis) throws IOException {
		super(dis);
		type = dis.readByte();
		subType = dis.readByte();
	}

	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeByte(type);
		dos.writeByte(subType);
	}

	
	public static PickUp getBySign(SignData data) {
		if(data.lines[0].equals("hp") || data.lines[0].equals("armor")) {
			byte sub = PickUp.HEALTH_ARMOR_5;
			switch(data.lines[1]) {
			case "25": sub = PickUp.HEALTH_ARMOR_25; break;
			case "50": sub = PickUp.HEALTH_ARMOR_50; break;
			case "100": sub = PickUp.HEALTH_ARMOR_100; break;
			}
			return new PickUp(data.position, Mapper.threeFloatArgumentsFromLine(data.lines[2]), data.lines[0].equals("hp")?PickUp.TYPE_HEALTH:PickUp.TYPE_ARMOR, sub);
		} else if(data.lines[0].equals("gun") || data.lines[0].equals("ammo")) {
			byte sub = PickUp.GUN_AMMO_MACHINE;
			switch(data.lines[1]) {
			case "shotgun": sub = PickUp.GUN_AMMO_SHOTGUN; break;
			case "rocket": sub = PickUp.GUN_AMMO_ROCKET; break;
			case "plasma": sub = PickUp.GUN_AMMO_PLASMA; break;
			case "thunder": sub = PickUp.GUN_AMMO_THUNDER; break;
			case "rail": sub = PickUp.GUN_AMMO_RAIL; break;
			}
			return new PickUp(data.position, Mapper.threeFloatArgumentsFromLine(data.lines[2]), data.lines[0].equals("gun")?PickUp.TYPE_GUN:PickUp.TYPE_AMMO, sub);
		}
		return null;
	}

	public SignData toSign() {
		String typeS = "hp";
		switch(type) {
		case TYPE_ARMOR: typeS = "armor"; break;
		case TYPE_GUN: typeS = "gun"; break;
		case TYPE_AMMO: typeS = "ammo";
		}
		String sub = "unknown";
		if(type == TYPE_HEALTH || type == TYPE_ARMOR) {
			switch(subType) {
			case HEALTH_ARMOR_5: sub = "5"; break;
			case HEALTH_ARMOR_25: sub = "25"; break;
			case HEALTH_ARMOR_50: sub = "50"; break;
			case HEALTH_ARMOR_100: sub = "100";
			}
		} else {
			switch(subType) {
			case GUN_AMMO_MACHINE: sub = "machine"; break;
			case GUN_AMMO_SHOTGUN: sub = "shotgun"; break;
			case GUN_AMMO_ROCKET: sub = "rocket"; break;
			case GUN_AMMO_PLASMA: sub = "plasma"; break;
			case GUN_AMMO_THUNDER: sub = "thunder"; break;
			case GUN_AMMO_RAIL: sub = "rail";
			}
		}
		return new SignData(position, new String[] {typeS, sub, Mapper.byteArrayToFloatString(offset), ""});
	}
}