package se.microo.radioclock.model;

public class MediaData {

	private String title, artist, path, displayName, songDuration;

	public MediaData(String title, String artist, String path, String displayName, String songDuration) {
		this.title = title;
		this.artist = artist;
		this.path = path;
		this.displayName = displayName;
		this.songDuration = songDuration;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getPath() {
		return path;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSongDuration() {
		return songDuration;
	}

	@Override
	public String toString() {
		return "MediaData [title=" + title + ", artist=" + artist + ", path="
				+ path + ", displayName=" + displayName + ", songDuration="
				+ songDuration + "]";
	}
}
