package neil.demo.fitba;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name="team")
@Slf4j
public class Team implements Cloneable, Comparable<Team>, IdentifiedDataSerializable, Serializable {

	@Column                     	private String league;
	@Column							private int pos;
	@Id @Column						private String name;
	@Column							private int pld;
	@Column							private int w;
	@Column							private int d;
	@Column							private int l;
	@Column							private int gf;
	@Column							private int ga;
	@Column							private int gd;
	@Column							private int pts;
	
	
	// Sort on team name ? Or on position in the league ?
    @Override
    public int compareTo(Team that) {
    	//return this.name.compareTo(that.getName());
    	return (this.pos - that.getPos());
    }
    
	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		log.trace("Serialize: {}", this);
		out.writeUTF(this.league);
		out.writeInt(this.pos);
		out.writeUTF(this.name);
		out.writeInt(this.pld);
		out.writeInt(this.w);
		out.writeInt(this.d);
		out.writeInt(this.l);
		out.writeInt(this.gf);
		out.writeInt(this.ga);
		out.writeInt(this.gd);
		out.writeInt(this.pts);
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.league = in.readUTF();
		this.pos = in.readInt();
		this.name = in.readUTF();
		this.pld = in.readInt();
		this.w = in.readInt();
		this.d = in.readInt();
		this.l = in.readInt();
		this.gf = in.readInt();
		this.ga = in.readInt();
		this.gd = in.readInt();
		this.pts = in.readInt();
		log.trace("De-Serialize: {}", this);
	}

	@Override
	public int getFactoryId() {
		return MyConstants.CLASS_ID_MYDATASERIALIZABLEFACTORY;
	}

	@Override
	public int getId() {
		return MyConstants.CLASS_ID_TEAM;
	}
	
	@Override
	public Team clone() {
		try {
			return (Team) super.clone();
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

}
