package goalKeepin.config;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class GoalKeepinProps {

	private int pageSize = 10;
}
