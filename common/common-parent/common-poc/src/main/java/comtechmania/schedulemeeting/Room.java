package comtechmania.schedulemeeting;

public class Room {
	private Integer roomId;
	private boolean available;
	private Slot[] slots;
	
	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

}
