package goalKeepin.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

	private final long id;
	private final String name;
}
