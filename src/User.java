
import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 5382539892804239553L;

	private String name;
	
	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
