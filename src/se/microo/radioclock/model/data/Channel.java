package se.microo.radioclock.model.data;

/**
 * Data class that stores channel variables.
 * 
 * @author Micro
 */
public class Channel implements Comparable<Channel> {

	private String name, m3u, mp3;

	/**
	 * Generates a new channel with name and link to m3u and mp3.
	 * 
	 * @param name
	 * @param m3u
	 * @param mp3
	 */
	public Channel(String name, String m3u, String mp3) {
		this.name = name;
		this.m3u = m3u;
		this.mp3 = mp3;
	}

	public String getName() {
		return name;
	}

	public String getM3u() {
		return m3u;
	}

	public String getMp3() {
		return mp3;
	}
	
	public String getStorageFormat() {
		return name+";"+m3u+";"+mp3+";";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m3u == null) ? 0 : m3u.hashCode());
		result = prime * result + ((mp3 == null) ? 0 : mp3.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (m3u == null) {
			if (other.m3u != null)
				return false;
		} else if (!m3u.equals(other.m3u))
			return false;
		if (mp3 == null) {
			if (other.mp3 != null)
				return false;
		} else if (!mp3.equals(other.mp3))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Channel [name=" + name + ", m3u=" + m3u + ", mp3=" + mp3 + "]";
	}

	@Override
	public int compareTo(Channel another) {
		return name.compareTo(another.name);
	}
	
	/**
	 * Parses a String containing channel data.
	 * String must look like: channelName;m3uLink;mp3Link;
	 * 
	 * @see Channel#getStorageFormat()
	 * @param s String to create channel from
	 * @return A new Channel object from provided data
	 */
	public static Channel createChannel(String s) {
		String[] values = s.split(";", -1);
		return new Channel(values[0], values[1], values[2]);
	}
}
