package se.microo.radioclock.model.data;

public class Alarm {

	private long fireTime, volume;
	private String channelName, channelStream;
	
	public Alarm(long fireTime, long volume, String channelName, String channelStream) {
		this.fireTime = fireTime;
		this.volume = volume;
		this.channelName = channelName;
		this.channelStream = channelStream;
	}
	
	public Alarm(Alarm newAlarm) {
		this(newAlarm.fireTime, newAlarm.volume, newAlarm.channelName, newAlarm.channelStream);
	}
	
	
	public long getFireTime() {
		return fireTime;
	}
	
	public long getVolume() {
		return volume;
	}
	
	public String getChannelName() {
		return channelName;
	}

	public String getChannelStream() {
		return channelStream;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((channelName == null) ? 0 : channelName.hashCode());
		result = prime * result
				+ ((channelStream == null) ? 0 : channelStream.hashCode());
		result = prime * result + (int) (fireTime ^ (fireTime >>> 32));
		result = prime * result + (int) (volume ^ (volume >>> 32));
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
		Alarm other = (Alarm) obj;
		if (channelName == null) {
			if (other.channelName != null)
				return false;
		} else if (!channelName.equals(other.channelName))
			return false;
		if (channelStream == null) {
			if (other.channelStream != null)
				return false;
		} else if (!channelStream.equals(other.channelStream))
			return false;
		if (fireTime != other.fireTime)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Alarm [fireTime=" + fireTime + ", volume=" + volume
				+ ", channelName=" + channelName + ", channelStream="
				+ channelStream + "]";
	}
}
